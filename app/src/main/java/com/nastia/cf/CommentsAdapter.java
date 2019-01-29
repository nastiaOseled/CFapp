package com.nastia.cf;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    List<Comment> mCommentsList;

    public CommentsAdapter(List<Comment> commentsList) {
        mCommentsList = commentsList;
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_comment, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder viewHolder, int i) {

        Comment c=mCommentsList.get(i);

        TextView textView = viewHolder.comNickname;
        textView.setText(c.getNickname()+"");
        textView = viewHolder.comDate;
        textView.setText(c.getDate()+"");
        textView = viewHolder.comTime;
        textView.setText(c.getTime()+"");
        textView = viewHolder.comText;
        textView.setText(c.getText()+"");
    }

    @Override
    public int getItemCount() {
        return this.mCommentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView comNickname;
        public TextView comDate;
        public TextView comTime;
        public TextView comText;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            comNickname = (TextView) itemView.findViewById(R.id.comNickname);
            comDate = (TextView) itemView.findViewById(R.id.comDate);
            comTime = (TextView) itemView.findViewById(R.id.comTime);
            comText = (TextView) itemView.findViewById(R.id.comText);
        }
    }
}
