package garbagecollectors.com.unipool;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class UniPool extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();

		FirebaseDatabase.getInstance().setPersistenceEnabled(true);
	}
}
