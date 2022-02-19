package com.opensource.ganada;

public class UserModel {
    public String name;
    public String birth;
    public String uid;

    public UserModel() {}

    public UserModel(String name, String birth, String uid) {
        this.uid = uid;
        this.name = name;
        this.birth = birth;
    }
}
