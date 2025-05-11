package com.veritas.veritas.DB;

import static com.veritas.veritas.Util.PublicVariables.getGames;
import static com.veritas.veritas.Util.PublicVariables.getModes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class GamesDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "games.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_GAMES = "games";
    private static final String TABLE_MODES = "modes";
    private static final String TABLE_GAME_MODES = "game_modes";
    private static final String TABLE_RESPONSES = "responses";

    private static final String CREATE_TABLE_GAMES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_GAMES + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL UNIQUE" +
                    ");";

    private static final String CREATE_TABLE_MODES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_MODES + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL UNIQUE" +
                    ");";

    private static final String CREATE_TABLE_GAME_MODES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_GAME_MODES + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "game_id INTEGER NOT NULL REFERENCES " + TABLE_GAMES + "(id) ON DELETE CASCADE, " +
                    "mode_id INTEGER NOT NULL REFERENCES " + TABLE_MODES + "(id) ON DELETE CASCADE, " +
                    "request_num INTEGER NOT NULL DEFAULT 5, " +
                    "UNIQUE(game_id, mode_id)" +
                    ");";

    private static final String CREATE_TABLE_RESPONSES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_RESPONSES + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "game_mode_id INTEGER NOT NULL REFERENCES " + TABLE_GAME_MODES + "(id) ON DELETE CASCADE, " +
                    "type TEXT NOT NULL CHECK(type IN ('like','dislike')), " +
                    "response_text TEXT NOT NULL" +
                    ");";

    public GamesDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_GAMES);
        db.execSQL(CREATE_TABLE_MODES);
        db.execSQL(CREATE_TABLE_GAME_MODES);
        db.execSQL(CREATE_TABLE_RESPONSES);

        String[] games = getGames();
        for (String game : games) {
            ContentValues cv = new ContentValues();
            cv.put("name", game);
            db.insert(TABLE_GAMES, null, cv);
        }

        for (String mode : getModes()) {
            ContentValues cv = new ContentValues();
            cv.put("name", mode);
            db.insert(TABLE_MODES, null, cv);
        }

        for (String game : games) {
            for (String mode : getModes()) {
                db.execSQL(
                        "INSERT OR IGNORE INTO " + TABLE_GAME_MODES + " (game_id, mode_id, request_num) " +
                                "VALUES ((SELECT id FROM " + TABLE_GAMES + " WHERE name=?), " +
                                "(SELECT id FROM " + TABLE_MODES + " WHERE name=?), 5)",
                        new Object[]{game, mode}
                );
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESPONSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME_MODES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MODES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
        onCreate(db);
    }

    // Retrieve request_num for a given game and mode
    public int getRequestNum(String gameName, String modeName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT request_num FROM " + TABLE_GAME_MODES + " gm " +
                        "JOIN " + TABLE_GAMES + " g ON g.id = gm.game_id " +
                        "JOIN " + TABLE_MODES + " m ON m.id = gm.mode_id " +
                        "WHERE g.name = ? AND m.name = ?",
                new String[] {gameName, modeName}
        );
        int result = -1;
        if (c.moveToFirst()) {
            result = c.getInt(c.getColumnIndexOrThrow("request_num"));
        }
        c.close();
        Log.d("GamesDB", "getRequestNum: " + result);
        return result;
    }

    // Update request_num using explicit WHERE
    public void updateRequestNum(String gameName, String modeName, int num) {
        Log.d("GamesDB", String.format(
                "updateRequestNum: gameName=%s, modeName=%s, num=%d", gameName, modeName, num
        ));
        SQLiteDatabase db = getWritableDatabase();
        String sql = "UPDATE " + TABLE_GAME_MODES + " SET request_num = ? " +
                "WHERE game_id = (SELECT id FROM " + TABLE_GAMES + " WHERE name = ?) " +
                "AND mode_id = (SELECT id FROM " + TABLE_MODES + " WHERE name = ?)";
        db.execSQL(sql, new Object[] {num, gameName, modeName});
    }

    // Add a like or dislike response
    public long addResponse(String gameName, String modeName, String type, String text) {
        SQLiteDatabase db = getWritableDatabase();
        // get game_mode_id
        Cursor c = db.rawQuery(
                "SELECT gm.id FROM " + TABLE_GAME_MODES + " gm " +
                        "JOIN " + TABLE_GAMES + " g ON g.id = gm.game_id " +
                        "JOIN " + TABLE_MODES + " m ON m.id = gm.mode_id " +
                        "WHERE g.name = ? AND m.name = ?", new String[]{gameName, modeName}
        );
        long gameModeId = -1;
        if (c != null && c.moveToFirst()) {
            gameModeId = c.getLong(c.getColumnIndexOrThrow("id"));
            c.close();
        }
        if (gameModeId == -1) return -1;

        ContentValues cv = new ContentValues();
        cv.put("game_mode_id", gameModeId);
        cv.put("type", type);
        cv.put("response_text", text);
        return db.insert(TABLE_RESPONSES, null, cv);
    }

    // Fetch responses by type
    public Cursor getResponses(String gameName, String modeName, String type) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT r.response_text FROM " + TABLE_RESPONSES + " r " +
                        "JOIN " + TABLE_GAME_MODES + " gm ON gm.id = r.game_mode_id " +
                        "JOIN " + TABLE_GAMES + " g ON g.id = gm.game_id " +
                        "JOIN " + TABLE_MODES + " m ON m.id = gm.mode_id " +
                        "WHERE g.name = ? AND m.name = ? AND r.type = ?", new String[]{gameName, modeName, type}
        );
    }
}
