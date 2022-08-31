package com.opensource.ganada;

public class LearningContents {

    private String contents;
    private String ex1_imgUrl;
    private String ex1_text;
    private String ex2_imgUrl;
    private String ex2_text;
    private String contentsText;
    private String videoUrl;

    public LearningContents() {

    }

    public LearningContents(
            String contents,
            String ex1_imgUrl,
            String ex1_text,
            String ex2_imgUrl,
            String ex2_text,
            String contentsText,
            String videoUrl) {
        this.contents = contents;
        this.ex1_imgUrl = ex1_imgUrl;
        this.ex1_text = ex1_text;
        this.ex2_imgUrl = ex2_imgUrl;
        this.ex2_text = ex2_text;
        this.contentsText = contentsText;
        this.videoUrl = videoUrl;
    }

    public String getContents() {
        return contents;
    }

    public String getEx1_imgUrl() {
        return ex1_imgUrl;
    }

    public String getEx1_text() {
        return ex1_text;
    }

    public String getEx2_imgUrl() {
        return ex2_imgUrl;
    }

    public String getEx2_text() {
        return ex2_text;
    }

    public String getContentsText() {
        return contentsText;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}
