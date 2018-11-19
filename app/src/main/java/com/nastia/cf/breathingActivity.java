package com.nastia.cf;

import android.app.Activity;
import android.content.Intent;

import android.os.CountDownTimer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.view.View;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class breathingActivity extends AppCompatActivity {

    ImageView lungs ;
    Button startBtn;
    TextView b1;
    TextView b2;
    TextView b3;
    TextView b4;
    TextView b5;
    CheckBox c1;
    CheckBox c2;
    CheckBox c3;
    TextView seconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathing);

        lungs = findViewById(R.id.lungs);
        startBtn = findViewById(R.id.cntBtn);
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4);
        b5 = findViewById(R.id.b5);
        c1 = findViewById(R.id.c1);
        c2 = findViewById(R.id.c2);
        c3 = findViewById(R.id.c3);
        c1.setEnabled(false);
        c2.setEnabled(false);
        c3.setEnabled(false);
        seconds = findViewById(R.id.second);

    }

    public void onClick(View v) {
        lungs.animate().
                scaleX(2f).
                scaleY(2f).
                setDuration(3000).start();
        b1.setVisibility(View.INVISIBLE);
        b2.setVisibility(View.INVISIBLE);
        b3.setVisibility(View.INVISIBLE);
        b4.setVisibility(View.INVISIBLE);
        b5.setVisibility(View.INVISIBLE);
        startBtn.setVisibility(View.INVISIBLE);
        seconds.setVisibility(View.VISIBLE);
        new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                if(millisUntilFinished/1000 >1){
                    String sec = millisUntilFinished/1000-1 +"";
                    seconds.setText(sec);
                }
                else{
                    seconds.setText("לשחרר");
                    lungs.animate().
                                scaleX(1f).
                                scaleY(1f).
                                setDuration(1000).start();
                }
            }

            public void onFinish() {
                        b1.setVisibility(View.VISIBLE);
                        b2.setVisibility(View.VISIBLE);
                        b3.setVisibility(View.VISIBLE);
                        b4.setVisibility(View.VISIBLE);
                        b5.setVisibility(View.VISIBLE);
                        startBtn.setVisibility(View.VISIBLE);
                        seconds.setVisibility(View.INVISIBLE);
                        startBtn.setText("בצע שוב");
                        if(!c1.isChecked()){
                            c1.setChecked(true);
                        }
                        else if(!c2.isChecked()){
                            c2.setChecked(true);
                        }
                        else{
                            c3.setChecked(true);
//                            Map<String, Object> activity = new HashMap<>();
//                            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
//                            Date date = new Date();
//                            String stringdate = dt.format(date);
//                            activity.put("Date", stringdate);
//                            activity.put("done", true);
//
//
//                            db.collection("user_details").document(mAuth.getCurrentUser().getUid()).set(user);
                            Intent intent = new Intent(breathingActivity.this, menuActivity.class);
                            startActivity(intent);
                        }
            }
        }.start();

    }

}
