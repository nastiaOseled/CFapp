package com.nastia.cf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LauncherActivity extends AppCompatActivity {

    public static SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        //check if user is already connected to app
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        boolean b=sharedPref.getBoolean("userConnected", false);
        if( ! b){
            Intent in=new Intent(this, welcomeActivity.class);
            startActivity(in);
        }

        else {
            Intent in=new Intent(this, menuActivity.class);
            startActivity(in);
        }

    }

    public static SharedPreferences getPref(){
        return sharedPref;
    }
}
