package edu.oakland.lifestory;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import edu.oakland.lifestory.model.Memory;

public class MemoryActivity extends AppCompatActivity {
    EditText memoryTitle, memoryContent = null;
    Button createMemButton = null;
    ImageButton backButton = null;
    RelativeLayout toolHolder = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
        Toolbar toolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);

        toolHolder = toolbar.findViewById(R.id.toolHolder);
        backButton = toolHolder.findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent("edu.oakland.lifestory.ReturnHome");
                v.getContext().startActivity(homeIntent);
            }
        });

        memoryTitle = findViewById(R.id.memoryTitle);
        memoryContent = findViewById(R.id.memoryContent);
        createMemButton = findViewById(R.id.createMemButton);

        createMemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memoryTag = memoryTitle.getText().toString();
                String memoryText = memoryContent.getText().toString();

                //Add date and time of creation as well
                Memory memory = new Memory(memoryTag, memoryText);
                //Toast.makeText(getApplicationContext(), "Memory Created", Toast.LENGTH_LONG).show();

                Intent homeIntent = new Intent("edu.oakland.lifestory.ReturnHome");
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                homeIntent.putExtra("Memory", memory);
                v.getContext().startActivity(homeIntent);
            }
        });
    }
}
