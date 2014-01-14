package com.vdzon.windapp.activity.fragment;


import java.io.IOException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vdzon.windapp.R;
import com.vdzon.windapp.activity.listener.SwipeListener;
import com.vdzon.windapp.consts.Const;
import com.vdzon.windapp.pojo.SpotData;
import com.vdzon.windapp.pojo.SpotWindData;
import com.vdzon.windapp.util.Util;
import com.vdzon.windapp.view.WindImageView;
import com.vdzon.windapp.view.WindGraphView;

/**
 * This Fragment contains a single ListView.
 * A reference is kept for the WindGraphView and WindImageView which are the first two views of the ListView  
 */

public final class WindFragment extends Fragment {

	private static String TAG = WindFragment.class.getName();
	private static final String KEY_SPOTDATA = "WindFragment:spotdata";
	private static final String KEY_DAY = "WindFragment:day";
	private static final String KEY_SPOTWINDDATA = "WindFragment:SpotWindData";

	private ListView mListView;
	private WindListviewAdapter mAdapter;
	private SpotWindData mSpotWindData;
	private SpotData mSpotData = null;
	private WindGraphView  mWindGraphView = null;
	private WindImageView  mWindImageView = null;
	private int mDay;

	/**
	 * Define a BroadcastReceiver for this fragment
	 * A message will be send to this when new wind data is available
	 */
	private BroadcastReceiver bReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(Const.UPDATE_WINDVIEW)) {
				mSpotWindData=null;
				loadDataInBackground();
			}
		}
	};	    


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Const.UPDATE_WINDVIEW);
		activity.registerReceiver(bReceiver, intentFilter);	    
	}

	@Override
	public void onDetach() {
		super.onDetach();
		getActivity().unregisterReceiver(bReceiver);
	}

	public static WindFragment newInstance(SpotData spotData, int _day ) {
		if (spotData==null) throw new RuntimeException("spotData is null");
		WindFragment fragment = new WindFragment();
		fragment.mSpotData = spotData;
		fragment.mDay = _day;
		return fragment;
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		try{
			outState.putString(KEY_SPOTDATA, Util.objectToString(mSpotData));
			outState.putString(KEY_SPOTWINDDATA, Util.objectToString(mSpotWindData));
			outState.putInt(KEY_DAY, mDay);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}


	public void loadFromBundle(Bundle savedInstanceState) {
		if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_SPOTDATA) && savedInstanceState.containsKey(KEY_DAY) && savedInstanceState.containsKey(KEY_SPOTWINDDATA)  ) {
			mDay = savedInstanceState.getInt(KEY_DAY);
			String siteDataString = savedInstanceState.getString(KEY_SPOTDATA);
			String spotWindDataString = savedInstanceState.getString(KEY_SPOTWINDDATA);
			try{
				mSpotData = (SpotData) Util.objectFromString(siteDataString);
				mSpotWindData = (SpotWindData) Util.objectFromString(spotWindDataString);
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}
		}
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadFromBundle(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		new Exception().printStackTrace();
		if (!isAdded()) return null;
		if (getActivity()==null) return null;
		loadFromBundle(savedInstanceState);
		View fragmentView = inflater.inflate(R.layout.fragment_spotdetails, container, false);
		mListView = (ListView)fragmentView.findViewById(R.id.listview);
		mAdapter = new WindListviewAdapter(getActivity(), this);
		mListView.setAdapter(mAdapter);
		mListView.setSelector(android.R.color.transparent);
		mListView.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				/*
				 * Swiping might be disabled because the windGraphView has disabled that.
				 * When we detect an ACTION_UP from any view, we enable swiping again
				 */
				if (event.getAction()==MotionEvent.ACTION_UP){
					Activity activity = getActivity();
					if (activity instanceof SwipeListener){
						SwipeListener swipeListener = (SwipeListener)activity;
						swipeListener.enableSwipe();
					}
				}
				return false;
			}
		});				
		loadDataInBackground();
		return fragmentView;
	}

	public void setDay(int _day){
		mDay = _day;
		mSpotWindData=null;
		loadDataInBackground();
	}

	public void loadDataInBackground(){
		new AsyncTask<Void, Void, SpotWindData>() {
			protected SpotWindData doInBackground(Void... params) {
				SpotWindData spotWindData = loadData(mDay);
				return spotWindData;
			}

			protected void onPostExecute(SpotWindData result) {
				if (mAdapter==null) return;
				if (result==null) return;
				setData(result);
				printGraph();
				printData();
				mAdapter.init(result);
			};
		}.execute();
	}        


	public SpotWindData loadData(int day){
		if (mSpotWindData!=null) return mSpotWindData;
		if (isAdded()){
			Context context = getActivity().getApplicationContext();
			SpotWindData spotWindData = new SpotWindData();
			spotWindData.load(context, mSpotData, day);
			return spotWindData;
		}
		return null;

	}

	public void printData(){
		if (!isAdded()) return;
		if (mAdapter==null) return;
		try{
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mAdapter.notifyDataSetChanged();
				}
			}); 					
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void printGraph(){
		if (!isAdded()) return;
		if (getActivity()==null) return;
		if (mSpotWindData==null) return;
		mAdapter.setData(mSpotWindData.getWindDatas());
	}

	public void setData(SpotWindData _spotDayData){
		this.mSpotWindData = _spotDayData;
	}


	public void onMove(MotionEvent arg0){
		/*
		 * When an onMove (touch event) is detected, then detect if the touch is on the graph.
		 * If that is not the case, then ignore the touch. When it is on the graph, then pass the touch position to the mWindGraphView and mWindImageView
		 */
		if (mListView==null) return;
		if (mWindImageView==null) return;
		if (mWindGraphView==null) return;
		
		float x = arg0.getX();
		float y = arg0.getY();

		// check if the touch is on the mWindGraphView
		int loc[] = new int[2];
		mWindGraphView.getLocationOnScreen(loc);
		int y1  = loc[1];
		int y2 = y1+mWindGraphView.getHeight();
		if (y<y1){
			// above
			return;
		}
		if (y>y2){
			// below
			return;
		}
		
		// pass the touch position
		mWindGraphView.setTouchPos(x);
		mWindImageView.setTouchPos(x);
	}

	public void setGraphWindView(WindGraphView wv){
		mWindGraphView = wv;
	}

	public void setImageWindView(WindImageView iv){
		mWindImageView = iv;
	}

	public int getSpotID(){
		if (mSpotData==null) throw new RuntimeException("mSpotData is null");
		return mSpotData.getSiteID();
	}

}
