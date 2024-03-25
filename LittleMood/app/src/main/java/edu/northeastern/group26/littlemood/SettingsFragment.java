package edu.northeastern.group26.littlemood;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.PreferenceFragmentCompat;

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
    }


}