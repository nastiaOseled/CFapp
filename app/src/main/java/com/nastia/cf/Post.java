package com.nastia.cf;

import java.util.ArrayList;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return likes == post.likes &&
                type == post.type &&
                Objects.equals(userId, post.userId) &&
                Objects.equals(nickname, post.nickname) &&
                Objects.equals(date, post.date) &&
                Objects.equals(time, post.time) &&
                Objects.equals(comments, post.comments) &&
                Objects.equals(postText, post.postText);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId, nickname, date, time, likes, comments, postText, type);
    }
}
