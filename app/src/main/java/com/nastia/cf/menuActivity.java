package com.nastia.cf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class menuActivity extends AppCompatActivity implements View.OnClickListener{

    Button logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        logOut=(Button) findViewById(R.id.logOutBtn);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = LauncherActivity.getPref();
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();

                finishAffinity();
                startActivity(new Intent(menuActivity.this, signUpActivity.class));
            }
        });

    }

    @Override
    public void onClick(View v){
        int i = v.getId();
        if(i==R.id.fav){
            Intent intent = new Intent(menuActivity.this, fev1_editActivity.class);
            startActivity(intent);
        }
        if(i==R.id.qa){
            Intent intent = new Intent(menuActivity.this, questionsActivity.class);
            startActivity(intent);
        }
        if(i==R.id.breath){
            Intent intent = new Intent(menuActivity.this, breathingActivity.class);
            startActivity(intent);
        }
        if(i==R.id.medc) {
            Intent intent = new Intent(menuActivity.this, MedicinesActivity.class);
            startActivity(intent);
        }
        if(i==R.id.food) {
            Intent intent = new Intent(menuActivity.this, NutritionActivity.class);
            startActivity(intent);
        }
        if(i==R.id.sport) {
            Intent intent = new Intent(menuActivity.this, sportWheel.class);
            startActivity(intent);
        }
        if(i==R.id.cal) {
            Intent intent = new Intent(menuActivity.this, ContactsActivity.class);
            startActivity(intent);
        }
        if(i==R.id.feed) {
            Intent intent = new Intent(menuActivity.this, FeedActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
