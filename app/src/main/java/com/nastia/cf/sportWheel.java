package com.nastia.cf;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class sportWheel extends AppCompatActivity {

    ImageView imageViewThumb;
    ImageView imageView3;
    ImageView popup;
    TextView act;
    Button BtnRoll;
    Button BtnDone;


    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = signUpActivity.class.getSimpleName();
    private static final float ROTATE_FROM = 1.0f;
    private static final float ROTATE_TO = 360.0f;
    RotateAnimation r; // = new RotateAnimation(ROTATE_FROM, ROTATE_TO);

    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_wheel);


        act=(TextView) findViewById(R.id.act);
        act.setVisibility(View.INVISIBLE);
        popup=(ImageView) findViewById(R.id.popup);
        popup.setVisibility(View.INVISIBLE);
        imageView3= (ImageView) findViewById(R.id.imageView3);
        imageViewThumb= (ImageView) findViewById(R.id.wheel);
        BtnRoll = findViewById(R.id.cntBtn3);
        BtnDone = findViewById(R.id.done);
        BtnDone.setVisibility(View.INVISIBLE);
        BtnDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        BtnRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float to = ROTATE_TO + (ROTATE_TO*(float)Math.random());
                r = new RotateAnimation(ROTATE_FROM, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                r.setDuration((long) 2*700);
                r.setFillAfter(true);
                r.setFillEnabled(true);
                r.setRepeatCount(0);
                imageViewThumb.startAnimation(r);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        //ImageViewCompat.setImageTintList(imageViewThumb, ColorStateList.valueOf(Color.parseColor("#6F000000")));
                        imageViewThumb.setColorFilter(Color.argb(110, 0, 0, 0));
                        imageView3.setColorFilter(Color.argb(100, 0, 0, 0));
                        //ImageViewCompat.setImageTintList(imageView3, ColorStateList.valueOf(Color.parseColor("#6F000000")));

                        db.collection("Physical Activity")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        act.setVisibility(View.VISIBLE);
                                        popup.setVisibility(View.VISIBLE);
                                        if (task.isSuccessful()) {
                                            count = 0;
                                            for (DocumentSnapshot document : task.getResult()) {
                                                count++;
                                            }
                                            int rand = (int) Math.floor(Math.random() * count);
                                            for (DocumentSnapshot document : task.getResult()) {
                                                if(count-- == rand){
                                                   act.setText(document.get("description").toString());

                                                }
                                            }
                                            BtnRoll.setVisibility(View.INVISIBLE);
                                            BtnDone.setVisibility(View.VISIBLE);
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                    }
                }, 2000);

            }
        });
    }

    /*public void saveBtn(View v){
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
*/

}
