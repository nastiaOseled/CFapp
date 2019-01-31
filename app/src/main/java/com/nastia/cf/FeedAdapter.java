package com.nastia.cf;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Post> mPostsList;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    public SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
    public SimpleDateFormat stf = new SimpleDateFormat("HH:mm");
    private static final String TAG = "feed adapter";
    public FeedAdapter(List<Post> posts) {
        this.mPostsList = posts;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView;

        switch (viewType) {
            case 0: {
                postView = inflater.inflate(R.layout.item_post_text, viewGroup, false);
                return new TextViewHolder(postView);
            }
            case 1:
                postView = inflater.inflate(R.layout.item_post_image, viewGroup, false);
                return new ImageViewHolder(postView);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        Post p = mPostsList.get(position);
        TextView textView;
        int id;
        Context context;
        ImageView imageView;
        ImageView image;

        switch ((int) p.getType()) {
            case 0:
                TextViewHolder tvh = (TextViewHolder) viewHolder;
                textView = tvh.postID;
                textView.setText(p.getPostId());
                textView = tvh.nickname;
                textView.setText(p.getNickname());
                textView = tvh.date;
                textView.setText(p.getDate() + "");
                textView = tvh.time;
                textView.setText(p.getTime() + "");
                textView = tvh.text;
                textView.setText(p.getPostText() + "");
                imageView = tvh.imageLogo;
                textView = tvh.likesNum;
                textView.setText(p.getLikes() + "");
                textView = tvh.commentsNum;
                int num=p.getComments().size();
                textView.setText(num+" תגובות");
                context = imageView.getContext();
                id = context.getResources().getIdentifier(menuActivity.IMAGE, "drawable", context.getPackageName());
                imageView.setImageResource(id);

                ArrayList<Comment> c = p.getComments();

                tvh.adapter=new CommentsAdapter(c);
                tvh.commentsList.setAdapter(tvh.adapter);
                // Set layout manager to position the items
                tvh.commentsList.setLayoutManager(new LinearLayoutManager(context));


                break;
            case 1:
                ImageViewHolder ivh = (ImageViewHolder) viewHolder;
                textView = ivh.postID;
                textView.setText(p.getPostId());
                textView = ivh.nickname;
                textView.setText(p.getNickname());
                textView = ivh.date;
                textView.setText(p.getDate() + "");
                textView = ivh.time;
                textView.setText(p.getTime() + "");
                textView=ivh.text;
                textView.setText(p.getPostText()+"");
                String imageName = (p.getImageName());
                textView = ivh.likesNum;
                textView.setText(p.getLikes() + "");
                textView = ivh.commentsNum;
                int num2=p.getComments().size();
                textView.setText(num2+" תגובות");
                imageView = ivh.imageLogo;
                context = imageView.getContext();
                id = context.getResources().getIdentifier(menuActivity.IMAGE, "drawable", context.getPackageName());
                imageView.setImageResource(id);
                image = ivh.imagePost;
                StorageReference storageReference = storageRef.child("images/"+ imageName);

                Glide.with( context)
                        .load(storageReference)
                        .into(image);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mPostsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Post p = mPostsList.get(position);
        return (int) p.getType();
    }


    class TextViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageLogo;
        public TextView nickname;
        public TextView date;
        public TextView time;
        public TextView text;
        public TextView postID;
        public TextView likesNum;
        public TextView commentsNum;
        public RecyclerView commentsList;

        public Button addComBtn;
        public CommentsAdapter adapter;



        public TextViewHolder(final View itemView) {
            super(itemView);

            imageLogo = (ImageView) itemView.findViewById(R.id.imageLogo);
            nickname = (TextView) itemView.findViewById(R.id.nickname);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
            text = (TextView) itemView.findViewById(R.id.text);
            likesNum = (TextView) itemView.findViewById(R.id.likesNum);
            commentsNum = (TextView) itemView.findViewById(R.id.commentsNum);
            commentsList = (RecyclerView) itemView.findViewById(R.id.commentLayout);
            commentsList.setVisibility(View.GONE);
            commentsNum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(commentsList.getVisibility()==View.VISIBLE)
                        commentsList.setVisibility(View.GONE);
                    else commentsList.setVisibility(View.VISIBLE);
                }
            });
            postID = (TextView) itemView.findViewById(R.id.postID);
            addComBtn=(Button) itemView.findViewById(R.id.addComBtn);
            addComBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addCommentDialog(postID.getText()+"");
                }
            });
        }

        public void addCommentDialog(final String postId){
            // get prompts.xml view
            LayoutInflater layoutInflater = LayoutInflater.from(itemView.getContext());
            View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(itemView.getContext());
            alertDialogBuilder.setView(promptView);
            TextView textView=(TextView)promptView.findViewById(R.id.textView);
            textView.setText("הקלד את תגובתך");


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
                            String comText=editText.getText()+"";
                            addComment(comText,postId);
                            adapter.notifyDataSetChanged();
                            //commentsNum.setText(commentsList.+" תגובות");
                            ///////////////////////////////////////////////////////////////////////////////////////////
                        }
                    });

            // create an alert dialog
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();


        }

        public CommentsAdapter getAdapter() {
            return adapter;
        }
    }

    private void addComment(String comText, String postId){
        //add new comment
        Map<String, Object> newComment = new HashMap<>();
        newComment.put("userId", LauncherActivity.mAuth.getCurrentUser().getUid());
        newComment.put("nickname", menuActivity.NICKNAME);
        Date today = new Date();
        String stringToday = sdf.format(today);
        newComment.put("date", stringToday);
        String nowTime=stf.format(today.getTime());
        newComment.put("time", nowTime);
        newComment.put("text", comText);

        LauncherActivity.db.collection("Posts").document(postId).collection("comments").document()
                .set(newComment)
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
        Comment c=new Comment(menuActivity.NICKNAME, stringToday, nowTime, comText);
        Post postToInsert;
        for(Post p : mPostsList) {
            if (p.getPostId().equals(postId)) {
                p.getComments().add(c);
                break;
            }
        }

    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePost;
        ImageView imageLogo;
        public TextView nickname;
        public TextView date;
        public TextView time;
        public TextView text;
        public TextView commentsNum;
        public TextView likesNum;
        public TextView postID;

        public ImageViewHolder(View itemView) {

            super(itemView);

            imagePost = (ImageView) itemView.findViewById(R.id.imagePost);
            imageLogo = (ImageView) itemView.findViewById(R.id.imageLogo);
            nickname = (TextView) itemView.findViewById(R.id.nickname);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
            text = (TextView) itemView.findViewById(R.id.textPost);
            likesNum = (TextView) itemView.findViewById(R.id.likesNum);
            commentsNum = (TextView) itemView.findViewById(R.id.commentsNum);
            postID = (TextView) itemView.findViewById(R.id.postID);
        }
    }





}
