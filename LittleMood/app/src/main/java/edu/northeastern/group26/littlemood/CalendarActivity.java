package edu.northeastern.group26.littlemood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.SearchView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ImageView searchIcon = findViewById(R.id.searchIcon);
        CalendarView calendarView = findViewById(R.id.calendarView);
        final SearchView searchView = findViewById(R.id.searchView);
        // Fade-in animation
        final Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(200); // Set animation duration

        // Fade-out animation
        final Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(200);

        searchIcon.setOnClickListener(v -> {
            if (searchView.getVisibility() == View.GONE) {
                searchView.setVisibility(View.VISIBLE);
                searchView.startAnimation(fadeIn);
            } else {
                searchView.startAnimation(fadeOut);
                searchView.setVisibility(View.GONE);
            }
        });
        setDay(calendarView);

    }

    private void setDay(CalendarView calendarView){
        List<EventDay> events = new ArrayList<>();

        // Happy day
        Calendar happyDay = Calendar.getInstance();
        happyDay.set(2024, Calendar.MARCH, 15);
        events.add(new EventDay(happyDay, R.drawable.ic_smiling_face));

        // Sad day
        Calendar sadDay = Calendar.getInstance();
        sadDay.set(2024, Calendar.MARCH, 20);
        events.add(new EventDay(sadDay, R.drawable.ic_sad_face));

        // Add more events with different moods/icons as needed...
        calendarView.setEvents(events);
    }
}