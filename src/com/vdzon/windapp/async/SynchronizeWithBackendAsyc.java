package com.vdzon.windapp.async;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.vdzon.windapp.async.pojo.UpdateParameters;
import com.vdzon.windapp.consts.Const;
import com.vdzon.windapp.db.DatabaseHelper;
import com.vdzon.windapp.db.DbHandle;
import com.vdzon.windapp.pojo.WindData;
import com.vdzon.windapp.pojo.WindResult;
import com.vdzon.windapp.util.Util;

/**
 * Created by robbert on 27-11-13.
 */
public class SynchronizeWithBackendAsyc extends AsyncTask<UpdateParameters, Void, Boolean>
{
	@Override
	protected Boolean doInBackground( UpdateParameters... params )
	{
		// get database helper
		DatabaseHelper databaseHelper = new DatabaseHelper(params[0].getContext());
		DbHandle handle = databaseHelper.beginReadTransaction();

		// get the day from the parameter
		int day = params[0].getDay();

		// find the last update time
		long lastUpdateTime = databaseHelper.findLastUpdate(day, handle);
		databaseHelper.endTransaction(handle);

		// get the new windstats from the last update
		List<WindData> newWindStats = getNewWindStats(day,lastUpdateTime);

		// update the database
		updateDatabase(databaseHelper, newWindStats);
		databaseHelper.close();

		// update the widgets when there is any new data available
		if (newWindStats.size()>0){
			// update the appWidget
			WindData windData = newWindStats.get(newWindStats.size()-1);
			Intent in = new Intent(Const.WIDGET_UPDATE_ACTION);
			params[0].getContext().sendBroadcast(in);

			// update the application 
			in = new Intent(Const.UPDATE_WINDVIEW);
			params[0].getContext().sendBroadcast(in);

		}

		return true;
	}

	public List<WindData> getNewWindStats(int day,long lastUpdateTime){
		// load the wind data from the server
		List<WindData> res = new ArrayList<WindData>();
		try{
			URL url = new URL("http://www.mijnsportwedstrijden.nl/wind/getwinddata.php?day="+day+"&lastmodified="+lastUpdateTime);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			InputStream source = new BufferedInputStream(urlConnection.getInputStream());
			Gson gson = new Gson();
			Reader reader = new InputStreamReader(source);
			WindResult windResult = gson.fromJson(reader, WindResult.class);
			res=windResult.getValues();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return res;
	}

	public void updateDatabase(DatabaseHelper databaseHelper, List<WindData> newWindStats){
		// add the new data to the sql server
		DbHandle handle = databaseHelper.beginUpdateTransaction();
		for (WindData windStat:newWindStats){
			//			if (windStat.getSpotID()==2) continue;
			databaseHelper.insertOrUpdateWindStat(windStat, handle);
		}
		databaseHelper.endTransaction(handle);
	}


	@Override
	protected void onPostExecute( Boolean result )
	{
		super.onPostExecute(result);
	}
}
