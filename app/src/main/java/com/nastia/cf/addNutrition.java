package com.nastia.cf;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class addNutrition extends AppCompatActivity {

    private static final int CONVERT_G_TO_UNIT = 3;
    private static final int CONVERT_G_TO_CUP = 2;
    private static final String TAG = addNutrition.class.getSimpleName();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    Spinner spinner2;   //food type
    Spinner spinner3;   //unit of measurement
    TextView amount;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nutrition);

        spinner2=(Spinner)findViewById(R.id.spinner2);
        spinner3=(Spinner)findViewById(R.id.spinner3);
        amount=(TextView)findViewById(R.id.editText2);
        saveBtn=(Button)findViewById(R.id.saveBtn);

        setComboBox();

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(parent.getContext(),
                        "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_LONG).show();
                if(parent.getItemAtPosition(position).toString().equals("אחר"))
                        getInputFromUser();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // adapter for unit of measurement spinner
        @SuppressLint("ResourceType") ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.unit_of_measure, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter2);


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentSnapshot document;
                String anotherFood=spinner2.getSelectedItem().toString();
                DocumentReference user_details = db.collection("Nutrition")
                        .document(anotherFood);

                user_details.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            long calories=0;
                            if (document.exists())
                                calories=document.getLong("calories");
                            int caloriesToInsert=0;
                            int amount2=Integer.parseInt(amount.getText().toString());
                            String anotherFood=spinner2.getSelectedItem().toString();
                            Map<String, Object> newFood = new HashMap<>();
                            switch(spinner3.getSelectedItem().toString()){
                                case  "כוס" :
                                    caloriesToInsert=(int) calories *CONVERT_G_TO_CUP*amount2;
                                    newFood.put("name", anotherFood);
                                    newFood.put("calories", caloriesToInsert);
                                    break;

                                case  "יחידה" :
                                    caloriesToInsert=(int) calories *CONVERT_G_TO_UNIT*amount2;
                                    newFood.put("name", anotherFood);
                                    newFood.put("calories", caloriesToInsert);
                                    break;

                                default:
                                    caloriesToInsert=(int) calories *amount2;
                                    newFood.put("name", anotherFood);
                                    newFood.put("calories", caloriesToInsert);
                                    break;
                            }

                            //insert new food to user's nutrition reports list
                            insertNewFoodToUserNutritionList(anotherFood, newFood);
                        } }});
                startActivity(new Intent(addNutrition.this,NutritionActivity.class));
            }

        });

    }


    public void setComboBox(){

        final List<CharSequence> spinnerArray = new ArrayList<>();

        db.collection("Nutrition")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                spinnerArray.add(document.getString("name"));
                            }
                            spinnerArray.add("אחר");

                            //set adapter
                              ArrayAdapter<CharSequence> adapter = new ArrayAdapter(addNutrition.this,
                              android.R.layout.simple_spinner_item, spinnerArray);
                                 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                 if(spinnerArray.isEmpty())
                                    Log.i("","array is empty");
                                 else
                                     Log.i("","array is not empty");
                                  spinner2.setAdapter(adapter);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getInputFromUser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("נא הכנס סוג מזון");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
      //  input.setRawInputType(Configuration.KEYBOARD_12KEY);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String anotherFood = input.getText().toString();

                //set the document before insert
                Map<String, Object> newFood = new HashMap<>();
                newFood.put("name", anotherFood);
                newFood.put("calories", 100);
                newFood.put("unit", "100 גרם");

                //insert new value to nutrition list
                insertNewValueToNutritionCollection(anotherFood, newFood);

                //insert new value to user's nutrition reports list
                insertNewFoodToUserNutritionList(anotherFood, newFood);

                dialog.dismiss();
                //TODO
           //     spinner2.setSelection(spinner2.getIte);
            }
        });
        builder.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    public void insertNewValueToNutritionCollection(String anotherFood, Map<String, Object> newFood){
        db.collection("Nutrition").document(anotherFood)
                .set(newFood)
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
            }


    public void insertNewFoodToUserNutritionList(String anotherFood, Map<String, Object> newFood){
        final DocumentReference user_details = db.collection("user_details")
                .document(mAuth.getCurrentUser().getUid());

        user_details.collection("nutrition reports").document(anotherFood)
                .set(newFood, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written to user!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document to user", e);
                    }
                });
    }
}
