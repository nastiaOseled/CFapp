package com.nastia.cf;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private List<Post> mPosts;

    public PostsAdapter(List<Post> mPosts) {
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View postView = inflater.inflate(R.layout.item_nutrition, viewGroup, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder viewHolder, int i) {
        // Get the data model based on position
        Post p = mPosts.get(i);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nickname;
        textView.setText(3000000+"");
//        textView = viewHolder.date;
//        textView.setText(p.getDate());

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

      //  ImageView image;
        public TextView nickname;
//        public TextView date;
//        public TextView time;
//        public TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

          //  image = (ImageView) itemView.findViewById(R.id.imageLogo);
            nickname = (TextView) itemView.findViewById(R.id.nutrition_calories);
//            date = (TextView) itemView.findViewById(R.id.date);
//            time = (TextView) itemView.findViewById(R.id.time);
//            text = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
