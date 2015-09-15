package com.database.rentalapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Sainath on 22-07-2015.
 */
public class RentalDatabaseHelper extends SQLiteOpenHelper {

    private final static String POST_TABLE = "CREATE TABLE IF NOT EXISTS POST_TABLE (id INTEGER PRIMARY KEY AUTOINCREMENT ," +
            "name TEXT NOT NULL, description TEXT NOT NULL, price INTEGER NOT NULL, category TEXT NOT NULL," +
            "subcategory TEXT, POST_LATITUDE INTEGER, POST_LONGITUDE INTEGER, POST_ADDRESS TEXT, duration TEXT, " +
            "thumbnailList BLOB, userName TEXT) ";

    private static final String USER_DETAILS = "CREATE TABLE IF NOT EXISTS USER_DETAILS (userName TEXT, password TEXT, firstName TEXT, " +
            "lastName TEXT, initials TEXT, birthDate INTEGER, gender TEXT, emailId TEXT, mobileNumber TEXT, " +
            "officeNumber TEXT, profileImage TEXT) ";

    private static final String USER_ADDRESS = "CREATE TABLE IF NOT EXISTS USER_ADDRESS (userName TEXT, houseNumber TEXT, addressLine1 TEXT," +
            "addressLine2 TEXT, addressLine3 TEXT, addressLine4 TEXT, addressLine5 TEXT, Latitude TEXT, Longitude TEXT)";

    public RentalDatabaseHelper(Context context) {
        super(context, "RentalPost", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(POST_TABLE);
        db.execSQL(USER_DETAILS);
        db.execSQL(USER_ADDRESS);
        Log.i("Sainath", "On Create of Rental DB Helper");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public SQLiteDatabase getRentalDatabase() {
        return this.getWritableDatabase();
    }
}