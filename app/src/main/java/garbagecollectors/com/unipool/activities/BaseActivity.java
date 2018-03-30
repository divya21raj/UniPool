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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import garbagecollectors.com.unipool.Message;
import garbagecollectors.com.unipool.R;
import garbagecollectors.com.unipool.TripEntry;
import garbagecollectors.com.unipool.User;
import garbagecollectors.com.unipool.UtilityMethods;
import garbagecollectors.com.unipool.activities.RequestActivity.ChatFragment;
import garbagecollectors.com.unipool.activities.RequestActivity.ReceivedRequestsFragment;
import garbagecollectors.com.unipool.activities.RequestActivity.RequestActivity;
import garbagecollectors.com.unipool.activities.RequestActivity.SentRequestsFragment;

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

    private static DatabaseReference expiryDatabaseReference = FirebaseDatabase.getInstance().getReference("deleteExpired");

    public static User finalCurrentUser;

    protected static ArrayList<TripEntry> tripEntryList = SplashActivity.getTripEntryList();
    protected static HashMap<String, User> chatMap; //key = UserId

    protected static HashMap<String, HashMap<String, Message>> messages = new HashMap<>();   //Key - PairUpID, Value- List of messages in that pairUp

    protected static Message defaultMessage = new Message("def@ult", "", "", "", "", 1l);

    private static ArrayList<String> chatUserNotifs;  //userIds who have unseen messages

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
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

                for (DataSnapshot dataSnapshot : messageData.getChildren())
                {
                    Message message = dataSnapshot.getValue(Message.class);

                    assert message != null;
                    if (!(message.getMessageId().equals("def@ult")))
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
                    Toast.makeText(getApplicationContext(), "Network Issues!", Toast.LENGTH_SHORT).show();
                }
            });

            userDatabaseReference.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    try
                    {
                        finalCurrentUser = dataSnapshot.getValue(User.class);
                        UtilityMethods.populateChatMap(dataSnapshot);
                        ReceivedRequestsFragment.refreshRecycler();
                        SentRequestsFragment.refreshRecycler();
                        ChatFragment.refreshRecycler();
                    }
                    catch (DatabaseException dbe)
                    {
                        Toast.makeText(getApplicationContext(), "Some problems, mind restarting the app?", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error)
                {
                    // Failed to read value
                        Log.w("UserDB", "Failed to read userDB value.", error.toException());
                        Toast.makeText(getApplicationContext(), "Network Issues!", Toast.LENGTH_SHORT).show();
                    }
                });

                bottomNavigationView = findViewById(R.id.bottom_navigation);
                bottomNavigationView.setOnNavigationItemSelectedListener(this);
        }

        catch(NullPointerException nlp)
        {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        updateNavigationBarState();
        finalCurrentUser.setOnline(true);
        userDatabaseReference.child("isOnline").setValue("true");
    }

    // Remove inter-activity transition to avoid screen tossing on tapping bottom bottom_nav items
    @Override
    public void onPause()
    {
        super.onPause();

        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finalCurrentUser.setOnline(false);
        userDatabaseReference.child("isOnline").setValue("false");
        expiryDatabaseReference.child(currentUser.getUid()).removeValue();
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
			case R.id.nav_about:
				startActivity(new Intent(this, AboutActivity.class));
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

            case R.id.nav_chat:
                finish();
                Intent chatIntent = new Intent(this, RequestActivity.class);
                chatIntent.putExtra("openingTab", 2);
                startActivity(chatIntent);
                break;
		}
	}

	protected void setNavHeaderStuff()
	{
		TextView userNameOnHeader = findViewById(R.id.header_username);
		userNameOnHeader.setText(finalCurrentUser.getName());

		TextView emailOnHeader = findViewById(R.id.header_email);
		emailOnHeader.setText(currentUser.getEmail());

        CircleImageView userImageOnHeader = findViewById(R.id.header_userImage);
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

    public static DatabaseReference getUserDatabaseReference()
    {
        return userDatabaseReference;
    }

    public static DatabaseReference getEntryDatabaseReference()
    {
        return entryDatabaseReference;
    }

    public static HashMap<String, User> getChatMap()
    {
        return chatMap;
    }

    public static HashMap<String, HashMap<String, Message>> getMessages()
    {
        return messages;
    }

    public static void setChatMap(HashMap<String, User> chatMap)
    {
        BaseActivity.chatMap = chatMap;
    }

    public static Message getDefaultMessage()
    {
        return defaultMessage;
    }

    public static ArrayList<String> getChatUserNotifs()
    {
        return chatUserNotifs;
    }

    public static void setChatUserNotifs(ArrayList<String> chatUserNotifs)
    {
        BaseActivity.chatUserNotifs = chatUserNotifs;
    }


}
