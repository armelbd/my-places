package net.esial.myplaces.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseCreator extends SQLiteOpenHelper {
	
	// Describe database and its tables
	private static final String DATABASE_NAME = "myplaces.db";
	private static final int DATABASE_VERSION = 1;
    
	public DataBaseCreator(Context context) {
		super(context,DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		PlacesTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/* Nothing here since we are at database version 1 */
	}

}
