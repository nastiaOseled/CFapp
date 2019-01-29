package com.nastia.cf;

public class Comment {
    String nickname;
    String date;
    String time;
    String text;

    public Comment(String nickname, String date, String time, String text) {
        this.nickname = nickname;
        this.date = date;
        this.time = time;
        this.text = text;
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

    public String getText() {
        return text;
    }
}
