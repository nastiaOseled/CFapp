package com.nastia.cf;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class addNutrition extends AppCompatActivity {

    private static final int CONVERT_G_TO_UNIT = 3;
    private static final int CONVERT_G_TO_CUP = 2;
    private static final String TAG = addNutrition.class.getSimpleName();
    public static SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/YYYY");

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    Spinner spinner2;   //food type
    Spinner spinner3;   //unit of measurement
    TextView amount;
    Button saveBtn;
    Map<String, Object> anotherFoodMap = new HashMap<>();
    String newFoodName;
    int newFoodCal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nutrition);

        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner3 = (Spinner) findViewById(R.id.spinner3);
        amount = (TextView) findViewById(R.id.editText2);
        saveBtn = (Button) findViewById(R.id.saveBtn);

        setComboBox();

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("אחר"))
                    showInputDialog();
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

                String anotherFood = spinner2.getSelectedItem().toString();
                DocumentReference nutrition = db.collection("Nutrition")
                        .document(anotherFood);

                nutrition.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            long calories = 0;
                            if (document.exists())
                                calories = document.getLong("calories");
                            int caloriesToInsert = 0;
                            String unitToInsert="";
                            int amount2 ;
                            if (amount.getText().toString().matches(""))
                                Toast.makeText(addNutrition.this, "נא הכנס כמות", Toast.LENGTH_LONG).show();
                            else {
                                amount2 = Integer.parseInt(amount.getText().toString());
                                String anotherFood = spinner2.getSelectedItem().toString();
                                Map<String, Object> newFood = new HashMap<>();
                                if(anotherFood == "אחר") {
                                    newFood = anotherFoodMap;
                                    anotherFood = newFoodName;
                                    caloriesToInsert = newFoodCal;
                                    unitToInsert ="";
                                }
                                else {

                                    switch (spinner3.getSelectedItem().toString()) {
                                        case "כוס":
                                            caloriesToInsert = (int) calories * CONVERT_G_TO_CUP * amount2;
                                            unitToInsert = "(" + amount2 + " כוסות)";
                                            newFood.put("name", anotherFood);
                                            newFood.put("calories", caloriesToInsert);
                                            newFood.put("unit", unitToInsert);
                                            break;

                                        case "יחידה":
                                            caloriesToInsert = (int) calories * CONVERT_G_TO_UNIT * amount2;
                                            unitToInsert = "(" + amount2 + " יחידות)";
                                            newFood.put("name", anotherFood);
                                            newFood.put("calories", caloriesToInsert);
                                            newFood.put("unit", unitToInsert);
                                            break;

                                        case "100 גרם":
                                            caloriesToInsert = (int) calories * amount2;
                                            unitToInsert = "(" + amount2 * 100 + " גרם)";
                                            newFood.put("name", anotherFood);
                                            newFood.put("calories", caloriesToInsert);
                                            newFood.put("unit", unitToInsert);
                                            break;
                                    }
                                }

                                //insert new food to user's nutritionList reports list
                                insertNewFoodToUserNutritionList(anotherFood, newFood);

                                NutritionActivity.nutritionList.add(new Food(anotherFood, (long) caloriesToInsert, unitToInsert));
                                NutritionActivity.sum+=caloriesToInsert;
                                finish();
                            }
                        }
                    }
                });

            }

        });


    }


    /**populate nutrition list before the user choose food item to add.
     * taken from the DB*/
    public void setComboBox() {

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

                            spinner2.setAdapter(adapter);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    /**add new food item to nutrition list in thr DB*/
    public void insertNewValueToNutritionCollection(String anotherFood, Map<String, Object> newFood) {
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

    /**when choosing "other" in the nutrition list, an alert dialog pop up window pops and you type the new food item you want to add*/
    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog_nutrition, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setNegativeButton("ביטול",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String anotherFood = editText.getText().toString();

                        //set the document before insert
                        //Map<String, Object> newFood = new HashMap<>();
                        anotherFoodMap.put("name", anotherFood);
                        anotherFoodMap.put("calories", 100);
                        anotherFoodMap.put("unit", "100 גרם");

                        newFoodName = anotherFood + "(100 גרם)";
                        newFoodCal = 100;

                    }
                });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    /**after adding food item to the daily nutrition list, method insert the new food to the user's daily nutrition list on th DB*/
    public void insertNewFoodToUserNutritionList(final String anotherFood, final Map<String, Object> newFood) {

        //add new food
        menuActivity.user_details.collection("nutrition reports").add(newFood)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

}
