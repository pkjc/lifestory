package edu.oakland.lifestory;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AppIntroActivity extends AppCompatActivity {
    Button createMemButton, skipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro);

        createMemButton = findViewById(R.id.createMemButton);
        skipButton = findViewById(R.id.skipButton);

        createMemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Navigate to create memory activity
                Intent intent = new Intent(AppIntroActivity.this, MemoryActivity.class);
                startActivity(intent);
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Navigate to home screen
                Intent intent = new Intent(AppIntroActivity.this, AppHomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
