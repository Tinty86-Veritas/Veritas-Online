package com.veritas.veritas.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.veritas.veritas.Adapters.entity.User;
import com.veritas.veritas.DB.entity.UserEntityDB;

import java.util.ArrayList;

public class PlayersTable {
    private static final String TABLE_NAME = "players";
    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 3;

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_SEX_ID = "sex_id";

    private static final int NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_NAME = 1;
    private static final int NUM_COLUMN_SEXID = 2;

    private SQLiteDatabase mDataBase;

    private Context context;

    public PlayersTable(Context context) {
        this.context = context;
        OpenHelper mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }

    public long insert(String name, long sex_id) {
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_NAME, name.trim());
        cv.put(COLUMN_SEX_ID, sex_id);
        return mDataBase.insert(TABLE_NAME, null, cv);
    }

    public int update(UserEntityDB userEntityDB) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, userEntityDB.getName());
        cv.put(COLUMN_SEX_ID, userEntityDB.getSexId());
        return mDataBase.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[] {String.valueOf(userEntityDB.getId())});
    }

    public void deleteAll() {
        mDataBase.delete(TABLE_NAME, null, null);
    }

    public void delete(String name) {
        mDataBase.delete(TABLE_NAME, COLUMN_NAME + " = ?", new String[] {name.trim()});
    }

    public UserEntityDB select(long id) {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[] {String.valueOf(id)}, null, null, null);

        mCursor.moveToFirst();
        String name = mCursor.getString(NUM_COLUMN_NAME);
        long sex_id = Long.parseLong(mCursor.getString(NUM_COLUMN_SEXID));
        return new UserEntityDB(id, name, sex_id);
    }

    public ArrayList<User> selectAll() {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, null, null, null, null, null);

        ArrayList<User> arr = new ArrayList<User>();
        SexesTable sexesTable = new SexesTable(context);
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                String name = mCursor.getString(NUM_COLUMN_NAME);
                long sex_id = Long.parseLong(mCursor.getString(NUM_COLUMN_SEXID));
                String sex = sexesTable.select(sex_id);
                arr.add(new User(name, sex));
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
                    COLUMN_NAME+ " TEXT NOT NULL UNIQUE, " +
                    COLUMN_SEX_ID + " INTEGER NOT NULL," +
                    "FOREIGN KEY (" + COLUMN_SEX_ID + ") REFERENCES sexes(id)" + ");";
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

}
