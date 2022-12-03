package com.opensource.ganada;

import java.io.Serializable;

public class PostItem implements Serializable {
    private String post_key;
    private String title;
    private String writer;
    private String content;
    private String date;
    private String like;
    private String role;
    private boolean annoymity;

    public PostItem() {
    }

    public PostItem(String post_key, String title, String writer, String content, String date, String like, String role, boolean annoymity) {
        this.post_key = post_key;
        this.title = title;
        this.writer = writer;
        this.content = content;
        this.date = date;
        this.like = like;
        this.role = role;
        this.annoymity = annoymity;
    }

    public String getPost_key() {
        return post_key;
    }

    public void setPost_key(String post_key) {
        this.post_key = post_key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public boolean isAnnoymity() {
        return annoymity;
    }

    public void setAnnoymity(boolean annoymity) {
        this.annoymity = annoymity;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
