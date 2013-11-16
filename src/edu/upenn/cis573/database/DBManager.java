package edu.upenn.cis573.database;

import java.util.ArrayList;
import java.util.Iterator;

import edu.upenn.cis573.StudySpace;
import edu.upenn.cis573.datastructure.Room;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;
import android.util.Log;

public class DBManager {

    private static DBHelper helper;
    private static SQLiteDatabase db;
    private static int MAX_NOTE_SIZE = 1;
    private static int MAX_ENTRY_NUM = 1;

    public static void initDB(Context context) {
        helper = new DBHelper(context);         
        db = helper.getWritableDatabase(); 
    }

    /**
     * Adds new StudySpace entry to the database.
     * @param history New StudySpace entry to be added.
     */
    public static int add(StudySpace history) {

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
        values.put(DBHelper.COLUMN_NAME_NOTE,      "");

        // Insert the new row, returning the primary key value of the new row
        if(size() >= MAX_ENTRY_NUM) {
            Log.i("DATABASE", "Database is full."); 
            return -1;
        }else {
            db.insert(DBHelper.TABLE_NAME, null, values);
            Log.i("Database", "Entry written to database: " + 
                    history.getBuildingName() + " " + history.getSpaceName());
            return 1;
        }
    }  



    public static int updateDb(String noteText){  
        //ArrayList<StudySpace> histories = new ArrayList<StudySpace>();  
        Cursor c = queryTheCursor(true);
        c.moveToFirst();


        ContentValues values = new ContentValues();

        values.put(DBHelper.COLUMN_NAME_DATE,          c.getLong(c.getColumnIndex(DBHelper.COLUMN_NAME_DATE)));
        values.put(DBHelper.COLUMN_NAME_BUILDINGNAME,  c.getString(c.getColumnIndex(DBHelper.COLUMN_NAME_BUILDINGNAME)));
        values.put(DBHelper.COLUMN_NAME_SPACENAME,     c.getString(c.getColumnIndex(DBHelper.COLUMN_NAME_SPACENAME)));
        values.put(DBHelper.COLUMN_NAME_LATITUDE,      c.getDouble(c.getColumnIndex(DBHelper.COLUMN_NAME_LATITUDE)));
        values.put(DBHelper.COLUMN_NAME_LONGITUDE,     c.getDouble(c.getColumnIndex(DBHelper.COLUMN_NAME_LONGITUDE)));
        values.put(DBHelper.COLUMN_NAME_ROOMNUM,       c.getInt(c.getColumnIndex(DBHelper.COLUMN_NAME_ROOMNUM)));
        values.put(DBHelper.COLUMN_NAME_MAXOCCUPANCY,  c.getInt(c.getColumnIndex(DBHelper.COLUMN_NAME_MAXOCCUPANCY)));
        values.put(DBHelper.COLUMN_NAME_HASWHITEBOARD, parseBoolean(c.getInt(c.getColumnIndex(DBHelper.COLUMN_NAME_HASWHITEBOARD))));
        values.put(DBHelper.COLUMN_NAME_PRIVACY,       c.getString(c.getColumnIndex(DBHelper.COLUMN_NAME_PRIVACY)));
        values.put(DBHelper.COLUMN_NAME_HASCOMPUTER,   parseBoolean(c.getInt(c.getColumnIndex(DBHelper.COLUMN_NAME_HASCOMPUTER))));
        values.put(DBHelper.COLUMN_NAME_RESERVETYPE,   c.getString(c.getColumnIndex(DBHelper.COLUMN_NAME_RESERVETYPE)));
        values.put(DBHelper.COLUMN_NAME_HASBIGSCREEN,  parseBoolean(c.getInt(c.getColumnIndex(DBHelper.COLUMN_NAME_HASBIGSCREEN))));
        values.put(DBHelper.COLUMN_NAME_COMMENTS,      c.getString(c.getColumnIndex(DBHelper.COLUMN_NAME_COMMENTS)));
        values.put(DBHelper.COLUMN_NAME_ROOMNAME,      new Room[] {new Room(c.getString(c.getColumnIndex(DBHelper.COLUMN_NAME_ROOMNAME)))}[0].getRoomName());
        values.put(DBHelper.COLUMN_NAME_NOTE,     noteText);

        int rowsAffected = 0;
        String[] args = {String.valueOf(c.getLong(c.getColumnIndex(DBHelper.COLUMN_NAME_DATE)))}; 
        if(spaceUsed() + noteText.length() > MAX_NOTE_SIZE) {
            Log.i("DATABASE", "Database is full."); 
            return -1;
        }else {
            rowsAffected = db.update(DBHelper.TABLE_NAME, values, "date=?",args);    
        }
        return rowsAffected;
    }  


    /** 
     * Queries all histories, return an ArrayList of StudySpace objects.
     * @return An ArrayList of StudySpace objects. 
     */  
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
            history.setNote(c.getString(c.getColumnIndex(DBHelper.COLUMN_NAME_NOTE)));
            histories.add(history);
            c.moveToNext();
        }
        c.close();
        Log.i("Database", "Entries read from database (size: " + histories.size() + ").");
        return histories;  
    }  


    /** 
     * Queries a given history, return note associated with this history.
     * @return a String containing note text. 
     */  
    public static String query(StudySpace history) {  
        ArrayList<StudySpace> histories = query(); 
        Iterator<StudySpace> it = histories.iterator();
        String noteText;
        while (it.hasNext()) {
            StudySpace current = it.next();
            if(current.getDate() == history.getDate()
                    && current.getSpaceName().equals(history.getSpaceName())
                    && current.getBuildingName().equals(history.getBuildingName())) {
                noteText = current.getNote();
                Log.i("Database", "Note read from database (length: " + noteText.length() + ").");
                return noteText;
            }
        }
        return null;
    }  

    /** 
     * Queries all histories, return a cursor.
     * @param reversed Set true if desired return list is
     * in a descending order; false otherwise.
     * @return A Cursor.
     */  
    public static Cursor queryTheCursor(boolean reversed) {  
        return db.rawQuery(
                "SELECT * FROM " + DBHelper.TABLE_NAME +
                " ORDER BY " + DBHelper.COLUMN_NAME_ID + 
                (reversed ? " DESC" : " ASC"), null);  
    }

    /**
     * Clears the database
     */
    public static void clearDB() {
        db.execSQL("DELETE FROM " + DBHelper.TABLE_NAME);
    }
    
    public static double spaceUsed() {
        Cursor c = db.rawQuery("SELECT sum(length(" + DBHelper.COLUMN_NAME_NOTE + ")) FROM " + DBHelper.TABLE_NAME + ";", null);
        c.moveToFirst();
        int noteSize = c.getInt(0);
        c.close();
        return noteSize;
    }

    /**
     * Gets the total number of  entries in the database.
     * @return The size of total entries in the database.
     */
    public static int size() {
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DBHelper.TABLE_NAME, null);
        c.moveToFirst();
        int cnt= c.getInt(0);
        c.close();
        return cnt;
    }

    /**
     * Checks whether the database is empty.
     * @return True if the database is empty;
     * false otherwise.
     */
    public static boolean isEmpty() {
        return size() == 0;
    }

    /** 
     * Close the database. 
     */  
    public static void closeDB() {  
        db.close();  
    }

    /**
     * Converts integers to booleans.
     */
    private static boolean parseBoolean(int b) {
        return b == 1;
    }
}
