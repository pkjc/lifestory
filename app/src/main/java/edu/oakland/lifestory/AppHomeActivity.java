package edu.oakland.lifestory;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import edu.oakland.lifestory.model.Memory;

public class AppHomeActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private TextView noMemory = null;
    ArrayList<Memory> memories = new ArrayList<Memory>();
    LinearLayout memoryLayout = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_settings:
                    //mTextMessage.setText(R.string.action_settings);
                    return true;
                case R.id.navigation_add:
                    //mTextMessage.setText(R.string.title_create);
                    return true;
                case R.id.navigation_dashboard:
                    //mTextMessage.setText(R.string.title_search);
                    return true;
                case R.id.navigation_calendar:
                    //mTextMessage.setText(R.string.title_calendar);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_home);

        memoryLayout = findViewById(R.id.memoryLayout);
        memoryLayout.setDividerDrawable(getDrawable(R.drawable.separator_style));
        memories.add(new Memory("First memory", "Glad to have journal of my own!"));
        memories.add(new Memory("Second Memory", "It's quite interesting with Android"));
       // mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(memories.isEmpty()) {
            noMemory = new TextView(getApplicationContext());
            noMemory.setText("No memories Yet! Click on Create Memory to add.");
            memoryLayout.addView(noMemory);
        }

        for (Memory memory : memories) {
            LayoutInflater inflater = LayoutInflater.from(this);
            ConstraintLayout constraintLayout = (ConstraintLayout) inflater.inflate(R.layout.activity_memory, null);

            TextView memoryTitle = constraintLayout.findViewById(R.id.memoryTitle);
            TextView memoryText = constraintLayout.findViewById(R.id.memoryText);

            memoryTitle.setText(memory.getMemoryTitle());
            memoryText.setText(memory.getMemoryText());

            memoryLayout.addView(constraintLayout);

        }

    }

}
