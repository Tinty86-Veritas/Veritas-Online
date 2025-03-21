package com.veritas.veritas.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.veritas.veritas.DB.entity.Sex;

public class SexesTable {
    private static final String DATABASE_NAME = "veritas.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "sexes";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "Title";

    private static final int NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_TITLE = 1;

    private SQLiteDatabase mDataBase;

    public SexesTable(Context context) {
        SexesTable.OpenHelper mOpenHelper = new SexesTable.OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }

    public Sex select(long id) {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[] {String.valueOf(id)}, null, null, null);

        mCursor.moveToFirst();
        String title = mCursor.getString(NUM_COLUMN_TITLE);
        return new Sex(id, title);
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

//            String drop_query = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
//            db.execSQL(drop_query);

            String create_query = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " VARCHAR(7) NOT NULL" + ");";
            db.execSQL(create_query);

            String insert_query = "INSERT INTO " + TABLE_NAME + "(Title)"
                    + "VALUES" + "('Male'), ('Female');";

            db.execSQL(insert_query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

}

