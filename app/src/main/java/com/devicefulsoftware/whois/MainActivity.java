package com.devicefulsoftware.whois;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import org.apache.commons.net.whois.WhoisClient;
import java.io.BufferedReader;
import java.io.StringReader;
public class MainActivity extends AppCompatActivity {
    private TextView txtOutput;
    public static final String WHOIS_PREFERENCES = "Whois_Preferences";
    private String WHOIS_THEME="WhoIsDark";
    private boolean WHOIS_FIRSTRUN=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences(WHOIS_PREFERENCES,0);
        if (sharedPreferences.getString("Theme","WhoIsDark").equals("WhoIsDark")){
            setTheme(R.style.Theme_WhoIsDark);
        }else{
            setTheme(R.style.Theme_WhoIsLight);
        }
        setContentView(R.layout.activity_main);
        MobileAds.initialize(getApplicationContext(),"ca-app-pub-6633448073266283~9823432419");
        NativeExpressAdView adView = (NativeExpressAdView)findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder().build();
        adView.loadAd(request);
        txtOutput = (TextView)findViewById(R.id.txtOutput);
        //Code to do stuff on Enter press
        final EditText edittext = (EditText) findViewById(R.id.editText);
        edittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    whoisLookup(findViewById(R.id.mainLayout));
                    return true;
                }
                return false;
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        SharedPreferences themePreference = getSharedPreferences(WHOIS_PREFERENCES, 0);
        MenuInflater inflater = getMenuInflater();
        if (themePreference.getString("Theme","WhoIsDark").equals("WhoIsDark")){
            inflater.inflate(R.menu.mainmenu_dark, menu);
        }else{
            inflater.inflate(R.menu.mainmenu_light, menu);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        SharedPreferences themePreference = getSharedPreferences(WHOIS_PREFERENCES, 0);
        SharedPreferences.Editor editor = themePreference.edit();
        switch (item.getItemId()){

            case R.id.about:
                startActivity(new Intent(MainActivity.this, aboutScreen.class));
                return super.onOptionsItemSelected(item);

            case R.id.light_theme:
                editor.putString("Theme","WhoIsLight");
                editor.apply();
                finish();
                startActivity(new Intent(this, this.getClass()));
                return super.onOptionsItemSelected(item);

            case R.id.dark_theme:
                editor.putString("Theme","WhoIsDark");
                editor.apply();
                finish();
                startActivity(new Intent(this, this.getClass()));
                return super.onOptionsItemSelected(item);

            case R.id.exit:
                this.exitApplication();
                return super.onOptionsItemSelected(item);

            default:
                return super.onOptionsItemSelected(item);

        }
    }
    public void exitApplication(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_NEGATIVE:
                        //android.os.Process.killProcess(android.os.Process.myPid());
                        finish();
                        System.exit(0);
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you wish to exit?").setNegativeButton("Yes", dialogClickListener).setPositiveButton("No", dialogClickListener).show();
    }
    public void whoisLookup(View view){
        EditText editText = (EditText)findViewById(R.id.editText);
        Whois whois = new Whois();
        //Collapse the keyboard
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
        whois.execute(editText.getText().toString());
    }
    private class Whois extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... domains) {
            WhoisClient whoisClient = new WhoisClient();
            String sloppyServer[];
            String server = "Unknown";
            String originalAnswer = "No answer.";
            String answer = "Unknown error attempting to perform Whois lookup.";
            try{
                whoisClient.connect("whois.verisign-grs.com");
                originalAnswer = whoisClient.query("=" + domains[0]);
                BufferedReader reader = new BufferedReader(new StringReader(originalAnswer));
                whoisClient.disconnect();
                String currentLine;
                while ((currentLine = reader.readLine()) != null){
                    /*if (currentLine.matches("(.*)WHOIS Server:(.*)")){
                        String[] serverMatch = currentLine.split(":");
                        sloppyServer = serverMatch[1].split(" ");
                        server = sloppyServer[1];
                    }*/
                }
                if (server.equals("Unknown")){
                    return originalAnswer;
                }else{
                    try{
                        whoisClient.connect(server);
                        answer = whoisClient.query(domains[0]);
                        whoisClient.disconnect();
                    }catch(Exception e){
                        return e.toString();
                    }
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