package com.nastia.cf;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class SettingsActivity extends AppCompatActivity {

    TextView name;
    TextView age;
    TextView weight;
    TextView height;
    TextView calories;
    Button editBtn;


    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        name = findViewById(R.id.name);
        age = findViewById(R.id.ageEdit);
        weight = findViewById(R.id.weightEdit);
        height = findViewById(R.id.heightEdit);
        calories = findViewById(R.id.caloriesEdit);
        editBtn = findViewById(R.id.editBtn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(SettingsActivity.this, signUp_detailsActivity.class);
                Bundle b = new Bundle();
                b.putBoolean("update", true);
                myIntent.putExtras(b);
                startActivityForResult(myIntent, 0);
                //startActivity(intent);
            }
        });

        DocumentReference user_details = db.collection("user_details")
                .document(mAuth.getCurrentUser().getUid());

        user_details.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if(!document.contains("name")){
                            name.setText("");
                        }
                        else{
                            name.setText(document.getString("name"));
                        }
                        if(!document.contains("weight")){
                            weight.setText("");
                        }
                        else{
                            weight.setText(document.getDouble("weight").toString());
                        }
                        if(!document.contains("height")){
                            height.setText("");
                        }
                        else{
                            height.setText(document.getDouble("height").toString());
                        }
                        if(!document.contains("recommendedCaloriesPerDay")){
                            calories.setText("");
                        }
                        else {
                            calories.setText(document.getDouble("recommendedCaloriesPerDay").intValue() + "");
                        }
                        if(!document.contains("birthDate")){
                            calories.setText("");
                        }
                        else{
                            String dateString = document.getString("birthDate");
                            String dateStrin = "5/14/15";
                            DateFormat dateFormat = new SimpleDateFormat("dd.mm.yy");
                            Date date = null;
                            try {
                                date = dateFormat.parse(dateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            LocalDate date1 = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            Date input = new Date();
                            LocalDate now = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                            int y =Period.between(date1, now).getYears();
                            age.setText(y+"");

                        }


                    } else {

                    }
                } else {

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                //set from menu
            }
        }
    }
}
