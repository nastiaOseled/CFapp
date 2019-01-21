package com.nastia.cf;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nastia.cf.NutritionActivity.nutritionList;

public class FeedActivity extends AppCompatActivity {
    private static final String TAG = "feed activity";
    public SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
    public SimpleDateFormat stf = new SimpleDateFormat("HH:mm");


    Button changeBtn;
    RecyclerView rvPosts;
    public FeedAdapter adapter;
    TextView nickname;
    public List<Post> posts = new ArrayList<>();
    public List<Comment> comments = new ArrayList<>();
    Button backBtn;
    ImageView iconImg;
    TextView addText;
    TextView addImage;
    TextView addLocation;
    public static final int PICK_IMAGE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        changeBtn = findViewById(R.id.button3);
        rvPosts = findViewById(R.id.rvPosts);
        rvPosts.setVisibility(View.VISIBLE);
        nickname = findViewById(R.id.nickname);
        backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iconImg = (ImageView) findViewById(R.id.iconImg);
        addText=(TextView) findViewById(R.id.text);
        addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog("הקלד את הטקסט שברצונך לשתף", "addText");
            }
        });
        addImage=(TextView) findViewById(R.id.image);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageInputDialog("נא בחר תמונה שברצונך לשתף", "addImage");
            }
        });
        addLocation=(TextView) findViewById(R.id.location);

        Context context = iconImg.getContext();
        int id = context.getResources().getIdentifier(menuActivity.IMAGE, "drawable", context.getPackageName());
        iconImg.setImageResource(id);

        nickname.setText(menuActivity.NICKNAME + "");

//        ArrayList<Comment> c = new ArrayList<Comment>();
//        c.add(new Comment("u222", "25/1/18", "14:20", "tetetet"));
//        Post p = new Post(mAuth.getCurrentUser().getUid(), "nastia", "23/1/18", "16:33", 11, c, "blblblblb", 1);
//        addPostToDB(p);
//        posts.add(p);
//        p = new Post(mAuth.getCurrentUser().getUid(), "nof", "23/1/18", "16:33", 11, c, "jhjhjhh", 0);
//        addPostToDB(p);
//        posts.add(p);
//       Post p = new Post(LauncherActivity.mAuth.getCurrentUser().getUid(), "Location", "23/1/18", "16:33", 11, null, "jhjhjhh", 1);
//        addPostToDB(p);
//        posts.add(p);

        adapter = new FeedAdapter(posts);
        // Attach the adapter to the recyclerview to populate items
        rvPosts.setAdapter(adapter);
        // Set layout manager to position the items
        rvPosts.setLayoutManager(new LinearLayoutManager(FeedActivity.this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvPosts.getContext(),
                DividerItemDecoration.VERTICAL);
        rvPosts.addItemDecoration(dividerItemDecoration);
        adapter.notifyDataSetChanged();


        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog("הכנס כינוי חדש", "changeNickname");
            }
        });

        importPosts();
    }

    private void importPosts() {
        //populate posts array list and notify adapter
        LauncherActivity.db.collection("Posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, " getting documents: ", task.getException());
                                final String postId = document.getId();
                                final String userId = document.getString("userId");
                                final String nick = document.getString("nickname");
                                final String d = document.getString("date");
                                final String t = document.getString("time");
                                final long likes = (long) document.get("likes");
                                final String text = document.getString("text");
                                final long type = (long) document.get("type");

                                comments.clear();

                                if (document.getReference().collection("comments") != null) {
                                    CollectionReference com = (CollectionReference) document.getReference().collection("comments");
                                    com.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot q : task.getResult()) {
                                                    final String comNickname = q.getString("nickname");
                                                    final String comDate = q.getString("date");
                                                    final String comTime = q.getString("time");
                                                    final String comText = q.getString("text");
                                                    comments.add(new Comment(comNickname, comDate, comTime, comText));
                                                  //  createComment(comNickname, comDate, comTime, comText);
                                                }
                                            }
                                            posts.add(new Post(postId, userId, nick, d, t, likes, (ArrayList<Comment>) comments, text, type));
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                               // posts.add(new Post(postId, userId, nick, d, t, likes, (ArrayList<Comment>) comments, text, type));
                            }
                            //adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    protected void showInputDialog(String displayText, final String actionType) {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.input_image_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        TextView textView=(TextView)promptView.findViewById(R.id.textView);
        textView.setText(displayText+"");
        Button getImage=(Button)promptView.findViewById(R.id.done2);
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setNegativeButton("ביטול",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switch (actionType){
                            case "changeNickname":
                                changeNickname(editText.getText()+"");
                                break;
                            case "addText":
                                addTextPost(editText.getText()+"");


                        }
                    }
                });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    protected void showImageInputDialog(String displayText, final String actionType) {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.input_image_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        TextView textView=(TextView)promptView.findViewById(R.id.textView);
        textView.setText(displayText+"");

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setNegativeButton("ביטול",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switch (actionType){
                            case "changeNickname":
                                changeNickname(editText.getText()+"");
                                break;
                            case "addText":
                                addTextPost(editText.getText()+"");


                        }
                    }
                });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void addTextPost(String text) {

        //add new post
        Map<String, Object> newPost = new HashMap<>();
        newPost.put("userId", LauncherActivity.mAuth.getCurrentUser().getUid());
        newPost.put("nickname", menuActivity.NICKNAME);
        Date today = new Date();
        String stringToday = sdf.format(today);
        newPost.put("date", stringToday);
        String nowTime=stf.format(today.getTime());
        newPost.put("time", nowTime);
        newPost.put("likes", 0);
        newPost.put("text", text);
        newPost.put("type", 0);
        // newPost.put("comments", post.getComments());

        LauncherActivity.db.collection("Posts").document()
                .set(newPost)
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

        Post p=new Post("", LauncherActivity.mAuth.getCurrentUser().getUid(), menuActivity.NICKNAME, stringToday,
                nowTime, 0, new ArrayList<Comment>(), text,0);
        this.posts.add(p);
        this.adapter.notifyDataSetChanged();

    }


    /**create new comment and add it to comments list*/
    public void createComment(String comNickname, String comDate, String comTime, String comText){
        Comment c=new Comment(comNickname, comDate, comTime, comText);
        this.comments.add(c);
    }

    /**
     * add comment to post in posts list
     * add comment to post document in DB
     *
     * @param post
     */
    public void addCommentToPost(Post post, Comment comment) {

        //insert to comments list
        post.getComments().add(comment);

        //insert to DB

    }

    public void changeNickname(String newNickname){
        nickname.setText(newNickname + "");
        menuActivity.NICKNAME = newNickname;
        LauncherActivity.user_details.update("name", newNickname);
    }
}

