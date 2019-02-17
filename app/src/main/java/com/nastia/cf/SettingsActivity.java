package com.nastia.cf;

import android.app.DatePickerDialog;
import android.content.Context;
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
import android.widget.ImageView;
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
    ImageView img;


    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        img = findViewById(R.id.image);
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

        Context context = img.getContext();
        int id = context.getResources().getIdentifier(menuActivity.IMAGE, "drawable", context.getPackageName());
        img.setImageResource(id);

        name.setText(menuActivity.NICKNAME);
        weight.setText(menuActivity.WEIGHT + "");
        height.setText(menuActivity.HEIGHT + "");
        String dateString = menuActivity.BIRTHDAY;
        DateFormat dateFormat = new SimpleDateFormat("dd/mm/yy");
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LocalDate date1 = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date input = new Date();
        LocalDate now = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int y = Period.between(date1, now).getYears();
        age.setText(y + "");
        calories.setText(menuActivity.RECOMMENDED_CALORIES + "");

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override

    //when result returned from 'sign up activity', update the details
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Context context = img.getContext();
                int id = context.getResources().getIdentifier(menuActivity.IMAGE, "drawable", context.getPackageName());
                img.setImageResource(id);

                name.setText(menuActivity.NICKNAME);
                weight.setText(menuActivity.WEIGHT + "");
                height.setText(menuActivity.HEIGHT + "");
                String dateString = menuActivity.BIRTHDAY;
                DateFormat dateFormat = new SimpleDateFormat("dd/mm/yy");
                Date date = null;
                try {
                    date = dateFormat.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                    LocalDate date1 = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    Date input = new Date();
                    LocalDate now = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    int y = Period.between(date1, now).getYears();
                    age.setText(y + "");
                }

                calories.setText(menuActivity.RECOMMENDED_CALORIES + "");
            }
        }
    }
}
