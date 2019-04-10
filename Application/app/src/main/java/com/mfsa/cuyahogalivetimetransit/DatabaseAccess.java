package com.mfsa.cuyahogalivetimetransit;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    Cursor c = null;


    private DatabaseAccess(Context context){
        this.openHelper=new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context){
        if(instance == null){
            instance = new DatabaseAccess(context);

        }
        return instance;
    }

    public void open(){
        this.db = openHelper.getWritableDatabase();

    }

    public void close(){
        if(db != null){
            this.db.close();
        }
    }


    // TODO implement custom queries, this one will Give the URLS where the Direction = East
    public String getAddress(String name){
        c = db.rawQuery("Select * From RDS_table where Direction = ?;", new String[]{"East"});
        StringBuffer buffer = new StringBuffer();
        while(c.moveToNext()){
            String address = c.getString(3);
            buffer.append(" "+address);
        }
        return buffer.toString();
    }
}
