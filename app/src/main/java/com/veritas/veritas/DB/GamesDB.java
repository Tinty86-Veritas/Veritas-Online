package com.veritas.veritas.DB;

import static com.veritas.veritas.Util.PublicVariables.getModes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class GamesDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "games.db";
    private static final int DATABASE_VERSION = 1;


    private static final String TABLE_TRUTH = "truth";
    private static final String TABLE_DARE = "dare";
    private static final String TABLE_NEVEREVER = "neverEver";
    private static final String CREATE_TABLE_TRUTH =
            "CREATE TABLE IF NOT EXISTS " +
                    TABLE_TRUTH +
                    " (" + "title TEXT PRIMARY KEY, " +
                    "question_num INTEGER DEFAULT 5);";

    private static final String CREATE_TABLE_DARE =
            "CREATE TABLE IF NOT EXISTS " +
                    TABLE_DARE +
                    " (" + "title TEXT PRIMARY KEY, " +
                    "question_num INTEGER DEFAULT 5);";

    private static final String CREATE_TABLE_NEVEREVER =
            "CREATE TABLE IF NOT EXISTS " +
                    TABLE_NEVEREVER +
                    " (" + "title TEXT PRIMARY KEY, " +
                    "question_num INTEGER DEFAULT 5);";

    public GamesDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private long insertIntoGame(String title, String game, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put("title", title.trim());
        cv.put("question_num", 5);
        return db.insert(game, null, cv);
    }

//    public long updateTruth(String title, int num) {
//        return updateGame(title, num, TABLE_TRUTH);
//    }
//
//    public long updateDare(String title, int num) {
//        return updateGame(title, num, TABLE_DARE);
//    }
//
//    public long updateNeverEver(String title, int num) {
//        return updateGame(title, num, TABLE_NEVEREVER);
//    }

    public int updateGame(String game, String mode, int num) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", mode.trim());
        cv.put("question_num", num);
        return db.update(game, cv, "title = ?", new String[] {mode});
    }

//    public int selectFromTruth(String title) {
//        return selectFromGame(title, TABLE_TRUTH);
//    }
//
//    public int selectFromDare(String title) {
//        return selectFromGame(title, TABLE_DARE);
//    }
//
//    public int selectFromNeverEver(String title) {
//        return selectFromGame(title, TABLE_NEVEREVER);
//    }

    public int selectFromGame(String game, String mode) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCursor = db.query(game, new String[]{"question_num"}, "title = ?", new String[] {mode},
                null, null, null);

        if (mCursor.moveToFirst()) {
            int num = mCursor.getInt(0);
            mCursor.close();
            return num;
        } else {
            mCursor.close();
            return 5;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TRUTH);
        db.execSQL(CREATE_TABLE_DARE);
        db.execSQL(CREATE_TABLE_NEVEREVER);

        for (String mode : getModes()) {
            insertIntoGame(mode, TABLE_TRUTH, db);
            insertIntoGame(mode, TABLE_DARE, db);
            insertIntoGame(mode, TABLE_NEVEREVER, db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) { }
}
