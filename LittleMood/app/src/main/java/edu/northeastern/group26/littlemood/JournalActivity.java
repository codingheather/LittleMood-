package edu.northeastern.group26.littlemood;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class JournalActivity extends AppCompatActivity {

    JournalEntry newEntry;
    FirebaseUtil firebaseUtil = new FirebaseUtil();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        String name=getIntent().getStringExtra("name");
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