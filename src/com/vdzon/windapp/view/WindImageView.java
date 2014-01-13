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

public class WindImageView extends android.view.View{

	private SpotWindData mSpotWindData = null;
	private WidgetLayoutDetails mWidgetLayoutDetails = null;
	private double mTouchPos = 99999;
	

	public WindImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public WindImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WindImageView(Context context) {
		super(context);
	}

	public void init(SpotWindData spotDayData){
		this.mSpotWindData = spotDayData;		
	}
	
	public void setTouchPos(double touchPos){
		this.mTouchPos = touchPos;
		invalidate();
	}
	
	protected void onDraw(Canvas canvas){
		int startX = 50;
		int endX = this.getWidth()-30;
		int startY = this.getHeight()/3+10;
		int endY = this.getHeight()-30;

		if (mSpotWindData==null) return;

		if (mWidgetLayoutDetails==null){
			mWidgetLayoutDetails = new WidgetLayoutDetails();
		}

		mWidgetLayoutDetails.yOffsetImage = 5;
		mWidgetLayoutDetails.xOffsetImage = 50;
		mWidgetLayoutDetails.spotImageHeight = this.getHeight()-mWidgetLayoutDetails.yOffsetImage;
		mWidgetLayoutDetails.windSpeedCircelDiameter = mWidgetLayoutDetails.spotImageHeight/4;		
		mWidgetLayoutDetails.arrowImageHeight = mWidgetLayoutDetails.spotImageHeight;
		mWidgetLayoutDetails.widgetHeight = this.getHeight();
		mWidgetLayoutDetails.widgetWidth = this.getWidth();
		mWidgetLayoutDetails.drawGraph = false;
		mWidgetLayoutDetails.drawImage = true;
		
		PaintUtil.paintCanvas(getContext(), mWidgetLayoutDetails, canvas, mSpotWindData, null, null, (float)mTouchPos, false);
	}


}
