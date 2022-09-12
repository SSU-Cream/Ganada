package com.opensource.ganada;

import java.io.Serializable;

public class StudentItem implements Serializable {
    private int studentNum;
    private String name;
    private int age;
    private String detailedRecord="";
    private int score;

    public StudentItem() {
    }

    public StudentItem(String name, int age, String detailedRecord, int score) {
        this.name = name;
        this.age = age;
        this.detailedRecord = detailedRecord;
        this.score = score;
    }

    public int getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(int studentNum) {
        this.studentNum = studentNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDetailedRecord() {
        return detailedRecord;
    }

    public void setDetailedRecord(String detailedRecord) {
        this.detailedRecord = detailedRecord;
    }
}
