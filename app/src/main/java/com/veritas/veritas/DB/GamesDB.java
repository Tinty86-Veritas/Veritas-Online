package com.veritas.veritas.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class GamesDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "games.db";
    private static final int DATABASE_VERSION = 1;


    private static final String TABLE_GAMES_QUESTION_NUM = "games_question_num";
    private static final String CREATE_TABLE_GAMES_QUESTION_NUM =
            "CREATE TABLE IF NOT EXISTS " +
                    TABLE_GAMES_QUESTION_NUM +
                    " (" + "title TEXT PRIMARY KEY, " +
                    "question_num INTEGER DEFAULT 5);";

    public GamesDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public long insertIntoGamesQuestionNum(String title, int num) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title.trim());
        cv.put("question_num", num);
        return db.insert(TABLE_GAMES_QUESTION_NUM, null, cv);
    }

    public long updateGamesQuestionNum(String title, int num) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title.trim());
        cv.put("question_num", num);
        return db.update(CREATE_TABLE_GAMES_QUESTION_NUM, cv, "title = ?", new String[] {title});
    }

    public int selectFromGamesQuestionNum(String title) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCursor = db.query(TABLE_GAMES_QUESTION_NUM, null, "title = ?", new String[] {title}, null, null, null);

        mCursor.moveToFirst();
        int num = mCursor.getInt(0);
        return num;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_GAMES_QUESTION_NUM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) { }
}
