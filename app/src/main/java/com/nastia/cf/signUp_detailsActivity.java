package com.nastia.cf;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.Calendar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class signUp_detailsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView name;
    private TextView weight;
    private TextView height;
    private TextView birthDate;
    private TextView calories;
    Boolean updateFlag=false;
    Button addContactBtn;
    TextView addCon;
    ViewFlipper viewFlipper;
    ImageView next;
    ImageView previous;
    FrameLayout lay;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final String TAG = signUpActivity.class.getSimpleName();
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_details);
        Bundle b = getIntent().getExtras();
        if(b != null) {
            updateFlag = b.getBoolean("update");
        }
        name= findViewById(R.id.name);
        weight= findViewById(R.id.weight);
        height =findViewById(R.id.height);
        birthDate= findViewById(R.id.birthDate);
        calories = findViewById(R.id.calories);
        addContactBtn=findViewById(R.id.button);
        addCon = findViewById(R.id.addCon);
        lay = findViewById(R.id.lay);
        if(updateFlag){
            addContactBtn.setVisibility(View.INVISIBLE);
            addCon.setVisibility(View.INVISIBLE);
            name.setText(menuActivity.NICKNAME);
            weight.setText(menuActivity.WEIGHT+"");
            height.setText(menuActivity.HEIGHT+"");
            birthDate.setText(menuActivity.BIRTHDAY+"");
            calories.setText(menuActivity.RECOMMENDED_CALORIES+"");
        }
        lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                InputMethodManager inputMethodManager =
                        (InputMethodManager) signUp_detailsActivity.this.getSystemService(
                                Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(
                        signUp_detailsActivity.this.getCurrentFocus().getWindowToken(), 0);
                DatePickerDialog dialog = new DatePickerDialog(
                        signUp_detailsActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyy: " + day + "/" + month + "/" + year);

                String date = month + "/" + day + "/" + year;
                birthDate.setText(date);
            }
        };
        calories = findViewById(R.id.calories);

        if(mAuth.getCurrentUser()==null){
            Intent intent = new Intent(signUp_detailsActivity.this, signUpActivity.class);
            startActivity(intent);
        }

        viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
        next =  findViewById(R.id.next);
        previous = findViewById(R.id.previous);

        next.setOnClickListener(this);
        previous.setOnClickListener(this);


    }

    // check fields validation
    public void continueBtnListener(View v){
        if(name.getText().toString().isEmpty()){
            Toast.makeText(signUp_detailsActivity.this, "הכנס שם", Toast.LENGTH_SHORT).show();
            return;
        }
        if(weight.getText().toString().isEmpty()){
            Toast.makeText(signUp_detailsActivity.this, "הכנס משקל", Toast.LENGTH_SHORT).show();
            return;
        }
        if(height.getText().toString().isEmpty()){
            Toast.makeText(signUp_detailsActivity.this, "הכנס גובה", Toast.LENGTH_SHORT).show();
            return;
        }
        if(birthDate.getText().toString().isEmpty()){
            Toast.makeText(signUp_detailsActivity.this, "הכנס תאריך לידה", Toast.LENGTH_SHORT).show();
            return;
        }
        if(calories.getText().toString().isEmpty()){
            Toast.makeText(signUp_detailsActivity.this, "הכנס כמות קלוריות", Toast.LENGTH_SHORT).show();
            return;
        }
        addUserDate();

    }

    //save user information to DB
    public void addUserDate() {

        Map<String, Object> user = new HashMap<>();
        user.put("name", name.getText().toString());
        user.put("weight", Double.parseDouble(weight.getText().toString()));
        user.put("height", Integer.parseInt(height.getText().toString()));
        user.put("recommendedCaloriesPerDay", Integer.parseInt(calories.getText().toString()));
        user.put("birthDate", birthDate.getText().toString());
        user.put("image",String.valueOf(viewFlipper.getCurrentView().getTag()));


        db.collection("user_details").document(mAuth.getCurrentUser().getUid()).set(user);

        SharedPreferences sharedPref =LauncherActivity.getPref();
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("userConnected", true);
        editor.commit();
        if(updateFlag){
            menuActivity.NICKNAME=name.getText().toString();
            menuActivity.RECOMMENDED_CALORIES=Integer.parseInt(calories.getText().toString());
            menuActivity.HEIGHT=Integer.parseInt(height.getText().toString());
            menuActivity.WEIGHT=Double.parseDouble(weight.getText().toString());
            menuActivity.BIRTHDAY=birthDate.getText().toString();
            menuActivity.IMAGE=String.valueOf(viewFlipper.getCurrentView().getTag());
            setResult(Activity.RESULT_OK);
            finish();
        }
        else{
            Intent intent = new Intent(signUp_detailsActivity.this, menuActivity.class);
            startActivity(intent);
        }

    }

    //navigate to contacts activity
    public void addContacts(View v){
        Intent intent = new Intent(signUp_detailsActivity.this, ContactsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v == next) {
            viewFlipper.showNext();
        }
        else if (v == previous) {
            viewFlipper.showPrevious();
        }
    }
}

