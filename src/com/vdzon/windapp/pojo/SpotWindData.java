package com.vdzon.windapp.pojo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.vdzon.windapp.db.DatabaseHelper;
import com.vdzon.windapp.db.DbHandle;

public class SpotWindData implements Serializable {
	private List<WindData> windDatas = new LinkedList <WindData>();
	private WindData last = null;
	private WindData first = null;
	private SpotData spotData = null;

	public SpotData getSpotData() {
		return spotData;
	}

	public void setSpotData(SpotData spotData) {
		this.spotData = spotData;
	}

	public List<WindData> getWindDatas() {
		return windDatas;
	}

	public WindData getLastWindData() {
		return last;
	}

	public WindData getFirstWindData() {
		return first;
	}

	public void setWindDatas(List<WindData> windDatas) {
		this.windDatas = windDatas;
		WindData _last = null;
		first = null;
		for (WindData windData:windDatas){
			if (first==null && windData.getStartTime()<windData.getEndTime()){
				first = windData;
			}
			_last = windData;
		}	
		last = _last;
	}

	public WindData getWindData(double searchTime){
		for (WindData windData:windDatas){
			double startTime = windData.getStartHour()+(double)windData.getStartMinute()/60;
			double endTime = windData.getEndHour()+(double)windData.getEndMinute()/60;
			if (searchTime>=startTime && searchTime<=endTime) {
				return windData;
			}
		}
		return null;
	}


	public void load(Context context, SpotData spotData, int day){
		List<WindData> _windDatas = new LinkedList<WindData>();
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		DbHandle handle = databaseHelper.beginReadTransaction();
		Cursor c = databaseHelper.getWindStatsOfDay(spotData.getSiteID(),day, handle);
		if (c.moveToFirst()){
			WindData windStat = databaseHelper.getWindStatFromCursor(c);

			if (windStat.getWind()==-1 || windStat.getGust()==-1 || windStat.getAngle()==-1 ){
				// skip
			}
			else{
				_windDatas.add(windStat);
			}
		}
		while (c.moveToNext()){
			WindData windStat = databaseHelper.getWindStatFromCursor(c);

			if (windStat.getWind()==-1 || windStat.getGust()==-1 || windStat.getAngle()==-1 ){
				// skip
			}
			else{
				_windDatas.add(windStat);
			}
		}
		c.close();
		databaseHelper.endTransaction(handle);
		this.setWindDatas(_windDatas);
		this.setSpotData(spotData);
	}
}
