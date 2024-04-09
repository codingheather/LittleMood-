package edu.northeastern.group26.littlemood;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatisticActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        recyclerView = findViewById(R.id.recyclerView);

        initData();
    }


    private void initData() {

        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("JournalEntries");
        // get the data of current user
        Query query = myRef.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int emojiNum;
                List<JournalEntry> mList = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();
                int month = calendar.get(Calendar.MONTH);

                String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                String nowMonth=months[month];
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    JournalEntry userEntry = snapshot.getValue(JournalEntry.class);
                    String subString = userEntry.date.substring(0,  userEntry.date.indexOf(" "));
                    Log.i("TAG", "onDataChange: "+userEntry.date);
                    if(nowMonth.contains(subString)){
                        mList.add(userEntry);
                    }

                }
                emojiNum = mList.size();
                try {
                    for (int i = 0; i < mList.size() - 1; i++) {
                        for (int j = mList.size() - 1; j > i; j--) {
                            if (mList.get(j).emoji.equals(mList.get(i).emoji)) {
                                mList.remove(j);
                                mList.get(i).emailNum++;
                            }
                        }
                    }
                    mList.sort((o1, o2) -> Integer.compare(o2.emailNum, o1.emailNum));

                    Log.i("TAG", "onDataChange: "+mList.size());
                    EmojiAdapter adapter=new EmojiAdapter(StatisticActivity.this,
                            mList, emojiNum);

                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(StatisticActivity.this,
                            LinearLayoutManager.VERTICAL, false));

                } catch (Exception e) {
                    System.out.println("Error parsing the date.");
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });


    }


}

