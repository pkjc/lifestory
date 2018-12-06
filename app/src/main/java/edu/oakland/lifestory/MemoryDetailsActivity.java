package edu.oakland.lifestory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import edu.oakland.lifestory.model.Memory;

public class MemoryDetailsActivity extends AppCompatActivity {
    TextView memoryContent, memoryTitle;
    ImageView memoryImage;
    LinearLayout linearLayout;
    NestedScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        Memory memoryView = (Memory) intent.getSerializableExtra("MemoryView");

        renderMemoryView(memoryView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabClose);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Click here to close", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent homeIntent = new Intent("edu.oakland.lifestory.ReturnHome");
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                view.getContext().startActivity(homeIntent);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
       Memory memoryView = (Memory) intent.getSerializableExtra("MemoryView");

        renderMemoryView(memoryView);
    }

    private void renderMemoryView(Memory memoryView){
        //Set the memory title here
        memoryTitle = findViewById(R.id.memoryTitle);
        memoryTitle.setText(memoryView.getMemoryTitle());

        Toast.makeText(this, memoryView.getMemoryTitle(), Toast.LENGTH_SHORT).show();
        scrollView = findViewById(R.id.scrollView);
        linearLayout = scrollView.findViewById(R.id.linearLayout);
        memoryContent = linearLayout.findViewById(R.id.memoryContent);
        memoryImage = linearLayout.findViewById(R.id.memoryImage);

       memoryContent.setText(memoryView.getMemoryText());
        if(memoryView.getBitMapUri() != null) {
            memoryImage.setImageURI(Uri.parse(memoryView.getBitMapUri()));
        }
    }
}
