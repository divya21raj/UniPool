package garbagecollectors.com.unipool.firebase;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import garbagecollectors.com.unipool.activities.BaseActivity;
import garbagecollectors.com.unipool.activities.HomeActivity;
import garbagecollectors.com.unipool.application.Globals;
import garbagecollectors.com.unipool.application.UtilityMethods;
import garbagecollectors.com.unipool.models.TripEntry;

import static garbagecollectors.com.unipool.activities.BaseActivity.tripEntryList;

public class FirebaseInteractions
{
	public static void addTripEntryChildListener(Context context)
	{
		Globals.entryDatabaseReference.addChildEventListener(new ChildEventListener()
		{
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s)
			{
				TripEntry tripEntry = dataSnapshot.getValue(TripEntry.class);
				UtilityMethods.updateTripList(tripEntryList, tripEntry, false);

				HomeActivity.updateRecycleAdapter();
			}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s)
			{
				TripEntry tripEntry = dataSnapshot.getValue(TripEntry.class);
				UtilityMethods.updateTripList(tripEntryList, tripEntry, true);

				HomeActivity.updateRecycleAdapter();
			}

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot)
			{
				TripEntry tripEntry = dataSnapshot.getValue(TripEntry.class);
				if (tripEntry != null)
				{
					UtilityMethods.removeFromList(tripEntryList, tripEntry.getEntry_id());
					HomeActivity.updateRecycleAdapter();
				}

			}

			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String s)
			{
				//IDK
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{
				// Failed to read value
				Log.w("Hello", "Failed to read value.", databaseError.toException());
				Toast.makeText(context, "Network Issues!", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static void addMegaEntryChildListener(Context context)
	{
		Globals.megaEntryDatabaseReference.addChildEventListener(new ChildEventListener()
		{
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s)
			{
				try
				{
					TripEntry tripEntry = dataSnapshot.getValue(TripEntry.class);
					UtilityMethods.updateTripList(tripEntryList, tripEntry, true);

					HomeActivity.updateRecycleAdapter();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s)
			{
				TripEntry tripEntry = dataSnapshot.getValue(TripEntry.class);
				UtilityMethods.updateTripList(tripEntryList, tripEntry, true);

				HomeActivity.updateRecycleAdapter();
			}

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot)
			{
				try
				{
					TripEntry tripEntry = dataSnapshot.getValue(TripEntry.class);
					if (tripEntry != null)
					{
						UtilityMethods.removeFromList(tripEntryList, tripEntry.getEntry_id());
						HomeActivity.updateRecycleAdapter();
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}

			}

			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String s)
			{
				//IDK
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{
				// Failed to read value
				Log.w("Hello", "Failed to read value.", databaseError.toException());
				Toast.makeText(context, "Network Issues!", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static void getTripEntries(Context context)
	{
		Globals.entryDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot entryData)
			{
				for (DataSnapshot dataSnapshot : entryData.getChildren())
				{
					TripEntry tripEntry = dataSnapshot.getValue(TripEntry.class);
					UtilityMethods.updateTripList(BaseActivity.getTripEntryList(), tripEntry, true);

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

	public static void getMegaTripEntries(Context context)
	{
		Globals.megaEntryDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot entryData)
			{
				for (DataSnapshot dataSnapshot : entryData.getChildren())
				{
					try
					{
						TripEntry tripEntry = dataSnapshot.getValue(TripEntry.class);
						if (!tripEntry.getEntry_id().equals("dummy"))
						{
							UtilityMethods.updateTripList(BaseActivity.getTripEntryList(), tripEntry, true);

							HomeActivity.updateRecycleAdapter();
						}
					} catch (Exception e)
					{
						e.printStackTrace();
					}
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
