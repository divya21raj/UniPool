package garbagecollectors.com.unipool.application;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class UniPool extends Application
{
	// Called when the application is starting, before any other application objects have been created.
	// Overriding this method is totally optional!
	@Override
	public void onCreate() {
		super.onCreate();
		checkIfUpdateRequired();
	}

	// Called by the system when the device configuration changes while your component is running.
	// Overriding this method is totally optional!
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	// This is called when the overall system is running low on memory,
	// and would like actively running processes to tighten their belts.
	// Overriding this method is totally optional!
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}


	private void checkIfUpdateRequired()
	{
		final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

		// set in-app defaults
		Map<String, Object> remoteConfigDefaults = new HashMap();
		remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
		remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL,
				"https://play.google.com/store/apps/details?id=garbagecollectors.com.unipool");

		firebaseRemoteConfig.setDefaults(remoteConfigDefaults);
		firebaseRemoteConfig.fetch(60) // fetch every minutes
				.addOnCompleteListener(task -> {
					if (task.isSuccessful()) {
						Log.d(TAG, "remote config is fetched.");
						firebaseRemoteConfig.activateFetched();
					}
				});

	}
}
