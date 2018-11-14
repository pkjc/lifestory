package edu.oakland.lifestory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.oakland.lifestory.model.Memory;

public class MemoryActivity extends AppCompatActivity {
    EditText memoryTitle, memoryContent = null;
    Button createMemButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

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
                Toast.makeText(getApplicationContext(), "Memory Created", Toast.LENGTH_LONG).show();
            }
        });
    }
}
