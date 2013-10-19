package edu.upenn.cis573.database;

import java.util.ArrayList;

import edu.upenn.cis573.Room;
import edu.upenn.cis573.StudySpace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBManager {

    private static DBHelper helper;
    private static SQLiteDatabase db;
      
    public static void init(Context context) {
        helper = new DBHelper(context);         
        db = helper.getWritableDatabase(); 
    }
    
    public static void add(StudySpace history) {
    	ContentValues values = new ContentValues();
    	values.put(DBHelper.COLUMN_NAME_DATE,          history.getDate());
    	values.put(DBHelper.COLUMN_NAME_BUILDINGNAME,  history.getBuildingName());
    	values.put(DBHelper.COLUMN_NAME_SPACENAME,     history.getSpaceName());
    	values.put(DBHelper.COLUMN_NAME_LATITUDE,      history.getSpaceLatitude());
    	values.put(DBHelper.COLUMN_NAME_LONGITUDE,     history.getSpaceLongitude());
    	values.put(DBHelper.COLUMN_NAME_ROOMNUM,       history.getNumberOfRooms());
    	values.put(DBHelper.COLUMN_NAME_MAXOCCUPANCY,  history.getMaximumOccupancy());
    	values.put(DBHelper.COLUMN_NAME_HASWHITEBOARD, history.hasWhiteboard());
    	values.put(DBHelper.COLUMN_NAME_PRIVACY,       history.getPrivacy());
    	values.put(DBHelper.COLUMN_NAME_HASCOMPUTER,   history.hasComputer());
    	values.put(DBHelper.COLUMN_NAME_RESERVETYPE,   history.getReserveType());
    	values.put(DBHelper.COLUMN_NAME_HASBIGSCREEN,  history.has_big_screen());
    	values.put(DBHelper.COLUMN_NAME_COMMENTS,      history.getComments());
    	values.put(DBHelper.COLUMN_NAME_ROOMNAME,      history.getRooms()[0].getRoomName());

    	// Insert the new row, returning the primary key value of the new row
    	db.insert(DBHelper.TABLE_NAME, null, values);
    	Log.i("Database", "Entry written to database: " + history);
    }  
      
    public static ArrayList<StudySpace> query() {  
        ArrayList<StudySpace> histories = new ArrayList<StudySpace>();  
        Cursor c = queryTheCursor(true);
        c.moveToFirst();
        while (!c.isAfterLast()) {  
            StudySpace history = new StudySpace();  
            history.setDate(c.getLong(c.getColumnIndex(DBHelper.COLUMN_NAME_DATE)));
            history.setBuildingName(c.getString(c.getColumnIndex(DBHelper.COLUMN_NAME_BUILDINGNAME)));
            history.setSpaceName(c.getString(c.getColumnIndex(DBHelper.COLUMN_NAME_SPACENAME)));
            history.setLatitude(c.getDouble(c.getColumnIndex(DBHelper.COLUMN_NAME_LATITUDE)));
            history.setLongitude(c.getDouble(c.getColumnIndex(DBHelper.COLUMN_NAME_LONGITUDE)));
            history.setNumber_of_rooms(c.getInt(c.getColumnIndex(DBHelper.COLUMN_NAME_ROOMNUM)));
            history.setMax_occupancy(c.getInt(c.getColumnIndex(DBHelper.COLUMN_NAME_MAXOCCUPANCY)));
            history.setHas_whiteboard(parseBoolean(c.getInt(c.getColumnIndex(DBHelper.COLUMN_NAME_HASWHITEBOARD))));
            history.setPrivacy(c.getString(c.getColumnIndex(DBHelper.COLUMN_NAME_PRIVACY)));
            history.setHas_computer(parseBoolean(c.getInt(c.getColumnIndex(DBHelper.COLUMN_NAME_HASCOMPUTER))));
            history.setReserve_type(c.getString(c.getColumnIndex(DBHelper.COLUMN_NAME_RESERVETYPE)));
            history.setHas_big_screen(parseBoolean(c.getInt(c.getColumnIndex(DBHelper.COLUMN_NAME_HASBIGSCREEN))));
            history.setComments(c.getString(c.getColumnIndex(DBHelper.COLUMN_NAME_COMMENTS)));
            history.setRooms(new Room[] {new Room(c.getString(c.getColumnIndex(DBHelper.COLUMN_NAME_ROOMNAME)))});
            histories.add(history);
            c.moveToNext();
        }
        c.close();
        Log.i("Database", "Entries read from database (size: " + histories.size() + ").");
        return histories;  
    }  
      
    /** 
     * query all histories, return cursor 
     * @return Cursor 
     */  
    public static Cursor queryTheCursor(boolean reversed) {  
        return db.rawQuery(
                "SELECT * FROM " + DBHelper.TABLE_NAME +
                " ORDER BY " + DBHelper.COLUMN_NAME_ID + 
                (reversed ? " DESC" : " ASC"), null);  
    }
      
    /** 
     * close database 
     */  
    public static void closeDB() {  
        db.close();  
    }
    
    /**
     * converts integers to booleans
     */
    private static boolean parseBoolean(int b) {
        return b == 1 ? true : false;
    }
}
