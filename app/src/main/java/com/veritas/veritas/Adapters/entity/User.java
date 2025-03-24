package com.veritas.veritas.Adapters.entity;

public class User {
    private final String name;
    private final String sex;

    public User(String name, String sex) {
        this.name = name;
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }
}