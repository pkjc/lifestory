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

import org.json.JSONException;
import org.json.JSONObject;

import edu.oakland.lifestory.model.Sentiment;

public class SentimentAnalysisActivity extends BaseActivity {

    Sentiment sentiment = new Sentiment();

    private class AskWatsonTask extends AsyncTask<String, Void, Sentiment> {
        private static final String TAG = "AskWatsonTask";
        @Override
        protected Sentiment doInBackground(String... textsToAnalyze) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showProgressDialog();
                }
            });

            IamOptions options = new IamOptions.Builder().apiKey("-4o_NtMCXiiHOlj-kbVnRItp_e3Obk0hbVMRd3oI7NxN").build();

            NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding("2018-03-16", options);
            service.setEndPoint("https://gateway-wdc.watsonplatform.net/natural-language-understanding/api");

            String text = "Fruits Apples and Oranges I love apples! I don't like oranges.";

//            List<String> targets = new ArrayList<>();
//            targets.add("apples");
//            targets.add("oranges");

            EmotionOptions emotionOps = new EmotionOptions.Builder().build();

            Features features = new Features.Builder()
                    .emotion(emotionOps)
                    .build();

            AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                    .text(textsToAnalyze[0])
                    .features(features)
                    .build();

            AnalysisResults response = service
                    .analyze(parameters)
                    .execute();
            System.out.println(response);

            try {
                JSONObject reader = new JSONObject(response.getEmotion().toString());
                JSONObject emotion  = reader.getJSONObject("document").getJSONObject("emotion");
                sentiment.setAnger(emotion.getString("anger"));
                sentiment.setDisgust(emotion.getString("disgust"));
                sentiment.setFear(emotion.getString("fear"));
                sentiment.setJoy(emotion.getString("joy"));
                sentiment.setSadness(emotion.getString("sadness"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return sentiment;
        }

        @Override
        protected void onPostExecute(Sentiment result) {
            hideProgressDialog();
            Log.d(TAG, "onPostExecute: " + result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentiment_analysis);
        handleBackBtn();

        AskWatsonTask task = new AskWatsonTask();
        String[] textsToAnalyze = {"Fruits Apples and Oranges I love apples! I don't like oranges."};
        task.execute(textsToAnalyze);
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

