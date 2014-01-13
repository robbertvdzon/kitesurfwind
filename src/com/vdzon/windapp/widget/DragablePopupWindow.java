package com.vdzon.windapp.widget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vdzon.windapp.R;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;

/**
 * A subclass of PopupWindow which enables dragging the popup window 
 * 
 */
public class DragablePopupWindow extends PopupWindow{
	private static final Logger logger = LoggerFactory.getLogger(DragablePopupWindow.class); 	

	private int mStartX;
	private int mStartY;

	// the last offset position of the popup. This is needed for dragging the popupwindow
	private int mLastPopupOffsetX;
	private int mLastPopupOffsetY;

	/**
	 * constructor
	 */	
	public DragablePopupWindow(View view){
		super(view);
		setFocusable(false);
		setOutsideTouchable(true); 
		setAnimationStyle(R.style.PopupAnimation);
		setClippingEnabled(false);
		getContentView().setFocusableInTouchMode(true);

		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				switch (motionEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mStartX = (int) motionEvent.getRawX()-mLastPopupOffsetX;
					mStartY = (int) motionEvent.getRawY()-mLastPopupOffsetY;
					break;
				case MotionEvent.ACTION_MOVE:
					int x = (int) motionEvent.getRawX();
					int y = (int) motionEvent.getRawY();
					int dx =  (x - mStartX);
					int dy =   (y - mStartY);
					int lastDX = dx;
					int lastDY = dy;
					mLastPopupOffsetX = lastDX;
					mLastPopupOffsetY = lastDY;
					update(dx, dy, -1, -1,true);
					break;
				}
				return true;
			}
		});			
	}
	
	/**
	 * function moveToLastPosition
	 * 
	 * when the popupwindow was moved before, then closed and opened again, this function can be used
	 * to place the popupwindow on the same location as when it was closed.
	 */	
	public void moveToLastPosition(){
		update(mLastPopupOffsetX, mLastPopupOffsetY, -1, -1,true);
	}
	
	
}
