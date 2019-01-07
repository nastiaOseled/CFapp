package com.nastia.cf;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;

import com.github.jinatonic.confetti.CommonConfetti;
import com.github.jinatonic.confetti.ConfettiManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class sportWheel extends AppCompatActivity {

    ImageView imageViewThumb;
    ImageView imageView3;
    ImageView popup;
    ImageView crown;
    ImageView share;
    TextView act;
    TextView finish;
    Button BtnRoll;
    Button BtnDone;
    Button BtnRollAgain;
    String ActivityId;
    Button backBtn;

    protected int goldDark, goldMed, gold, goldLight;
    protected int[] colors;

    private final List<ConfettiManager> activeConfettiManagers = new ArrayList<>();

    protected ViewGroup container;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final String TAG = signUpActivity.class.getSimpleName();
    private static final float ROTATE_FROM = 1.0f;
    private static final float ROTATE_TO = 360.0f;
    RotateAnimation r; // = new RotateAnimation(ROTATE_FROM, ROTATE_TO);

    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_wheel);

        crown = findViewById(R.id.crown);
        crown.setVisibility(View.INVISIBLE);
        share = findViewById(R.id.share);
        share.setVisibility(View.INVISIBLE);
        finish = findViewById(R.id.finishText);
        finish.setVisibility(View.INVISIBLE);
        act=(TextView) findViewById(R.id.act);
        act.setVisibility(View.INVISIBLE);
        popup=(ImageView) findViewById(R.id.popup);
        popup.setVisibility(View.INVISIBLE);
        imageView3= (ImageView) findViewById(R.id.imageView3);
        imageViewThumb= (ImageView) findViewById(R.id.wheel);
        BtnRoll = findViewById(R.id.cntBtn2);
        BtnDone = findViewById(R.id.done);
        BtnRollAgain = findViewById(R.id.again);
        BtnDone.setVisibility(View.INVISIBLE);
        BtnRollAgain.setVisibility(View.INVISIBLE);
        BtnDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BtnDone.setVisibility(View.INVISIBLE);
                BtnRollAgain.setVisibility(View.INVISIBLE);
                container.setVisibility(View.VISIBLE);
                crown.setVisibility(View.VISIBLE);
                share.setVisibility(View.VISIBLE);
                finish.setVisibility(View.VISIBLE);
                activeConfettiManagers.add(generateInfinite());
                SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String stringDate = dt.format(date);
                Map<String, Object> activity = new HashMap<>();
                activity.put("Activity ID", ActivityId);
                activity.put("done", true);
                db.collection("user_details").document(mAuth.getCurrentUser().getUid())
                        .collection("Physical Activity").document(stringDate).collection(stringDate)
                        .add(activity);
            }
        });
        BtnRollAgain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String stringDate = dt.format(date);
                Map<String, Object> activity = new HashMap<>();
                activity.put("Activity ID", ActivityId);
                activity.put("done", false);
                db.collection("user_details").document(mAuth.getCurrentUser().getUid())
                        .collection("Physical Activity").document(stringDate).collection(stringDate)
                        .add(activity);
                animation();

            }
        });
        BtnRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animation();
            }
        });
        container = (ViewGroup) findViewById(R.id.container);
        container.setVisibility(View.INVISIBLE);
        final Resources res = getResources();
        goldDark = res.getColor(R.color.gold_dark);
        goldMed = res.getColor(R.color.gold_med);
        gold = res.getColor(R.color.gold);
        goldLight = res.getColor(R.color.gold_light);
        colors = new int[] { goldDark, goldMed, gold, goldLight };

        backBtn=(Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void animation(){
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
                imageViewThumb.setColorFilter(Color.argb(110, 0, 0, 0));
                imageView3.setColorFilter(Color.argb(100, 0, 0, 0));

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
                                            ActivityId = document.getId();
                                            act.setText(document.get("description").toString());

                                        }
                                    }
                                    BtnRoll.setVisibility(View.INVISIBLE);
                                    BtnDone.setVisibility(View.VISIBLE);
                                    BtnRollAgain.setVisibility(View.VISIBLE);
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        }, 1300);
    }

    protected ConfettiManager generateInfinite() {
        return CommonConfetti.rainingConfetti(container, colors)
                .infinite();
    }

}
