package com.nastia.cf;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NutritionActivity extends AppCompatActivity {

    //DB connection
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    Button addBtn;
    TextView calories;
    TextView caloriesLeft;
    TableLayout table;
    //map to save all foods of user in a certain day
    public Map <String, Long> foodsMap=new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);

        calories=(TextView) findViewById(R.id.textView13);
        caloriesLeft=(TextView) findViewById(R.id.textView15);

        addBtn=findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(NutritionActivity.this, addNutrition.class));
            }
        });

        setCalories();
    }



    private void setCalories() {

        //get recommended calories per day
        final DocumentReference user_details = db.collection("user_details")
                .document(mAuth.getCurrentUser().getUid());
        user_details.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final int recommendedCalories = document.getLong("recommendedCaloriesPerDay").intValue();
                        CollectionReference foods=user_details.collection("nutrition reports");
                        foods.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                String st="hello";
                                long sum=0;
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        sum+=(Long)document.getLong("calories");
                                       // insertToFoodMap(document.getString("name"),(Long)document.getLong("calories"));

                                        //set table
                                        table = findViewById(R.id.table1);

                                        TableRow tr = new TableRow(NutritionActivity.this);

                                        TextView calories = new TextView(NutritionActivity.this);
                                        calories.setText((Long)document.getLong("calories")+"");
                                        tr.addView(calories);

                                        TextView space = new TextView(NutritionActivity.this);
                                        space.setText("      ");
                                        tr.addView(space);

                                        TextView foodItem = new TextView(NutritionActivity.this);
                                        foodItem.setText(document.getString("name"));
                                        tr.addView(foodItem);





                                        table.addView(tr);
                                    }


                                     //set calories amount so far
                                    calories.setText(sum+"");

                                    //set how much calories left
                                    if(recommendedCalories-sum > 0)
                                        caloriesLeft.setText((recommendedCalories-sum)+"");
                                    else
                                        caloriesLeft.setText(0+"");
                                }

                            }
                        });
                    }
                }
            }
        });


    }

}
