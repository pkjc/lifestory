package edu.oakland.lifestory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.oakland.lifestory.model.Memory;

public class MemoryActivity extends AppCompatActivity {
    EditText memoryTitle, memoryContent = null;
    ImageButton backButton, createMemButton = null;

    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("memories/memory");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
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

        memoryTitle = findViewById(R.id.imgMemTitle);
        memoryContent = findViewById(R.id.memoryContent);
        createMemButton = toolbar.findViewById(R.id.createMemButton);

        createMemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memoryTag = memoryTitle.getText().toString();
                String memoryText = memoryContent.getText().toString();

                //Add date and time of creation as well
                Memory memory = new Memory(memoryTag, memoryText);

                mDocRef.set(memory).addOnCompleteListener(new OnCompleteListener<Void>() {
                    private static final String TAG = "MemoryActivity";
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MemoryActivity.this, "Memory Added Successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MemoryActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: ", task.getException());
                        }
                    }
                });

                Intent homeIntent = new Intent("edu.oakland.lifestory.ReturnHome");
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                memory.setMemoryType("Memory");
                homeIntent.putExtra("Memory", memory);
                v.getContext().startActivity(homeIntent);
            }
        });
    }
}
