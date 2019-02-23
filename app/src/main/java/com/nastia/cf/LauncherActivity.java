package com.nastia.cf;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LauncherActivity extends AppCompatActivity {

    public final static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public final static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        startService(new Intent(this, StepsCounterService.class));
        //start steps counter service
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //set time to start


        //check if user is already connected to app.
        //YES-go to the main menu. NO-go to login screen
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        boolean b = sharedPref.getBoolean("userConnected", false);
        if (!b) {
            //if not connected
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent in = new Intent(LauncherActivity.this, welcomeActivity.class);
                    startActivity(in);
                }
            }, 2000);
        } else {
            //if connected
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent in = new Intent(LauncherActivity.this, menuActivity.class);
                    startActivity(in);

                }
            }, 2000);

        }


    }

    public static SharedPreferences getPref() {
        return sharedPref;
    }
}
