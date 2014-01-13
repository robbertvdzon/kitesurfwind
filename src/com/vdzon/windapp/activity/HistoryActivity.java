package com.vdzon.windapp.activity;

import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vdzon.windapp.R;
import com.vdzon.windapp.activity.fragment.WindFragment;
import com.vdzon.windapp.async.SynchronizeWithBackendAsyc;
import com.vdzon.windapp.async.pojo.UpdateParameters;
import com.vdzon.windapp.consts.Const;
import com.vdzon.windapp.db.SpotStorage;
import com.vdzon.windapp.pojo.SpotData;
import com.vdzon.windapp.util.Util;

/**
 * This activity shows one fragment, which is the windFragent. Above the fragment there are a decrement, increment and dateView component to select the day to show.
 */
public class HistoryActivity extends FragmentActivity{

	private TextView mDateOutput;
	private WindFragment mNewFragment;
	private SpotData mSpotData;

	private int mYear;
	private int mMonth;
	private int mDay;
	private int mSpotID;

	static final int DATE_PICKER_ID = 1111; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_history);
		LinearLayout linearLayout = (LinearLayout)findViewById(R.id.historyLayout);
		ImageView decrementButton = (ImageView)findViewById(R.id.decrementButton);
		ImageView incrementButton = (ImageView)findViewById(R.id.incrementButton);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();		
		mSpotID=-1;
		if (extras != null) {
			mSpotID = extras.getInt(Const.EXTRA_INTENTINFO_SPOTID,-1);
		}

		if (mSpotID==-1){
			throw new RuntimeException("spotid not passed in Intent");
		}

		// load the spotdata
		mSpotData = SpotStorage.getSpotData(this, mSpotID);

		// load the fragment with the spotdata and the current day
		FragmentManager fragmentManager = getSupportFragmentManager();
		mNewFragment = WindFragment.newInstance(mSpotData, Util.calculateDay(new Date()));
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.historyLayout, mNewFragment);
		transaction.commit();

		// Get current date by calender
		final Calendar c = Calendar.getInstance();
		mYear  = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay   = c.get(Calendar.DAY_OF_MONTH);

		// load the current date in the mDateOutput field 
		mDateOutput = (TextView) findViewById(R.id.date_output);
		mDateOutput.setText(new StringBuilder().append(mMonth + 1).append("-").append(mDay).append("-").append(mYear));

		// Button listener to show date picker dialog
		mDateOutput.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// On button click show datepicker dialog
				showDialog(DATE_PICKER_ID);
			}
		});

		// decrement and increment buttons
		decrementButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				decrementDay();
			}
		});

		incrementButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				incrementDay();
			}
		});
	}

	public void setDay(int day){
		// set the day to a new value
		// also trigger the system to ask the server to update the local database with the database
		// on the server with this day
		SynchronizeWithBackendAsyc task = new SynchronizeWithBackendAsyc();
		UpdateParameters updateParameters = new UpdateParameters();
		updateParameters.setContext(getApplicationContext());
		updateParameters.setDay(day);
		task.execute(new UpdateParameters[]{updateParameters});
		mNewFragment.setDay(day);
	}

	public void decrementDay(){
		final Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, mYear);
		c.set(Calendar.MONTH, mMonth);
		c.set(Calendar.DAY_OF_MONTH, mDay);
		c.add(Calendar.DAY_OF_MONTH, -1);
		mYear  = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay   = c.get(Calendar.DAY_OF_MONTH);
		int day2 = Util.calculateDay(c.getTime());
		mDateOutput.setText(new StringBuilder().append(mMonth + 1).append("-").append(mDay).append("-").append(mYear).append(" "));          
		setDay(day2);
	}

	public void incrementDay(){
		final Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, mYear);
		c.set(Calendar.MONTH, mMonth);
		c.set(Calendar.DAY_OF_MONTH, mDay);
		c.add(Calendar.DAY_OF_MONTH, 1);
		mYear  = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay   = c.get(Calendar.DAY_OF_MONTH);
		int day2 = Util.calculateDay(c.getTime());
		mDateOutput.setText(new StringBuilder().append(mMonth + 1).append("-").append(mDay).append("-").append(mYear).append(" "));          
		setDay(day2);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_PICKER_ID:
			return new DatePickerDialog(this, pickerListener, mYear, mMonth,mDay);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {
		// when dialog box is closed, below method will be called.
		@Override
		public void onDateSet(DatePicker view, int selectedYear,int selectedMonth, int selectedDay) {
			mYear  = selectedYear;
			mMonth = selectedMonth;
			mDay   = selectedDay;
			final Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR,mYear);
			c.set(Calendar.MONTH, mMonth);
			c.set(Calendar.DAY_OF_MONTH, mDay);
			int day = Util.calculateDay(c.getTime());
			mDateOutput.setText(new StringBuilder().append(mMonth + 1).append("-").append(mDay).append("-").append(mYear).append(" "));          
			setDay(day);
		}
	};	


	/**
	 * get the touch event and call onMone on the active fragment   
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev){
		if (mNewFragment!=null){
			mNewFragment.onMove(ev);			
		}
		return super.dispatchTouchEvent(ev);    
	}	
	

}
