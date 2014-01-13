package com.vdzon.windapp.widgetproviders;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.vdzon.windapp.R;
import com.vdzon.windapp.activity.MainActivity;
import com.vdzon.windapp.async.InitializeGcmAsync;
import com.vdzon.windapp.consts.Const;
import com.vdzon.windapp.db.LocalStorage;
import com.vdzon.windapp.db.SpotStorage;
import com.vdzon.windapp.pojo.Bar;
import com.vdzon.windapp.pojo.SpotWindData;
import com.vdzon.windapp.pojo.WidgetLayoutDetails;
import com.vdzon.windapp.util.PaintUtil;
import com.vdzon.windapp.util.Util;

public abstract class WindWidgetProvider extends AppWidgetProvider {

	static final String TAG = WindWidgetProvider.class.getSimpleName();

	public abstract int getDefaultWidgetCols();
	public abstract int getDefaultWidgetRows();

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		addToWidgetIDs(context, appWidgetIds);
		initializeGoogleCloudMessaging(context);
		startAlarm(context);
		initialize(context);
	}

	private void initialize(Context context) {
		updateWidgets(context);
	}

	private void initializeGoogleCloudMessaging(Context context){
		InitializeGcmAsync initializeGoogleCloudMessagingAsync = new InitializeGcmAsync();
		initializeGoogleCloudMessagingAsync.execute(new Context[]{  context });
	}

	@Override
	public void onDeleted (Context context, int[] appWidgetIds) {
		removeFromWidgetIDs(context, appWidgetIds);
	}

	private Set<Integer> getAllWidgetIDs(Context context){
		Set<Integer> result = new HashSet<Integer>();
		String idsString = LocalStorage.getAllWidgetIDs(context);
		String[] idsStrArray = idsString.trim().split(" ");
		for (String idStr:idsStrArray){
			try{
				Integer id = new Integer(idStr);
				result.add(id);
			}
			catch(Exception ex){
			}
		}
		return result;
	}

	private void saveWidgetIDs(Context context, Set<Integer> widgetIDs){
		String widgetIDsStr = "";
		for (Integer id:widgetIDs){
			widgetIDsStr+=""+id+" ";
		}
		LocalStorage.setAllWidgetIDs(context, widgetIDsStr);
	}

	private void addToWidgetIDs(Context context, int[] newIDs){
		Set<Integer> widgets = getAllWidgetIDs(context);
		for (int id:newIDs){
			if (!widgets.contains(new Integer(id))){
				widgets.add(new Integer(id));
			}
		}
		saveWidgetIDs(context, widgets);
	}

	private void removeFromWidgetIDs(Context context, int[] newIDs){
		Set<Integer> widgets = getAllWidgetIDs(context);
		for (int id:newIDs){
			if (widgets.contains(new Integer(id))){
				widgets.remove(new Integer(id));
			}
		}
		saveWidgetIDs(context, widgets);
	}


	@Override
	public void onEnabled(Context context) {
		startAlarm(context);
	}

	@Override
	public void onDisabled(Context context) {
		stopAlarm(context);
	}


	public void startAlarm(Context context) {
		Intent intent = new Intent();
		intent.setAction(Const.UPDATE_DB_ACTION);
		PendingIntent pendingIntent2 = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10*60 * 1000, pendingIntent2);
	}


	public void stopAlarm(Context context) {
		Intent intent = new Intent();
		intent.setAction(Const.UPDATE_DB_ACTION);
		PendingIntent pendingIntent2 = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent2);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int day = Util.calculateDay(new Date());
		if (intent.getAction().equals("com.sec.android.widgetapp.APPWIDGET_RESIZE")) {
			Bundle bundle = intent.getExtras();
			int appWidgetId = bundle.getInt("widgetId",-1); // bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			int widgetSpanX = bundle.getInt("widgetspanx", 1);
			int widgetSpanY = bundle.getInt("widgetspany", 1);
			widgetResized(context, appWidgetId, widgetSpanX,widgetSpanY);
			updateWidgets(context);
		}
		if (intent.getAction().equals(Const.WIDGET_UPDATE_ACTION)) {
			updateWidgets(context);
		}
		super.onReceive(context, intent);
	}


	public void updateWidgets(Context context) {
		int day = Util.calculateDay(new Date());
		for (Integer appWidgetId: getAllWidgetIDs(context)){
			SpotWindData spotWindData = new SpotWindData();
			int spotID = LocalStorage.getSpotID(context, appWidgetId); 
			spotWindData.load(context, SpotStorage.getSpotData(context, spotID), day);
			updateWidgetPictureAndButtonListener(context, spotWindData,appWidgetId);
		}
	}

	public WidgetLayoutDetails getWidgetDetails(Context context, int appWidgetID){
		int cols = LocalStorage.getWidgetCols(context, appWidgetID);
		int rows = LocalStorage.getWidgetRows(context, appWidgetID);
		if (cols==-1 || rows==-1){
			cols = getDefaultWidgetCols();
			rows = getDefaultWidgetRows();
			widgetResized(context, appWidgetID, cols, rows);

		}
//		int pixelWidthDp = 70*cols-30; 
//		int pixelHeightDp = 70*rows-30;
		int pixelWidthDp = 70*cols; 
		int pixelHeightDp = 70*rows;
		int spotImageDp = 70;
		
		int pixelWidthPx = (int)Util.convertDpToPixel(pixelWidthDp)*2; // 2 times as much for better resolution
		int pixelHeightPx = (int)Util.convertDpToPixel(pixelHeightDp)*2; // 2 times as much for better resolution
		int spotImagePx = (int)Util.convertDpToPixel(spotImageDp)*2; // 2 times as much for better resolution
		WidgetLayoutDetails widgetLayoutDetails = new WidgetLayoutDetails();
		widgetLayoutDetails.widgetHeight=pixelHeightPx;
		widgetLayoutDetails.widgetWidth=pixelWidthPx;
		widgetLayoutDetails.textPos=spotImagePx+100;
		widgetLayoutDetails.arrowImageHeight = spotImagePx;
		widgetLayoutDetails.spotImageHeight = spotImagePx;
		widgetLayoutDetails.drawTimeline = false;
		widgetLayoutDetails.drawGraph = rows>1;
		widgetLayoutDetails.drawImage = true;
		widgetLayoutDetails.drawImageText = cols>1;
		widgetLayoutDetails.drawWindInCorner = cols==1;
		widgetLayoutDetails.drawWhiteBackground = cols!=1 || rows!=1;
		widgetLayoutDetails.windSpeedCircelDiameter = spotImagePx/4;
		
		if (widgetLayoutDetails.drawGraph && cols>1){
			widgetLayoutDetails.xOffsetImage = 50;
		}
		
		if (rows>1 && cols>1){
			widgetLayoutDetails.yOffsetImage = 5;
		}
		else{
			widgetLayoutDetails.yOffsetImage = 0;
		}
		
		return widgetLayoutDetails;
	}



	private void widgetResized(Context context, int appWidgetID, int minWidth, int minHeight){
		LocalStorage.setWidgetCols(context, appWidgetID, minWidth);
		LocalStorage.setWidgetRows(context, appWidgetID, minHeight);
	}


	public void updateWidgetPictureAndButtonListener(Context context, SpotWindData spotDayData,int appWidgetID ) {
		WidgetLayoutDetails widgetLayoutDetails = getWidgetDetails(context, appWidgetID);
		Bitmap bitmapCombined = Bitmap.createBitmap(widgetLayoutDetails.widgetWidth, widgetLayoutDetails.widgetHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmapCombined);
		int startX = 50;
		int endX = canvas.getWidth()-30;
		int startY = canvas.getHeight()/3+10;
		int endY = canvas.getHeight()-30;
		Set<Bar> windGraphPath = PaintUtil.createWindGraph(spotDayData, startX, startY, endX, endY);
		Set<Bar> gustGraphPath = PaintUtil.createGustGraph(spotDayData, startX, startY, endX, endY);
		PaintUtil.paintCanvas(context, widgetLayoutDetails, canvas, spotDayData, windGraphPath, gustGraphPath, canvas.getWidth(), true);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget_layout);
		remoteViews.setImageViewBitmap(R.id.imageViewCircle, bitmapCombined);
		remoteViews.setOnClickPendingIntent(R.id.widgetLayout, buildButtonPendingIntent(context, appWidgetID, spotDayData));
		pushWidgetUpdate(context.getApplicationContext(), remoteViews,appWidgetID);
	}


	public static PendingIntent buildButtonPendingIntent(Context context, int appWidgetID, SpotWindData spotDayData) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.putExtra(Const.EXTRA_INTENTINFO_WIDGETID, appWidgetID);
		if (spotDayData!=null){
			intent.putExtra(Const.EXTRA_INTENTINFO_SPOTID, spotDayData.getSpotData().getSiteID());
		}
		intent.setAction("AppWidgetID_"+appWidgetID);//this to make sure that the pendingIntends of all widgets on the screen are different. Otherwise it will be reused
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;

	}


	public void pushWidgetUpdate(Context context, RemoteViews remoteViews, int widgetID) {
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		try {
			manager.updateAppWidget(widgetID, remoteViews);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
