package edu.oakland.lifestory;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EmotionOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.service.security.IamOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.oakland.lifestory.model.Memory;
import edu.oakland.lifestory.model.Sentiment;

public class SentimentAnalysisActivity extends BaseActivity {

    Sentiment sentiment = new Sentiment();
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private String current_user_id;
    WebView webView;
    static WebViewBridge bridge;
    List<Sentiment> sentimentList = new ArrayList<>();

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

            EmotionOptions emotionOps = new EmotionOptions.Builder().build();

            Features features = new Features.Builder()
                    .emotion(emotionOps)
                    .build();

            AnalyzeOptions parameters = new AnalyzeOptions.Builder().language("English")
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
            Log.d(TAG, "onPostExecute: " + sentimentList.size());
            sentimentList.add(result);
            plotGraph(sentimentList);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentiment_analysis);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        //current_user_id = "AjKLJ0N8p5at5fsnSLLuHuPL2Zr1";

        handleBackBtn();

        getMemoriesFromDB();
    }

    private void plotGraph(final List<Sentiment> sentimentList) {



        webView = findViewById(R.id. webView);
        webView.getSettings().setJavaScriptEnabled(true);

        bridge = new WebViewBridge(webView);
        webView.addJavascriptInterface(bridge, "Android");

        /* WebViewClient must be set BEFORE calling loadUrl! */
        webView.setWebViewClient(new WebViewClient() {

            double anger= 0.0, disgust= 0.0, joy= 0.0, fear= 0.0, sadness = 0.0;

            @Override
            public void onPageFinished(WebView view, String url) {
                for(Sentiment sentiment : sentimentList){
                    anger = anger + Double.parseDouble(sentiment.getAnger());
                    disgust = disgust + Double.parseDouble(sentiment.getDisgust());
                    joy = joy + Double.parseDouble(sentiment.getJoy());
                    fear = fear + Double.parseDouble(sentiment.getFear());
                    sadness = sadness + Double.parseDouble(sentiment.getSadness());
                    Log.d("WEBVIEW >>>>> ", "onPageFinished: " + sentiment.getAnger());
                    Log.d("WEBVIEW >>>>> ", "onPageFinished: " + sentiment.getDisgust());
                    Log.d("WEBVIEW >>>>> ", "onPageFinished: " + sentiment.getJoy());
                    Log.d("WEBVIEW >>>>> ", "onPageFinished: " + sentiment.getFear());
                    Log.d("WEBVIEW >>>>> ", "onPageFinished: " + sentiment.getSadness());
                }

                bridge.addDataToWebView(new String[]{Double.toString(anger), Double.toString(disgust),
                        Double.toString(fear), Double.toString(joy), Double.toString(sadness)});
            }
        });
        webView.loadUrl("file:///android_asset/index.html");
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
                    startWatsonTask(memories.get(0));
                    //Log.d("DATA =======> ",  memories.size()+"");

                } else {
                    Log.d("ERROR =======> ", "error getting documents: ", task.getException());
                }
            }
        });
    }

    private void startWatsonTask(Memory memory) {
        AskWatsonTask task = new AskWatsonTask();
        task.execute(memory.getMemoryTitle());
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

