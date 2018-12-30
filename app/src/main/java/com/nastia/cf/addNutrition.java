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
                Toast.makeText(parent.getContext(),
                        "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_LONG).show();
                if (parent.getItemAtPosition(position).toString().equals("אחר"))
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
                                switch (spinner3.getSelectedItem().toString()) {
                                    case "כוס":
                                        caloriesToInsert = (int) calories * CONVERT_G_TO_CUP * amount2;
                                        unitToInsert="("+amount2+" כוסות)";
                                        newFood.put("name", anotherFood);
                                        newFood.put("calories", caloriesToInsert);
                                        newFood.put("unit",unitToInsert);
                                        break;

                                    case "יחידה":
                                        caloriesToInsert = (int) calories * CONVERT_G_TO_UNIT * amount2;
                                        unitToInsert="("+amount2+" יחידות)";
                                        newFood.put("name", anotherFood);
                                        newFood.put("calories", caloriesToInsert);
                                        newFood.put("unit", unitToInsert);
                                        break;

                                    case "100 גרם":
                                        caloriesToInsert = (int) calories * amount2;
                                        unitToInsert="("+amount2*100+" גרם)";
                                        newFood.put("name", anotherFood);
                                        newFood.put("calories", caloriesToInsert);
                                        newFood.put("unit", unitToInsert);
                                        break;
                                }

                                //insert new food to user's nutritionList reports list
                                insertNewFoodToUserNutritionList(anotherFood, newFood);
                                //startActivity(new Intent(addNutrition.this,NutritionActivity.class));

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

    public void getInputFromUser() {
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

                //insert new value to nutritionList list
                insertNewValueToNutritionCollection(anotherFood, newFood);

                //insert new value to user's nutritionList reports list
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

        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

    }

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


    public void insertNewFoodToUserNutritionList(final String anotherFood, final Map<String, Object> newFood) {

//        final Date today = new Date();
//        final String stringToday=sfd.format(today);

//        DocumentReference docRef = db.collection("user_details").
//                document(mAuth.getCurrentUser().getUid()).collection("nutrition reports").document("Date");
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                DocumentSnapshot document = task.getResult();
//                String time = document.getString("date");
//                if ( ! stringToday.equals(time)) {
//                    NutritionActivity.deleteAllNutrition(stringToday,time);
//                }//if date is not today
//            }
//        });

        //add new food
        db.collection("user_details").
                document(mAuth.getCurrentUser().getUid()).collection("nutrition reports").add(newFood)
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
