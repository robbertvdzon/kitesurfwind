package com.vdzon.windapp.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class LocalStorage {

	public static String getDBName(){
		return "com.vdzon.windapp.db.LocalStorage";
	}
	
	private static String FIELD_MIN_WIND = "minWind";
	private static String FIELD_OPTIMAL_WIND = "optimalWind";
	private static String FIELD_MUCH_WIND = "muchWind";
	private static String FIELD_TOOMUCH_WIND = "toomuchWind";
	private static String FIELD_SPOT_ID = "spotId";
	private static String FIELD_FAVORITE_SPOT_ID = "favoriteSpotId";
	private static String FIELD_CACHE_SPOTS = "cachedSpots";
	private static String FIELD_GCM_REGID = "gcmRegID";
	private static String FIELD_APP_VERSION = "appVersion";
	private static String FIELD_ALL_WIDGET_IDS = "allWidgetIDs";
	private static String FIELD_WIDGET_COLS = "widgetCols";
	private static String FIELD_WIDGET_ROWS = "widgetRows";
	
	public static float getMinimalWind(Context context){return getFloat(context,FIELD_MIN_WIND,8);}
	public static void setMinimalWind(Context context, float value){setFloat(context,FIELD_MIN_WIND,value);}

	public static float getOptimalWind(Context context){return getFloat(context,FIELD_OPTIMAL_WIND,10);}
	public static void setOptimalWind(Context context, float value){setFloat(context,FIELD_OPTIMAL_WIND,value);}

	public static float getMuchWind(Context context){return getFloat(context,FIELD_MUCH_WIND,15);}
	public static void setMuchWind(Context context, float value){setFloat(context,FIELD_MUCH_WIND,value);}
	
	public static float getToomuchWind(Context context){return getFloat(context,FIELD_TOOMUCH_WIND,18);}
	public static void setToomuchWind(Context context, float value){setFloat(context,FIELD_TOOMUCH_WIND,value);}
	
	public static int getSpotID(Context context, long mAppWidgetId){return getInt(context,FIELD_SPOT_ID,getDBName()+mAppWidgetId,1);}
	public static void setSpotID(Context context, long mAppWidgetId, int value){setInt(context,FIELD_SPOT_ID,getDBName()+mAppWidgetId,value);}

	public static int getFavoriteSpotID(Context context){return getInt(context,FIELD_FAVORITE_SPOT_ID,1);}
	public static void setFavoriteSpotID(Context context, int value){setInt(context,FIELD_FAVORITE_SPOT_ID,value);}

	public static String getCachedSpots(Context context){return getString(context,FIELD_CACHE_SPOTS,"");}
	public static void setCachedSpots(Context context, String value){setString(context,FIELD_CACHE_SPOTS,value);}

	public static String getGcmRegID(Context context){return getString(context,FIELD_GCM_REGID,"");}
	public static void setGcmRegID(Context context, String value){setString(context,FIELD_GCM_REGID,value);}
	
	public static int getAppVersion(Context context){return getInt(context,FIELD_APP_VERSION,1);}
	public static void setAppVersion(Context context, int value){setInt(context,FIELD_APP_VERSION,value);}

	public static String getAllWidgetIDs(Context context){return getString(context,FIELD_ALL_WIDGET_IDS,"");}
	public static void setAllWidgetIDs(Context context, String value){setString(context,FIELD_ALL_WIDGET_IDS,value);}

	public static int getWidgetCols(Context context, long mAppWidgetId){return getInt(context,FIELD_WIDGET_COLS,getDBName()+mAppWidgetId,-1);}
	public static void setWidgetCols(Context context, long mAppWidgetId, int value){setInt(context,FIELD_WIDGET_COLS,getDBName()+mAppWidgetId,value);}
	
	public static int getWidgetRows(Context context, long mAppWidgetId){return getInt(context,FIELD_WIDGET_ROWS,getDBName()+mAppWidgetId,-1);}
	public static void setWidgetRows(Context context, long mAppWidgetId, int value){setInt(context,FIELD_WIDGET_ROWS,getDBName()+mAppWidgetId,value);}
	
	
	/*
	 * All private functions for storing and getting values
	 */
	private static int getInt(Context context, String fieldName, int defaultValue){
		return getInt(context, getDBName(), fieldName, defaultValue);
	}

	private static void setInt(Context context, String fieldName, int value){
		setInt(context, getDBName(), fieldName, value);
	}

	private static float getFloat(Context context, String fieldName, float defaultValue){
		return getFloat(context, getDBName(), fieldName, defaultValue);
	}

	private static void setFloat(Context context, String fieldName, float value){
		setFloat(context, getDBName(), fieldName, value);
	}


	private static String getString(Context context, String fieldName, String defaultValue){
		return getString(context, getDBName(), fieldName, defaultValue);
	}

	private static void setString(Context context, String fieldName, String value){
		setString(context, getDBName(), fieldName, value);
	}	

	private static int getInt(Context context, String dbName, String fieldName, int defaultValue){
		SharedPreferences sp = context.getSharedPreferences(dbName, Context.MODE_PRIVATE);
		return sp.getInt(fieldName, defaultValue);
	}

	private static void setInt(Context context, String dbName, String fieldName, int value){
		SharedPreferences sp = context.getSharedPreferences(dbName , Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(fieldName, value);
		editor.commit();
	}

	private static float getFloat(Context context, String dbName, String fieldName, float defaultValue){
		SharedPreferences sp = context.getSharedPreferences(dbName, Context.MODE_PRIVATE);
		return sp.getFloat(fieldName, defaultValue);
	}

	private static void setFloat(Context context, String dbName, String fieldName, float value){
		SharedPreferences sp = context.getSharedPreferences(dbName , Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putFloat(fieldName, value);
		editor.commit();
	}

	private static String getString(Context context, String dbName, String fieldName, String defaultValue){
		SharedPreferences sp = context.getSharedPreferences(dbName, Context.MODE_PRIVATE);
		return sp.getString(fieldName, defaultValue);
	}

	private static void setString(Context context, String dbName, String fieldName, String value){
		SharedPreferences sp = context.getSharedPreferences(dbName , Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(fieldName, value);
		editor.commit();
	}

}
