package com.devicefulsoftware.whois;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
/**
 * Created by cletus on 5/26/17.
 */
public class aboutScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences themePreference = getSharedPreferences(MainActivity.WHOIS_PREFERENCES, 0);
        if (themePreference.getString("Theme","WhoIsDark").equals("WhoIsDark")){
            setTheme(R.style.Theme_WhoIsDark);
        }else{
            setTheme(R.style.Theme_WhoIsLight);
        }
        setContentView(R.layout.about_page);
    }
}
