package com.devicefulsoftware.whois;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import org.apache.commons.net.whois.WhoisClient;

import java.io.BufferedReader;
import java.io.StringReader;

public class MainActivity extends AppCompatActivity {
    private AdView mAdView;
    private TextView txtOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(getApplicationContext(),"ca-app-pub-6633448073266283~9823432419");
        NativeExpressAdView adView = (NativeExpressAdView)findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder().build();
        adView.loadAd(request);
        txtOutput = (TextView)findViewById(R.id.txtOutput);
    }

    public void whoisLookup(View view){
        EditText editText = (EditText)findViewById(R.id.editDomain);
        Whois whois = new Whois();
        whois.execute(new String[] { editText.getText().toString() });
    }
    private class Whois extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... domains) {
            WhoisClient whoisClient = new WhoisClient();
            String sloppyServer[];
            String server = "Unknown";
            String answer = "Unknown error attempting to perform Whois lookup.";
            try{
                whoisClient.connect(WhoisClient.DEFAULT_HOST);
                BufferedReader reader = new BufferedReader(new StringReader(whoisClient.query(domains[0])));
                whoisClient.disconnect();
                String currentLine;
                while ((currentLine = reader.readLine()) != null){
                    if (currentLine.matches("(.*)Whois Server:(.*)")){
                        String[] serverMatch = currentLine.split(":");
                        sloppyServer = serverMatch[1].split(" ");
                        server = sloppyServer[1];
                    }
                }
                try{
                    whoisClient.connect(server);
                    answer = whoisClient.query(domains[0]);
                    whoisClient.disconnect();
                }catch(Exception e){
                    return e.toString();
                }
                return answer;
            }catch(Exception e){
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result){
            txtOutput.setText(result);
        }
    }

}
