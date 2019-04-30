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

    public float[] getLatLong(String stop) {
        c = db.rawQuery("Select stop_lat, stop_lon  from RDS_location where stop_name = ?;", new String[]{stop});
        float[] latLong = {0, 0};
        int numResults = c.getCount();

        if (numResults > 0) {
            c.moveToNext();
            latLong[0] = Float.parseFloat(c.getString(0));
            latLong[1] = Float.parseFloat(c.getString(1));
        }

        return latLong;
    }
}
