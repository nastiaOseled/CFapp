package com.nastia.cf;

import java.util.ArrayList;

class Post {

    String userId;
    String nickname;
    String date;
    String time;
    long likes;
    ArrayList <Comment> comments;
    String postText;
    long type;

    public Post(String userId, String nickname, String date, String time, long likes, ArrayList<Comment> comments, String postText, long type) {
        this.userId = userId;
        this.nickname = nickname;
        this.date = date;
        this.time = time;
        this.likes = likes;
        this.comments = comments;
        this.postText = postText;
        this.type = type;
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
}
