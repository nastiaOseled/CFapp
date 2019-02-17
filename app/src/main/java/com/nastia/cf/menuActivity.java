package com.nastia.cf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class menuActivity extends AppCompatActivity implements View.OnClickListener{

    Button logOut;
    public final static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public final static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static DocumentReference user_details;

    public  static String NICKNAME;
    public  static int HEIGHT;
    public  static int fev1;
    public  static double WEIGHT;
    public  static String BIRTHDAY;
    public  static int RECOMMENDED_CALORIES;
    public  static String IMAGE;
    public static ArrayList<Contact> contacts=new ArrayList<>();
    private ProgressBar pgsBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        user_details = db.collection("user_details")
                .document(mAuth.getCurrentUser().getUid());
        pgsBar = (ProgressBar) findViewById(R.id.pBar);
        pgsBar.setVisibility(View.VISIBLE);
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
        // set user static user details parameters
        user_details.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if(!document.contains("name"))
                            NICKNAME="";
                        else NICKNAME=(document.getString("name"));
                        if(!document.contains("recommendedCaloriesPerDay"))
                            RECOMMENDED_CALORIES=0;
                        else RECOMMENDED_CALORIES=document.getLong("recommendedCaloriesPerDay").intValue();
                        if(!document.contains("height"))
                            HEIGHT=0;
                        else HEIGHT=document.getLong("height").intValue();
                        if(!document.contains("weight"))
                            WEIGHT=0;
                        else WEIGHT=document.getLong("weight");
                        if(!document.contains("birthDate"))
                            BIRTHDAY="";
                        else BIRTHDAY=(document.getString("birthDate"));
                        if(!document.contains("image"))
                            IMAGE="img1";
                        else IMAGE=(document.getString("image"));
                        if(!document.contains("fev1"))
                            fev1=70;
                        else fev1=document.getLong("fev1").intValue();

                        //import contacts
                        user_details.collection("contacts")
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()){
                                        String name=document.getString("name");
                                        String phone=document.getString("phone");
                                        Contact c=new Contact(name, phone);
                                        if( ! contacts.contains(c))
                                            contacts.add(c);
                                    }
                                }
                            }
                        });
                    }pgsBar.setVisibility(View.INVISIBLE);
                }
            }
        });


    }
    //navigate to the chosen activity
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
        if(i==R.id.settings) {
            Intent intent = new Intent(menuActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        if(i==R.id.step) {
            Intent intent = new Intent(menuActivity.this, stepCounter.class);
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
