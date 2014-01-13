package com.vdzon.windapp.async;

/**
 * Created by Robbert on 11/22/13.
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.vdzon.windapp.util.Util;

import android.os.AsyncTask;

/**
 * Register our regID to the backend so that the backend knows that this regID is valid
 */
public class RegisterGcmAsyc extends AsyncTask<String, Void, Boolean>
{
	@Override
	protected Boolean doInBackground( String... params )
	{
		String regID = params[0];
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL("http://www.mijnsportwedstrijden.nl/android/registergcm/gcmregister.php?key="+regID+"&app=windapp&data=none");
			urlConnection = (HttpURLConnection) url.openConnection();
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			Util.readStream(in);
		}
		catch(Exception ex){
		}
		finally {
			urlConnection.disconnect();
		}
		return true;
	}

	@Override
	protected void onPostExecute( Boolean result )
	{
		super.onPostExecute(result);
	}
}