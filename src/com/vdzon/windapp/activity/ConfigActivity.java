package com.vdzon.windapp.activity;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.vdzon.windapp.R;
import com.vdzon.windapp.consts.Const;
import com.vdzon.windapp.db.LocalStorage;
import com.vdzon.windapp.db.SpotStorage;
import com.vdzon.windapp.db.callback.SpotsLoadedCallback;
import com.vdzon.windapp.pojo.SpotData;
import com.vdzon.windapp.pojo.WindData;
//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.ActionBar;
import com.vdzon.windapp.util.Util;

/**
 * This shows the configuration of the app.
 * This is called when the AppWidget is placed on the screen and also when the properties are set of the application
 */
public class ConfigActivity extends Activity implements SpotsLoadedCallback {

	private static final String KEY_SPOTS = "spots";
	private static final String KEY_SPINNER_POS1 = "spinnerPos1";
	private static final String KEY_SPINNER_POS2 = "spinnerPos2";
	private static final String KEY_MIN_WIND = "minWind";
	private static final String KEY_OPT_WIND = "optWind";
	private static final String KEY_MUCH_WIND = "muchWind";
	private static final String KEY_TOOMUCH_WIND = "toomuchWind";


	private static int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private Spinner mDropdown;
	private Spinner mDropdown2;
	private TextView mSelectSpotLabel;
	private TextView mSelectSpotLabel2;
	private EditText mMinimalWindEditText;
	private EditText mOptimalWindEditText;
	private EditText mMuchWindEditText;
	private EditText mToomuchWindEditText;
	private List<SpotData> mSpots; 
	private boolean mIsWidget;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// first show a loading screen and start to load the spots. When the spots are loaded, show the spots.
		setContentView(R.layout.activity_details_loading);

		if (
				(savedInstanceState != null) 
				&& 
				savedInstanceState.containsKey(KEY_SPOTS) 
				&& 
				savedInstanceState.containsKey(KEY_SPINNER_POS1)
				&& 
				savedInstanceState.containsKey(KEY_SPINNER_POS2)
				&& 
				savedInstanceState.containsKey(KEY_MIN_WIND)
				&& 
				savedInstanceState.containsKey(KEY_OPT_WIND)
				&& 
				savedInstanceState.containsKey(KEY_MUCH_WIND)
				&& 
				savedInstanceState.containsKey(KEY_TOOMUCH_WIND)
				) {
			try {
				// load the data from the savedInstanceState
				mSpots = (List<SpotData>)Util.objectFromString(savedInstanceState.getString(KEY_SPOTS));
				showSpots();// first load all spots before setting the spinner positions
				mDropdown.setSelection(savedInstanceState.getInt(KEY_SPINNER_POS1));
				mDropdown2.setSelection(savedInstanceState.getInt(KEY_SPINNER_POS2));
				mMinimalWindEditText.setText(savedInstanceState.getString(KEY_MIN_WIND));
				mOptimalWindEditText.setText(savedInstanceState.getString(KEY_OPT_WIND));
				mMuchWindEditText.setText(savedInstanceState.getString(KEY_MUCH_WIND));
				mToomuchWindEditText.setText(savedInstanceState.getString(KEY_TOOMUCH_WIND));
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}
		}
		else{
			// there is no savedInstanceState so load the spots in the background
			loadSpotsInBackground();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		try {
			outState.putInt(KEY_SPINNER_POS1, mDropdown.getSelectedItemPosition());
			outState.putInt(KEY_SPINNER_POS2, mDropdown2.getSelectedItemPosition());
			outState.putString(KEY_MIN_WIND, mMinimalWindEditText.getText().toString());
			outState.putString(KEY_OPT_WIND, mOptimalWindEditText.getText().toString());
			outState.putString(KEY_MUCH_WIND, mMuchWindEditText.getText().toString());
			outState.putString(KEY_TOOMUCH_WIND, mToomuchWindEditText.getText().toString());
			outState.putString(KEY_SPOTS, Util.objectToString((Serializable)mSpots));
		} catch (IOException e) {
		} catch (NullPointerException e) {
		}		
	}		


	private void showLoading(){
		setContentView(R.layout.activity_details_loading);

	}
	private void showNoData(){
		setContentView(R.layout.activity_details_no_data);
	}

	protected void showSpots() {
		setContentView(R.layout.activity_config);

		// Check the intent to check if the widgetID is passed.
		// When this is not a widget, this is not passed
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(Const.EXTRA_INTENTINFO_WIDGETID,-1);
		}        
		if (mAppWidgetId==-1){
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,-1);
		}
		mIsWidget = mAppWidgetId!=-1;

		mDropdown = (Spinner)findViewById(R.id.spotSpinner);
		mDropdown2 = (Spinner)findViewById(R.id.defaultSpotSpinner);
		mSelectSpotLabel = (TextView)findViewById(R.id.widgetSelectSpotLabel);
		mSelectSpotLabel2 = (TextView)findViewById(R.id.widgetSelectDefaultSpotLabel);
		mMinimalWindEditText = (EditText)findViewById(R.id.minimalWindEditText);
		mOptimalWindEditText = (EditText)findViewById(R.id.optimalWindEditText);
		mMuchWindEditText = (EditText)findViewById(R.id.muchWindEditText);
		mToomuchWindEditText = (EditText)findViewById(R.id.toomuchWindEditText);

		loadValues();

		if (!mIsWidget){
			// hide the spinner when the configuration is not called from a widget
			mDropdown.setVisibility(View.GONE);
			mSelectSpotLabel.setVisibility(View.GONE);
			loadFavoriteSpot();
		}
		else{
			// only populate the spinner when this configuration is called from a widget
			mDropdown2.setVisibility(View.GONE);
			mSelectSpotLabel2.setVisibility(View.GONE);
			loadWidgetData();
		}	        
	}

	/**
	 * get the back button click. Save on that
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (mIsWidget){
					saveWidgetData();
				}
				else{
					saveFavoriteSpot();
				}
				saveValues();				
				finish();
				updateWidgets();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}	
	
	private void updateWidgets(){
		Intent in = new Intent(Const.WIDGET_UPDATE_ACTION);
		this.sendBroadcast(in);
	}



	private void loadValues(){
		mMinimalWindEditText.setText(""+LocalStorage.getMinimalWind(this));
		mOptimalWindEditText.setText(""+LocalStorage.getOptimalWind(this));
		mMuchWindEditText.setText(""+LocalStorage.getMuchWind(this));
		mToomuchWindEditText.setText(""+LocalStorage.getToomuchWind(this));
	}

	private void saveValues(){
		LocalStorage.setMinimalWind(this, Util.stringToFloat(mMinimalWindEditText.getText().toString()));
		LocalStorage.setOptimalWind(this, Util.stringToFloat(mOptimalWindEditText.getText().toString()));
		LocalStorage.setMuchWind(this, Util.stringToFloat(mMuchWindEditText.getText().toString()));
		LocalStorage.setToomuchWind(this, Util.stringToFloat(mToomuchWindEditText.getText().toString()));
	}


	public void loadWidgetData(){
		String[] items = new String[mSpots.size()];
		int selectID = -1;
		int currentSpotId = LocalStorage.getSpotID(this, mAppWidgetId);
		for (int i = 0; i<mSpots.size(); i++){
			items[i] = mSpots.get(i).getTitle();
			if (mSpots.get(i).getSiteID()==currentSpotId){
				selectID = i;
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, items);
		mDropdown.setAdapter(adapter);
		if (selectID!=-1){
			mDropdown.setSelection(selectID);
		}
	}

	public void saveWidgetData(){
		int index = mDropdown.getSelectedItemPosition();
		SpotData spotData = mSpots.get(index);
		LocalStorage.setSpotID(this, mAppWidgetId, new Integer(spotData.getSiteID()));
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
	}

	public void loadFavoriteSpot(){
		String[] items = new String[mSpots.size()];
		int selectID = -1;
		int currentSpotId = LocalStorage.getFavoriteSpotID(this);
		for (int i = 0; i<mSpots.size(); i++){
			items[i] = mSpots.get(i).getTitle();
			if (mSpots.get(i).getSiteID()==currentSpotId){
				selectID = i;
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, items);
		mDropdown2.setAdapter(adapter);
		if (selectID!=-1){
			mDropdown2.setSelection(selectID);
		}
	}


	public void saveFavoriteSpot(){
		int index = mDropdown2.getSelectedItemPosition();
		SpotData spotData = mSpots.get(index);
		LocalStorage.setFavoriteSpotID(this, new Integer(spotData.getSiteID()));
	}


	@Override
	public void spotsLoaded(boolean fromCache, List<SpotData> _spots) {
		// this function is called when the spots are loaded.
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
		showSpots();
	}	

	protected void loadSpotsInBackground() {
		SpotStorage.loadSpotsAsync(this, this);
	}	
}
