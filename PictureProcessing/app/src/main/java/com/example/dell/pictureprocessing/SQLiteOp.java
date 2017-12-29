package com.example.dell.pictureprocessing;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by dell on 2017/11/30.
 */

public class SQLiteOp extends SQLiteOpenHelper {
    public SQLiteOp(Context context)
    {
        super(context,"PictureProcess.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE picture(id INTEGER PRIMARY KEY, picture BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
