package com.nastia.cf;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NutritionActivity extends AppCompatActivity {

    //DB connection
    private final static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    Button addBtn;
    TextView calories;
    TextView caloriesLeft;
    TableLayout table;
    RecyclerView rvNutrition;
    TextView recommended;
    ImageView recommendedFrame;
    TextView date;

    //all food items of user in a certain day
    public static ArrayList<Food> nutritionList = new ArrayList<>();

    public static NutritionAdapter adapter;

    //sums the amount of calories for today
    public static long sum = 0;
    int recommendedCalories = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);

        calories = (TextView) findViewById(R.id.textView13);
        caloriesLeft = (TextView) findViewById(R.id.textView15);
        rvNutrition = (RecyclerView) findViewById(R.id.rvNutrition);
        recommended = (TextView) findViewById(R.id.textView23);
        recommendedFrame = (ImageView) findViewById(R.id.imageView5);
        date = (TextView) findViewById(R.id.textView25);
        addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(NutritionActivity.this, addNutrition.class));
            }
        });

        //TODO import nutritionList

        adapter = new NutritionAdapter(nutritionList);
        // Attach the adapter to the recyclerview to populate items
        rvNutrition.setAdapter(adapter);
        // Set layout manager to position the items
        rvNutrition.setLayoutManager(new LinearLayoutManager(NutritionActivity.this));

        setCalories();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // nutritionList.add(new Food("test", Long.parseLong("100")));
        adapter.notifyDataSetChanged();
        //set calories amount so far
        calories.setText(sum + "");

        //set how much calories left
        if (recommendedCalories - sum > 0)
            caloriesLeft.setText((recommendedCalories - sum) + "");
        else
            caloriesLeft.setText(0 + "");

    }


    private void setCalories() {

        final Date today = new Date();
        final String stringToday = addNutrition.sfd.format(today);

        //get recommended calories per day
        final DocumentReference user_details = db.collection("user_details")
                .document(mAuth.getCurrentUser().getUid());
        user_details.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        recommendedCalories = document.getLong("recommendedCaloriesPerDay").intValue();
                        recommended.setText(recommendedCalories + "");
                        caloriesLeft.setText(recommendedCalories + "");
                        final CollectionReference foods = user_details.collection("nutrition reports");

                        //check if today's date matches DB date
                        foods.document("Date").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot document = task.getResult();
                                String time = document.getString("date");
                                if (!stringToday.equals(time)) {
                                    deleteAllNutrition(stringToday, time);
                                } else {
                                    //get today's calories sum so far
                                    date.setText(time+"");
                                    sum = 0;
                                    nutritionList.clear();
                                    foods.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (document.getString("date") != null)
                                                        continue;
                                                    sum += (Long) document.getLong("calories");
                                                    nutritionList.add(new Food(document.getString("name"),
                                                            document.getLong("calories")
                                                            , document.getString("unit")));
                                                }

                                                adapter.notifyDataSetChanged();

                                                //set calories amount so far
                                                calories.setText(sum + "");

                                                //set how much calories left
                                                if (recommendedCalories - sum > 0)
                                                    caloriesLeft.setText((recommendedCalories - sum) + "");
                                                else
                                                    caloriesLeft.setText(0 + "");
                                            }

                                        }
                                    });
                                }
                            }
                        });

                    }
                }
            }
        });

    }

    /**
     * delete all nutrition reports of the user in case today's date does not matches DB date
     */
    public void deleteAllNutrition(final String stringToday, final String previousDate) {

        //calculate sum calories
        for (Food f : nutritionList)
            sum += f.getCalories();
        nutritionList.clear();
        adapter.notifyDataSetChanged();
        calories.setText("0");
        db.collection("user_details").
                document(mAuth.getCurrentUser().getUid()).collection("nutrition reports").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //delete all documents
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                            }
                        }
                        //add today date
                        Map<String, Object> todayDate = new HashMap<>();
                        todayDate.put("date", stringToday);
                        db.collection("user_details").
                                document(mAuth.getCurrentUser().getUid()).collection("nutrition reports")
                                .document("Date").set(todayDate, SetOptions.merge());

                        todayDate.clear();
                        todayDate.put("date", previousDate);
                        todayDate.put("calories", sum);
                        db.collection("user_details").
                                document(mAuth.getCurrentUser().getUid()).collection("calories_reports")
                                .add(todayDate);
                        sum = 0;
                    }
                });
    }

}
