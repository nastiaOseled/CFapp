package com.nastia.cf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signUpActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = signUpActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private boolean isSignIn = true ;
    private TextView signUpBtn;
    private TextView signInBtn;
    private View signUpLine;
    private View signInLine;
    private EditText signin_email;
    private EditText signin_psw;
    private EditText signup_email;
    private EditText signup_psw;
    private EditText signup_psw2;
    private CheckBox terms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        // Views
        signUpBtn = findViewById(R.id.signinBtn);
        signInBtn = findViewById(R.id.signupBtn);

        signUpLine= findViewById(R.id.signinLine);
        signInLine = findViewById(R.id.signupLine);

        signin_email = findViewById(R.id.signin_email);
        signin_psw = findViewById(R.id.signin_psw);

        signup_email = findViewById(R.id.signup_email);
        signup_psw = findViewById(R.id.signup_psw);
        signup_psw2 = findViewById(R.id.signup_psw2);
        terms = findViewById(R.id.termsCheckBox);

        signup_email.setVisibility(View.INVISIBLE);
        signup_psw.setVisibility(View.INVISIBLE);
        signup_psw2.setVisibility(View.INVISIBLE);
        terms.setVisibility(View.INVISIBLE);

        // Buttons
        /*findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        *//*findViewById(R.id.email_create_account_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.verify_email_button).setOnClickListener(this);*/

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        //showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(signUpActivity.this, signUp_detailsActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(signUpActivity.this, "ההרשמה נכשלה",
                                    Toast.LENGTH_SHORT).show();
                            
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();   ??
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }
    
    private boolean validateForm() {
        return true;
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        SharedPreferences sharedPref =LauncherActivity.getPref();
        final SharedPreferences.Editor editor = sharedPref.edit();


        //showProgressDialog();    ??

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");

                            editor.putBoolean("userConnected", true);
                            editor.commit();

                            Intent intent = new Intent(signUpActivity.this, menuActivity.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(signUpActivity.this, "ההתחברות נכשלה.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
//                            mStatusTextView.setText(R.string.auth_failed);
                        }
                        //hideProgressDialog();    ??
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    @Override
    public void onClick(View v) {
       /* int i = v.getId();
        if (i == R.id.email_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.verify_email_button) {
            sendEmailVerification();
        }*/
    }

    public void onClickSignIn(View v){
        isSignIn=true;
        signin_email.setVisibility(View.VISIBLE);
        signin_psw.setVisibility(View.VISIBLE);
        signup_email.setVisibility(View.INVISIBLE);
        signup_psw.setVisibility(View.INVISIBLE);
        signup_psw2.setVisibility(View.INVISIBLE);
        terms.setVisibility(View.INVISIBLE);
        signInBtn.setTextColor(Color.GRAY);
        signInLine.setBackgroundColor(Color.GRAY);
        signUpBtn.setTextColor(Color.WHITE);
        signUpLine.setBackgroundColor(Color.WHITE);
    }

    public void onClickSignUp(View v){
        isSignIn=false;
        signin_email.setVisibility(View.INVISIBLE);
        signin_psw.setVisibility(View.INVISIBLE);
        signup_email.setVisibility(View.VISIBLE);
        signup_psw.setVisibility(View.VISIBLE);
        signup_psw2.setVisibility(View.VISIBLE);
        terms.setVisibility(View.VISIBLE);
        signInBtn.setTextColor(Color.WHITE);
        signInLine.setBackgroundColor(Color.WHITE);
        signUpBtn.setTextColor(Color.GRAY);
        signUpLine.setBackgroundColor(Color.GRAY);
    }

    public void continueBtnListener(View v){
        if(isSignIn){
            signIn(signin_email.getText().toString(),signin_psw.getText().toString());
        }
        else{
            if(signup_psw.getText().toString().equals( signup_psw2.getText().toString()) && terms.isChecked()){
                createAccount(signup_email.getText().toString(),signup_psw.getText().toString());
            }
            else if(!terms.isChecked()){
                Toast.makeText(signUpActivity.this, "אשר את התקנון ותנאי השירות",
                        Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(signUpActivity.this, "הסיסמאות אינן תואמות",
                        Toast.LENGTH_SHORT).show();
            }
        }




    }
    
}
