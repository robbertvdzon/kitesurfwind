package com.vdzon.windapp.db;

import android.database.sqlite.SQLiteDatabase;

public class DbHandle {
	private SQLiteDatabase db;

	public SQLiteDatabase getDb() {
		return db;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}

}
