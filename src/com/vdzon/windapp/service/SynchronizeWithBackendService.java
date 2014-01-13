package com.vdzon.windapp.service;

import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.vdzon.windapp.async.SynchronizeWithBackendAsyc;
import com.vdzon.windapp.async.pojo.UpdateParameters;
import com.vdzon.windapp.util.Util;


public class SynchronizeWithBackendService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// call the SynchronizeWithServerAsyc task
		SynchronizeWithBackendAsyc task = new SynchronizeWithBackendAsyc();
		int day = Util.calculateDay(new Date());
		UpdateParameters updateParameters = new UpdateParameters();
		updateParameters.setContext(getApplicationContext());
		updateParameters.setDay(day);
		task.execute(new UpdateParameters[]{updateParameters});
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}

