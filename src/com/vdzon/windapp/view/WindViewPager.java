package com.vdzon.windapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class WindViewPager extends android.support.v4.view.ViewPager {

	public WindViewPager(Context context) {
		super(context);
	}

	public WindViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private boolean swipe = true;

	public boolean isSwipe() {
		return swipe;
	}

	public void setSwipe(boolean swipe) {
		this.swipe = swipe;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		/*
		 * When swipe is disabled, return false on mouse events
		 */
		if (swipe) {
			return super.onInterceptTouchEvent(arg0);
		}

		return false;
	}	

}
