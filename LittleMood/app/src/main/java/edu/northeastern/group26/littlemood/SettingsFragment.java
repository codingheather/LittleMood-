package edu.northeastern.group26.littlemood;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_settings, rootKey);

        findPreference("change_password").setOnPreferenceClickListener(preference -> {
            // Implement or call your change password logic here
            return true;
        });

        findPreference("change_username").setOnPreferenceClickListener(preference -> {
            // Implement or call your change username logic here
            return true;
        });

        findPreference("logout").setOnPreferenceClickListener(preference -> {
            logOutUser();
            return true;
        });

        findPreference("clear_data").setOnPreferenceClickListener(preference -> {
            clearUserData();
            return true;
        });
    }

    private void logOutUser() {
        // Your log out logic here
    }

    private void clearUserData() {
        // Your clear data logic here
    }

}