package com.veritas.veritas.DB.entity;

import java.io.Serializable;

public class SexEntityDB implements Serializable {
    private final long id;
    private final String title;

    public SexEntityDB(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

}
