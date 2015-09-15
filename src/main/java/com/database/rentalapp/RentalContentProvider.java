package com.database.rentalapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class RentalContentProvider extends ContentProvider {
    private SQLiteDatabase mRentalDB = null;
    private Context mContext = null;
    private String mTableName = null;

    public RentalContentProvider() {

        Log.i("Sainath", "Constructor of Rental Database");
    }

    public void setDBTable(String tableName) {
        mTableName = tableName;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (mRentalDB != null) {
            mTableName = uri.toString();
            mTableName = mTableName.substring(mTableName.lastIndexOf('/') + 1, mTableName.length());
            return mRentalDB.delete(mTableName, selection, selectionArgs);
        } else
            Log.e("Sainath", "Delete Failed due to Table Name");

        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        Log.i("Sainath", "On insert of Rental Database");
        if (mRentalDB != null) {
            mTableName = uri.toString();
            mTableName = mTableName.substring(mTableName.lastIndexOf('/') + 1, mTableName.length());
            mRentalDB.insert(mTableName, null, values);
        } else
            Log.e("Sainath", "Insert Failed due to Table Name");

        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        Log.i("Sainath", "On Create of Rental Database");
        mRentalDB = new RentalDatabaseHelper(getContext()).getRentalDatabase();
        Log.i("Sainath", "On Created of Rental Database");
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        if (mRentalDB != null) {
            mTableName = uri.toString();
            mTableName = mTableName.substring(mTableName.lastIndexOf('/') + 1, mTableName.length());
            return mRentalDB.query(mTableName, projection, selection, selectionArgs, null, null, sortOrder);
        } else
            Log.e("Sainath", "Query Failed due to Table Name");

        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        if (mRentalDB != null) {
            mTableName = uri.toString();
            mTableName = mTableName.substring(mTableName.lastIndexOf('/') + 1, mTableName.length());
            return mRentalDB.update(mTableName, values, selection, selectionArgs);
        } else
            Log.e("Sainath", "Update Failed due to Table Name");

        return 0;
    }
}
