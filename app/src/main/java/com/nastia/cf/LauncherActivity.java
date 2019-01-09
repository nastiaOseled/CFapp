package com.nastia.cf;

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
    public final static DocumentReference user_details = db.collection("user_details")
            .document(mAuth.getCurrentUser().getUid());

    public  static String NICKNAME;
    public  static int RECOMMENDED_CALORIES;
    public  static double WEIGHT;

    private static SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        //check if user is already connected to app
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        boolean b=sharedPref.getBoolean("userConnected", false);
        if( ! b){
            //if not connected
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent in=new Intent(LauncherActivity.this, welcomeActivity.class);
                    startActivity(in);
                }
            }, 2000);
        }

        else {
            //if connected
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent in=new Intent(LauncherActivity.this, menuActivity.class);
                    startActivity(in);

                    user_details.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                   NICKNAME=(document.getString("name"));
                                   RECOMMENDED_CALORIES=document.getLong("recommendedCaloriesPerDay").intValue();

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
//                                                    if( ! contacts.contains(c))
//                                                        contacts.add(c);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });

                    //import user's nutrition list and calories sum


                }
            }, 2000);

        }

    }

    public static SharedPreferences getPref(){
        return sharedPref;
    }
}
