package com.vdzon.windapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vdzon.windapp.pojo.WindData;

public class DatabaseHelper extends SQLiteOpenHelper {

	static final String DBNAME="wind";
	static final String WINDTABLE="wind";
	static final String COL_ID="_id";
	static final String COL_SPOT="spotid";
	static final String COL_DAY="day";// year*1000 + day in year
	static final String COL_HOUR="hour";
	static final String COL_MINUTE="minute";
	static final String COL_START_HOUR="starthour";
	static final String COL_START_MINUTE="startminute";
	static final String COL_WIND="wind";
	static final String COL_GUST="gust";
	static final String COL_ANGLE="angle";
	static final String COL_LAST_MODIFIED="lastModified";

	static final String INDEX_SPOT="spotIndex";
	static final String INDEX_DAY="dayIndex";
	static final String INDEX_HOUR="hourIndex";
	static final String INDEX_MINUTE="minuteIndex";
	static final String INDEX_LAST_MODIFIED="lastModifiedIndex";

	public DatabaseHelper(Context context) {
		super(context, DBNAME, null,8); 
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "+WINDTABLE+"("+
				COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
				COL_SPOT+" INTEGER, "+
				COL_LAST_MODIFIED+" INTEGER, "+
				COL_DAY+" INTEGER, "+
				COL_HOUR+" INTEGER, "+
				COL_MINUTE+" INTEGER, "+
				COL_START_HOUR+" INTEGER, "+
				COL_START_MINUTE+" INTEGER, "+
				COL_WIND+" REAL, "+
				COL_GUST+" REAL, "+
				COL_ANGLE+" INTEGER );");

		db.execSQL("CREATE INDEX "+INDEX_LAST_MODIFIED+" on " + WINDTABLE + "("+COL_LAST_MODIFIED+");");
		db.execSQL("CREATE INDEX "+INDEX_SPOT+" on " + WINDTABLE + "("+COL_SPOT+");");
		db.execSQL("CREATE INDEX "+INDEX_DAY+" on " + WINDTABLE + "("+COL_DAY+");");
		db.execSQL("CREATE INDEX "+INDEX_HOUR+" on " + WINDTABLE + "("+COL_HOUR+");");
		db.execSQL("CREATE INDEX "+INDEX_MINUTE+" on " + WINDTABLE + "("+COL_MINUTE+");");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP INDEX IF EXISTS "+INDEX_LAST_MODIFIED);
		db.execSQL("DROP INDEX IF EXISTS "+INDEX_SPOT);
		db.execSQL("DROP INDEX IF EXISTS "+INDEX_DAY);
		db.execSQL("DROP INDEX IF EXISTS "+INDEX_HOUR);
		db.execSQL("DROP INDEX IF EXISTS "+INDEX_MINUTE);
		db.execSQL("DROP TABLE IF EXISTS "+WINDTABLE);
		onCreate(db);
	}


	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	public DbHandle beginUpdateTransaction() {
		SQLiteDatabase db=this.getWritableDatabase();
		db.beginTransaction();
		DbHandle handle = new DbHandle();
		handle.setDb(db);
		return handle;
	}	

	public DbHandle beginReadTransaction() {
		SQLiteDatabase db=this.getReadableDatabase();
		db.beginTransaction();
		DbHandle handle = new DbHandle();
		handle.setDb(db);
		return handle;
	}	

	public void endTransaction(DbHandle handle) {
		handle.getDb().setTransactionSuccessful();
		handle.getDb().endTransaction();
		handle.getDb().close();
	}	

	public void insertOrUpdateWindStat(WindData windStat, DbHandle handle)
	{
		// find if row already exists
		Cursor cursor = getWindStatsOfDayAndTime(windStat.getSpotID(), windStat.getDay(), windStat.getEndHour(), windStat.getEndMinute(), handle);
		if (cursor.moveToFirst()){
			// already exists, do an update
			int id = cursor.getInt(cursor.getColumnIndex(COL_ID));  
			UpdateWindStat(windStat,id, handle);
		}
		else{
			// insert new row
			InsertWindStat(windStat, handle);
		}
		cursor.close();
	}

	public WindData getWindStatFromCursor(Cursor cursor){
		WindData windStat = new WindData();
		windStat.setSpotID(cursor.getInt(cursor.getColumnIndex(COL_SPOT)));
		windStat.setDay(cursor.getInt(cursor.getColumnIndex(COL_DAY)));
		windStat.setendHour(cursor.getInt(cursor.getColumnIndex(COL_HOUR)));
		windStat.setEndMinute(cursor.getInt(cursor.getColumnIndex(COL_MINUTE)));
		windStat.setStartHour(cursor.getInt(cursor.getColumnIndex(COL_START_HOUR)));
		windStat.setStartMinute(cursor.getInt(cursor.getColumnIndex(COL_START_MINUTE)));
		windStat.setWind(cursor.getFloat(cursor.getColumnIndex(COL_WIND)));
		windStat.setGust(cursor.getFloat(cursor.getColumnIndex(COL_GUST)));
		windStat.setAngle(cursor.getInt(cursor.getColumnIndex(COL_ANGLE)));
		windStat.setLastModified(cursor.getInt(cursor.getColumnIndex(COL_LAST_MODIFIED)));
		return windStat;
	}

	public void UpdateWindStat(WindData windStat, int id, DbHandle handle)
	{
		SQLiteDatabase db=handle.getDb();
		ContentValues cv=new ContentValues();
		cv.put(COL_DAY, windStat.getDay());
		cv.put(COL_SPOT, windStat.getSpotID());
		cv.put(COL_HOUR, windStat.getEndHour());
		cv.put(COL_MINUTE, windStat.getEndMinute());
		cv.put(COL_START_HOUR, windStat.getStartHour());
		cv.put(COL_START_MINUTE, windStat.getStartMinute());
		cv.put(COL_WIND, windStat.getWind());
		cv.put(COL_GUST, windStat.getGust());
		cv.put(COL_ANGLE, windStat.getAngle());
		cv.put(COL_LAST_MODIFIED, windStat.getLastModified());
		db.update(WINDTABLE, cv, COL_ID+"=?", new String []{""+id});
	}

	public void InsertWindStat(WindData windStat, DbHandle handle)
	{
		SQLiteDatabase db=handle.getDb();
		ContentValues cv=new ContentValues();
		cv.put(COL_SPOT, windStat.getSpotID());
		cv.put(COL_DAY, windStat.getDay());
		cv.put(COL_HOUR, windStat.getEndHour());
		cv.put(COL_MINUTE, windStat.getEndMinute());
		cv.put(COL_START_HOUR, windStat.getStartHour());
		cv.put(COL_START_MINUTE, windStat.getStartMinute());
		cv.put(COL_WIND, windStat.getWind());
		cv.put(COL_GUST, windStat.getGust());
		cv.put(COL_ANGLE, windStat.getAngle());
		cv.put(COL_LAST_MODIFIED, windStat.getLastModified());
		db.insert(WINDTABLE, null, cv);   
	}

	public Cursor getWindStatsOfDay(int spotId, int day, DbHandle handle)
	{
		String[] whereValues = new String[] {""+spotId,""+day};
		Cursor cur=handle.getDb().rawQuery("SELECT * from "+WINDTABLE+" WHERE "+COL_SPOT+"=? AND "+COL_DAY+" =? ORDER BY "+COL_HOUR+","+COL_MINUTE ,whereValues);
		return cur;
	}

	public Cursor getWindStatsOfDayAndTime(int spotId, int day, int hour, int minute, DbHandle handle)
	{
		String[] whereValues = new String[] {""+spotId,""+day,""+hour,""+minute};
		Cursor cur=handle.getDb().rawQuery("SELECT * from "+WINDTABLE+" WHERE "+COL_SPOT+"=? AND "+COL_DAY+" =? AND "+COL_HOUR+"=? AND "+COL_MINUTE+"=? ",whereValues);
		return cur;
	}

	public long findLastUpdate(int day, DbHandle handle){
		long lastTime = 0;
		String[] whereValues = new String[] {""+day};
		Cursor cur=handle.getDb().rawQuery("SELECT max("+COL_LAST_MODIFIED+") from "+WINDTABLE+" WHERE "+COL_DAY+" =?",whereValues);
		if (cur.moveToFirst()){
			lastTime = cur.getLong(0); 
		}
		return lastTime;

	}

}



