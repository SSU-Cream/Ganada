package com.opensource.ganada;

public class StudentItem {
    private String name;
    private int age;
    private String detailedRecord;

    public StudentItem(String name, int age, String detailedRecord) {
        this.name = name;
        this.age = age;
        this.detailedRecord = detailedRecord;
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

    public String getDetailedRecord() {
        return detailedRecord;
    }

    public void setDetailedRecord(String detailedRecord) {
        this.detailedRecord = detailedRecord;
    }
}
