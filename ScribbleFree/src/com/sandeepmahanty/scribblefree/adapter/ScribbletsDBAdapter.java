/*
 * @author Sandeep Kumar Mahanty

 * 
 */

package com.sandeepmahanty.scribblefree.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScribbletsDBAdapter {
	
	public static final String COL_ROW_ID="_id";
	public static final String COL_NOTE_TITLE="title";
	public static final String COL_NOTE_CONTENT="content";
	public static final String COL_NOTE_DATE="date";
	public static final String COL_NOTE_COLOR="color";
	public static final String COL_NOTE_TYPE="type";
	public static final String COL_NOTE_LOCKED="islocked";
	
	public static final String COL_SCRIBBLE_ID="_id";
	public static final String COL_SCRIBBLE_TITLE="title";
	public static final String COL_SCRIBBLE_PATH="file_path";
	public static final String COL_SCRIBBLE_DATE="date";
	
	public static final String DB_TAG="ScribbletDatabaseAdapter";
	private SQLiteDatabase mDB;
	private DatabaseHelper mDBHelper;
	
	private static final String DB_NAME="Scribblets";
	private static final String DB_TABLE_NOTES="Notes";
	private static final String DB_TABLE_SCRIBBLES="Scribbles";
	private static final int 	DB_VERSION=1;
	
	private final Context mContext;
	private static final String DATABASE_CREATE_NOTES =
			  "CREATE TABLE if not exists " + DB_TABLE_NOTES + " (" +
			  COL_ROW_ID + " integer PRIMARY KEY autoincrement," +
			  COL_NOTE_TITLE + "," +
			  COL_NOTE_CONTENT + "," +
			  COL_NOTE_COLOR + "," +
			  COL_NOTE_DATE + "," +
			  COL_NOTE_LOCKED +");";
	
	private static final String DATABASE_CREATE_SCRIBBLE =
			  "CREATE TABLE if not exists " + DB_TABLE_SCRIBBLES+ " (" +
			   COL_SCRIBBLE_ID + " integer PRIMARY KEY autoincrement," +
			   COL_SCRIBBLE_TITLE+ "," +
			   COL_SCRIBBLE_PATH + "," +
			  COL_SCRIBBLE_DATE +");";
			  
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		
		DatabaseHelper(Context context)
		{
			super(context,DB_NAME,null,DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			
			db.execSQL(DATABASE_CREATE_NOTES);
			db.execSQL(DATABASE_CREATE_SCRIBBLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NOTES);
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_SCRIBBLES);
			onCreate(db);
		}
		
	}
	
	public ScribbletsDBAdapter(Context context)	{
		mContext=context;
	}
	
	public ScribbletsDBAdapter open() throws SQLException {
		mDBHelper = new DatabaseHelper(mContext);
		mDB = mDBHelper.getWritableDatabase();				
		return this;
		
	}
	public void close() throws SQLException{
		if(mDB != null && mDB.isOpen())
	    	mDB.close();
	}
		
	public long createScribble(String title,String path,String date){
		
		ContentValues values= new ContentValues();
		values.put(COL_SCRIBBLE_TITLE, title);
		values.put(COL_SCRIBBLE_PATH, path);
		values.put(COL_SCRIBBLE_DATE, date);
		return mDB.insert(DB_TABLE_SCRIBBLES, null, values);
		
	}
	public long createNote(String title,String content,String color,String date,String isSecure) {
		
		ContentValues values= new ContentValues();
		values.put(COL_NOTE_TITLE, title);
		values.put(COL_NOTE_CONTENT, content);
		values.put(COL_NOTE_COLOR, color);
		values.put(COL_NOTE_DATE, date);		
		values.put(COL_NOTE_LOCKED, isSecure);
		return mDB.insert(DB_TABLE_NOTES, null, values);
	}
	public long updateScribble(String title,String id,String path,String date){
		
		ContentValues values= new ContentValues();
		values.put(COL_SCRIBBLE_TITLE, title);
		values.put(COL_SCRIBBLE_PATH, path);
		values.put(COL_SCRIBBLE_DATE, date);
		return mDB.update(DB_TABLE_SCRIBBLES, values,COL_SCRIBBLE_ID+"="+id, null);
	}
	public long updateNote(String id,String title,String content,String color,String date,String isSecure) {
		
		ContentValues values= new ContentValues();
		values.put(COL_NOTE_TITLE, title);
		values.put(COL_NOTE_CONTENT, content);
		values.put(COL_NOTE_COLOR, color);
		values.put(COL_NOTE_DATE, date);
		values.put(COL_NOTE_LOCKED, isSecure);
		return mDB.update(DB_TABLE_NOTES, values,COL_ROW_ID+"="+id, null);
	}
	
	
	public long removeNote(String colValue){
		return mDB.delete(DB_TABLE_NOTES,COL_ROW_ID+"="+colValue,null);
	}
	public Cursor getAllScribbles() {
		
		Cursor mCursor=mDB.query(DB_TABLE_SCRIBBLES, new String[]{COL_SCRIBBLE_ID,COL_SCRIBBLE_TITLE,COL_SCRIBBLE_PATH,COL_SCRIBBLE_DATE},null, null, null, null, null);
		
		if(mCursor!=null) {
				mCursor.moveToFirst();
		}
		return mCursor;
	}
	public Cursor getAllNotes() {
		
		Cursor mCursor=mDB.query(DB_TABLE_NOTES, new String[]{COL_ROW_ID,COL_NOTE_TITLE,COL_NOTE_CONTENT,COL_NOTE_DATE,COL_NOTE_COLOR},COL_NOTE_LOCKED+"!='true'", null, null, null, null);
		
		if(mCursor!=null) {
				mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public Cursor getAllSecureNotes() {
		
		Cursor mCursor=mDB.query(DB_TABLE_NOTES, new String[]{COL_ROW_ID,COL_NOTE_TITLE,COL_NOTE_CONTENT,COL_NOTE_DATE,COL_NOTE_COLOR},COL_NOTE_LOCKED+"!='false'", null, null, null, null);
		
		if(mCursor!=null) {
				mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public Cursor getOneCursor(String row){
		Cursor mCursor=mDB.query(DB_TABLE_SCRIBBLES, new String[]{COL_SCRIBBLE_ID,COL_SCRIBBLE_PATH,COL_SCRIBBLE_DATE},COL_SCRIBBLE_ID + "=" + row, null, null, null, null);
		if(mCursor!=null) {
				mCursor.moveToFirst();
		}
		return mCursor;
	}
	

	public Cursor getOneScribble(String row) {
		
		Cursor mCursor=mDB.query(DB_TABLE_SCRIBBLES, new String[]{COL_SCRIBBLE_ID,COL_SCRIBBLE_TITLE,COL_SCRIBBLE_PATH,COL_NOTE_DATE},COL_SCRIBBLE_ID+ "=" + row, null, null, null, null);
		if(mCursor!=null) {
				mCursor.moveToFirst();
		}
		return mCursor;
	}
	public Cursor getOneNote(String row) {
		
		Cursor mCursor=mDB.query(DB_TABLE_NOTES, new String[]{COL_ROW_ID,COL_NOTE_TITLE,COL_NOTE_CONTENT,COL_NOTE_DATE,COL_NOTE_COLOR},COL_ROW_ID + "=" + row, null, null, null, null);
		if(mCursor!=null) {
				mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	
	public Cursor getNotesAfterFilter(String filterValue) throws SQLException {
		Cursor mCursor=null;
		if(filterValue ==null || filterValue.length()==0) {
			mCursor=mDB.query(DB_TABLE_NOTES, new String[]{COL_ROW_ID,COL_NOTE_TITLE,COL_NOTE_CONTENT,COL_NOTE_DATE,COL_NOTE_COLOR}, null, null, null, null, null);
		}
		else {
			mCursor=mDB.query(DB_TABLE_NOTES, new String[]{COL_ROW_ID,COL_NOTE_TITLE,COL_NOTE_CONTENT,COL_NOTE_DATE,COL_NOTE_COLOR},COL_NOTE_TITLE + " like '%" + filterValue + "%'", null, null, null, null, null);
		}
		if(mCursor!=null) {
			mCursor.moveToFirst();
	}
		return mCursor;
	}
	
}