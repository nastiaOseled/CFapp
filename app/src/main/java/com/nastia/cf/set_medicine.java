package com.nastia.cf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class set_medicine extends AppCompatActivity {

    private static final String TAG = set_medicine.class.getSimpleName();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private LinearLayout alarmsView;
    //private Spinner spinner;
    ArrayList<String> alertsTime = new ArrayList<>();
    int hour,min;
    String medId ;
    String medName ;
    EditText name;
    ImageView searchMed;
    String[] medArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_medicine);
        Bundle b = getIntent().getExtras();
        name = (EditText) findViewById(R.id.name);
        if(b != null) {
            medId = b.getString("id");
            medName = b.getString("name");
            name.setText(medName);
        }
        alarmsView = findViewById(R.id.alarms);
        searchMed = findViewById(R.id.searchMed);
        searchMed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openMedListDialog();
            }
        });
        if(medId != null) {
            DocumentReference medRef = db.collection("user_details").
                    document(mAuth.getCurrentUser().getUid()).collection("med").document(medId);
            medRef.collection("alarms").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    updateAlarms(document.getId());
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

        final Button addReminderBtn = findViewById(R.id.addReminderBtn);
        addReminderBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openTimePickerDialog();
            }
        });

        final Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("some_key", "String data");

                Map<String, Object> nameField = new HashMap<>();
                nameField.put("Name", name.getText().toString());
                if (medId == null) {
                    db.collection("user_details")
                            .document(mAuth.getCurrentUser().getUid()).collection("med")
                            .add(nameField)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //add all alarms
                                    for (String alarmTime:alertsTime) {
                                        Map<String, Object> alarm = new HashMap<>();
                                        alarm.put("time", alarmTime);
                                        documentReference.collection("alarms").document(alarmTime)
                                                .set(alarm);
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                    resultIntent.putExtra("changed", true);
                }
                else{
                    //set medicine name if changed
                    final CollectionReference user_med = db.collection("user_details")
                                .document(mAuth.getCurrentUser().getUid()).collection("med");
                    if(!name.getText().toString().equals(medName)) {
                        user_med.document(medId).set(nameField)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                        resultIntent.putExtra("changed", true);
                    }
                    else resultIntent.putExtra("changed", false);
                    //delete all existing alarms
                    user_med.document(medId).collection("alarms").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            document.getReference().delete();
                                        }
                                        //add all alarms
                                        for (String alarmTime:alertsTime) {
                                            Map<String, Object> alarm = new HashMap<>();
                                            alarm.put("time", alarmTime);
                                            user_med.document(medId).collection("alarms").document(alarmTime)
                                                    .set(alarm);
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void openTimePickerDialog()
    {
        TimePickerDialog dialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {

                if (min < 10) {
                    updateAlarms(String.valueOf(hour) + ":0" + String.valueOf(min));
            }
                else {
                    updateAlarms(String.valueOf(hour) + ":" + String.valueOf(min));
                }
            }
        },hour,min,false);
        dialog.show();
    }

    private void updateAlarms(String time){
        alertsTime.add(time);
        LayoutInflater factory = LayoutInflater.from(this);
        View myView = factory.inflate(R.layout.alarm, null);
        TextView tv_year = (TextView)myView.findViewById(R.id.time);
        Button delete = myView.findViewById(R.id.deleteAlarm);
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView t= v.getRootView().findViewById(R.id.time);
                alertsTime.remove(t.getText().toString());
                alarmsView.removeView((View)v.getParent());
            }
        });
        tv_year.setText(time);
        alarmsView.addView(myView);
    }

    private void openMedListDialog(){
        final ArrayList<CharSequence> spinnerArray = new ArrayList<>();


        db.collection("Medicines")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String n = document.getString("name");
                                spinnerArray.add(n);
                                if( n.equals(medName)){

                                }
                            }
                            medArr = new String[spinnerArray.size()];
                            medArr = spinnerArray.toArray(medArr);
                            AlertDialog.Builder builder = new AlertDialog.Builder(set_medicine.this);
                            builder.setTitle("ניתן לבחור תרופה מהרשימה הבאה")
                                    .setItems(medArr, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            name.setText(medArr[which].toString());
                                        }
                                    })
                                    .setNegativeButton("חזור", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User cancelled the dialog
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

  /*  public void setComboBox(){

        final List<CharSequence> spinnerArray = new ArrayList<>();

        db.collection("Medicines")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String n = document.getString("name");
                                spinnerArray.add(n);
                                if( n.equals(medName)){

                                }
                            }
                            spinnerArray.add("other");

                            // Create an ArrayAdapter using the string array and a default spinner layout
                            @SuppressLint("ResourceType") ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                                    set_medicine.this, android.R.layout.simple_spinner_item, spinnerArray);
                            // Specify the layout to use when the list of choices appears
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            // Apply the adapter to the spinner
                            spinner.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }*/


}
