package com.vdzon.windapp.activity;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vdzon.windapp.R;
import com.vdzon.windapp.activity.fragment.WindFragment;
import com.vdzon.windapp.activity.fragment.WindFragmentAdapter;
import com.vdzon.windapp.activity.listener.SwipeListener;
import com.vdzon.windapp.async.InitializeGcmAsync;
import com.vdzon.windapp.consts.Const;
import com.vdzon.windapp.db.LocalStorage;
import com.vdzon.windapp.db.SpotStorage;
import com.vdzon.windapp.db.callback.SpotsLoadedCallback;
import com.vdzon.windapp.pojo.SpotData;
import com.vdzon.windapp.util.Util;
import com.vdzon.windapp.view.WindViewPager;
import com.vdzon.windapp.widget.DragablePopupWindow;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends FragmentActivity implements SwipeListener, SpotsLoadedCallback{
	private static final String KEY_SPOTS = "MainActivity:spots";
	private static final String KEY_SAVED_OPEN_TAB = "MainActivity:openTab";

	private WindViewPager mPager;
	private PageIndicator mIndicator;
	private List<SpotData> mSpots = new LinkedList<SpotData>(); 
	private WindFragmentAdapter mFragmentAdapter;
	private TabPageIndicator mTabPageIndicator;

	private int mAppWidgetID = -1;
	private int mInitialSpotID = -1;
	private int mSavedOpenTab = -1;
	private boolean mInitialized = false;

	// popup layout and window (about screen) 
	private DragablePopupWindow mPopupWindow;
	private View mPopupLayout;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ((savedInstanceState != null) 
				&& 
				savedInstanceState.containsKey(KEY_SPOTS) 
				&& 
				savedInstanceState.containsKey(KEY_SAVED_OPEN_TAB)
				) {
			// load from the savedInstanceState
			String spotsString = savedInstanceState.getString(KEY_SPOTS);
			mSavedOpenTab = savedInstanceState.getInt(KEY_SAVED_OPEN_TAB);
			try {
				mSpots = (List<SpotData>)Util.objectFromString(spotsString);
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}
		}
		showSpots();
		loadSpotsInBackground();
		initializeGoogleCloudMessaging();
		startService(this);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mSpots!=null){
			try {
				outState.putString(KEY_SPOTS, Util.objectToString((Serializable)mSpots));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		if (mPager!=null){
			outState.putInt(KEY_SAVED_OPEN_TAB, mPager.getCurrentItem());
		}
	}	

	private void showStarting(){
		setContentView(R.layout.activity_details_starting);

	}
	private void showLoading(){
		setContentView(R.layout.activity_details_loading);

	}
	private void showNoData(){
		setContentView(R.layout.activity_details_no_data);
	}

	@Override
	public void spotsLoaded(boolean fromCache, List<SpotData> _spots) {
		// this is called when the spots are loaded.
		// it is called twice,first with values from the local cache and later with new updated values from internet 
		if (fromCache && _spots==null ){
			// since this is from cache, show a loading screen. Later this function is called again but with non-cached values
			showLoading();
			return;
		}
		if (!fromCache && _spots==null ){
			// ok, there are really no spots
			showNoData();
			return;
		}

		if (SpotStorage.sameSpots(_spots,mSpots)) {
			return;// no change
		}
		mSpots = _spots;
		if (mTabPageIndicator!=null && mFragmentAdapter!=null){
			mFragmentAdapter.setSpots(_spots);			
			mTabPageIndicator.notifyDataSetChanged();
		}
		showSpots();
	}

	protected void showSpots() {
		// only load this when not initialized before
		if (!mInitialized){
			mInitialized = true;
			setContentView(R.layout.activity_details);
			mFragmentAdapter = new WindFragmentAdapter(getSupportFragmentManager(), mSpots);
			mIndicator = (PageIndicator)findViewById(R.id.indicator);

			mPager = (WindViewPager)findViewById(R.id.pager);
			mPager.setAdapter(mFragmentAdapter);
			mTabPageIndicator = (TabPageIndicator)findViewById(R.id.indicator);
			mTabPageIndicator.setViewPager(mPager);

			// get the mAppWidgetID and the spotID, this is only available when called from a widget 
			mAppWidgetID = -1;
			if (getIntent()!=null && getIntent().getExtras()!=null){
				mAppWidgetID = getIntent().getExtras().getInt(Const.EXTRA_INTENTINFO_WIDGETID,-1);
				mInitialSpotID = getIntent().getExtras().getInt(Const.EXTRA_INTENTINFO_SPOTID,-1);
			}		
			// when mInitialSpotID is still -1, then set the mInitialSpotID to the favoriteSpotID which is set on the properties
			if (mInitialSpotID==-1){
				mInitialSpotID = LocalStorage.getFavoriteSpotID(this);
			}
		}

		// when we have a saved tab, open this tab again 
		if (mSavedOpenTab!=-1){
			mPager.setCurrentItem(mSavedOpenTab);
		}
		else{
			// if we did not have a saved spot, then open the tab that belongs to the mInitialSpotID 
			for (int i = 0; i<mSpots.size();i++){
				if (mSpots.get(i).getSiteID()==mInitialSpotID){
					mPager.setCurrentItem(i);
				}
			}
		}
	}	

	private void startService(Context context){
		// trigger the service to reload the data
		Intent intent = new Intent();
		intent.setAction(Const.UPDATE_DB_ACTION);
		context.startService(intent);
	}

	private void initializeGoogleCloudMessaging(){
		// initialize google cloud messaging in a Async task
		InitializeGcmAsync initializeGoogleCloudMessagingAsync = new InitializeGcmAsync();
		initializeGoogleCloudMessagingAsync.execute(new Context[]{  getApplicationContext() });
	}

	protected void loadSpotsInBackground() {
		SpotStorage.loadSpotsAsync(this, this);
	}

	public void disableSwipe(){
		mPager.setSwipe(false);
	}

	public void enableSwipe(){
		mPager.setSwipe(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId()==R.id.action_settings){
			openSettings();
			return true;
		}
		if (item.getItemId()==R.id.action_history){
			openHistory();
			return true;
		}
		if (item.getItemId()==R.id.action_about){
			showPopup();
			return true;
		}
		return super.onOptionsItemSelected(item);
	} 

	/**
	 * When the back key is pressed while the popup is visible, close the popup and keep the activity open
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (mPopupWindow!=null && mPopupWindow.isShowing()) {
					mPopupWindow.dismiss();
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}	


	/**
	 * get the touch event and call onMone on the active fragment   
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev){
		int item = mPager.getCurrentItem();
		WindFragment tf = (WindFragment)mFragmentAdapter.getItem(item);
		if (tf!=null){
			tf.onMove(ev);			
		}
		return super.dispatchTouchEvent(ev);    
	}	

	/**
	 * Show the about popup  
	 */
	private void showPopup() {
		if (mPopupWindow==null){
			LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mPopupLayout = inflater.inflate(R.layout.about_popup,(ViewGroup) findViewById(R.id.popup_element));
			mPopupLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			mPopupWindow = new DragablePopupWindow(mPopupLayout);
			int height = mPopupLayout.getMeasuredHeight();
			int width = mPopupLayout.getMeasuredWidth();
			mPopupWindow.setWidth(width);
			mPopupWindow.setHeight(height);			
			TextView websiteLink = (TextView) mPopupLayout.findViewById(R.id.about_website);
			websiteLink.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v)
				{
					// open the link on the default browser 
					String url = "http://www.robbertvdzon.com";
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
				}
			});
		}
		mPopupWindow.showAtLocation(mPopupLayout, Gravity.CENTER, 0, 0);
		mPopupWindow.moveToLastPosition();// in case popup was moved before
	}


	public void openSettings(){
		Context context = getApplicationContext();
		Intent intent = new Intent(context, ConfigActivity.class);
		intent.putExtra(Const.EXTRA_INTENTINFO_WIDGETID, mAppWidgetID);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public void openHistory(){
		Context context = getApplicationContext();
		Intent intent = new Intent(context, HistoryActivity.class);
		int item = mPager.getCurrentItem();
		WindFragment tf = (WindFragment)mFragmentAdapter.getItem(item);
		if (tf!=null){
			intent.putExtra(Const.EXTRA_INTENTINFO_SPOTID, tf.getSpotID());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}
} 