package com.rwthmcc103;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDbOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "MMAApp";
    private static final String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                "KEY_FILE_NAME" + " TEXT, " +
                "TITEL" + " TEXT, " +
                "DESCRIPTION" + " TEXT, " +
                "TAGS" + " TEXT);";

    MyDbOpenHelper(Context context) {
        super(context, "DATABASE_NAME" , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}