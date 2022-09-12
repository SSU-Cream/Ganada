package com.opensource.ganada;

import java.io.Serializable;
import java.util.List;

public class StudentItem implements Serializable {
    private int studentNum;
    private String name;
    private int age;
    private String detailedRecord="";
    private List<Score> scores;

    public StudentItem() {
    }

    public StudentItem(String name, int age, String detailedRecord) {
        this.name = name;
        this.age = age;
        this.detailedRecord = detailedRecord;
    }

    public StudentItem(String name, int age, String detailedRecord, List<Score> scores) {
        this.name = name;
        this.age = age;
        this.detailedRecord = detailedRecord;
        this.scores = scores;
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

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }

    public String getDetailedRecord() {
        return detailedRecord;
    }

    public void setDetailedRecord(String detailedRecord) {
        this.detailedRecord = detailedRecord;
    }
}
