package edu.oakland.lifestory;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class WebViewBridge {
    WebView webView;

    public WebViewBridge(WebView webView) {
        this.webView = webView;
    }

    public void addDataToWebView(String[] gain){
        for(String g:gain){
            Log.d(" BRIDGE ", "addDataToWebView: " + g);
        }
        double anger= Double.parseDouble(gain[0]);
        double disgust= Double.parseDouble(gain[1]);
        double fear= Double.parseDouble(gain[2]);
        double joy  = Double.parseDouble(gain[3]);
        double sadness= Double.parseDouble(gain[4]);


        Log.d("BRIDGE >>>>> ", "addDataToWebView: " + "javascript:addTimeGain("+gain+", "+anger+", "+disgust+", "+fear+", "+joy+", "+sadness+")");
        webView.loadUrl("javascript:addTimeGain("+anger+", "+disgust+", "+fear+", "+joy+", "+sadness+")");
    }

    @JavascriptInterface
    public void ShowToast(){
        Toast.makeText(webView.getContext(), "WebView button is clicked", Toast.LENGTH_LONG).show();
    }
}
