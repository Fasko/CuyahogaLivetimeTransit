package com.mfsa.cuyahogalivetimetransit;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

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

    public ArrayList<String> getRoutes(){
        ArrayList<String> routeList = new ArrayList<>();
        c = db.rawQuery("Select Distinct Route from RDS_table;", new String[]{});
        while (c.moveToNext()){
            routeList.add(c.getString(0));
        }
        return routeList;
    }


    public ArrayList<String> getDirections(String route){
        ArrayList<String> directionList = new ArrayList<>();
        c = db.rawQuery("Select Distinct Direction from RDS_table where Route = ?;", new String[]{route});
        while (c.moveToNext()){
            directionList.add(c.getString(0));
        }
        return directionList;
    }

    public ArrayList<String> getStops(String route, String direction){
        ArrayList<String> stopList = new ArrayList<>();
        c = db.rawQuery("Select Distinct Stop from RDS_table where Route = ? and Direction = ?;", new String[]{route, direction});
        while (c.moveToNext()){
            stopList.add(c.getString(0));
        }
        return stopList;
    }

    public String getURL(String route, String direction, String stop){
        c = db.rawQuery("Select URL from RDS_table where Route = ? and Direction = ? and Stop = ?;", new String[]{route, direction, stop});
        c.moveToNext();
        
        return (c.getString(0));
    }

}
