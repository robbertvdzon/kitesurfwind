package com.vdzon.windapp.async;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.vdzon.windapp.consts.Const;
import com.vdzon.windapp.util.GooglePlayUtil;

/**
 * This task registers itself to google to receive GCM updates
 */
public class InitializeGcmAsync  extends AsyncTask<Context, Void, Void>{

	@Override
	protected Void doInBackground(Context... arg0) {
		Context context = arg0[0];
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		if (GooglePlayUtil.checkPlayServices(context)) {
			gcm = GoogleCloudMessaging.getInstance(context);
			String regid = GooglePlayUtil.getRegistrationId(context);
			if (regid != null) {
				sendRegistrationIdToBackend(regid);
			}
			if (regid == null || regid.trim().equals("")) {
				try {
					regid = gcm.register(Const.GCM_SENDER_ID);
					sendRegistrationIdToBackend(regid);
					GooglePlayUtil.storeRegistrationId(context, regid);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
		return null;
	}

	private void sendRegistrationIdToBackend(String regid) {
		RegisterGcmAsyc task = new RegisterGcmAsyc();
		task.execute(new String[]{regid});
	}


}
