package com.veritas.veritas.DB.entity;

import java.io.Serializable;

public class UserEntityDB implements Serializable {
    private final long id;
    private final String name;
    private final long sex_id;

    public UserEntityDB(long id, String name, long sex_id) {
        this.id = id;
        this.name = name;
        this.sex_id = sex_id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getSexId() {
        return sex_id;
    }
}
