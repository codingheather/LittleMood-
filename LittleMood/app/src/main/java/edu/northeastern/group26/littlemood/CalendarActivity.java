package edu.northeastern.group26.littlemood;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.CalendarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CalendarActivity extends AppCompatActivity {
    private ImageView addJournalButton;
    private ImageView searchIcon;
    private ImageView settingIcon;
    private ImageView statisticIcon;
    private CalendarView calendarView;
    private SearchView searchInput;
    private TextView quoteTextView;
    private LinearLayout waitQuote, waitCalendar;
    private Calendar calendar;
    private String dateString;
    private List<CalendarDay> events = new ArrayList<>(); // events on the calendar
    private static final int SUCCESS = 100;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ExecutorService executorService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executorService = Executors.newFixedThreadPool(2);
        setContentView(R.layout.activity_calendar);
        searchIcon = findViewById(R.id.searchIcon);
        calendarView = findViewById(R.id.calendarView);
        addJournalButton = findViewById(R.id.addJournalButton);
        settingIcon = findViewById(R.id.settingsIcon);
        quoteTextView = findViewById(R.id.inspiringWordsTextView);
        searchInput = findViewById(R.id.searchView);
        statisticIcon = findViewById(R.id.chartIcon);
        waitCalendar = findViewById(R.id.loading_calendar_progressBar);
        waitQuote = findViewById(R.id.loading_quote_progressBar);

        // initial set calendar and daily quote to invisible
        calendarView.setVisibility(View.INVISIBLE);
        quoteTextView.setVisibility(View.INVISIBLE);
        waitCalendar.setVisibility(View.VISIBLE);
        waitQuote.setVisibility(View.VISIBLE);

        // update title (hello xyz)
        updateTitle();
        // get daily quote(inspiring words) from API and update UI
        getDailyQuote(quoteTextView);

        // enter mood page
        addJournalButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, MoodActivity.class);
            startActivity(intent);
        });

        // enter setting page
        settingIcon.setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        // enter statistic page
        statisticIcon.setOnClickListener(view -> {
            Intent intent = new Intent(this, StatisticActivity.class);
            startActivity(intent);
        });

        // define search activity
        searchIcon = findViewById(R.id.searchIcon);
        searchIcon.setOnClickListener(v -> showSearchedDate(searchInput));

        // set maximum date to today
        Calendar today = Calendar.getInstance();
        calendarView.setMaximumDate(today);

        // jump to recently updated journal month
        dateString = getIntent().getStringExtra("UPDATE DATE");
        if (dateString != null){
            try {
                SimpleDateFormat format = new SimpleDateFormat("MMMM dd. yyyy", Locale.US);
                // Extract the substring that matches the date format, excluding the day of the week
                String datePart = dateString.substring(0, dateString.lastIndexOf(" "));
                Date date = format.parse(datePart);
                calendarView.setDate(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // show emojis of each date
        setDay();

        // set date click event
        calendarView.setOnCalendarDayClickListener(calendarDay -> {
            Calendar clickedDay = calendarDay.getCalendar();
            // Check if the clicked day is before or the same as 'today'
            if (!clickedDay.after(today)) {
                FirebaseUser user = mAuth.getCurrentUser();
                final Intent[] intent = new Intent[1];

                assert user != null;

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd. yyyy EEE", Locale.getDefault());

                String calendarDate = dateFormat.format(calendarDay.getCalendar().getTime());

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query userQuery = ref.child("JournalEntries").orderByChild("email").equalTo(user.getEmail());

                userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            if (userSnapshot.child("date").getValue(String.class).equals(calendarDate)) {
                                intent[0] = new Intent(CalendarActivity.this, JournalActivity.class);
                                intent[0].putExtra("name", userSnapshot.child("emoji").getValue(String.class));
                                intent[0].putExtra("text", userSnapshot.child("text").getValue(String.class));
                                intent[0].putExtra("photo", userSnapshot.child("photo").getValue(String.class));
                            }
                        }

                        if (intent[0] == null) {
                            intent[0] = new Intent(CalendarActivity.this, MoodActivity.class);
                        }

                        // Extract the year, month, and day from the clicked CalendarDay
                        int year = calendarDay.getCalendar().get(Calendar.YEAR);
                        // month is zero-based Indexing
                        int month = calendarDay.getCalendar().get(Calendar.MONTH) + 1;
                        int day = calendarDay.getCalendar().get(Calendar.DAY_OF_MONTH);

                        // Put the year, month, emoji, and day as extras in the intent
                        intent[0].putExtra("YEAR", year);
                        intent[0].putExtra("MONTH", month);
                        intent[0].putExtra("DAY", day);

                        startActivity(intent[0]);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });
            } else {
                Toast.makeText(CalendarActivity.this, "Future dates are not selectable.", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void updateTitle(){
        TextView textView = findViewById(R.id.titleText);
        FirebaseUser user = mAuth.getCurrentUser();
        String userName = getIntent().getStringExtra("username");
        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            textView.setText(String.format("Hello %s!", user.getDisplayName()));
        } else if (userName != null){
            textView.setText(String.format("Hello %s!", userName));
        } else {
            textView.setText("Hello Guest!");
        }
    }

    private void setDay(){
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("JournalEntries");

        Query query = myRef.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                executorService.execute(() -> {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        JournalEntry userEntry = snapshot.getValue(JournalEntry.class);

                        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd. yyyy EEE");
                        Date date;
                        try {
                            date = sdf.parse(userEntry.date);
                            String emoji = userEntry.emoji;
                            calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            Drawable emojiDrawable = EmojiUtil.emojiToDrawable(CalendarActivity.this, emoji, 100);
                            CalendarDay calendarDay = new CalendarDay(calendar);
                            calendarDay.setImageDrawable(emojiDrawable);
                            events.add(calendarDay);
                        } catch (ParseException e) {
                            Log.e("Parsed error", "Error parsing the date.");
                            e.printStackTrace();
                        }
                    }

                    // Make UI updates after data is processed
                    runOnUiThread(() -> {
                        calendarView.setCalendarDays(events);
                        waitCalendar.setVisibility(View.GONE);
                        calendarView.setVisibility(View.VISIBLE);
                    });
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private void showSearchedDate(@NonNull SearchView searchView){
        searchView.setQueryHint("YYYY-MM");
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();

        if (searchView.getVisibility() == View.GONE) {
            searchView.setVisibility(View.VISIBLE);
        } else {
            searchView.setVisibility(View.GONE);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
                format.setLenient(false);
                try {
                    Date date = format.parse(query);
                    if (date != null) {
                        calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        Toast.makeText(CalendarActivity.this, "Searched month: " + query, Toast.LENGTH_SHORT).show();
                        calendarView.setDate(date);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    // Notify user about the wrong date format
                    Toast.makeText(CalendarActivity.this, "Invalid month format. Please use YYYY-MM.", Toast.LENGTH_SHORT).show();
                }
                searchView.setVisibility(View.GONE);
                searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void getDailyQuote(TextView textView){
        executorService.execute(() -> {
            String jsonData = askZenQuote();
            Log.d("jsonData：",jsonData);
            Message message=Message.obtain();
            Bundle bundle=new Bundle();
            bundle.putString("data",jsonData);
            message.setData(bundle);
            message.what=SUCCESS;
            String responseData = message.getData().getString("data");
            try {
                JSONArray jsonArray = new JSONArray(responseData);
                if (!jsonArray.isNull(0)) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String quoteText = jsonObject.optString("q");
                    String author = jsonObject.optString("a");
                    String fullQuote = "\"" + quoteText + "\" - " + author;
                    // update UI with the quote
                    runOnUiThread(() -> {
                        textView.setText(fullQuote);
                        waitQuote.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private String askZenQuote(){
        try{
            String url1 = "https://zenquotes.io/api/today"; // Get a quote for daily inspiration
            URL url = new URL(url1);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                String strRead;
                while ((strRead = bufferedReader.readLine()) != null) {
                    stringBuilder.append(strRead).append("\r\n");
                }
                bufferedReader.close();
                inputStream.close();
                return stringBuilder.toString();
            }
        } catch (Exception e){
            e.printStackTrace();
            return e.toString();
        }
        return "Failed to fetch quote";
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTitle();
    }
}