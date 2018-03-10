package garbagecollectors.com.snucabpool.activities.RequestActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.TripEntry;
import garbagecollectors.com.snucabpool.UtilityMethods;
import garbagecollectors.com.snucabpool.activities.BaseActivity;

public class RequestActivity extends BaseActivity
{
	private TabLayout tabLayout;
	private ViewPager viewPager;

	public static ProgressBar requestsProgressBar;

	static DatabaseReference sentRequestsDatabaseReference = FirebaseDatabase.getInstance().getReference("users/" + finalCurrentUser.getUserId() + "/requestSent");
	static DatabaseReference receivedRequestsDatabaseReference = FirebaseDatabase.getInstance().getReference("users/" + finalCurrentUser.getUserId() + "/requestsReceived");

	static TaskCompletionSource<DataSnapshot> sentRequestsSource;
	static TaskCompletionSource<DataSnapshot> receivedRequestsSource;

	static Task<DataSnapshot> sentRequestsDBTask;
	static Task<DataSnapshot> receivedRequestsDBTask;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request);

		final ActionBar actionBar = getSupportActionBar();
		if(actionBar != null)
		{
			actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		drawerLayout = (DrawerLayout) findViewById(R.id.requests_layout);

		navDrawerStateListener();

		navigationView = (NavigationView) findViewById(R.id.nav_drawer);
		navigationView.setNavigationItemSelectedListener(menuItem ->
		{
			dealWithSelectedMenuItem(menuItem);
			drawerLayout.closeDrawers();

			return true;
		});

		bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
		bottomNavigationView.setOnNavigationItemSelectedListener(this);

		viewPager = (ViewPager) findViewById(R.id.viewpager);
		setupViewPager(viewPager);

		tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(viewPager);

		requestsProgressBar = (ProgressBar) findViewById(R.id.requests_progressBar);
		requestsProgressBar.setVisibility(View.INVISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_requests, menu);
		return true;
	}

	public static void refreshRequests(Context context)
	{
		requestsProgressBar.setVisibility(View.VISIBLE);

		sentRequestsSource = new TaskCompletionSource<>();
		receivedRequestsSource = new TaskCompletionSource<>();

		sentRequestsDBTask = sentRequestsSource.getTask();
		receivedRequestsDBTask = receivedRequestsSource.getTask();

		sentRequestsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				sentRequestsSource.setResult(dataSnapshot);
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{
				sentRequestsSource.setException(databaseError.toException());
			}
		});

		receivedRequestsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				receivedRequestsSource.setResult(dataSnapshot);
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{
				receivedRequestsSource.setException(databaseError.toException());
			}
		});

		Task<Void> allTask = Tasks.whenAll(sentRequestsDBTask, receivedRequestsDBTask);
		allTask.addOnSuccessListener((Void aVoid) ->
		{
			finalCurrentUser.getRequestSent().clear();
			finalCurrentUser.getRequestsReceived().clear();

			ReceivedRequestsFragment.getReceivedRequestsList().clear();

			DataSnapshot sentRequestsData = sentRequestsDBTask.getResult();
			DataSnapshot receivedRequestsData = receivedRequestsDBTask.getResult();

			for (DataSnapshot ds : sentRequestsData.getChildren())
				finalCurrentUser.getRequestSent().add(ds.getValue(TripEntry.class));

			ArrayList<String> userIdList = new ArrayList<>();

			for (DataSnapshot dataSnapshot : receivedRequestsData.getChildren())
			{
				for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
					userIdList.add(dataSnapshot1.getValue(String.class));

				finalCurrentUser.getRequestsReceived().put(dataSnapshot.getKey(), userIdList);
			}

			Task task = UtilityMethods.populateReceivedRequestsList(ReceivedRequestsFragment.getReceivedRequestsList(), finalCurrentUser.getRequestsReceived(), tripEntryList);

			task.addOnSuccessListener(o ->
			{
				ReceivedRequestsFragment.refreshRecycler();
				SentRequestsFragment.refreshRecycler();

				requestsProgressBar.setVisibility(View.INVISIBLE);
			});
		});

		allTask.addOnFailureListener(e ->
				Toast.makeText(context, "Can't fetch requests, problem with the internet connection!", Toast.LENGTH_LONG).show());
	}

	private void setupViewPager(ViewPager viewPager)
	{
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(new SentRequestsFragment(), "Sent");
		adapter.addFragment(new ReceivedRequestsFragment(), "Received");
		adapter.addFragment(new ChatFragment(), "Chat");
		viewPager.setAdapter(adapter);
	}

	class ViewPagerAdapter extends FragmentPagerAdapter
	{
		private final List<Fragment> mFragmentList = new ArrayList<>();
		private final List<String> mFragmentTitleList = new ArrayList<>();

		ViewPagerAdapter(FragmentManager manager)
		{
			super(manager);
		}

		@Override
		public Fragment getItem(int position)
		{
			return mFragmentList.get(position);
		}

		@Override
		public int getCount()
		{
			return mFragmentList.size();
		}

		void addFragment(Fragment fragment, String title)
		{
			mFragmentList.add(fragment);
			mFragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			return mFragmentTitleList.get(position);
		}
	}

	@Override
	protected int getContentViewId()
	{
		return R.layout.activity_request;
	}

	@Override
	protected int getNavigationMenuItemId()
	{
		return R.id.navigation_requests;
	}
}
