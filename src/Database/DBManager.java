package Database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

	private DBHelper helper;  
    private SQLiteDatabase db;  
      
    public DBManager(Context context) {  
        helper = new DBHelper(context);         
        db = helper.getWritableDatabase();  
    }  
      
    
    public void add(History history) {  
    	
    	ContentValues values = new ContentValues();
    	values.put(DBHelper.COLUMN_NAME_ID, history._id);
    	values.put(DBHelper.COLUMN_NAME_DATE, history.date);
    	values.put(DBHelper.COLUMN_NAME_FROM, history.from);
    	values.put(DBHelper.COLUMN_NAME_TO, history.to);
    	values.put(DBHelper.COLUMN_NAME_PeopleNum, history.peopleNum);
    	values.put(DBHelper.COLUMN_NAME_ROOM, history.room);

    	// Insert the new row, returning the primary key value of the new row
    	db.insert(
	         DBHelper.TABLE_NAME,
	         null,
	         values);
    }  
      
    /** 
     * query all persons, return list 
     * @return List<Person> 
     */  
    public List<History> query() {  
        ArrayList<History> histories = new ArrayList<History>();  
        Cursor c = queryTheCursor();  
        while (c.moveToNext()) {  
            History history = new History();  
            history._id = c.getInt(c.getColumnIndex(DBHelper.COLUMN_NAME_ID));  
            history.date = c.getString(c.getColumnIndex(DBHelper.COLUMN_NAME_DATE));  
            history.from = c.getString(c.getColumnIndex(DBHelper.COLUMN_NAME_FROM));  
            history.to = c.getString(c.getColumnIndex(DBHelper.COLUMN_NAME_TO));
            history.peopleNum = c.getInt(c.getColumnIndex(DBHelper.COLUMN_NAME_PeopleNum));
            history.room = c.getString(c.getColumnIndex(DBHelper.COLUMN_NAME_ROOM));
            histories.add(history);  
        }  
        c.close();  
        return histories;  
    }  
      
    /** 
     * query all histories, return cursor 
     * @return  Cursor 
     */  
    public Cursor queryTheCursor() {  
        Cursor c = db.rawQuery("SELECT * FROM person", null);  
        return c;  
    }  
      
    /** 
     * close database 
     */  
    public void closeDB() {  
        db.close();  
    }  
}
