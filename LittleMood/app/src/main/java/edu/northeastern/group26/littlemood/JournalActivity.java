package edu.northeastern.group26.littlemood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class JournalActivity extends AppCompatActivity {

    JournalEntry newEntry;
    FirebaseUtil firebaseUtil = new FirebaseUtil();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private String photoUrl = "";

    private EditText textInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        textInput = (EditText) findViewById(R.id.add_content);
        textInput.setText(getIntent().getStringExtra("text"));
        // year month day from calendar, name(emoji) from mood
        String name = getIntent().getStringExtra("name");
        Integer year = getIntent().getIntExtra("YEAR", -1);
        Integer month = getIntent().getIntExtra("MONTH", -1);
        Integer day = getIntent().getIntExtra("DAY", -1);
        photoUrl = getIntent().getStringExtra("photo");
        // if not received date, default today
        if (year == -1 || month == -1 || day == -1) {
            // Data is missing or invalid, use today's date as default
            Calendar today = Calendar.getInstance();
            year = today.get(Calendar.YEAR);
            // month is zero-based Indexing
            month = today.get(Calendar.MONTH) + 1;
            day = today.get(Calendar.DAY_OF_MONTH);
        }

        // update time text ui
        TextView timeTextView = findViewById(R.id.time);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year); // Set the year
        calendar.set(Calendar.MONTH, month - 1); // Set the month (0-based index)
        calendar.set(Calendar.DAY_OF_MONTH, day); // Set the day of the month
        // Format the date as "MMMM dd. EEE" (e.g., "March 24. Sat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd. yyyy EEE", Locale.getDefault());
        timeTextView.setText(dateFormat.format(calendar.getTime()));

        TextView img1=findViewById(R.id.img1);
        img1.setText(name);

        // Capture image when captureButton is clicked
        ImageView captureButton = (ImageView) findViewById(R.id.img);
        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(this)
                    .load(photoUrl)
                    .into(captureButton);
        }

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("heather", "no code");
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image.jpg");
                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();

                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                int year = getIntent().getIntExtra("YEAR", -1);
                int month = getIntent().getIntExtra("MONTH", -1);
                int day = getIntent().getIntExtra("DAY", -1);

                intent.putExtra("YEAR", year);
                intent.putExtra("MONTH", month);
                intent.putExtra("DAY", day);
                intent.putExtra("name", getIntent().getStringExtra("name"));
                intent.putExtra("text", textInput.getText().toString());
                intent.putExtra("photo", photoUrl);

                startActivity(intent);
            }
        });

        ImageView ok = findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();
                String date = ((TextView) findViewById(R.id.time)).getText().toString();
                String emoji = ((TextView) findViewById(R.id.img1)).getText().toString();
                String text = ((EditText) findViewById(R.id.add_content)).getText().toString();
                String email = user.getEmail();
                newEntry = new JournalEntry(date, emoji, text, photoUrl, email);
                firebaseUtil.saveJournalEntry(newEntry);
                Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(intent);
            }
        });

        ImageView close=findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}