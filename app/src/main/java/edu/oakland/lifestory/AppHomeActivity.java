package edu.oakland.lifestory;

import android.content.Intent;
import android.graphics.Bitmap;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;

import edu.oakland.lifestory.model.Memory;

public class AppHomeActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private TextView noMemory;
    ArrayList<Memory> memories = new ArrayList<Memory>();
    LinearLayout memoryLayout;
    ImageButton backButton, quickCreateText, quickCreateImage, quickCreateAudio;

    BottomNavigationView navigation;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private String current_user_id;

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

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

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

        getMemoriesFromDB();
    }

    private void getMemoriesFromDB() {
        final ArrayList<Memory> memories = new ArrayList<>();
        //CollectionReference memoriesCollection = mFirestore.collection("memories");
        Query query = mFirestore.collection("memories").whereEqualTo("userId", current_user_id);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot document: task.getResult()){
                        memories.add(document.toObject(Memory.class));
                    }
                    Log.d("DATA =======> ",  memories.size()+"");
                    renderMemories(memories);
                } else {
                    Log.d("ERROR =======> ", "error getting documents: ", task.getException());
                }
            }
        });

//        memoriesCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()){
//                    for (DocumentSnapshot document: task.getResult()){
//                        memories.add(document.toObject(Memory.class));
//                    }
//                    Log.d("DATA =======> ",  memories.size()+"");
//                    renderMemories(memories);
//                } else {
//                    Log.d("ERROR =======> ", "error getting documents: ", task.getException());
//                }
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!memories.isEmpty()) {
            memoryLayout.removeAllViews();
        }
        getMemoriesFromDB();
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
        getMemoriesFromDB();
        resetNavigation();
    }

    private void renderMemories(ArrayList<Memory> memories){
        if (memories.isEmpty()) {
            noMemory = new TextView(getApplicationContext());
            noMemory.setText("No memories Yet! Click on Create Memory to add.");
            noMemory.setTextColor(getResources().getColor(android.R.color.black));
            memoryLayout.addView(noMemory);
        } else {
            memoryLayout.removeAllViews();
            for (Memory memory : memories) {
                LayoutInflater inflater = LayoutInflater.from(this);
                LinearLayout linearLayout = null;
                CardView cardView = null;
                LinearLayout viewHolder = null;
                TextView memoryTitle = null;
                ImageView memoryImage = null;
                switch(memory.getMemoryType()){
                    case "Memory":
                        linearLayout = (LinearLayout) inflater.inflate(R.layout.activity_memory_card, null);
                        cardView = linearLayout.findViewById(R.id.cardView);
                        viewHolder = cardView.findViewById(R.id.viewHolder);
                        memoryTitle = viewHolder.findViewById(R.id.imgMemTitle);
                        //TextView memoryText = viewHolder.findViewById(R.id.memoryText);
                        TextView createDate = viewHolder.findViewById(R.id.createDate);
                        memoryImage = viewHolder.findViewById(R.id.memoryImage);

                        memoryTitle.setText(memory.getMemoryTitle());
                        //memoryText.setText(memory.getMemoryText());
                        if(memory.getMemoryCreateDate() != null) {
                            String formatDate = DateFormat.getDateTimeInstance().format(memory.getMemoryCreateDate());
                            createDate.setText(formatDate);
                        }
                        if(memory.getBitMapUri() != null){
                            memoryImage.setImageURI(Uri.parse(memory.getBitMapUri()));
                        }
                        memoryLayout.addView(linearLayout);
                        break;
                    case "ImageMemory":
                        linearLayout = (LinearLayout) inflater.inflate(R.layout.activity_image_memory_card, null);
                        cardView = linearLayout.findViewById(R.id.cardView);

                        viewHolder = cardView.findViewById(R.id.viewHolder);
                        memoryTitle = viewHolder.findViewById(R.id.imgMemTitle);
                        memoryImage = viewHolder.findViewById(R.id.memoryImage);

                        memoryTitle.setText(memory.getMemoryTitle());
                        if(memory.getBitMapUri() != null) {
                            Uri imgUri = Uri.parse(memory.getBitMapUri());
                            try {
                                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                                Bitmap bitmap = getResizedBitmap(originalBitmap, 200);
                                memoryImage.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        memoryLayout.addView(linearLayout);
                        break;
                }
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
