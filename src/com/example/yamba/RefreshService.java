package com.example.yamba;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class RefreshService extends IntentService {
	static final String TAG = "RefreshService";

	public RefreshService() {
		super(TAG);

	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreated");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		((YambaApp)getApplication()).pullAndInsert();
		Log.d(TAG, "onHandleIntent");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroyed");
	}

}
