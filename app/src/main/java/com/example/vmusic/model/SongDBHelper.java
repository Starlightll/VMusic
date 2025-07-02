package com.example.vmusic.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SongDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "songDB.db";
    private static final int DB_VERSION = 1;

    public SongDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Songs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "mainArtist TEXT," +
                "language TEXT," +
                "releaseDate TEXT," +
                "primaryGenre TEXT," +
                "secondaryGenre TEXT," +
                "isExplicit INTEGER," +
                "isReleased INTEGER," +
                "urlImage TEXT," +
                "urlAudio TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Songs");
        onCreate(db);
    }
}