package com.englite3.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String WORD_TABLE_NAME = "WORD";
    public static final String EN = "EN", CN = "CN", PRON = "PRONOUNCE", COMBO = "COMBO", LEVEL = "LEVEL", E = "E", FLAG = "FLGA";

    public static final String WORD_TABLE_CREATE = new StringBuilder()
        .append(" CREATE TABLE " + WORD_TABLE_NAME)
        .append(" ( "+ EN + " TEXT PRIMARY KEY ")
        .append(", " + CN + " TEXT ")
        .append(", " + PRON + " TEXT ")
        .append(", " + COMBO +" TEXT ")
        .append(", " + LEVEL + " INTEGER ")
        .append(", " + E + " INTEGER ")
        .append(", " + FLAG + " INTEGER ")
        .append(" ); ")
        .toString();

    public static final String WORD_TABLE_DELETE = "DROP TABLE " + WORD_TABLE_NAME + " ;";

    public DbOpenHelper(Context context, String dbname) {
        super(context, dbname, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WORD_TABLE_CREATE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}