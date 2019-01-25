package com.nastia.cf;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.util.ArrayList;

public class MedicinesActivity extends AppCompatActivity {

    private static final String TAG = set_medicine.class.getSimpleName();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ArrayList<String> meds = new ArrayList<String>();
    private ArrayList<String> medsId = new ArrayList<String>();
    Button saveBtn;
    Button backBtn;

    ListView medList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicines);
        saveBtn =  findViewById(R.id.saveBtn);
        backBtn=(Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MedicinesActivity.this, set_medicine.class);
                startActivityForResult(myIntent, 0);
            }
        });
        importMeds();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Boolean returnValue = data.getBooleanExtra("changed",false);
                if (returnValue)
                    importMeds();
            }
        }
    }

    private void importMeds(){
        meds= new ArrayList<String>();
        medsId= new ArrayList<String>();
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
                            ListView medListView = (ListView) findViewById(R.id.medList);
                            medListView.setAdapter(cityAdapter);

                            medListView.setOnItemClickListener(

                                    new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            String cities = String.valueOf(parent.getItemAtPosition(position));
                                            Intent myIntent = new Intent(view.getContext(), set_medicine.class);
                                            Bundle b = new Bundle();
                                            b.putString("id", medsId.get(position)); //Your id
                                            b.putString("name", meds.get(position));
                                            myIntent.putExtras(b);
                                            startActivityForResult(myIntent, 0);
                                        }


                                    });
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
