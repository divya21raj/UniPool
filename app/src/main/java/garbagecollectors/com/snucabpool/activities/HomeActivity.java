package garbagecollectors.com.snucabpool.activities;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import garbagecollectors.com.snucabpool.Entry;
import garbagecollectors.com.snucabpool.MyAdapter;
import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.Sorting_Filtering;
import garbagecollectors.com.snucabpool.User;

public class HomeActivity extends BaseActivity {

    private TextView mTextMessage;

   // static ArrayList<Entry> entry_list = new ArrayList<>();                            //To store all the entries
    List<Entry> list;
    RecyclerView recycle;
    Button view;

    Sorting_Filtering sf = new Sorting_Filtering();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        view = (Button) findViewById(R.id.view);
        recycle = (RecyclerView) findViewById(R.id.recycle);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        setFinalCurrentUser();

        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                list = new ArrayList<Entry>();
                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){

                    Entry value = dataSnapshot1.getValue(Entry.class);
                    Entry entry = new Entry();
                    String date = value.getDate();
                    String user_id = value.getUser_id();
                    Object destination = value.getDestination();
                    Object source = value.getSource();
                    String name = value.getName();

                    entry.setDate(date);
                    entry.setSource(source);
                    entry.setDestination(destination);
                    entry.setUser_id(user_id);
                    entry.setName(name);
                    list.add(entry);

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
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });

    view.setOnClickListener(v -> {

        MyAdapter recyclerAdapter = new MyAdapter(list,HomeActivity.this);
        RecyclerView.LayoutManager recyce = new GridLayoutManager(HomeActivity.this,2);
        /// RecyclerView.LayoutManager recyce = new LinearLayoutManager(MainActivity.this);
        // recycle.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recycle.setLayoutManager(recyce);
        recycle.setItemAnimator( new DefaultItemAnimator());
        recycle.setAdapter(recyclerAdapter);

    });

}

    private void setFinalCurrentUser()
    {
        {
            userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if(finalCurrentUser == null)
                    {
                        for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren())
                        {
                            User user = dataSnapshot1.getValue(User.class);

                            if(user.getUserId().equals(currentUser.getUid()))
                            {
                                finalCurrentUser = user;
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                    // ...
                }
            });
        }
    }

    void setLambdaMapForAllEntries() throws ParseException
    {
        try
        {
            for(Entry e_user: finalCurrentUser.user_entries)
            {
                for(Entry e : sf.entry_list)
                {
                    e_user.lambdaMap.put(e.getEntry_id(), sf.calc_lambda(e_user, e));
                }
            }

        }catch (NullPointerException nlp)
        {

        }

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