package com.opensource.ganada;

import java.io.Serializable;

public class UserModel implements Serializable {
    private String name;
    private String birth;
    private String uid;
    private String role;

    public UserModel() {}

    public UserModel(String name, String birth, String uid) {
        this.name = name;
        this.birth = birth;
        this.uid = uid;
    }

    public UserModel(String name, String birth, String uid, String role) {
        this.name = name;
        this.birth = birth;
        this.uid = uid;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
