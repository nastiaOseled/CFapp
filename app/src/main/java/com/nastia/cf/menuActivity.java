package com.nastia.cf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
    public final static DocumentReference user_details = db.collection("user_details")
            .document(mAuth.getCurrentUser().getUid());

    public  static String NICKNAME;
    public  static int HEIGHT;
    public  static double WEIGHT;
    public  static String BIRTHDAY;
    public  static int RECOMMENDED_CALORIES;
    public  static String IMAGE;
    public static ArrayList<Contact> contacts=new ArrayList<>();


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

        user_details.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if(!document.contains("name"))
                            NICKNAME="";
                        else NICKNAME=(document.getString("name"));
                        RECOMMENDED_CALORIES=document.getLong("recommendedCaloriesPerDay").intValue();
                        HEIGHT=document.getLong("height").intValue();
                        WEIGHT=document.getLong("weight");
                        BIRTHDAY=(document.getString("birthDate"));
                        IMAGE=(document.getString("image"));

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
                    }
                }
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
        if(i==R.id.settings) {
            Intent intent = new Intent(menuActivity.this, SettingsActivity.class);
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
