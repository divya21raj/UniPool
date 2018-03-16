package garbagecollectors.com.unipool.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import garbagecollectors.com.unipool.Message;
import garbagecollectors.com.unipool.R;
import garbagecollectors.com.unipool.TripEntry;
import garbagecollectors.com.unipool.User;
import garbagecollectors.com.unipool.UtilityMethods;
import garbagecollectors.com.unipool.activities.RequestActivity.RequestActivity;
import garbagecollectors.com.unipool.activities.SettingsActivity.SettingsActivity;

import static garbagecollectors.com.unipool.activities.SplashActivity.MessageDBTask;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener
{
    protected BottomNavigationView bottomNavigationView;
	protected NavigationView navigationView;

    protected DrawerLayout drawerLayout;

    public static FirebaseAuth mAuth;
    public static FirebaseUser currentUser;

    protected static DatabaseReference userDatabaseReference;
    protected static DatabaseReference userMessageDatabaseReference;
    protected static DatabaseReference entryDatabaseReference = FirebaseDatabase.getInstance().getReference("entries");
    protected static DatabaseReference pairUpDatabaseReference = FirebaseDatabase.getInstance().getReference("pairUps");
    protected static DatabaseReference notificationDatabaseReference = FirebaseDatabase.getInstance().getReference("notifications");

    public static User finalCurrentUser;

    protected static ArrayList<TripEntry> tripEntryList = SplashActivity.getTripEntryList();
    protected static ArrayList<User> chatList;

    protected static HashMap<String, ArrayList<Message>> messages = new HashMap<>();   //Key - PairUpID, Value- List of messages in that pairUp

    protected static Message defaultMessage = new Message("def@ult", "", "", "", "", 1l);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userDatabaseReference = FirebaseDatabase.getInstance().getReference("users/" + finalCurrentUser.getUserId());
        userMessageDatabaseReference = FirebaseDatabase.getInstance().getReference("messages/" + finalCurrentUser.getUserId());

        MessageDBTask.addOnCompleteListener(task ->
        {
            DataSnapshot messageData = (DataSnapshot) MessageDBTask.getResult();

            for(DataSnapshot dataSnapshot: messageData.getChildren())
            {
                Message message = dataSnapshot.getValue(Message.class);

                assert message != null;
                if(!(message.getMessageId().equals("def@ult")))
                    UtilityMethods.putMessageInMap(messages, message);
            }
        });

        entryDatabaseReference.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                TripEntry tripEntry = dataSnapshot.getValue(TripEntry.class);
                UtilityMethods.updateTripList(tripEntryList, tripEntry);

                HomeActivity.updateRecycleAdapter();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                TripEntry tripEntry = dataSnapshot.getValue(TripEntry.class);
                UtilityMethods.updateTripList(tripEntryList, tripEntry);

                HomeActivity.updateRecycleAdapter();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                TripEntry tripEntry = dataSnapshot.getValue(TripEntry.class);
                UtilityMethods.removeFromList(tripEntryList, tripEntry.getEntry_id());

                HomeActivity.updateRecycleAdapter();
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
            }
        });

        userDatabaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                finalCurrentUser = dataSnapshot.getValue(User.class);
                UtilityMethods.populateChatList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                // Failed to read value
                Log.w("UserDB", "Failed to read userDB value.", error.toException());
            }
        });

        /*userMessageDatabaseReference.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Toast.makeText(getApplicationContext(), "Got it in Base activity", Toast.LENGTH_SHORT).show();

                Message message = dataSnapshot.getValue(Message.class);
                UtilityMethods.putMessageInMap(messages, message);

                //notify
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                //not happening
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                //not happening
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
                Log.w("userDB", "Failed to read UserMessages.", databaseError.toException());
            }
        });*/

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

	@Override
    protected void onStart()
    {
        super.onStart();
        updateNavigationBarState();
    }

    // Remove inter-activity transition to avoid screen tossing on tapping bottom bottom_nav items
    @Override
    public void onPause()
    {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        bottomNavigationView.postDelayed(() ->
        {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home)
            {
                startActivity(new Intent(this, HomeActivity.class));
            } else if (itemId == R.id.navigation_newEntry)
            {
                startActivity(new Intent(this, NewEntryActivity.class));
            } else if (itemId == R.id.navigation_requests)
            {
                startActivity(new Intent(this, RequestActivity.class));
            }
            finish();
        }, 300);
        return true;
    }

    protected void navDrawerStateListener()
    {
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener()
        {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                setNavHeaderStuff();
            }

            @Override
            public void onDrawerOpened(View drawerView)
            {
                setNavHeaderStuff();
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {}

            @Override
            public void onDrawerStateChanged(int newState)
            {}
        });
    }

    private void updateNavigationBarState()
    {
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    void selectBottomNavigationBarItem(int itemId)
    {
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++)
        {
            MenuItem item = menu.getItem(i);
            boolean shouldBeChecked = item.getItemId() == itemId;
            if (shouldBeChecked)
            {
                item.setChecked(true);
                break;
            }
        }
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
			    setNavHeaderStuff();
                drawerLayout.openDrawer(GravityCompat.START);
				return true;

			case R.id.action_refresh:
				RequestActivity.refreshRequests(getApplicationContext());
				break;

		}
		return super.onOptionsItemSelected(item);
	}


	protected void dealWithSelectedMenuItem(MenuItem menuItem)
	{
		switch (menuItem.getItemId())
		{
			case R.id.nav_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				break;

			case R.id.nav_logout:
				mAuth.signOut();
				finish();
				startActivity(new Intent(this, LoginActivity.class));
				break;

            case R.id.nav_home:
                finish();
                startActivity(new Intent(this, HomeActivity.class));
                break;

            case R.id.nav_newEntry:
                finish();
                startActivity(new Intent(this, NewEntryActivity.class));
                break;

            case R.id.nav_requests:
                finish();
                startActivity(new Intent(this, RequestActivity.class));
                break;
		}
	}

	protected void setNavHeaderStuff()
	{
		TextView userNameOnHeader = (TextView) findViewById(R.id.header_username);
		userNameOnHeader.setText(finalCurrentUser.getName());

		TextView emailOnHeader = (TextView) findViewById(R.id.header_email);
		emailOnHeader.setText(currentUser.getEmail());

        ImageView userImageOnHeader = (ImageView) findViewById(R.id.header_userImage);
        Picasso.get().load(currentUser.getPhotoUrl()).into(userImageOnHeader);
	}

    protected abstract int getContentViewId();

    protected abstract int getNavigationMenuItemId();

    public static FirebaseUser getCurrentUser()
    {
        return currentUser;
    }

    public static void setCurrentUser(FirebaseUser currentUser)
    {
        BaseActivity.currentUser = currentUser;
    }

    public static DatabaseReference getNotificationDatabaseReference()
    {
        return notificationDatabaseReference;
    }

    public static User getFinalCurrentUser()
    {
        return finalCurrentUser;
    }

    public static void setFinalCurrentUser(User finalCurrentUser)
    {
        BaseActivity.finalCurrentUser = finalCurrentUser;
    }

    public static ArrayList<TripEntry> getTripEntryList()
    {
        return tripEntryList;
    }

    public static DatabaseReference getPairUpDatabaseReference()
    {
        return pairUpDatabaseReference;
    }

    public static DatabaseReference getUserMessageDatabaseReference()
    {
        return userMessageDatabaseReference;
    }

    public static ArrayList<User> getChatList()
    {
        return chatList;
    }

    public static HashMap<String, ArrayList<Message>> getMessages()
    {
        return messages;
    }

    public static void setChatList(ArrayList<User> chatList)
    {
        BaseActivity.chatList = chatList;
    }

    public static Message getDefaultMessage()
    {
        return defaultMessage;
    }

}
