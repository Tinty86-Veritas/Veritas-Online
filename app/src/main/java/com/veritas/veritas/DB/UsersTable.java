package com.veritas.veritas.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.veritas.veritas.DB.entity.User;

import java.util.ArrayList;

public class UsersTable {
    private static final String DATABASE_NAME = "veritas.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "users";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_SEX = "Sex_id";

    private static final int NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_NAME = 1;
    private static final int NUM_COLUMN_SEX = 2;

    private SQLiteDatabase mDataBase;

    public UsersTable(Context context) {
        OpenHelper mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }

    public long insert(String name,String sex) {
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_SEX, sex);
        return mDataBase.insert(TABLE_NAME, null, cv);
    }

    public int update(User user) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, user.getName());
        cv.put(COLUMN_SEX, user.getSex());
        return mDataBase.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[] {String.valueOf(user.getId())});
    }

    public void deleteAll() {
        mDataBase.delete(TABLE_NAME, null, null);
    }

    public void delete(long id) {
        mDataBase.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] {String.valueOf(id)});
    }

    public User select(long id) {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[] {String.valueOf(id)}, null, null, null);

        mCursor.moveToFirst();
        String name = mCursor.getString(NUM_COLUMN_NAME);
        String sex = mCursor.getString(NUM_COLUMN_SEX);
        return new User(id, name, sex);
    }

    public ArrayList<User> selectAll() {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, null, null, null, null, null);

        ArrayList<User> arr = new ArrayList<User>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                String name = mCursor.getString(NUM_COLUMN_NAME);
                String sex = mCursor.getString(NUM_COLUMN_SEX);
                arr.add(new User(id, name, sex));
            } while (mCursor.moveToNext());
        }
        return arr;
    }

    public void drop() {
        mDataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    private class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {

            String query = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME+ " TEXT NOT NULL, " +
                    COLUMN_SEX + " INTEGER NOT NULL," +
                    "FOREIGN KEY (" + COLUMN_SEX + ") REFERENCES sexes(id)" + ");";
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

}
