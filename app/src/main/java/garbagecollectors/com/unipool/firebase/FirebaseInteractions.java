package garbagecollectors.com.unipool.firebase;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import garbagecollectors.com.unipool.activities.BaseActivity;
import garbagecollectors.com.unipool.activities.HomeActivity;
import garbagecollectors.com.unipool.application.Constants;
import garbagecollectors.com.unipool.application.UtilityMethods;
import garbagecollectors.com.unipool.models.TripEntry;

public class FirebaseInteractions
{
	public static void getTripEntries(Context context)
	{
		Constants.entryDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot entryData)
			{
				for (DataSnapshot dataSnapshot : entryData.getChildren())
				{
					TripEntry tripEntry = dataSnapshot.getValue(TripEntry.class);
					UtilityMethods.updateTripList(BaseActivity.getTripEntryList(), tripEntry);

					HomeActivity.updateRecycleAdapter();
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{
				Toast.makeText(context, "Network Issues!", Toast.LENGTH_SHORT).show();
				HomeActivity.updateRecycleAdapter();
			}
		});
	}
}
