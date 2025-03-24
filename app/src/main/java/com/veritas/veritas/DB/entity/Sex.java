package com.veritas.veritas.DB.entity;

import java.io.Serializable;

public class Sex implements Serializable {
    private final long id;
    private final String title;

    public Sex(long id, String title) {
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
