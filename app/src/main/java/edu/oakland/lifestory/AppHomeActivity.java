package edu.oakland.lifestory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import edu.oakland.lifestory.model.Memory;

public class AppHomeActivity extends BaseActivity {

    private TextView mTextMessage;
    private TextView noMemory;
    ArrayList<Memory> memories = new ArrayList<Memory>();
    LinearLayout memoryLayout;
    ImageButton backButton, quickCreateText, quickCreateImage, quickCreateAudio;

    BottomNavigationView navigation;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private String current_user_id;
    public Context context;
    public Date getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(Date searchDate) {
        this.searchDate = searchDate;
    }

    private Date searchDate;

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
                    Intent sentiMentAnalysisIntent = new Intent("edu.oakland.lifestory.SentimentAnalysisActivity");
                    startActivity(sentiMentAnalysisIntent);
                     return true;
                case R.id.navigation_calendar:
                     Intent searchIntent = new Intent("edu.oakland.lifestory.SearchMemory");
                     startActivity(searchIntent);
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
        //current_user_id = "AjKLJ0N8p5at5fsnSLLuHuPL2Zr1";

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
        //final ArrayList<Memory> memories = new ArrayList<>();
        //CollectionReference memoriesCollection = mFirestore.collection("memories");
        //Show progress dialog until memories load
        showProgressDialog();
        Query query = mFirestore.collection("memories").whereEqualTo("userId", current_user_id)
                .orderBy("memoryCreateDate", Query.Direction.DESCENDING).limit(5);

        //if search date available, perform search on selected date
        Date searchDate = getSearchDate();
        if(searchDate != null){
          Log.d("DATA", "Search date available"+searchDate);
          query = mFirestore.collection("memories").whereEqualTo("userId", current_user_id)
                    .whereGreaterThanOrEqualTo("memoryCreateDate", searchDate)
                  .orderBy("memoryCreateDate",Query.Direction.DESCENDING);
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    memories.clear();
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
        } else if (intent.hasExtra("SearchMemory")){
            //get the selected date from intent, call db query.
            Bundle bundle = intent.getExtras();
            setSearchDate(new Date(bundle.get("SelectedDate").toString()));
        } else if (intent.hasExtra("DialogAction")){
            //No search results found, load all memories
            setSearchDate(null);
            if(this.getSupportFragmentManager().findFragmentByTag("Dialog Fragment") != null){
                MessageDialogFragment dialogFragment = (MessageDialogFragment) this.getSupportFragmentManager().findFragmentByTag("Dialog Fragment");
                dialogFragment.dismiss();
            }
        }
        getMemoriesFromDB();
        resetNavigation();
    }

    private void renderMemories(final ArrayList<Memory> memories){
        //Hide progress dialog
        hideProgressDialog();
        memoryLayout.removeAllViews();
        if (memories.isEmpty()) {
            //if search date available its search result
            Date searchDate = getSearchDate();
            if(searchDate != null){
                //Show dialog that no results found
                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                MessageDialogFragment dialogFragment = new MessageDialogFragment();
                dialogFragment.show(fm, "Dialog Fragment");
            } else {
                noMemory = new TextView(getApplicationContext());
                noMemory.setText("No memories Yet! Click on Create Memory to add.");
                noMemory.setTextColor(getResources().getColor(android.R.color.black));
                memoryLayout.addView(noMemory);
            }
        } else {
            for (int position=0; position < memories.size(); position++) {
                Memory memory = memories.get(position);
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
                        cardView.setTag(position);
                        viewHolder = cardView.findViewById(R.id.viewHolder);
                        memoryTitle = viewHolder.findViewById(R.id.imgMemTitle);
                        //TextView memoryText = viewHolder.findViewById(R.id.memoryText);

                        TextView createDate = viewHolder.findViewById(R.id.createDate);
                        TextView createDate1 = viewHolder.findViewById(R.id.createDate1);
                        TextView createDate2 = viewHolder.findViewById(R.id.createDate2);

                        memoryImage = viewHolder.findViewById(R.id.memoryImage);

                        memoryTitle.setText(memory.getMemoryTitle());
                        //memoryText.setText(memory.getMemoryText());
                        if(memory.getMemoryCreateDate() != null) {
                            String formatDate = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US).format(memory.getMemoryCreateDate());

                            String[] dateSeparated = formatDate.split("\\s+");

                            createDate.setText(dateSeparated[0]);
                            createDate1.setText(dateSeparated[1].replace(",", ""));
                            createDate2.setText(dateSeparated[2]);
                        }
                        if(memory.getBitMapUri() != null){
                            //memoryImage.setImageURI(Uri.parse(memory.getBitMapUri()));
                            Glide.with(this).load(memory.getBitMapUri()).into(memoryImage);
                        }
                        //add listener for cardview
                        cardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               // Toast.makeText(v.getContext(), "Card view clicked!:", Toast.LENGTH_SHORT).show();
                                //get the memory object using memory title
                                int memPosition = (int) v.getTag();
                                Memory memoryDetail = memories.get(memPosition);
                                Log.d("MEMORY", ""+memoryDetail.getMemoryTitle()+":"+memoryDetail.getMemoryText());

                                Intent memoryDetailsIntent = new Intent(AppHomeActivity.this, MemoryDetailsActivity.class);
                                memoryDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                memoryDetailsIntent.putExtra("MemoryView", memoryDetail);
                                v.getContext().startActivity(memoryDetailsIntent);
                            }
                        });
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
//                            Uri imgUri = Uri.parse(memory.getBitMapUri());
//                            try {
//                                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
//                                Bitmap bitmap = getResizedBitmap(originalBitmap, 200);
//                                memoryImage.setImageBitmap(bitmap);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }

                            Log.d(" RENDER >> ", "renderMemories: " + memory.getBitMapUri());
                            Glide.with(AppHomeActivity.this).load(memory.getBitMapUri()).into(memoryImage);
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
