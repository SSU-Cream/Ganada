package com.opensource.ganada;


import java.io.Serializable;
import java.util.Date;

public class Score implements Serializable {
    private String practiceTime;
    private int score;

    public Score() {
    }

    public Score(String practiceTime, int score) {
        this.practiceTime = practiceTime;
        this.score = score;
    }

    public String getPracticeTime() {
        return practiceTime;
    }

    public void setPracticeTime(String practiceTime) {
        this.practiceTime = practiceTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
