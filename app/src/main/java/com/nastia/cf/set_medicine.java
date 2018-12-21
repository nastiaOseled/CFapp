package com.nastia.cf;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.*;
import com.sysdata.widget.accordion.ExpandableItemHolder;
import com.sysdata.widget.accordion.FancyAccordionView;
import com.sysdata.widget.accordion.Item;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class set_medicine extends AppCompatActivity {

    private static final String TAG = set_medicine.class.getSimpleName();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private LinearLayout alarmsView;
    private Spinner spinner;
    ArrayList<String> alertsTime = new ArrayList<String>() ;
    int hour,min;
    String medId ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        if(b != null)
            medId = b.getString("id");
        setContentView(R.layout.activity_set_medicine);
        alarmsView = (LinearLayout) findViewById(R.id.alarms);
        spinner = (Spinner) findViewById(R.id.spinner);

      /*  ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter); */

        setComboBox();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                  @Override
                  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                      //Toast.makeText(parent.getContext(), parent.getItemIdAtPosition(position)+" is selected", Toast.LENGTH_LONG).show();
                    /*  ((TextView) parent.getChildAt(0)).setTextColor(Color.BLUE);
                    String selected=parent.getItemAtPosition(position).toString();
                    if(selected.equals("other")){
                    getInputFromUser();
                    } */
                  }

                  @Override
                  public void onNothingSelected(AdapterView<?> parent) {

                  }
              });
                //setComboBox();

        final Button addReminderBtn = findViewById(R.id.addReminderBtn);
        addReminderBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openTimePickerDialog();
            }
        });

        final Button saveBtn = findViewById(R.id.saveBtn);
  /*      saveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Map<String, Object> nameField = new HashMap<>();
                nameField.put("Name", spinner.getSelectedItem().toString());
                if (medId == null) {
                    db.collection("user_details")
                            .document(mAuth.getCurrentUser().getUid()).collection("med")
                            .add(nameField)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                    Map<String, Object> alarm = new HashMap<>();
                                    alarm.put("time", "8:00");
                                    documentReference.collection("alarms").document("8:00")
                                            .set(alarm)
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
                                    ;
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                }
                else{
                    //set medicine name if changed
                    final CollectionReference user_med = db.collection("user_details")
                            .document(mAuth.getCurrentUser().getUid()).collection("med");
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
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                    //add all alarms
                    for (String alarmTime:alertsTime) {
                        Map<String, Object> alarm = new HashMap<>();
                        alarm.put("time", alarmTime);
                        user_med.document(medId).collection("alarms").add(alarm);
                    }


                }
                    finish();
/*                user_details.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if(!document.contains("med")){
                                    Map<String, Object> newField = new HashMap<>();
                                    newField.put("Name", spinner.getSelectedItem().toString());
                                    user_details.set(newField, SetOptions.merge());

                                }
                                else{
                                 //   user_details.update("fev1", newFev1);
                                }
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                             //   Intent intent = new Intent(fev1_editActivity.this, menuActivity.class);
                           //     startActivity(intent);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });*/
            }
        }); */
    }

    private void openTimePickerDialog()
    {
        TimePickerDialog dialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {

                if (min < 10) {
                    updateAlatms(String.valueOf(hour) + ":0" + String.valueOf(min));
            }
                else {
                    updateAlatms(String.valueOf(hour) + ":" + String.valueOf(min));
                }
            }
        },hour,min,false);
        dialog.show();


    }

    private void updateAlatms(String time){
        alertsTime.add(time);
        LayoutInflater factory = LayoutInflater.from(this);
        View myView = factory.inflate(R.layout.alarm, null);
        TextView tv_year = (TextView)myView.findViewById(R.id.time);
        tv_year.setText(time);
        alarmsView.addView(myView);
    }

    public void setComboBox(){

        final List<CharSequence> spinnerArray = new ArrayList<>();

        db.collection("Medicines")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                spinnerArray.add(document.getString("name"));
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





    }


    public void getInputFromUser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

// Set up the buttons
  /*      builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
            }
        }); */
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


}
