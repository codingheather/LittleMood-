package edu.northeastern.group26.littlemood;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);

        findPreference("change_password").setOnPreferenceClickListener(preference -> {
            // edit password in firebase
            Toast.makeText(getActivity(), "Successfully changed password", Toast.LENGTH_SHORT).show();
            return true;
        });

        findPreference("change_username").setOnPreferenceClickListener(preference -> {
            // edit username in firebase
            Toast.makeText(getActivity(), "Successfully changed username", Toast.LENGTH_SHORT).show();
            return true;
        });

        findPreference("logout").setOnPreferenceClickListener(preference -> {
            // log out and go back to log in page
            Toast.makeText(getActivity(), "Successfully logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            return true;
        });

        findPreference("clear_data").setOnPreferenceClickListener(preference -> {
            // delete user from database
            Toast.makeText(getActivity(), "Successfully cleared all the data", Toast.LENGTH_SHORT).show();
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
}