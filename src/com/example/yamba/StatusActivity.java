package com.example.yamba;

import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class StatusActivity extends Activity implements LocationListener {
	static final String TAG = "StatusActivity";
	static final String PROVIDER = LocationManager.GPS_PROVIDER;
	EditText editStatus;
	LocationManager locationManager;
	Location location;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		// Debug.startMethodTracing("Yamba.trace");

		Log.d(TAG, "onCreated with Bundle: " + bundle);

		setContentView(R.layout.status);

		editStatus = (EditText) findViewById(R.id.edit_status);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		location = locationManager.getLastKnownLocation(PROVIDER);

	}

	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(PROVIDER, 30000, 1000, this);

	}

	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

		// Debug.stopMethodTracing();
	}

	public void onClick(View v) {
		String statusText = editStatus.getText().toString();

		new PostToTwitter().execute(statusText);

		Log.d(TAG, "onClicked with text: " + statusText);
	}

	class PostToTwitter extends AsyncTask<String, Void, String> {

		/* New Thread */
		@Override
		protected String doInBackground(String... params) {
			try {
				((YambaApp) getApplication()).getTwitter().setStatus(params[0]);
				// Twitter twitter = new Twitter("student", "password");
				// twitter.setAPIRootUrl("http://yamba.marakana.com/api");
				// twitter.setStatus(params[0]);

				Log.d(TAG, "Succesfully posted: " + params[0]);
				return "Succesfully posted: " + params[0];
			} catch (TwitterException e) {
				Log.e(TAG, "Died", e);
				e.printStackTrace();
				return "Failed posting: " + params[0];
			}
		}

		/* UI Thread */
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG)
					.show();
		}
	}

	// --- LocationListener callbacks

	@Override
	public void onLocationChanged(Location l) {
		location = l;
		Log.d(TAG, "onLocationChanged: " + location.toString());
	}

	@Override
	public void onProviderDisabled(String arg0) {
	}

	@Override
	public void onProviderEnabled(String arg0) {
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}

}
