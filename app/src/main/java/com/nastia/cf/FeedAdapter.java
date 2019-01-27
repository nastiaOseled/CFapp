package com.nastia.cf;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Post> mPostsList;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

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
                textView.setText("13 תגובות");
                context = imageView.getContext();
                id = context.getResources().getIdentifier(menuActivity.IMAGE, "drawable", context.getPackageName());
                imageView.setImageResource(id);

                ArrayList<Comment> c = p.getComments();
                for (int i = 0; i < c.size(); i++) {
                //    View v=new View();
                  //  tvh.commentsLayout.addView(view, R.layout.item_comment);
                }

                break;
            case 1:
                ImageViewHolder ivh = (ImageViewHolder) viewHolder;
                textView = ivh.nickname;
                textView.setText(p.getNickname());
                textView = ivh.date;
                textView.setText(p.getDate() + "");
                textView = ivh.time;
                textView.setText(p.getTime() + "");
                textView=ivh.text;
               // textView.setText(p.getPostText()+"");
                String imageName = (p.getImageName());
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
        ImageView imageLogo;
        public TextView nickname;
        public TextView date;
        public TextView time;
        public TextView text;
        public TextView likesNum;
        public TextView commentsNum;
        public RecyclerView commentsList;

        List<Comment> cA=new ArrayList<>();



        public TextViewHolder(View itemView) {
            super(itemView);
            Comment co=new Comment("nichname", "23/1/19", "12:22", "blalala");
            cA.add(co);
            CommentsAdapter adapter=new CommentsAdapter(cA);
            //commentsList.setAdapter(adapter);
            // Set layout manager to position the items
            //commentsList.setLayoutManager(new LinearLayoutManager(FeedActivity.class));




            imageLogo = (ImageView) itemView.findViewById(R.id.imageLogo);
            nickname = (TextView) itemView.findViewById(R.id.nickname);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
            text = (TextView) itemView.findViewById(R.id.text);
            likesNum = (TextView) itemView.findViewById(R.id.likesNum);
            commentsNum = (TextView) itemView.findViewById(R.id.commentsNum);
            commentsList = (RecyclerView) itemView.findViewById(R.id.commentLayout);
            //commentsList.setVisibility(View.GONE);
            commentsNum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    commentsList.setVisibility(View.VISIBLE);
                    if(commentsList.getVisibility()==View.GONE)
                        commentsList.setVisibility(View.VISIBLE);
                    else
                        commentsList.setVisibility(View.GONE);
                }
            });
        }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePost;
        ImageView imageLogo;
        public TextView nickname;
        public TextView date;
        public TextView time;
        public TextView text;

        public ImageViewHolder(View itemView) {

            super(itemView);

            imagePost = (ImageView) itemView.findViewById(R.id.imagePost);
            imageLogo = (ImageView) itemView.findViewById(R.id.imageLogo);
            nickname = (TextView) itemView.findViewById(R.id.nickname);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }

}
