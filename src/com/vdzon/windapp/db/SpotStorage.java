package com.vdzon.windapp.db;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vdzon.windapp.db.callback.SpotsLoadedCallback;
import com.vdzon.windapp.pojo.SpotData;
import com.vdzon.windapp.pojo.SpotsResult;
import com.vdzon.windapp.pojo.WindData;
import com.vdzon.windapp.pojo.WindResult;
import com.vdzon.windapp.util.Util;

public class SpotStorage {

	private static HashMap<Integer, SpotData> mSpots = null;

	private static void init(Context context){
		synchronized(SpotStorage.class){
			if (mSpots!=null) return;
			loadSpotsFromLocalStorage(context);

			if (mSpots==null){
				// could not load from local storage, create an empty spots map
				mSpots = new HashMap<Integer, SpotData>();
			}
		}
	}
	
	private static void updateSpots(List<SpotData> spots){
		synchronized(SpotStorage.class){
			mSpots.clear();
			for (SpotData spotData:spots){
				mSpots.put(spotData.getSiteID(), spotData);
			}
		}
	}

	public static List<SpotData> getAllSpots(Context context){
		init(context);
		synchronized(SpotStorage.class){
			List<SpotData> result = new ArrayList<SpotData>(mSpots.values());
			return result;
		}
	}

	public static SpotData getSpotData(Context context, int spotID){
		init(context);
		synchronized(SpotStorage.class){
			return mSpots.get(new Integer(spotID));
		}
	}

	public static List<SpotData> loadSpotsFromCache(Context context){
		init(context);
		updateCachedImages(context, false);
		synchronized(SpotStorage.class){
			return new ArrayList<SpotData>(mSpots.values());
		}
	}

	public static List<SpotData> loadSpotsFromInet(Context context){
		init(context);
		try{
			URL url = new URL("http://www.mijnsportwedstrijden.nl/wind/getspots.php");
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			InputStream source = new BufferedInputStream(urlConnection.getInputStream());
			Gson gson = new Gson();
			Reader reader = new InputStreamReader(source);
			SpotsResult spotsResult = gson.fromJson(reader, SpotsResult.class);
			List<SpotData> spots = spotsResult.getSpots();
			updateSpots(spots);
			boolean spotsChanged = spotsChanged(context); 
			saveSpotsToLocalStorage(context);
			updateCachedImages(context, spotsChanged);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return getAllSpots(context);
	}
	
	private static boolean spotsChanged(Context context){
		try{
			String newSpotsString = Util.objectToString((Serializable)mSpots);
			String currentSpotsString = LocalStorage.getCachedSpots(context);
			return !newSpotsString.equals(currentSpotsString);
		}
		catch(Exception ex){
			return true;
		}
		
	}

	private static void saveSpotsToLocalStorage(Context context){
		try {
			synchronized(SpotStorage.class){
				String saveString = Util.objectToString((Serializable)mSpots);
				LocalStorage.setCachedSpots(context, saveString);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadSpotsFromLocalStorage(Context context){
		String spotsString = LocalStorage.getCachedSpots(context);
		if (spotsString!=null){
			synchronized(SpotStorage.class){
				try {
					mSpots = (HashMap<Integer, SpotData>)Util.objectFromString(spotsString);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}

		}

	}


	public static boolean sameSpots(List<SpotData> list1, List<SpotData> list2){
		if (list1==null && list2==null) return true;
		if (list1==null && list2!=null) return false;
		if (list1!=null && list2==null) return false;
		if (list1==list2) return true;
		if (list1.size()!=list2.size()) return false;
		for (int i = 0; i<list1.size(); i++){
			SpotData sd1 = list1.get(i);
			SpotData sd2 = list2.get(i);
			if (!sd1.equals(sd2)) return false;
		}
		return true;

	}



	public static void loadSpotsAsync(final Context context, final SpotsLoadedCallback spotsLoadedCallback) {
		new AsyncTask<Void, Void, List<SpotData> >() {
			protected List<SpotData>  doInBackground(Void... params) {
				List<SpotData> result = SpotStorage.loadSpotsFromCache(context);; 
				return result;
			}

			protected void onPostExecute(List<SpotData>  result) {
				spotsLoadedCallback.spotsLoaded(true, result);
				updateSpotsFromInetAsync(context, spotsLoadedCallback);
			};
		}.execute();

	}

	public static void updateSpotsFromInetAsync(final Context context, final SpotsLoadedCallback spotsLoadedCallback) {
		new AsyncTask<Void, Void, List<SpotData> >() {
			protected List<SpotData>  doInBackground(Void... params) {
				List<SpotData> result = SpotStorage.loadSpotsFromInet(context);
				return result;
			}

			protected void onPostExecute(List<SpotData>  result) {
				spotsLoadedCallback.spotsLoaded(false, result);
			};

		}.execute();

	}

	public static void updateCachedImages(Context context, boolean forceReload){

		init(context);
		for (SpotData spotData:getAllSpots(context)){
			String imageUrl = spotData.getSpotBitmapLocation();
			int spotId = spotData.getSiteID();
			String filename = "spot_"+spotId+".png";
			boolean fileExists = context.getFileStreamPath(filename).exists();
			if (forceReload || !fileExists){
				Bitmap image = getBitmapFromURL(imageUrl);
				saveImage(context,image,filename);
			}
		}		
	}


	
	public static void saveImage(Context context,Bitmap image, String filename){
		FileOutputStream outputStream;
		try {
			/*
            ONTHOUDEN!
            Als je de file opsla via context.openFileOutput dan wordt deze opgeslagen
            in locatie:/data/data/com.vdzon.windapp/files/

            Als je deze file weer wilt openen, dan doe je dat met:
            Bitmap locationImage =BitmapFactory.decodeFile(context.getFileStreamPath(siteData.getLocationFolder()+".png").getAbsolutePath());
			 */
			if (image!=null){
				outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
				image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
				outputStream.flush();
				outputStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
