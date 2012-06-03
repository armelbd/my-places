package net.esial.myplaces.model;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class DataProvider extends ContentProvider {
	
	public static final String AUTHORITY = "net.esial.myplaces.model.DataProvider";
	private static final String BASE_PATH = "places";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);
    
    public static final String PLACES_MIME_TYPE = 
    		ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.myplaces.places";
    public static final String PLACE_ID_MIME_TYPE = 
    		ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.myplaces.place";
    
    // Used for the UriMacher
 	private static final int PLACES = 0;
 	private static final int PLACE_ID = 1;
 	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
 	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, PLACES);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", PLACE_ID);
	}
	
	private DataBaseCreator dataBaseCreator;

	@Override
	public boolean onCreate() {
		dataBaseCreator = new DataBaseCreator(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// Check if the caller has requested a column which does not exists
		checkColumns(projection);

		// Build a query with a QueryBuilder
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(PlacesTable.TABLE_NAME);
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case PLACES:
			break;
		case PLACE_ID:
			// Adding the ID to the original query
			queryBuilder.appendWhere(PlacesTable.COLUMN_ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		// Perform the query on database
		SQLiteDatabase db = dataBaseCreator.getReadableDatabase();
		Cursor cursor = queryBuilder.query(
				db, projection, selection,selectionArgs, null, null, sortOrder);
		
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case PLACES:
			return PLACES_MIME_TYPE;
		case PLACE_ID:
			return PLACE_ID_MIME_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = dataBaseCreator.getWritableDatabase();
		long id = -1;
		switch (uriType) {
		case PLACES:
			id = sqlDB.insert(PlacesTable.TABLE_NAME, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = dataBaseCreator.getWritableDatabase();
		int rowsDeleted = -1;
		switch (uriType) {
		case PLACES:
			rowsDeleted = sqlDB.delete(PlacesTable.TABLE_NAME, selection,
					selectionArgs);
			break;
		case PLACE_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(
						PlacesTable.TABLE_NAME,
						PlacesTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(
						PlacesTable.TABLE_NAME,
						PlacesTable.COLUMN_ID + "=" + id 
						+ " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = dataBaseCreator.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType) {
		case PLACES:
			rowsUpdated = sqlDB.update(PlacesTable.TABLE_NAME, 
					values, 
					selection,
					selectionArgs);
			break;
		case PLACE_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(PlacesTable.TABLE_NAME, 
						values,
						PlacesTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsUpdated = sqlDB.update(PlacesTable.TABLE_NAME, 
						values,
						PlacesTable.COLUMN_ID + "=" + id 
						+ " and " 
						+ selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
	
	private void checkColumns(String[] projection) {
		String[] available = { 
				PlacesTable.COLUMN_ID,
				PlacesTable.COLUMN_LAT,
				PlacesTable.COLUMN_LNG,
				PlacesTable.COLUMN_FORMATED_ADDR,
				PlacesTable.COLUMN_DESCRIPTION
				};
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(
					Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(
					Arrays.asList(available));
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException(
						"Unknown columns in projection");
			}
		}
	}

}
