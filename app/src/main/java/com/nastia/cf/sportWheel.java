package com.nastia.cf;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import com.github.jinatonic.confetti.CommonConfetti;
import com.github.jinatonic.confetti.ConfettiManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    ArrayList mSelectedItems;
    final ArrayList<CharSequence> contactsArry = new ArrayList<>();
    String[] conArr;
    public static ArrayList<Contact> contacts=new ArrayList<>();

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

        importContacts();
        share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openShareListDialog();
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
    private static final int PERMISSION_SEND_SMS = 123;

    private void requestSmsPermission() {

        // check permission is given
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);
        } else {
            // permission already granted run sms send
            sendSms("", "");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_SEND_SMS: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    sendSms("", "");
                } else {
                    // permission denied
                }
                return;
            }
        }
    }

    private void sendSms(String phoneNumber, String message){
        Toast.makeText(this, "worked",
                Toast.LENGTH_LONG).show();
        //SmsManager sms = SmsManager.getDefault();
        //sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    private void openShareListDialog() {

/*        AlertDialog.Builder builder = new AlertDialog.Builder(sportWheel.this);
        builder.setTitle("ניתן לבחור תרופה מהרשימה הבאה")
                .setItems(medArr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        name.setText(medArr[which].toString());
                    }
                })
                .setNegativeButton("חזור", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();*/

        mSelectedItems = new ArrayList();  // Where we track the selected items
        conArr = new String[contactsArry.size()];
        conArr = contactsArry.toArray(conArr);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle("את מי תרצה לשתף?")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(conArr, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton("שתף", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog

                    }
                })
                .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void importContacts() {
        final DocumentReference user_details = db.collection("user_details")
                .document(mAuth.getCurrentUser().getUid());
        user_details.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user_details.collection("contacts")
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()){
                                        String name=document.getString("name");
                                        String phone=document.getString("phone");
                                        Contact c=new Contact(name, phone);
                                        if( ! contacts.contains(c))
                                            contacts.add(c);
                                        contactsArry.add(name);
                                    }
                                    //adapter.notifyDataSetChanged();
                                    //updateAddBtn();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

}
