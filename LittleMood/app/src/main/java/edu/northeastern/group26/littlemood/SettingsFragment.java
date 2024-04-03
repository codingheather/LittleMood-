package edu.northeastern.group26.littlemood;

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
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String CHANNEL_ID = "settings notif";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);

        findPreference("update_notifications").setOnPreferenceClickListener(preference -> {
            createNotificationChannel();
            addNotification();

            return true;
        });

        findPreference("change_password").setOnPreferenceClickListener(preference -> {
            // edit password in firebase
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.change_pw, null);
            builder.setView(dialogView);

            EditText newPw = dialogView.findViewById(R.id.NewPw);
            EditText confirmNewPw = dialogView.findViewById(R.id.ConfirmNewPw);

            builder.setPositiveButton("Confirm", (dialog, id) -> {
                String newPassword = newPw.getText().toString();
                String confirmNewPassword = confirmNewPw.getText().toString();

                if (confirmNewPassword.equals(newPassword)) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    assert user != null;
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("PasswordUpdate", "User password updated.");
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "check your new password", Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            Toast.makeText(getActivity(), "Successfully changed password", Toast.LENGTH_SHORT).show();
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
                            }
                        });
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            Toast.makeText(getActivity(), "Successfully changed username", Toast.LENGTH_SHORT).show();
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

    public void sendNotification() {

        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(getContext(), ReceiveNotificationActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(getContext(), (int) System.currentTimeMillis(), intent, 0);
        PendingIntent callIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(),
                new Intent(this, FakeCallActivity.class), 0);


        // Build notification
        // Need to define a channel ID after Android Oreo
        String channelId = getString(R.string.channel_id);
        NotificationCompat.Builder notifyBuild = new NotificationCompat.Builder(this, channelId)
                //"Notification icons must be entirely white."
                .setSmallIcon(R.drawable.foo)
                .setContentTitle("New mail from " + "test@test.com")
                .setContentText("Subject")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // hide the notification after its selected
                .setAutoCancel(true)
                .addAction(R.drawable.foo, "Call", callIntent)
                .setContentIntent(pIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // // notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, notifyBuild.build());

    }

    public void createNotificationChannel() {
        // This must be called early because it must be called before a notification is sent.
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}