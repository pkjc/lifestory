package edu.oakland.lifestory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;

import edu.oakland.lifestory.model.Memory;

public class AppHomeActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private TextView noMemory = null;
    ArrayList<Memory> memories = new ArrayList<Memory>();
    LinearLayout memoryLayout = null;
    ImageButton backButton, quickCreateText, quickCreateImage, quickCreateAudio;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                     return true;
                case R.id.navigation_settings:
                     return true;
                case R.id.navigation_add:
                     return true;
                case R.id.navigation_dashboard:
                     return true;
                case R.id.navigation_calendar:
                     return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_home);
        Toolbar toolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        backButton = toolbar.findViewById(R.id.backButton);
        quickCreateText = toolbar.findViewById(R.id.quickCreateText);
        quickCreateImage = toolbar.findViewById(R.id.quickCreateImage);
        quickCreateAudio = toolbar.findViewById(R.id.quickCreateAudio);

        //For home screen disable back button
        backButton.setVisibility(View.INVISIBLE);
        quickCreateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to create memory screen
                Intent intent = new Intent(AppHomeActivity.this, MemoryActivity.class);
                startActivity(intent);
            }
        });
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
            LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.activity_memory_card, null);
            CardView cardView = linearLayout.findViewById(R.id.cardView);

            LinearLayout viewHolder = cardView.findViewById(R.id.viewHolder);
            TextView memoryTitle = viewHolder.findViewById(R.id.memoryTitle);
            TextView memoryText = viewHolder.findViewById(R.id.memoryText);

            memoryTitle.setText(memory.getMemoryTitle());
            memoryText.setText(memory.getMemoryText());

            memoryLayout.addView(linearLayout);
        }

    }

}
