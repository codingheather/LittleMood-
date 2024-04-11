package edu.northeastern.group26.littlemood;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        FrameLayout frame = findViewById(R.id.settings_frame);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(frame.getId(), new SettingsFragment())
                .commit();
    }
}