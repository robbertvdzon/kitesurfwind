package com.vdzon.windapp.view;

import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;

import com.vdzon.windapp.pojo.Bar;
import com.vdzon.windapp.pojo.SpotWindData;
import com.vdzon.windapp.pojo.WidgetLayoutDetails;
import com.vdzon.windapp.util.PaintUtil;

public class WindGraphView extends android.view.View{

	private SpotWindData mSpotWindData = null;
	private Set<Bar> mWindGraphPath = null;
	private Set<Bar> mGustGraphPath = null;
	private WidgetLayoutDetails mWidgetLayoutDetails = null;
	private double mTouchPos = 99999;

	public WindGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public WindGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WindGraphView(Context context) {
		super(context);
	}

	public void init(SpotWindData spotDayData){
		this.mSpotWindData = spotDayData;		
		this.mWindGraphPath = null;// deze moet opnieuw gemaakt worden
		this.mGustGraphPath = null;// deze moet opnieuw gemaakt worden
	}

	public void setTouchPos(double touchPos){
		this.mTouchPos = touchPos;
		invalidate();
	}


	protected void onDraw(Canvas canvas){
		int startX = 50;
		int endX = this.getWidth()-30;
		int startY = 10;
		int endY = this.getHeight()-30;


		if (mSpotWindData==null) return;

		// create path
		if (mWindGraphPath==null){
			mWindGraphPath = PaintUtil.createWindGraph(mSpotWindData, startX, startY, endX, endY);
		}
		if (mGustGraphPath==null){
			mGustGraphPath = PaintUtil.createGustGraph(mSpotWindData, startX, startY, endX, endY);
		}

		if (mWidgetLayoutDetails==null){
			mWidgetLayoutDetails = new WidgetLayoutDetails();
		}

		mWidgetLayoutDetails.spotImageHeight = 0;
		mWidgetLayoutDetails.arrowImageHeight = mWidgetLayoutDetails.spotImageHeight;
		mWidgetLayoutDetails.windSpeedCircelDiameter = mWidgetLayoutDetails.spotImageHeight/4;
		
		mWidgetLayoutDetails.widgetHeight = this.getHeight();
		mWidgetLayoutDetails.widgetWidth = this.getWidth();
		mWidgetLayoutDetails.drawGraph = true;
		mWidgetLayoutDetails.drawImage = false;
		PaintUtil.paintCanvas(getContext(), mWidgetLayoutDetails, canvas, mSpotWindData, mWindGraphPath, mGustGraphPath, (float)mTouchPos, false);
	}


}
