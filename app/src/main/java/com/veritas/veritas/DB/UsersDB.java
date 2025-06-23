package com.veritas.veritas.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.veritas.veritas.Adapters.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// TODO: Нужно сделать защиту (хоть базовую) от SQL-инъекций

public class UsersDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_PLAYERS = "players";
    private static final String TABLE_SEXES = "sexes";

    private static final String PLAYERS_COLUMN_NAME = "name";
    private static final String PLAYERS_COLUMN_SEX_ID = "sex_id";

    private static final int PLAYERS_NUM_COLUMN_NAME = 1;
    private static final int PLAYERS_NUM_COLUMN_SEX_ID = 2;

    private static final String SEXES_COLUMN_ID = "id";

    private static final int SEXES_NUM_COLUMN_TITLE = 1;

    private static final String CREATE_TABLE_SEXES =
            "CREATE TABLE IF NOT EXISTS " +
                    TABLE_SEXES + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title VARCHAR(7) NOT NULL);";

    private static final String CREATE_TABLE_PLAYERS =
            "CREATE TABLE IF NOT EXISTS " +
                     TABLE_PLAYERS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "sex_id INTEGER NOT NULL, " +
                    "UNIQUE(name, sex_id), " +
                    "FOREIGN KEY (sex_id) REFERENCES sexes(id));";

    // It seems like a workaround
    private Map<String, Long> sexesMap = new HashMap<>();

    public UsersDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        sexesMap.put("Male", 1L);
        sexesMap.put("Female", 2L);
    }

    public long insertIntoPlayers(String name, int sex_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(PLAYERS_COLUMN_NAME, name.trim());
        cv.put(PLAYERS_COLUMN_SEX_ID, sex_id);
        return db.insert(TABLE_PLAYERS, null, cv);
    }

    public void deleteFromPlayers(String name, String sex) {
        SQLiteDatabase db = this.getWritableDatabase();

        Long sex_id = sexesMap.get(sex);

        String whereClause = PLAYERS_COLUMN_NAME + " = ? AND "
                + PLAYERS_COLUMN_SEX_ID + " = ?";

        db.delete(TABLE_PLAYERS, whereClause, new String[] {name.trim(), String.valueOf(sex_id)});
    }

    public String selectFromSexes(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCursor = db.query(TABLE_SEXES, null, SEXES_COLUMN_ID + " = ?", new String[] {String.valueOf(id)}, null, null, null);

        mCursor.moveToFirst();
        String title = mCursor.getString(SEXES_NUM_COLUMN_TITLE);
        return title;
    }

    public ArrayList<User> selectAllFromPlayers() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCursor = db.query(TABLE_PLAYERS, null, null, null, null, null, null);

        ArrayList<User> arr = new ArrayList<>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                String name = mCursor.getString(PLAYERS_NUM_COLUMN_NAME);
                long sex_id = Long.parseLong(mCursor.getString(PLAYERS_NUM_COLUMN_SEX_ID));
                String sex = selectFromSexes(sex_id);
                arr.add(new User(name, sex));
            } while (mCursor.moveToNext());
        }
        return arr;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SEXES);

        db.execSQL("INSERT INTO sexes (title) VALUES ('Male'), ('Female');");

        db.execSQL(CREATE_TABLE_PLAYERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEXES);
        onCreate(db);
    }
}
