package net.esial.myplaces.model;

import android.app.Application;
import android.content.ContentValues;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DataApi {
	
	@Inject private Application app;
	
	public void insertPlace(final String formatedAddr, final double lat, final double lng) {
		new Thread(new Runnable() {
	        public void run() {
	        	ContentValues values = new ContentValues();
	    		values.put(PlacesTable.COLUMN_FORMATED_ADDR, formatedAddr);
	    		values.put(PlacesTable.COLUMN_LAT, lat);
	    		values.put(PlacesTable.COLUMN_LNG, lng);
	    		app.getContentResolver().insert(DataProvider.CONTENT_URI,values);
	        }
	    }).start();
	}

}
