package com.example.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import winterwell.jtwitter.TwitterException;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

public class YambaApp extends Application implements
		OnSharedPreferenceChangeListener {
	static final String TAG = "YambaApp";
	public static final String ACTION_NEW_STATUS = "com.example.yamba.NEW_STATUS";
	public final static String ACTION_REFRESH = "com.example.yamba.RefreshService";
	private Twitter twitter;
	SharedPreferences prefs;
	StatusData statusData;

	@Override
	public void onCreate() {
		super.onCreate();

		// Prefs stuff
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);

		statusData = new StatusData(this);

		Log.d(TAG, "onCreated");
	}

	public Twitter getTwitter() {
		if (twitter == null) {
			// twitter stuff
			String username = prefs.getString("username", "");
			String password = prefs.getString("password", "");
			String server = prefs.getString("server", "");
			twitter = new Twitter(username, password);
			twitter.setAPIRootUrl(server);
		}
		return twitter;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		twitter = null;

		Log.d(TAG, "OnSharedPreferenceChanged for key: " + key);
	}

	long lastTimestampSeen = -1;

	public int pullAndInsert() {
		int count = 0;
		long biggestTimestamp = -1;
		try {
			List<Status> timeline = getTwitter().getPublicTimeline();

			for (Status status : timeline) {
				statusData.insert(status);
				if (status.createdAt.getTime() > this.lastTimestampSeen) {
					count++;
					biggestTimestamp = (status.createdAt.getTime() > biggestTimestamp) ? status.createdAt
							.getTime() : biggestTimestamp;
					Log.d(TAG, String.format("%s: %s", status.user.name,
							status.text));
				}
			}
		} catch (TwitterException e) {
			Log.e(TAG, "Failed to pull timeline", e);
		}
		if (count > 0) {
			sendBroadcast(new Intent(ACTION_NEW_STATUS)
					.putExtra("count", count));
		}
		
		lastTimestampSeen = biggestTimestamp;
		return count;
	}

}
