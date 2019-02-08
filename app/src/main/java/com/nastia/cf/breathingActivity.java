package com.nastia.cf;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.CountDownTimer;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.view.View;
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class breathingActivity extends AppCompatActivity {

    ImageView lungs ;
    Button startBtn;
    TextView b1;
    TextView b2;
    TextView b3;
    TextView b4;
    TextView b5;
    CheckBox c1;
    CheckBox c2;
    CheckBox c3;
    TextView finish;
    TextView seconds;
    ImageView crown;
    ImageView share;
    protected int goldDark, goldMed, gold, goldLight;
    protected int[] colors;
    protected ViewGroup container;
    private final List<ConfettiManager> activeConfettiManagers = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Button backBtn;
    ArrayList<Integer> mSelectedItems;
    final ArrayList<CharSequence> contactsArry = new ArrayList<>();
    String[] conArr;
    public static ArrayList<Contact> contacts=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathing);

        crown = findViewById(R.id.crown);
        crown.setVisibility(View.INVISIBLE);
        share = findViewById(R.id.share);
        share.setVisibility(View.INVISIBLE);
        finish = findViewById(R.id.finishText);
        finish.setVisibility(View.INVISIBLE);
        container = (ViewGroup) findViewById(R.id.container);
        container.setVisibility(View.INVISIBLE);
        final Resources res = getResources();
        goldDark = res.getColor(R.color.gold_dark);
        goldMed = res.getColor(R.color.gold_med);
        gold = res.getColor(R.color.gold);
        goldLight = res.getColor(R.color.gold_light);
        colors = new int[] { goldDark, goldMed, gold, goldLight };

        lungs = findViewById(R.id.lungs);
        startBtn = findViewById(R.id.cntBtn);
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4);
        b5 = findViewById(R.id.b5);
        c1 = findViewById(R.id.c1);
        c2 = findViewById(R.id.c2);
        c3 = findViewById(R.id.c3);
        c1.setEnabled(false);
        c2.setEnabled(false);
        c3.setEnabled(false);
        seconds = findViewById(R.id.second);

        backBtn=(Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        importContacts();
        share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openShareListDialog();
            }
        });
    }

    //
    public void onClick(View v) {
        lungs.animate().
                scaleX(2f).
                scaleY(2f).
                setDuration(3000).start();
        b1.setVisibility(View.INVISIBLE);
        b2.setVisibility(View.INVISIBLE);
        b3.setVisibility(View.INVISIBLE);
        b4.setVisibility(View.INVISIBLE);
        b5.setVisibility(View.INVISIBLE);
        startBtn.setVisibility(View.INVISIBLE);
        seconds.setVisibility(View.VISIBLE);
        new CountDownTimer(8000, 1000) {
            public void onTick(long millisUntilFinished) {
               if(millisUntilFinished/1000 >4){
                    seconds.setText("קח נשימה עמוקה");
                }
                else if(millisUntilFinished/1000 >1 ){
                    String sec = millisUntilFinished/1000-1 +"";
                    seconds.setText(sec);
                }
                else{
                    seconds.setText("לשחרר");
                    lungs.animate().
                                scaleX(1f).
                                scaleY(1f).
                                setDuration(1000).start();
                }
            }

            public void onFinish() {
                        b1.setVisibility(View.VISIBLE);
                        b2.setVisibility(View.VISIBLE);
                        b3.setVisibility(View.VISIBLE);
                        b4.setVisibility(View.VISIBLE);
                        b5.setVisibility(View.VISIBLE);
                        startBtn.setVisibility(View.VISIBLE);
                        seconds.setVisibility(View.INVISIBLE);
                        startBtn.setText("בצע שוב");
                        if(!c1.isChecked()){
                            c1.setChecked(true);
                        }
                        else if(!c2.isChecked()){
                            c2.setChecked(true);
                        }
                        else{
                            c3.setChecked(true);
                            Map<String, Object> activity = new HashMap<>();
                            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = new Date();
                            String stringDate = dt.format(date);
                            activity.put("Date", stringDate);
                            activity.put("done", true);

                            db.collection("user_details").document(mAuth.getCurrentUser().getUid())
                                    .collection("breathing Activity").document(stringDate).set(activity);
                            container.setVisibility(View.VISIBLE);
                            startBtn.setVisibility(View.INVISIBLE);
                            crown.setVisibility(View.VISIBLE);
                            share.setVisibility(View.VISIBLE);
                            finish.setVisibility(View.VISIBLE);
                            activeConfettiManagers.add(generateInfinite());
                        }
            }
        }.start();

    }

    protected ConfettiManager generateInfinite() {
        return CommonConfetti.rainingConfetti(container, colors)
                .infinite();
    }

    private void openShareListDialog() {

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
                        if(!mSelectedItems.isEmpty())
                            requestSmsPermission();

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
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    protected void sendSMSMessage() {
        for (Integer index:mSelectedItems) {
            for (Contact c : contacts) {
                if ((contactsArry.get(index)).equals(c.myName)) {
                    String phoneNo = c.myNumber;
                    String message = "ביצעתי את פעילות הספורט היומית שלי באפליקציית 'אני CF'!";
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                }
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMSMessage();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }


    private void requestSmsPermission() {
        String permission = Manifest.permission.SEND_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 0);
        }
        else sendSMSMessage();
    }

}
