package edu.oakland.lifestory;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class SearchMemoryActivity extends AppCompatActivity {
    CalendarView calendarView;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_memory);
        Toolbar toolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);
        //Hide the app name
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        backButton = toolbar.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent("edu.oakland.lifestory.ReturnHome");
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                v.getContext().startActivity(homeIntent);
            }
        });
        calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = new GregorianCalendar(year, month, dayOfMonth);
                //Toast.makeText(SearchMemoryActivity.this, (month+1)+"/"+dayOfMonth+"/"+year, Toast.LENGTH_SHORT).show();
                Intent searchMemory = new Intent(SearchMemoryActivity.this, AppHomeActivity.class);
                searchMemory.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                Bundle bundle = new Bundle();
                bundle.putString("SearchMemory", "SearchMemory");
                bundle.putString("SelectedDate", selectedDate.getTime().toString());
                searchMemory.putExtras(bundle);
                view.getContext().startActivity(searchMemory);
            }
        });
    }
}
