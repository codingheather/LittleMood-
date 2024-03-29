package edu.northeastern.group26.littlemood;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class JournalActivity extends AppCompatActivity {

    JournalEntry newEntry;
    FirebaseUtil firebaseUtil = new FirebaseUtil();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        // year month day from calendar, name(emoji) from mood
        String name = getIntent().getStringExtra("name");
        Integer year =getIntent().getIntExtra("YEAR", -1);
        Integer month =getIntent().getIntExtra("MONTH", -1);
        Integer day =getIntent().getIntExtra("DAY", -1);
        // if not received date, default today
        if (year == -1 || month == -1 || day == -1) {
            // Data is missing or invalid, use today's date as default
            Calendar today = Calendar.getInstance();
            year = today.get(Calendar.YEAR);
            // month is zero-based Indexing
            month = today.get(Calendar.MONTH) + 1;
            day = today.get(Calendar.DAY_OF_MONTH);
        }
//        // date string
//        String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);

        // update time text ui
        TextView timeTextView = findViewById(R.id.time);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year); // Set the year
        calendar.set(Calendar.MONTH, month - 1); // Set the month (0-based index)
        calendar.set(Calendar.DAY_OF_MONTH, day); // Set the day of the month
        // Format the date as "MMMM dd. EEE" (e.g., "March 24. Sat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd. EEE", Locale.getDefault());
        timeTextView.setText(dateFormat.format(calendar.getTime()));

        TextView img1=findViewById(R.id.img1);
        img1.setText(name);

        ImageView ok = findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            FirebaseUser user = mAuth.getCurrentUser();
            String date = ((TextView) findViewById(R.id.time)).getText().toString();
            String emoji = ((TextView) findViewById(R.id.img1)).getText().toString();
            String text = ((EditText) findViewById(R.id.add_content)).getText().toString();
            String photo = "placeholder: for getting the index/id of the photo";
            String email = user.getEmail();
            @Override
            public void onClick(View view) {
                newEntry = new JournalEntry(date, emoji, text, photo, email);
                firebaseUtil.saveJournalEntry(newEntry);
                finish();
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