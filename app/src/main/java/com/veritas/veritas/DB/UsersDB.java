package com.veritas.veritas.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UsersDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 3;

    private static final String CREATE_TABLE_SEXES =
            "CREATE TABLE sexes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title VARCHAR(7) NOT NULL);";

    private static final String CREATE_TABLE_PLAYERS =
            "CREATE TABLE players (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL UNIQUE, " +
                    "sex_id INTEGER NOT NULL, " +
                    "FOREIGN KEY (sex_id) REFERENCES sexes(id));";

    public UsersDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void dropDB(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SEXES);

        db.execSQL("INSERT INTO sexes (title) VALUES ('Male'), ('Female');");

        db.execSQL(CREATE_TABLE_PLAYERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS players");
    }
}
