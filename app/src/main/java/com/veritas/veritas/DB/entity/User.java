package com.veritas.veritas.DB.entity;

import java.io.Serializable;

public class User implements Serializable {
    private final long id;
    private final String name;
    private final String sex;

    public User(long id, String name, String sex) {
        this.id = id;
        this.name = name;
        this.sex = sex;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }
}
