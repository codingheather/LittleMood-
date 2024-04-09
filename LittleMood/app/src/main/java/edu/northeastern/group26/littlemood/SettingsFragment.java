package edu.northeastern.group26.littlemood;


import android.app.AlertDialog;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String CHANNEL_ID = "settings notif";
    private static final int PERMISSION_REQUEST_CODE = 112;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);
        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }

        CheckBoxPreference notificationCheckBox = findPreference("update_notifications");
        notificationCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                boolean isChecked = (boolean) newValue;
                if (isChecked) {
                    addNotification();
                } else {
                    stopPushingNotification();
                }
                return true;
            }
        });


        findPreference("change_password").setOnPreferenceClickListener(preference -> {
            // edit password in firebase
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.change_pw, null);
            builder.setView(dialogView);

            EditText newPw = dialogView.findViewById(R.id.NewPw);
            EditText confirmNewPw = dialogView.findViewById(R.id.ConfirmNewPw);
            EditText currentPw = dialogView.findViewById(R.id.CurrentPw);

            builder.setPositiveButton("Confirm", (dialog, id) -> {
                String currentPassword = currentPw.getText().toString();
                String newPassword = newPw.getText().toString();
                String confirmNewPassword = confirmNewPw.getText().toString();

                if (confirmNewPassword.equals(newPassword)) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    assert user != null;

                    mAuth.signInWithEmailAndPassword(user.getEmail(), currentPassword).addOnCompleteListener((OnCompleteListener<AuthResult>) signInTask -> {
                        if (signInTask.isSuccessful()) {
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Log.d("PasswordUpdate", "User password updated.");
                                            Toast.makeText(getActivity(), "Successfully changed password", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Exception exception = updateTask.getException();
                                            if (exception != null) {
                                                Toast.makeText(getActivity(), "Failed to change password " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            if (signInTask.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getActivity(), "Wrong current password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "New passwords don't match", Toast.LENGTH_SHORT).show();
                }
            }).setNegativeButton("Back", (dialog, id) -> {
                dialog.dismiss();
            });

            AlertDialog dialog = builder.create();
            dialog.show();


            return true;
        });

        findPreference("change_username").setOnPreferenceClickListener(preference -> {
            // edit username in firebase
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.change_username, null);
            builder.setView(dialogView);

            EditText newU = dialogView.findViewById(R.id.NewU);

            builder.setPositiveButton("Confirm", (dialog, id) -> {
                String newEmail = newU.getText().toString();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                assert user != null;
                user.updateEmail(newEmail)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("EmailUpdate", "User email updated.");
                                Toast.makeText(getActivity(), "Successfully changed username", Toast.LENGTH_SHORT).show();
                            }
                        });
            })
                    .setNegativeButton("Back", (dialog, id) -> {
                        dialog.dismiss(); 
                    });

            AlertDialog dialog = builder.create();
            dialog.show();


            return true;
        });

        findPreference("logout").setOnPreferenceClickListener(preference -> {
            // log out and go back to log in page
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getActivity(), "Successfully logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
            return true;
        });

        findPreference("clear_data").setOnPreferenceClickListener(preference -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Clear Data")
                    .setMessage("Are you sure you want to clear all your data? This cannot be undone.")
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        assert user != null;
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query userQuery = ref.child("JournalEntries").orderByChild("email").equalTo(user.getEmail());

                        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                    userSnapshot.getRef().removeValue();
                                }

                                Log.d(getTag(), "Cleared all ser data.");
                                Toast.makeText(getActivity(), "Successfully cleared all user data", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), CalendarActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                getActivity().finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e(getTag(), "onCancelled", databaseError.toException());
                            }
                        });
                    })
                    .setNegativeButton(android.R.string.no, null).show();
            return true;
        });

        findPreference("delete_account").setOnPreferenceClickListener(preference -> {

            new AlertDialog.Builder(getContext())
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This cannot be undone.")
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        assert user != null;
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query userQuery = ref.child("JournalEntries").orderByChild("email").equalTo(user.getEmail());

                        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                    userSnapshot.getRef().removeValue();
                                }

                                user.delete().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d(getTag(), "User account and data deleted.");
                                        Toast.makeText(getActivity(), "Successfully deleted the account", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e(getTag(), "onCancelled", databaseError.toException());
                            }
                        });
                    })
                    .setNegativeButton(android.R.string.no, null).show();

            return true;
        });

        findPreference("delete_account").setOnPreferenceClickListener(preference -> {

            new AlertDialog.Builder(getContext())
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This cannot be undone.")
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        assert user != null;
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query userQuery = ref.child("JournalEntries").orderByChild("email").equalTo(user.getEmail());

                        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                    userSnapshot.getRef().removeValue();
                                }

                                user.delete().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d(getTag(), "User account and data deleted.");
                                        Toast.makeText(getActivity(), "Successfully deleted the account", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e(getTag(), "onCancelled", databaseError.toException());
                            }
                        });
                    })
                    .setNegativeButton(android.R.string.no, null).show();

            return true;
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(R.color.light_yellow)); // Ensure you have light_yellow defined in your colors.xml
    }

    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                        .setSmallIcon(R.drawable.bell)
                        .setContentTitle("Notification")
                        .setContentText("Notification enabled!");

        Intent notificationIntent = new Intent(getActivity(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    public void createNotificationChannel() {
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }  else {
                    Toast.makeText(getContext(), "Notification permission denied", Toast.LENGTH_SHORT).show();
                }
                return;

        }

    }

    private void stopPushingNotification() {
        NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);

        if (notificationManager != null) {
            notificationManager.cancelAll();
            Toast.makeText(getContext(), "Notification cancelled", Toast.LENGTH_SHORT).show();
        }

    }

}