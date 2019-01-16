package com.nastia.cf;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
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
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class fev1_editActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView fevText;
    private NumberPicker np;
    Button backBtn;
    TextView finish;
    TextView seconds;
    ImageView crown;
    Button save;
    ImageView share;
    protected int goldDark, goldMed, gold, goldLight;
    protected int[] colors;
    protected ViewGroup container;
    ArrayList<Integer> mSelectedItems;
    final ArrayList<CharSequence> contactsArry = new ArrayList<>();
    String[] conArr;
    public static ArrayList<Contact> contacts=new ArrayList<>();
    int fevNum;
    private final List<ConfettiManager> activeConfettiManagers = new ArrayList<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fev1_edit);
        save = findViewById(R.id.save);
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

         np = findViewById(R.id.numberPicker);
         fevText = findViewById(R.id.fevText);
        backBtn=(Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        np.setMinValue(40);
        np.setMaxValue(100);

        DocumentReference user_details = db.collection("user_details")
                .document(mAuth.getCurrentUser().getUid());

        user_details.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if(!document.contains("fev1")){
                        fevText.setText("");
                    }
                    else{
                        fevNum = document.getLong("fev1").intValue();
                        String text = fevNum + " %";
                        fevText.setText(text);
                        np.setValue(fevNum);
                    }
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        importContacts();
        share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openShareListDialog();
            }
        });

    }

    public void saveBtn(View v){
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
                            newField.put("fev1", 70);
                            user_details.set(newField, SetOptions.merge());
                        }
                        else{
                            user_details.update("fev1", newFev1);

                        }
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        if(newFev1 > fevNum){
            container.setVisibility(View.VISIBLE);
            crown.setVisibility(View.VISIBLE);
            share.setVisibility(View.VISIBLE);
            finish.setVisibility(View.VISIBLE);
            save.setVisibility(View.INVISIBLE);
            activeConfettiManagers.add(generateInfinite());
        }
        else finish();

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

    protected ConfettiManager generateInfinite() {
        return CommonConfetti.rainingConfetti(container, colors)
                .infinite();
    }

    protected void sendSMSMessage() {
        for (Integer index:mSelectedItems) {
            for (Contact c : contacts) {
                if ((contactsArry.get(index)).equals(c.myName)) {
                    String phoneNo = c.myNumber;
                    String message = "שיפרתי את מדדה-FEV1 שלי!!! נשלח באמצעות באפליקציית 'אני CF'!";
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
