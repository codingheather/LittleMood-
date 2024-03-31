package edu.northeastern.group26.littlemood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

public class StatisticActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        recyclerView = findViewById(R.id.recyclerView);

        // TODO: Set up RecyclerView with LayoutManager and Adapter

    }
}