package com.opensource.ganada;

public class CommentItem {
    private String comment_key;
    private String post_key;
    private String writer;
    private String content;
    private String date;
    private String like;
    private boolean annoymity;

    public CommentItem() {
    }

    public CommentItem(String comment_key, String post_key, String writer, String content, String date, String like, boolean annoymity) {
        this.comment_key = comment_key;
        this.post_key = post_key;
        this.writer = writer;
        this.content = content;
        this.date = date;
        this.like = like;
        this.annoymity = annoymity;
    }

    public String getComment_key() {
        return comment_key;
    }

    public void setComment_key(String comment_key) {
        this.comment_key = comment_key;
    }

    public String getPost_key() {
        return post_key;
    }

    public void setPost_key(String post_key) {
        this.post_key = post_key;
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

    @Override
    public String toString() {
        return "CommentItem{" +
                "comment_key='" + comment_key + '\'' +
                ", post_key='" + post_key + '\'' +
                ", writer='" + writer + '\'' +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                ", like='" + like + '\'' +
                ", annoymity=" + annoymity +
                '}';
    }
}
