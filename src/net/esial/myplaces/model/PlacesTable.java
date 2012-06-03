package net.esial.myplaces.model;

import android.database.sqlite.SQLiteDatabase;

public class PlacesTable {

	/* Describe the Places table */
	public static final String TABLE_NAME = "places";
	public static final String COLUMN_ID = "id";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LNG = "lng";
    public static final String COLUMN_FORMATED_ADDR = "formated_address";
    public static final String COLUMN_DESCRIPTION = "description";
    
 // Query to create the Places table
    private static final String CREATE_TABLE_PLACES = 
    		"CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_LAT + " REAL,"
            + COLUMN_LNG + " REAL,"
            + COLUMN_FORMATED_ADDR + " TEXT,"
            + COLUMN_DESCRIPTION+ " TEXT"
            + ");";
    
    public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_PLACES);
	}
    
    public static void onUpgrade(SQLiteDatabase database, int oldVersion,int newVersion) {
    	/* Nothing here for now */
    }
    
}
