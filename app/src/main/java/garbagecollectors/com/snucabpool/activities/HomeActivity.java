package garbagecollectors.com.snucabpool.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;

import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.Sorting_Filtering;
import garbagecollectors.com.snucabpool.TripEntry;
import garbagecollectors.com.snucabpool.User;
import garbagecollectors.com.snucabpool.UtilityMethods;
import garbagecollectors.com.snucabpool.adapters.HomeActivityTEA;

public class HomeActivity extends BaseActivity
{
    RecyclerView recycle;
    Button viewButton, signOutButton;

    Sorting_Filtering sf = new Sorting_Filtering();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        viewButton = (Button) findViewById(R.id.viewButton);
        signOutButton = (Button) findViewById(R.id.sign_out_button);
        recycle = (RecyclerView) findViewById(R.id.recycle);

        viewButton.setOnClickListener(v ->
        {
            HomeActivityTEA recyclerAdapter = new HomeActivityTEA(tripEntryList,HomeActivity.this);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(HomeActivity.this,1);

            recycle.setLayoutManager(layoutManager);
            recycle.setItemAnimator( new DefaultItemAnimator());
            recycle.setAdapter(recyclerAdapter);

        });

        signOutButton.setOnClickListener(v ->
        {
            mAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
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
                    /*
                    try
                    {
                        setLambdaMapForAllEntries();
                    } catch (ParseException e)
                    {
                        e.printStackTrace();
                    }*/
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
            for(TripEntry e_user: finalCurrentUser.getUserTripEntries())
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
    protected int getNavigationMenuItemId()
    {
        return R.id.navigation_home;
    }
    @Override
    protected int getContentViewId()
    {
        return R.layout.activity_home;
    }

}