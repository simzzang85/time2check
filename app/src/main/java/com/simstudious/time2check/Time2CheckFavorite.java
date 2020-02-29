package com.simstudious.time2check;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class Time2CheckFavorite extends SQLiteOpenHelper{
/**
 * Table name : TC_HIST
 * Server URL saving and showing
 //* id, seq
 * web_name
 * web_url
 * search_date
 * search_time
 * search_frequent
 * use_yn
 */

    public static final String DATABASE_NAME = "DB_FAVORITE";
    public static final String TABLE_NAME = "TC_HIST";
    public static final String _ID  = "id";
    public static final String COLUMN_NAME_WEBNAME  = "web_name";
    public static final String COLUMN_NAME_WEBURL   = "web_url";
    public static final String COLUMN_NAME_SEARCHDATE = "search_date";
    public static final String COLUMN_NAME_SEARCHTIME = "search_time";
    public static final String COLUMN_NAME_SEARCHFREQUENT = "search_frequent";
    public static final String COLUMN_NAME_USEYN = "use_yn";

    public static final String FIRST_COLUMN="First";
    public static final String SECOND_COLUMN="Second";
    public static final String THIRD_COLUMN="Third";

    private final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_WEBNAME + " TEXT," +
                    COLUMN_NAME_WEBURL + " TEXT, " +
                    COLUMN_NAME_SEARCHDATE + " TEXT, " +
                    COLUMN_NAME_SEARCHTIME + " TEXT, " +
                    COLUMN_NAME_SEARCHFREQUENT + " INTEGER, " +
                    COLUMN_NAME_USEYN + " TEXT " +
                    ")";
    private final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    public Time2CheckFavorite(Context context) { super(context, DATABASE_NAME , null, 1); }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL( SQL_CREATE_ENTRIES );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        //db.execSQL(SQL_DELETE_ENTRIES);
        //onCreate(db);
    }
    public boolean insertServerUrl (String web_name, String web_url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("web_name", web_name);
        contentValues.put("web_url", web_url);
        contentValues.put("search_date", "datetime('now')");
        //contentValues.put("search_time", search_time);
        //contentValues.put("search_frequent", "ifnull(0,search_frequent+1)");
        //contentValues.put("use_yn", use_yn);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor selectServerUrl(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_NAME + " where id= " + id , null );
        return res;
    }
    public Cursor selectServerUrl2() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select id, web_url, 'del' from " + TABLE_NAME , null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public boolean updateServerUrl (String id, String web_name, String web_url, String search_date, String search_time, String search_frequent,String use_yn) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("web_name", web_name);
        contentValues.put("web_url", web_url);
        contentValues.put("search_date", search_date);
        contentValues.put("search_time", search_time);
        contentValues.put("search_frequent", search_frequent);
        contentValues.put("use_yn", use_yn);
        db.update(TABLE_NAME, contentValues, "id = ? ", new String[] { id } );
        return true;
    }

    public Integer deleteServerUrl (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                "id = ? ",
                new String[] { id });
    }

    public ArrayList<HashMap<String, String>> selectAll() {
        ArrayList<HashMap<String, String>> array_list = new ArrayList<HashMap<String, String>>();
        //hp = new HashMap();
/*
        hashmap.put(FIRST_COLUMN, "Allo messaging");
        hashmap.put(SECOND_COLUMN, "1");
        hashmap.put(THIRD_COLUMN, "Free");
        arryList.add(hashmap);
 */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select id, web_url, 'Del' as DEL from " + TABLE_NAME , null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            /*
            array_list.add(res.getString(res.getColumnIndex(_ID)));
            array_list.add(res.getString(res.getColumnIndex(COLUMN_NAME_WEBURL)));
            array_list.add(res.getString(res.getColumnIndex(DEL)));
             */
            HashMap<String,String> hashmap=new HashMap<String, String>();
            hashmap.put(FIRST_COLUMN, res.getString(res.getColumnIndex(_ID)));
            hashmap.put(SECOND_COLUMN, res.getString(res.getColumnIndex(COLUMN_NAME_WEBURL)));
            hashmap.put(THIRD_COLUMN, res.getString(res.getColumnIndex("DEL")));
            array_list.add(hashmap);
            res.moveToNext();
        }
        return array_list;
    }
}

