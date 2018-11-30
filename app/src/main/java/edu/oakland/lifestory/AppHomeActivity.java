package edu.oakland.lifestory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.io.IOException;
import java.util.ArrayList;

import edu.oakland.lifestory.model.Memory;

public class AppHomeActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private TextView noMemory = null;
    ArrayList<Memory> memories = new ArrayList<Memory>();
    LinearLayout memoryLayout = null;
    ImageButton backButton, quickCreateText, quickCreateImage, quickCreateAudio;
    BottomNavigationView navigation = null;

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
                     Intent intent = new Intent("edu.oakland.lifestory.AddMemory");
                     startActivity(intent);
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

        quickCreateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to create image memory screen
                Intent intent = new Intent(AppHomeActivity.this, ImageMemoryActivity.class);
                startActivity(intent);
            }
        });
        memoryLayout = findViewById(R.id.memoryLayout);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(memories.isEmpty()) {
            noMemory = new TextView(getApplicationContext());
            noMemory.setText("No memories Yet! Click on Create Memory to add.");
            noMemory.setTextColor(getResources().getColor(android.R.color.black));
            memoryLayout.addView(noMemory);
        }
        renderMemories();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!memories.isEmpty()) {
            memoryLayout.removeAllViews();
        }
        renderMemories();
        resetNavigation();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("LIFESTORY", "Here for new memory");

        if(intent.hasExtra("Memory")){
            Memory newMemory = (Memory) intent.getSerializableExtra("Memory");
            memories.add(newMemory);
        } else if(intent.hasExtra("ImageMemory")){
            Memory newMemory = (Memory) intent.getSerializableExtra("ImageMemory");
            memories.add(newMemory);
        }
        memoryLayout.removeAllViews();
        renderMemories();
        resetNavigation();
    }

    private void renderMemories(){
        for (Memory memory : memories) {
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout linearLayout = null;
            CardView cardView = null;
            LinearLayout viewHolder = null;
            TextView memoryTitle = null;
            switch(memory.getMemoryType()){
                case "Memory":
                    linearLayout = (LinearLayout) inflater.inflate(R.layout.activity_memory_card, null);
                    cardView = linearLayout.findViewById(R.id.cardView);

                    viewHolder = cardView.findViewById(R.id.viewHolder);
                    memoryTitle = viewHolder.findViewById(R.id.imgMemTitle);
                    TextView memoryText = viewHolder.findViewById(R.id.memoryText);

                    memoryTitle.setText(memory.getMemoryTitle());
                    memoryText.setText(memory.getMemoryText());
                    memoryLayout.addView(linearLayout);
                    break;
                case "ImageMemory":
                    linearLayout = (LinearLayout) inflater.inflate(R.layout.activity_image_memory_card, null);
                    cardView = linearLayout.findViewById(R.id.cardView);

                    viewHolder = cardView.findViewById(R.id.viewHolder);
                    memoryTitle = viewHolder.findViewById(R.id.imgMemTitle);
                    ImageView memoryImage = viewHolder.findViewById(R.id.memoryImage);

                    memoryTitle.setText(memory.getMemoryTitle());
                    Uri imgUri = Uri.parse(memory.getBitMapUri());
                    try {
                        Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                        Bitmap bitmap = getResizedBitmap(originalBitmap, 200);
                        memoryImage.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    memoryLayout.addView(linearLayout);
                    break;
            }
        }
    }
    private Bitmap getResizedBitmap(Bitmap image, int maxSize){
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width/(float)height;
        if(bitmapRatio > 0){
            width = maxSize;
            height = (int)(width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height*bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    private void resetNavigation(){
        if(navigation != null) {
            navigation.setSelectedItemId(R.id.navigation_home);
        }

    }
}
