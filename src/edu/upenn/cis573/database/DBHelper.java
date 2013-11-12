package edu.upenn.cis573.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "StudySpace.db";  
    private static final int DATABASE_VERSION = 1;  
    
    public  static final String TABLE_NAME                = "history";
    public  static final String COLUMN_NAME_ID            = "_id";
    public  static final String COLUMN_NAME_DATE          = "date";
    public  static final String COLUMN_NAME_BUILDINGNAME  = "buildingName";
    public  static final String COLUMN_NAME_SPACENAME     = "spaceName";
    public  static final String COLUMN_NAME_LATITUDE      = "latitude";
    public  static final String COLUMN_NAME_LONGITUDE     = "longitude";
    public  static final String COLUMN_NAME_ROOMNUM       = "number_of_rooms";
    public  static final String COLUMN_NAME_MAXOCCUPANCY  = "max_occupancy";
    public  static final String COLUMN_NAME_HASWHITEBOARD = "has_whiteboard";
    public  static final String COLUMN_NAME_PRIVACY       = "privacy";
    public  static final String COLUMN_NAME_HASCOMPUTER   = "has_computer";
    public  static final String COLUMN_NAME_RESERVETYPE   = "reserve_type";
    public  static final String COLUMN_NAME_HASBIGSCREEN  = "has_big_screen";
    public  static final String COLUMN_NAME_COMMENTS      = "comments";
    public  static final String COLUMN_NAME_ROOMNAME      = "roomName";
    public  static final String COLUMN_NAME_NOTE          = "notes";
    public  static final String COLUMN_NAME_STARTTIME     = "startTime";
    public  static final String COLUMN_NAME_ENDTIME       = "endTime";
    
    
      
    public DBHelper(Context context) {  
        //CursorFactory is set to null, as default  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }  
  
    //The first time database has been created, onCreate will be called  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
        db.execSQL("CREATE TABLE IF NOT EXISTS history(" +  
                COLUMN_NAME_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                COLUMN_NAME_DATE          + " TEXT, " +
                COLUMN_NAME_BUILDINGNAME  + " TEXT, " +
                COLUMN_NAME_SPACENAME     + " TEXT, " +
                COLUMN_NAME_LATITUDE      + " REAL, " +
                COLUMN_NAME_LONGITUDE     + " REAL, " +
                COLUMN_NAME_ROOMNUM       + " INTEGER, " +
                COLUMN_NAME_MAXOCCUPANCY  + " INTEGER, " +
                COLUMN_NAME_HASWHITEBOARD + " INTEGER, " +
                COLUMN_NAME_PRIVACY       + " TEXT, " +
                COLUMN_NAME_HASCOMPUTER   + " INTEGER, " +
                COLUMN_NAME_RESERVETYPE   + " TEXT, " +
                COLUMN_NAME_HASBIGSCREEN  + " INTEGER, " +
                COLUMN_NAME_COMMENTS      + " TEXT, " +
                COLUMN_NAME_ROOMNAME      + " TEXT, " +
                COLUMN_NAME_STARTTIME     + " INTEGER, " +
                COLUMN_NAME_ENDTIME       + " INTEGER, " +
                COLUMN_NAME_NOTE      + " TEXT)");  
    }  
  
    //If DATABASE_VERSION has been modified, system would find the difference of versions, onUpgrade would be called  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { /* not yet implemented! */ }
}
