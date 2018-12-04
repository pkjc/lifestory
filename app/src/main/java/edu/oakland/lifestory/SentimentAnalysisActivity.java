package edu.oakland.lifestory;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EmotionOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.service.security.IamOptions;

import java.util.ArrayList;
import java.util.List;

public class SentimentAnalysisActivity extends BaseActivity {

    String sentiment;

    private class AskWatsonTask extends AsyncTask<String, Void, String> {
        private static final String TAG = "AskWatsonTask";
        @Override
        protected String doInBackground(String... textsToAnalyze) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Msg to show while fetching from API
                }
            });

            IamOptions options = new IamOptions.Builder().apiKey("-4o_NtMCXiiHOlj-kbVnRItp_e3Obk0hbVMRd3oI7NxN").build();

            NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding("2018-03-16", options);
            service.setEndPoint("https://gateway-wdc.watsonplatform.net/natural-language-understanding/api");

            String html = "Fruits Apples and Oranges I love apples! I don't like oranges.";

            List<String> targets = new ArrayList<>();
            targets.add("apples");
            targets.add("oranges");

            EmotionOptions emotion= new EmotionOptions.Builder()
                    .targets(targets)
                    .build();

            Features features = new Features.Builder()
                    .emotion(emotion)
                    .build();

            AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                    .text(html)
                    .features(features)
                    .build();

            AnalysisResults response = service
                    .analyze(parameters)
                    .execute();
            System.out.println(response);


            sentiment = "Test sentiment";

            return response.getEmotion().toString();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: " + result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentiment_analysis);

        handleBackBtn();

        AskWatsonTask task = new AskWatsonTask();
        task.execute(new String[]{});

    }

    private void handleBackBtn() {
        Toolbar toolbar = findViewById(R.id.appToolbar);
        ImageButton backButton = toolbar.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent("edu.oakland.lifestory.ReturnHome");
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                v.getContext().startActivity(homeIntent);
            }
        });
    }
}
