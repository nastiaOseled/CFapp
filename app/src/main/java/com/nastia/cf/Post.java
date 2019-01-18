package com.nastia.cf;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Objects;

class Post {

    String postId;
    String userId;
    String nickname;
    String date;
    String time;
    long likes;
    ArrayList <Comment> comments;
    String postText;
    long type;

    public Post(String postId, String userId, String nickname, String date, String time, long likes, ArrayList<Comment> comments, String postText, long type) {
        this.postId=postId;
        this.userId = userId;
        this.nickname = nickname;
        this.date = date;
        this.time = time;
        this.likes = likes;
        this.comments = comments;
        this.postText = postText;
        this.type = type;
    }

    public String getPostId() {
        return postId;
    }

    public long getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public long getLikes() {
        return likes;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public String getPostText() {
        return postText;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(postId, post.postId);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {

        return Objects.hash(postId);
    }
}
