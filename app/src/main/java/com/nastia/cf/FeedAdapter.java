package com.nastia.cf;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FeedAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Post> mPostsList;

    public FeedAdapter(ArrayList<Post> posts) {
        this.mPostsList = posts;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView;
        switch (i){
            case 0:
                postView = inflater.inflate(R.layout.item_post_text, viewGroup, false);
                return new TextViewHolder(postView);
            case 1:
                postView = inflater.inflate(R.layout.item_post_image, viewGroup, false);
                return new ImageViewHolder(postView);
            case 2:
                postView = inflater.inflate(R.layout.item_post_location, viewGroup, false);
                return new LocationViewHolder(postView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        Post p=mPostsList.get(i);
        TextView textView;
        switch (viewHolder.getItemViewType()){
            case 0:
                TextViewHolder tvh=(TextViewHolder) viewHolder;
                textView = tvh.nickname;
                textView.setText(p.getNickname());
                textView=tvh.date;
                textView.setText(p.getDate()+"");
                break;
            case 1:
                ImageViewHolder ivh=(ImageViewHolder) viewHolder;
                textView = ivh.text;
                textView.setText("test image post");
                break;
            case 2:
                LocationViewHolder lvh=(LocationViewHolder) viewHolder;
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        Post p=mPostsList.get(position);
        return (int)p.getType();
    }

    class TextViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public TextView nickname;
        public TextView date;
        public TextView time;
        public TextView text;

        public TextViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.imageLogo);
            nickname = (TextView) itemView.findViewById(R.id.nickname);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public TextView text;

        public ImageViewHolder(View itemView) {

            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.imageView7);
            text = (TextView) itemView.findViewById(R.id.textView22);
        }
    }
        class LocationViewHolder extends RecyclerView.ViewHolder {

            public LocationViewHolder(View itemView) {
                super(itemView);
            }
        }

}
