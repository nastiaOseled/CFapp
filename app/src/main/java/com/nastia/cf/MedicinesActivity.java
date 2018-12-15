package com.nastia.cf;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MedicinesActivity extends AppCompatActivity {

    private static final String TAG = set_medicine.class.getSimpleName();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ArrayList<String> meds = new ArrayList<String>();
    private ArrayList<String> medsId = new ArrayList<String>();

    ListView medList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicines);

        db.collection("user_details").
                document(mAuth.getCurrentUser().getUid()).collection("med").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                meds.add(document.getString("Name"));
                                medsId.add(document.getId());
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            ListAdapter cityAdapter = new ArrayAdapter<String>(MedicinesActivity.this, android.R.layout.simple_list_item_1, meds);
                            ListView cityListView = (ListView) findViewById(R.id.medList);
                            cityListView.setAdapter(cityAdapter);

                            cityListView.setOnItemClickListener(
                                    new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            String cities = String.valueOf(parent.getItemAtPosition(position));
                                            Intent myIntent = new Intent(view.getContext(), set_medicine.class);
                                            Bundle b = new Bundle();
                                            b.putString("id", medsId.get(position)); //Your id
                                            myIntent.putExtras(b);
                                            startActivityForResult(myIntent, 0);
                                        }


                                    });
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
/*        Cities.add("bdfbhgfb");
        ListAdapter cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cities);
        ListView cityListView = (ListView) findViewById(R.id.medList);
        cityListView.setAdapter(cityAdapter);

        cityListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String cities = String.valueOf(parent.getItemAtPosition(position));
                        Toast.makeText(MedicinesActivity.this, cities, Toast.LENGTH_LONG).show();

                       *//* if (position == 1) {
                            //code specific to first list item
                            Intent myIntent = new Intent(view.getContext(), NewYork.class);
                            startActivityForResult(myIntent, 0);


                        }*//*
                    }


                });*/

    }

}
