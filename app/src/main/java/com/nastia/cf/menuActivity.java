package com.nastia.cf;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class menuActivity extends AppCompatActivity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


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
        if(i==R.id.qa) {
            Intent intent = new Intent(menuActivity.this, questionsActivity.class);
            startActivity(intent);
        }
        if(i==R.id.sport) {
            Intent intent = new Intent(menuActivity.this, sportWheel.class);
            startActivity(intent);
        }
    }
}
