package com.vdzon.windapp.util;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.vdzon.windapp.R;
import com.vdzon.windapp.db.ImageStorage;
import com.vdzon.windapp.db.LocalStorage;
import com.vdzon.windapp.pojo.Bar;
import com.vdzon.windapp.pojo.Images;
import com.vdzon.windapp.pojo.SpotWindData;
import com.vdzon.windapp.pojo.WidgetLayoutDetails;
import com.vdzon.windapp.pojo.WindData;

/**
 * Created by Robbert on 11/27/13.
 */
public class PaintUtil {
	static final String TAG = "PaintUtil";
	static final int MAXWIND = 30;
	private static final int Y_RASTERCOUNT_LARGE = 6;
	private static final int Y_RASTERCOUNT_MEDIUM = 3;
	private static final int Y_RASTERCOUNT_SMALL = 4;
	private static final int X_RASTERCOUNT_LARGE = 12;
	private static final int X_RASTERCOUNT_MEDIUM = 6;
	private static final int X_RASTERCOUNT_SMALL = 3;

	enum WIND_DIRECTORION {
		AFLANDIG, SIDESHORE, AANLANDIG, OUTDATED
	}

	enum WIND_POWER{
		TOO_SHORT, OK, GOOD, WARNING, DANGER, OUTDATED
	}

	private static void drawSpotImage(Bitmap spotBitmap, WidgetLayoutDetails widgetLayoutDetails,  Canvas canvas, int xOffset, int yOffset){
		Paint paint = new Paint();
		float origImgSize = spotBitmap.getHeight();
		float scale = widgetLayoutDetails.spotImageHeight/origImgSize;
		Matrix spotImageMatrix = new Matrix();
		spotImageMatrix.setScale(scale, scale);
		spotImageMatrix.postTranslate(xOffset, yOffset);
		canvas.drawBitmap(spotBitmap, spotImageMatrix, paint);
	}

	private static void drawWhiteBackground(WidgetLayoutDetails widgetLayoutDetails,  Canvas canvas){
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.FILL); 
		paint.setAlpha(95); 
		Rect rect = new Rect(0, 0,widgetLayoutDetails.widgetWidth, widgetLayoutDetails.widgetHeight);
		RectF rectF = new RectF(rect);
		canvas.drawRoundRect(rectF, 10, 10, paint);
	}

	private static void paintGraph(Canvas canvas, Set<Bar> windGraphPath,Set<Bar> gustGraphPath){
		// paint wind
		Paint pathPaint = new Paint();
		pathPaint.setColor(Color.argb(50, 0, 100, 0));
		pathPaint.setStyle(Style.FILL); 
		pathPaint.setStrokeWidth(1); 
		for (Bar bar:windGraphPath){
			canvas.drawRect(bar.getX1(),bar.getY1(),bar.getX2(),bar.getY2(), pathPaint);
		}

		// paint gust
		Paint pathGustPaint = new Paint();
		pathGustPaint.setColor(Color.argb(50, 100, 0, 0));
		pathGustPaint.setStyle(Style.FILL); 
		pathGustPaint.setStrokeWidth(1); 
		for (Bar bar:gustGraphPath){
			canvas.drawRect(bar.getX1(),bar.getY1(),bar.getX2(),bar.getY2(), pathGustPaint);
		}
	}

	private static void paintArrowsInGraph(Canvas canvas, SpotWindData spotWindData, Images images, int startX, int endX, int xSteps, int startY, int endY,int stepXCount){
		Paint paint = new Paint();
		int hourPos = 0;
		for (int x = startX; x<=endX; x+=xSteps){
			canvas.drawLine(x, startY, x, endY, paint);
			WindData windData = spotWindData.getWindData(hourPos);
			if (windData!=null){
				Matrix arrowImageMatrix = new Matrix();
				float origArrowSize = images.getArrowBitmap().getHeight();
				int arrowSize = 30;
				float arrowScale = arrowSize/origArrowSize;
				int rotation = windData.getAngle();
				arrowImageMatrix.postScale(arrowScale, arrowScale);
				arrowImageMatrix.postRotate(rotation, arrowSize / 2, arrowSize / 2);
				arrowImageMatrix.postTranslate(x-arrowSize / 2, startY+5);
				canvas.drawBitmap(images.getArrowBitmap(), arrowImageMatrix, paint);
			}
			hourPos+=24/stepXCount;
		}
	}


	private static void paintTimeMarkLine(Canvas canvas, int endX, int startX,  WidgetLayoutDetails widgetLayoutDetails, float pos){
		int tenMinuteStep = (endX-startX)/(24*6);// 
		if (tenMinuteStep==0) tenMinuteStep = 1;
		Paint paint = new Paint();
		paint.setColor(Color.GRAY);
		paint.setStyle(Style.FILL); 
		paint.setStrokeWidth(1); 
		canvas.drawRect(pos-tenMinuteStep, 0, pos, widgetLayoutDetails.widgetHeight, paint);
	}

	private static void paintRaster(Canvas canvas, int startX, int endX, int xSteps, int endY, int startY, int stepXCount, int ySteps, int stepYCount){
		Paint paint = new Paint();
		paint.setColor(Color.GRAY);

		Paint fontPaint = new Paint();
		fontPaint.setColor(Color.BLACK); 
		fontPaint.setTextSize(20);

		// paint vertical lines
		int timeScale = 0;

		for (int x = startX; x<=endX; x+=xSteps){
			canvas.drawLine(x, startY, x, endY, paint);
			if (timeScale>0){
				canvas.drawText(""+timeScale, x-10, endY+20, fontPaint);
			}
			timeScale+=24/stepXCount;//2
		}

		// paint horizontal lines
		int windScale = MAXWIND;
		for (int y = startY; y<=endY; y+=ySteps){
			canvas.drawLine(startX, y, endX, y, paint);
			if (windScale>0){
				canvas.drawText(""+windScale, 10, y+10, fontPaint);
			}
			windScale-=MAXWIND/stepYCount;
		}
	}

	public static int calculateXCount(WidgetLayoutDetails widgetLayoutDetails){
		if (widgetLayoutDetails.widgetWidth<400){
			return X_RASTERCOUNT_SMALL;
		}
		if (widgetLayoutDetails.widgetWidth<700){
			return X_RASTERCOUNT_MEDIUM;
		}
		return X_RASTERCOUNT_LARGE;
	}

	public static int calculateYCount(WidgetLayoutDetails widgetLayoutDetails){
		if (widgetLayoutDetails.widgetHeight<400){
			return Y_RASTERCOUNT_SMALL;
		}
		if (widgetLayoutDetails.widgetHeight<700){
			return Y_RASTERCOUNT_MEDIUM;
		}
		return Y_RASTERCOUNT_LARGE;
	}

	public static int calculateXSteps(int endX, int startX, int stepXCount){
		int xSteps = (endX-startX)/stepXCount;
		if (xSteps==0) xSteps = 1;
		return xSteps;
	}

	public static int calculateYSteps(int endY, int startY, int stepYCount){
		int ySteps = (endY-startY)/stepYCount;
		if (ySteps==0) ySteps = 1;
		return ySteps;
	}


	public static double calculateTouchedTime(float touchPos, int startX, int graphWidth, WindData firstWindData, WindData lastWindData){
		double translatedPos = touchPos-startX;
		double time = 24*translatedPos/graphWidth;
		if (time<0) time =0;
		if (time>24) time =24;

		if (firstWindData!=null && time<firstWindData.getStartTime()) {
			time = firstWindData.getStartTime()+0.02; 
		}
		if (lastWindData!=null && time>lastWindData.getEndTime()){
			time = lastWindData.getEndTime()-0.02; 
		}
		return time;
	}


	private static void paintWindInCorner(Canvas canvas, int windSpeedCircelDiameter, int windInMsec, int xOffset, int yOffset){
		int windSpeedCircelX = xOffset+windSpeedCircelDiameter/2+10;
		int windSpeedCircelY = yOffset+windSpeedCircelDiameter/2+10;
		int strokeWidth = 4;
		int windSpeedCircelTextLeftBelow10 = xOffset+32;
		int windSpeedCircelTextLeftMoreThen10 = xOffset+22;
		int windSpeedCircelTextTop = 61;	

		Paint paintCircleInner= new Paint();
		Paint paintCircle = new Paint();
		Paint paint = new Paint();

		paintCircleInner.setColor(Color.GRAY);
		paintCircleInner.setStyle(Paint.Style.FILL);
		paintCircleInner.setAntiAlias(true);
		canvas.drawCircle(windSpeedCircelX, windSpeedCircelY, windSpeedCircelDiameter/2, paintCircleInner);

		paintCircle.setColor(Color.BLACK);
		paintCircle.setStyle(Paint.Style.STROKE);
		paintCircle.setStrokeWidth(strokeWidth);
		paintCircle.setAntiAlias(true);
		canvas.drawCircle(windSpeedCircelX, windSpeedCircelY, windSpeedCircelDiameter/2, paintCircle);

		paint.setColor(Color.WHITE);
		paint.setTextSize(40);

		if (windInMsec<10){
			canvas.drawText(""+windInMsec, windSpeedCircelTextLeftBelow10, windSpeedCircelTextTop, paint);
		}
		else{
			canvas.drawText(""+windInMsec, windSpeedCircelTextLeftMoreThen10, windSpeedCircelTextTop, paint);
		}
	}

	private static void paintAdviceIcon(Canvas canvas,  int windSpeedCircelDiameter, Bitmap globalAdviceBitmap, int adviceStatusIconLeft, int adviceStatusIconTop, int xOffset, int yOffset){
		Matrix matrixGlbalAdvice = new Matrix();
		float validBitmapScale =(float)(windSpeedCircelDiameter)/(float)globalAdviceBitmap.getHeight();
		matrixGlbalAdvice.postScale(validBitmapScale, validBitmapScale);
		matrixGlbalAdvice.postTranslate(adviceStatusIconLeft, adviceStatusIconTop);
		matrixGlbalAdvice.postTranslate(xOffset, yOffset);
		canvas.drawBitmap(globalAdviceBitmap, matrixGlbalAdvice, null);
	}

	private static int getWindPowerAdviceLevel(WIND_POWER windPower){
		int powerAdviceLevel = 0;
		switch(windPower){
		case OK:
			powerAdviceLevel = 0;
			break;
		case GOOD:
			powerAdviceLevel = 1;
			break;
		case TOO_SHORT:
			powerAdviceLevel = 2;
			break;
		case WARNING:
			powerAdviceLevel = 3;
			break;
		case DANGER:
			powerAdviceLevel = 4;
			break;
		case OUTDATED:
			powerAdviceLevel = 5;
			break;
		}
		return powerAdviceLevel;
	}


	private static int getAngleAdviceLevel(WIND_DIRECTORION windDirectorion){
		int angleAdviceLevel = 0;
		switch(windDirectorion){
		case AANLANDIG:
			angleAdviceLevel = 0;
			break;
		case SIDESHORE:
			angleAdviceLevel = 3;
			break;
		case AFLANDIG:
			angleAdviceLevel = 4;
			break;
		case OUTDATED:
			angleAdviceLevel = 5;
			break;
		}
		return angleAdviceLevel;
	}


	private static Bitmap getGlobalAdviceBitmap(Images images, int angleAdviceLevel, int powerAdviceLevel){
		Bitmap globalAdviceBitmap = null;
		int advice = angleAdviceLevel;
		if (advice<powerAdviceLevel) advice = powerAdviceLevel;
		switch(advice){
		case 0:
			globalAdviceBitmap = images.getOkBitmap();
			break;
		case 1:
			globalAdviceBitmap = images.getGoodBitmap();
			break;
		case 2:
			globalAdviceBitmap = images.getSadFaceBitmap();
			break;
		case 3:
			globalAdviceBitmap = images.getWarningBitmap();
			break;
		case 4:
			globalAdviceBitmap = images.getDangerBitmap();
			break;
		case 5:
			globalAdviceBitmap = images.getUnknowmBitmap();
			break;
		}
		return globalAdviceBitmap;
	}

	private static int getWindPowerColor(WIND_POWER windPower){
		int windPowerColor = Color.WHITE;
		switch(windPower){
		case OK:
			windPowerColor = Color.GREEN;
			break;
		case GOOD:
			windPowerColor = Color.GREEN;
			break;
		case TOO_SHORT:
			windPowerColor = Color.WHITE;
			break;
		case WARNING:
			windPowerColor = Color.parseColor("#ffa500");
			break;
		case DANGER:
			windPowerColor = Color.RED;
			break;
		case OUTDATED:
			windPowerColor = Color.WHITE;
			break;
		}	
		return windPowerColor;
	}



	private static String getAngleAdviceText(Context context, WIND_DIRECTORION windDirectorion){
		String windDirectionText = "";
		switch(windDirectorion){
		case AANLANDIG:
			windDirectionText = context.getResources().getString(R.string.graphics_onshore);
			break;
		case SIDESHORE:
			windDirectionText = context.getResources().getString(R.string.graphics_sideshore);
			break;
		case AFLANDIG:
			windDirectionText = context.getResources().getString(R.string.graphics_offshore);
			break;
		case OUTDATED:
			windDirectionText = context.getResources().getString(R.string.graphics_no_data);
			break;
		}
		return windDirectionText;
	}


	private static WIND_POWER calculateWindPower(Context context, WindData windData){
		if (windData.getAngle()==-1 || windData.getWind()==-1 || windData.getGust()==-1){
			return WIND_POWER.OUTDATED;
		}

		double wind = windData.getWind();
		double minWind = LocalStorage.getMinimalWind(context);
		double optimalWind = LocalStorage.getOptimalWind(context);
		double muchWind = LocalStorage.getMuchWind(context);
		double tooMuchWind = LocalStorage.getToomuchWind(context);

		if (wind<=minWind) return WIND_POWER.TOO_SHORT;
		if ((wind>minWind) && (wind<=optimalWind)) return WIND_POWER.OK;
		if ((wind>optimalWind) && (wind<=muchWind)) return WIND_POWER.GOOD;
		if ((wind>muchWind) && (wind<=tooMuchWind)) return WIND_POWER.WARNING;
		if (wind>tooMuchWind) return WIND_POWER.DANGER;
		return WIND_POWER.DANGER;// should never happen
	}

	private static WIND_DIRECTORION calculateWindAngleStatus(WindData windData, SpotWindData spotWindData){
		int angleDiff = windData.getAngle() - spotWindData.getSpotData().getLocationDirection();
		// when angle diff < -180, then add 360
		if (angleDiff<-180) angleDiff+=360;
		// make absolute
		if (angleDiff<0) angleDiff=angleDiff*-1;
		// check wind direction
		if (angleDiff<=85) return WIND_DIRECTORION.AANLANDIG;
		if ((angleDiff>85) && (angleDiff<=95)) return WIND_DIRECTORION.SIDESHORE;
		if (angleDiff>95) return WIND_DIRECTORION.AFLANDIG;
		return WIND_DIRECTORION.AFLANDIG;// should never happen
	}


	private static void paintArrow(Canvas canvas, WidgetLayoutDetails widgetLayoutDetails, Images images, WindData windData, int xOffset, int yOffset){
		Paint paint = new Paint();
		Matrix arrowImageMatrix = new Matrix();
		float origArrowSize = images.getArrowBitmap().getHeight();
		float arrowScale = widgetLayoutDetails.spotImageHeight/origArrowSize;
		float scaleForWind = (float)windData.getWind()/MAXWIND;
		if (scaleForWind>1) scaleForWind =1;
		int rotation = windData.getAngle();
		double arrowHeight = widgetLayoutDetails.spotImageHeight * scaleForWind;
		int offset = ((int)widgetLayoutDetails.spotImageHeight - (int) arrowHeight) / 2;

		arrowImageMatrix.postScale(arrowScale, arrowScale);
		arrowImageMatrix.postRotate(rotation, widgetLayoutDetails.spotImageHeight / 2, widgetLayoutDetails.spotImageHeight / 2);
		arrowImageMatrix.postScale(scaleForWind, scaleForWind);
		arrowImageMatrix.postTranslate(offset, offset);
		arrowImageMatrix.postTranslate(xOffset, yOffset);
		canvas.drawBitmap(images.getArrowBitmap(), arrowImageMatrix, paint);
	}


	private static void paintImageText(Context context, WidgetLayoutDetails widgetLayoutDetails, Canvas canvas, WindData windData, int xOffset, int yOffset){
		int xPosText = xOffset+(int)widgetLayoutDetails.spotImageHeight+30;
		int yPosTextTime = yOffset+(int)(widgetLayoutDetails.spotImageHeight/4)*1;
		int yPosTextWind = yOffset+(int)(widgetLayoutDetails.spotImageHeight/4)*2;
		int yPosTextAngle = yOffset+(int)(widgetLayoutDetails.spotImageHeight/4)*3;

		// paint text data
		Paint textPaint = new Paint();
		textPaint.setColor(Color.DKGRAY);
		textPaint.setTextSize(30);

		String timeStr = context.getResources().getString(R.string.graphics_no_data);
		String windStr = context.getResources().getString(R.string.graphics_no_data);
		String angleStr = context.getResources().getString(R.string.graphics_no_data);
		if (windData!=null){
			int wind = (int)Math.round(windData.getWind());
			int gust = (int)Math.round(windData.getGust());
			timeStr = windData.getTimeStr();
			windStr = wind+" - "+gust+" "+context.getResources().getString(R.string.graphics_mps);
			angleStr = windData.getAngle() + " "+context.getResources().getString(R.string.graphics_degrees);
		}

		canvas.drawText(timeStr,xPosText,yPosTextTime, textPaint);
		canvas.drawText(windStr,xPosText,yPosTextWind, textPaint);
		canvas.drawText(angleStr,xPosText,yPosTextAngle, textPaint);
	}
	
	private static void paintImageTextOutdated(Context context, WidgetLayoutDetails widgetLayoutDetails, Canvas canvas, int xOffset, int yOffset){
		int xPosText = xOffset+(int)widgetLayoutDetails.spotImageHeight+30;
		int yPosTextTime = yOffset+(int)(widgetLayoutDetails.spotImageHeight/4)*1;
		int yPosTextWind = yOffset+(int)(widgetLayoutDetails.spotImageHeight/4)*2;
		int yPosTextAngle = yOffset+(int)(widgetLayoutDetails.spotImageHeight/4)*3;

		// paint text data
		Paint textPaint = new Paint();
		textPaint.setColor(Color.DKGRAY);
		textPaint.setTextSize(30);

		String timeStr = context.getResources().getString(R.string.graphics_no_data);
		String windStr = context.getResources().getString(R.string.graphics_no_data);
		String angleStr = context.getResources().getString(R.string.graphics_no_data);
		canvas.drawText(timeStr,xPosText,yPosTextTime, textPaint);
		canvas.drawText(windStr,xPosText,yPosTextWind, textPaint);
		canvas.drawText(angleStr,xPosText,yPosTextAngle, textPaint);
	}
		

	public static void paintCanvas(Context context, WidgetLayoutDetails widgetLayoutDetails, Canvas canvas, SpotWindData spotWindData,Set<Bar> windGraphPath,Set<Bar> gustGraphPath, float touchPos, boolean checkOutDated) {
		// define graph boundaries
		int startGraphX = 50;
		int startGraphY = widgetLayoutDetails.spotImageHeight+10;
		int endGraphY = widgetLayoutDetails.widgetHeight-30;
		int endGraphX = widgetLayoutDetails.widgetWidth-30;
		int graphWidth = endGraphX-startGraphX;
		int yOffsetImage = widgetLayoutDetails.yOffsetImage;
		int xOffsetImage = widgetLayoutDetails.xOffsetImage;

		WindData lastWindData = spotWindData.getLastWindData();
		WindData firstWindData = spotWindData.getFirstWindData();

		// calculate the time that is touched (if no data is available, use the time most nearby)
		double time = calculateTouchedTime(touchPos, startGraphX, graphWidth, firstWindData, lastWindData);
		double selectedPos = (time*graphWidth)/24+startGraphX;

		// check preconditions
		if (spotWindData==null) return;
		if (spotWindData.getSpotData()==null) return;

		// get images
		Images images = ImageStorage.getImages(context);		
		if (images.getArrowBitmap()==null) return;

		// load spot bitmap
		Bitmap spotBitmap = ImageStorage.getSpotImage(context, spotWindData.getSpotData().getSiteID());
		if (spotBitmap==null) return;

		// draw white background
		if (widgetLayoutDetails.drawWhiteBackground){
			drawWhiteBackground(widgetLayoutDetails,  canvas);
		}

		// draw graph
		if (widgetLayoutDetails.drawGraph){
			int stepXCount = calculateXCount(widgetLayoutDetails);
			int stepYCount = calculateYCount(widgetLayoutDetails);
			int xSteps = calculateXSteps(endGraphX, startGraphX, stepXCount);
			int ySteps = calculateYSteps(endGraphY, startGraphY, stepYCount);
			// recalculate endX and endY with the new xSteps
			endGraphX = startGraphX+xSteps*stepXCount;
			endGraphY = startGraphY+ySteps*stepYCount;

			// paint time mark line
			if (widgetLayoutDetails.drawTimeline){
				paintTimeMarkLine(canvas, endGraphX, startGraphX, widgetLayoutDetails, (float)selectedPos);
			}

			// paint raster
			paintRaster(canvas, startGraphX, endGraphX, xSteps, endGraphY, startGraphY, stepXCount, ySteps, stepYCount);

			// paint graph
			paintGraph(canvas, windGraphPath, gustGraphPath);

			// paint all arrows
			paintArrowsInGraph(canvas, spotWindData, images, startGraphX, endGraphX, xSteps, startGraphY, endGraphY,  stepXCount);

		}

		// get the selected windData
		WindData windData = spotWindData.getWindData(time);

		// draw the image
		if (widgetLayoutDetails.drawImage){
			// define advice location
			int adviceStatusIconTop = 10;
			int adviceStatusIconLeft = (int)widgetLayoutDetails.spotImageHeight-widgetLayoutDetails.windSpeedCircelDiameter-10;
			
			// draw spot
			drawSpotImage(spotBitmap, widgetLayoutDetails, canvas, xOffsetImage, yOffsetImage);
			
			
			if (windData==null ){
				paintImageTextOutdated(context, widgetLayoutDetails, canvas, xOffsetImage, yOffsetImage);
				Bitmap globalAdviceBitmap = images.getUnknowmBitmap();
				paintAdviceIcon(canvas,widgetLayoutDetails.windSpeedCircelDiameter, globalAdviceBitmap, adviceStatusIconLeft, adviceStatusIconTop, xOffsetImage, yOffsetImage);
			}

			if (windData!=null ){

				// find windData
				double windDataTime = windData.getEndHour()+(double)windData.getEndMinute()/60;
				double currentTime = Util.calculateTime(new Date());
				double diffTime = (currentTime-windDataTime);
				boolean outDated = diffTime>1;
				
				
				if (outDated && checkOutDated) {
					paintImageTextOutdated(context, widgetLayoutDetails, canvas, xOffsetImage, yOffsetImage);
					Bitmap globalAdviceBitmap = images.getUnknowmBitmap();
					paintAdviceIcon(canvas,widgetLayoutDetails.windSpeedCircelDiameter, globalAdviceBitmap, adviceStatusIconLeft, adviceStatusIconTop, xOffsetImage, yOffsetImage);
				}
				else{

					// paint the arrow
					paintArrow(canvas, widgetLayoutDetails, images, windData, xOffsetImage, yOffsetImage);

					// check aanlandig/sideshore/aflandig
					WIND_DIRECTORION windDirectorion = calculateWindAngleStatus(windData, spotWindData);
					WIND_POWER windPower = calculateWindPower(context, windData);
					int angleAdviceLevel = getAngleAdviceLevel(windDirectorion);
					int powerAdviceLevel = getWindPowerAdviceLevel(windPower);
					String windDateText = getAngleAdviceText(context, windDirectorion);
					int windPowerColor = getWindPowerColor(windPower);

					// define global advice
					Bitmap globalAdviceBitmap = getGlobalAdviceBitmap(images, angleAdviceLevel, powerAdviceLevel);


					// print circel for wind
					if (widgetLayoutDetails.drawWindInCorner){
						int wind = (int)Math.round(windData.getWind());
						paintWindInCorner(canvas, widgetLayoutDetails.windSpeedCircelDiameter, wind, xOffsetImage, yOffsetImage);
					}

					paintAdviceIcon(canvas,widgetLayoutDetails.windSpeedCircelDiameter, globalAdviceBitmap, adviceStatusIconLeft, adviceStatusIconTop, xOffsetImage, yOffsetImage);

					if (widgetLayoutDetails.drawImageText){
						paintImageText(context, widgetLayoutDetails, canvas, windData, xOffsetImage, yOffsetImage);
					}
				}
			}
		}
	}

	public static Set<Bar> createWindGraph(SpotWindData spotDayData, int startX, int startY, int endX, int endY){
		Set<Bar> path = new LinkedHashSet<Bar>();
		double xDiff = endX-startX;
		double yDiff = endY-startY;
		for (WindData windData:spotDayData.getWindDatas()){			
			double wind = windData.getWind();
			if (wind>MAXWIND) wind = MAXWIND;
			double time2 = (double)windData.getEndHour()+(double)windData.getEndMinute()/60;
			double time1 = (double)windData.getStartHour()+(double)windData.getStartMinute()/60;
			double xProcent2 = time2/24;
			double yProcent = wind/MAXWIND;
			double xProcent1 = time1/24;
			double x1 = startX+xProcent1*xDiff;
			double y1 = endY-yProcent*yDiff;
			double x2 = startX+xProcent2*xDiff;
			if (x1>x2) x1=startX;// in case startTime was from the day before.
			double y2 = endY;
			Bar bar = new Bar();
			bar.setX1((int)x1);
			bar.setX2((int)x2);
			bar.setY1((int)y1);
			bar.setY2((int)y2);
			path.add(bar);
		}

		return path;

	}

	public static Set<Bar> createGustGraph(SpotWindData spotDayData, int startX, int startY, int endX, int endY){
		Set<Bar> path = new LinkedHashSet<Bar>();
		double xDiff = endX-startX;
		double yDiff = endY-startY;
		for (WindData windData:spotDayData.getWindDatas()){			
			double wind = windData.getWind();
			double gust = windData.getGust();
			if (wind>MAXWIND) wind = MAXWIND;
			if (gust>MAXWIND) gust = MAXWIND;
			double time2 = (double)windData.getEndHour()+(double)windData.getEndMinute()/60;
			double time1 = (double)windData.getStartHour()+(double)windData.getStartMinute()/60;
			double xProcent2 = time2/24;
			double yProcent2 = wind/MAXWIND;
			double yProcent1 = gust/MAXWIND;
			double xProcent1 = time1/24;
			double x1 = startX+xProcent1*xDiff;
			double y1 = endY-yProcent1*yDiff;
			double y2 = endY-yProcent2*yDiff;
			double x2 = startX+xProcent2*xDiff;
			if (x1>x2) x1=startX;// in case startTime was from the day before.

			Bar bar = new Bar();
			bar.setX1((int)x1);
			bar.setX2((int)x2);
			bar.setY1((int)y1);
			bar.setY2((int)y2);
			path.add(bar);
		}

		return path;
	}


}
