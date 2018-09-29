package com.example.jadhosn.group9_a3;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

/**
 * Created by JadHosn on 3/26/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_name = "db9.db";
    public static final String TABLE_NAME = "data";
    public static final String COLUMN_ACCELEROMETER_X = "Accel_X_";
    public static final String COLUMN_ACCELEROMETER_Y = "Accel_Y_";
    public static final String COLUMN_ACCELEROMETER_Z = "Accel_Z_";

    private String Generate_50_columns ()
    {
        String query ="";

        for (int i=1;i<=50;i++)
        {
            String x = COLUMN_ACCELEROMETER_X+Integer.toString(i)+" FLOAT,";
            String y = COLUMN_ACCELEROMETER_Y+Integer.toString(i)+" FLOAT,";
            String z = COLUMN_ACCELEROMETER_Z+Integer.toString(i)+" FLOAT,";
            query += x+y+z;
        }
        return query;
    }

    String QUERY_CREATE_TABLE = "(ID TEXT,"+Generate_50_columns()+"Activity_Label INTEGER);";

    public DatabaseHelper(Context context)
    {
        super(context, DB_name, null, 2);
        //super(context, Environment.getExternalStorageDirectory() + File.separator + "Android"+File.separator+"Data"+File.separator+"CSE535_ASSIGNMENT3"+File.separator+DB_name, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE= "CREATE TABLE data "+QUERY_CREATE_TABLE;
        db.execSQL(SQL_CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists data");
        onCreate(db);
    }


    public Cursor getAllData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from data order by ID desc" ,null);
        return result;
    }


    public void insertRow(String row)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO data VALUES (" + row + ");");
    }

    public void deleteTestTableContent()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM data"); // make sure to check whether this works
    }


}
