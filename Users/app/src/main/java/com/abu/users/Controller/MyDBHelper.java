package com.abu.users.Controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "sqlite.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_PHONE = "Phone";
    public static final String COLUMN_GENDER = "Gender";
    public static final String COLUMN_ADDRESS = "Address";

    public static final String CLOUD_DB_TABLE_NAME = "user";
    public static final String CLOUD_DB_COLUMN_ID = "id";
    public static final String CLOUD_DB_COLUMN_NAME = "name";
    public static final String CLOUD_DB_COLUMN_PHONE = "phone";
    public static final String CLOUD_DB_COLUMN_GENDER = "gender";
    public static final String CLOUD_DB_COLUMN_ADDRESS = "address";


    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_PHONE + " TEXT, "
                + COLUMN_GENDER + " INTEGER, "
                + COLUMN_ADDRESS + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


}
