package com.nastia.cf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
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
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent in=new Intent(LauncherActivity.this, welcomeActivity.class);
                    startActivity(in);
                }
            }, 2000);
        }

        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent in=new Intent(LauncherActivity.this, menuActivity.class);
                    startActivity(in);
                }
            }, 2000);

        }

    }

    public static SharedPreferences getPref(){
        return sharedPref;
    }
}
