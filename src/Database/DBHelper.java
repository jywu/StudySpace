package Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

	private static final String DATABASE_NAME = "StudySpace.db";  
    private static final int DATABASE_VERSION = 1;  
    public static final String TABLE_NAME = "history";
    public static final String COLUMN_NAME_ID = "_id";
    public static final String COLUMN_NAME_DATE = "date";
    public static final String COLUMN_NAME_FROM = "from";
    public static final String COLUMN_NAME_TO = "to";
    public static final String COLUMN_NAME_PeopleNum = "peopleNum";
    public static final String COLUMN_NAME_ROOM = "room";
    
      
    public DBHelper(Context context) {  
        //CursorFactory is set to null, as default  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }  
  
    //The first time database has been created, onCreate will be called  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
        db.execSQL("CREATE TABLE IF NOT EXISTS history" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, date VARCHAR, from VARCHAR, to VARCHAR, peopleNum INTEGER, room VARCHAR)");  
    }  
  
    //If DATABASE_VERSION has been modified, system would find the difference of versions, onUpgrade would be called  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
        db.execSQL("ALTER TABLE history ADD COLUMN other STRING");  
    }  
}
