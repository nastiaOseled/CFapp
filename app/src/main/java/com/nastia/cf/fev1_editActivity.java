package com.nastia.cf;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class fev1_editActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView fevText;
    private NumberPicker np;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fev1_edit);

         np = findViewById(R.id.numberPicker);
         fevText = findViewById(R.id.fevText);

        np.setMinValue(40);
        np.setMaxValue(100);

        DocumentReference user_details = db.collection("user_details")
                .document(mAuth.getCurrentUser().getUid());

        user_details.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if(!document.contains("fev1")){
                        fevText.setText("");
                    }
                    else{
                        int fevNum = document.getLong("fev1").intValue();
                        String text = fevNum + " %";
                        fevText.setText(text);
                        np.setValue(fevNum);
                    }
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void saveBtn(View v){
        final int newFev1 = np.getValue();
        final DocumentReference user_details = db.collection("user_details")
                .document(mAuth.getCurrentUser().getUid());

        user_details.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if(!document.contains("fev1")){
                            Map<String, Object> newField = new HashMap<>();
                            newField.put("fev1", 32);
                            user_details.set(newField, SetOptions.merge());
                        }
                        else{
                            user_details.update("fev1", newFev1);
                        }
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Intent intent = new Intent(fev1_editActivity.this, menuActivity.class);
                        startActivity(intent);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }


}
