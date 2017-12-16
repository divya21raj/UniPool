package garbagecollectors.com.snucabpool.activities;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;

import garbagecollectors.com.snucabpool.MyAdapter;
import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.Sorting_Filtering;
import garbagecollectors.com.snucabpool.TripEntry;
import garbagecollectors.com.snucabpool.User;
import garbagecollectors.com.snucabpool.UtilityMethods;

import static garbagecollectors.com.snucabpool.UtilityMethods.getUserFromDatabase;

public class HomeActivity extends BaseActivity
{
    RecyclerView recycle;
    Button viewButton;

    Sorting_Filtering sf = new Sorting_Filtering();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        viewButton = (Button) findViewById(R.id.view);
        recycle = (RecyclerView) findViewById(R.id.recycle);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(finalCurrentUser == null)
        {
            assert currentUser != null;
            finalCurrentUser = getUserFromDatabase(currentUser.getUid());
        }

        viewButton.setOnClickListener(v ->
        {
            MyAdapter recyclerAdapter = new MyAdapter(tripEntryList,HomeActivity.this);
            RecyclerView.LayoutManager recyce = new GridLayoutManager(HomeActivity.this,1);
            /// RecyclerView.LayoutManager recyce = new LinearLayoutManager(MainActivity.this);
            // recycle.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
            recycle.setLayoutManager(recyce);
            recycle.setItemAnimator( new DefaultItemAnimator());
            recycle.setAdapter(recyclerAdapter);

        });

        entryDatabaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren())
                {
                    TripEntry tripEntry = dataSnapshot1.getValue(TripEntry.class);

                    UtilityMethods.updateTripList(tripEntryList, tripEntry);

                    try
                    {
                        setLambdaMapForAllEntries();
                    } catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });

        userDatabaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren())
                {
                    User user = dataSnapshot1.getValue(User.class);
                    UtilityMethods.updateUserList(userList, user);
                }

            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });
    }

    void setLambdaMapForAllEntries() throws ParseException
    {
        try
        {
            for(TripEntry e_user: finalCurrentUser.getUser_entries())
            {
                for(TripEntry e : tripEntryList)
                {
                    e_user.lambdaMap.put(e.getEntry_id(), sf.calc_lambda(e_user, e));
                }
            }

        }catch (NullPointerException ignored)
        { }

    }

    @Override
    int getNavigationMenuItemId()
    {
        return R.id.navigation_home;
    }
    @Override
    int getContentViewId()
    {
        return R.layout.activity_home;
    }

}