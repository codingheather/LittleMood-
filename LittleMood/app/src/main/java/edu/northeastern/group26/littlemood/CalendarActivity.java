package edu.northeastern.group26.littlemood;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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


public class CalendarActivity extends AppCompatActivity {
    private ImageView addJournalButton;
    private ImageView searchIcon;
    private ImageView settingIcon;
    private CalendarView calendarView;
    private SearchView searchInput;
    private TextView quoteTextView;
    private Handler quoteHandler;
    private Calendar calendar;
    private List<CalendarDay> events = new ArrayList<>(); // events on the calendar
    private static final int SUCCESS = 100;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        searchIcon = findViewById(R.id.searchIcon);
        calendarView = findViewById(R.id.calendarView);
        addJournalButton = findViewById(R.id.addJournalButton);
        settingIcon = findViewById(R.id.settingsIcon);
        quoteTextView = findViewById(R.id.inspiringWordsTextView);
        searchInput = findViewById(R.id.searchView);

        // get daily quote(inspiring words) from API and update UI
        getDailyQuote();

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

        // define search activity
        searchIcon = findViewById(R.id.searchIcon);
        searchIcon.setOnClickListener(v -> showSearchedDate(searchInput));

        // show emojis under each date
        setDay(calendarView);

        calendarView.setOnCalendarDayClickListener(calendarDay -> {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final Intent[] intent = new Intent[1];

            assert user != null;

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd. yyyy EEE", Locale.getDefault());

            String calendarDate = dateFormat.format(calendarDay.getCalendar().getTime());

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query userQuery = ref.child("JournalEntries").orderByChild("email").equalTo(user.getEmail());

            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        if (userSnapshot.child("date").getValue(String.class).equals(calendarDate)) {
                            intent[0] = new Intent(CalendarActivity.this, JournalActivity.class);
                            intent[0].putExtra("name", userSnapshot.child("emoji").getValue(String.class));
                            intent[0].putExtra("text", userSnapshot.child("text").getValue(String.class));
                            intent[0].putExtra("photo", userSnapshot.child("photo").getValue(String.class));
                        }
                    }

                    if (intent[0] == null){
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


        });
    }

    private void setDay(CalendarView calendarView){
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("JournalEntries");

        Query query = myRef.orderByChild("email").equalTo(user.getEmail());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    JournalEntry userEntry = snapshot.getValue(JournalEntry.class);

                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd. yyyy EEE");
                    Date date;
                    try {
                        date = sdf.parse(userEntry.date);
                        String emoji = userEntry.emoji;
                        calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        Drawable emojiDrawable = emojiToDrawable(CalendarActivity.this, emoji, 100);
                        CalendarDay calendarDay = new CalendarDay(calendar);
                        calendarDay.setImageDrawable(emojiDrawable);
                        events.add(calendarDay);
                        calendarView.setCalendarDays(events);
                        System.out.println("Parsed date: " + date);
                    } catch (ParseException e) {
                        System.out.println("Error parsing the date.");
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void showSearchedDate(SearchView searchView){
        searchView.setQueryHint("Enter month (YYYY-MM)");
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
                        Toast.makeText(CalendarActivity.this, "Selected month: " + query, Toast.LENGTH_SHORT).show();
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

    private void getDailyQuote(){
        quoteHandler = new Handler(message -> {
            if(message.what == SUCCESS){
                String responseData = message.getData().getString("data");
                try {
                    JSONArray jsonArray = new JSONArray(responseData);
                    if (!jsonArray.isNull(0)) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String quoteText = jsonObject.optString("q"); // Assuming "q" is the quote text
                        String author = jsonObject.optString("a"); // Assuming "a" is the author
                        String fullQuote = "\"" + quoteText + "\" - " + author;
                        // update UI with the quote
                        runOnUiThread(() -> quoteTextView.setText(fullQuote));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        });

        new Thread(() -> {
            String jsonData = askZenQuote();
            Log.d("jsonData：",jsonData);
            JSONObject jsonObject = null;
            Message message=Message.obtain();
            Bundle bundle=new Bundle();
            bundle.putString("data",jsonData);
            message.setData(bundle);
            message.what=SUCCESS;
            quoteHandler.sendMessage(message);
        }).start();

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

    public Drawable emojiToDrawable(Context context, String emoji, int sizeInPixels) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(sizeInPixels);
        paint.setTypeface(Typeface.DEFAULT); // Set the typeface you want to use
        paint.setTextAlign(Paint.Align.LEFT);

        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(emoji) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(emoji, 0, baseline, paint);

        return new BitmapDrawable(context.getResources(), image);
    }
}