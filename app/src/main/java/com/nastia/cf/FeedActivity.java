package com.nastia.cf;

import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nastia.cf.NutritionActivity.nutritionList;

public class FeedActivity extends AppCompatActivity {
    private static final String TAG = "feed activity";


//    //DB connection
//    private final static FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
//    final DocumentReference user_details = db.collection("user_details")
//            .document(mAuth.getCurrentUser().getUid());


    Button changeBtn;
    RecyclerView rvPosts;
    public FeedAdapter adapter;
    //public PostsAdapter adapter;
    TextView nickname;
    public List<Post> posts = new ArrayList<>();
    Button backBtn;
    ImageView iconImg;


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
                showInputDialog();
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
                                final String id = document.getString("userId");
                                final String nick = document.getString("nickname");
                                final String d = document.getString("date");
                                final String t = document.getString("time");
                                final long likes = (long) document.get("likes");
                                final String text = document.getString("text");
                                final long type = (long) document.get("type");

                           /*     CollectionReference com = (CollectionReference) document.get("comments");
                                final ArrayList<Comment> comments = new ArrayList<>();
                                com.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot q : task.getResult()) {
                                                comments.add(new Comment(q.getString("nickname"),
                                                        q.getString("date"), q.getString("time"), q.getString("text")));
                                            }
                                        }
                                        posts.add(new Post(id, nick, d, t, likes, comments, text, type));
                                    }

                                });  */
                                posts.add(new Post(id, nick, d, t, likes, null, text, type));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

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
                        nickname.setText(editText.getText() + "");
                        menuActivity.NICKNAME = editText.getText().toString();
                        LauncherActivity.user_details.update("name", editText.getText().toString());
                    }
                });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void addPostToDB(Post post) {

        //add new post
        Map<String, Object> newPost = new HashMap<>();
        newPost.put("userId", post.getUserId());
        newPost.put("nickname", post.getNickname());
        newPost.put("date", post.getDate());
        newPost.put("time", post.getTime());
        newPost.put("likes", post.getLikes());
        newPost.put("text", post.getPostText());
        newPost.put("type", post.getType());
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
    }


    /**add comment to post in posts list
     * add comment to post document in DB
     * @param post
     */
    public void addCommentToPost(Post post, Comment comment){

        //insert to comments list
        post.getComments().add(comment);

        //insert to DB


    }
}

