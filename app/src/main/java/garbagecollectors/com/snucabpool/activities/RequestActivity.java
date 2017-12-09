package garbagecollectors.com.snucabpool.activities;

import android.os.Bundle;

import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

import garbagecollectors.com.snucabpool.MyAdapter;

import garbagecollectors.com.snucabpool.RequestAdapter;
import garbagecollectors.com.snucabpool.User;

import garbagecollectors.com.snucabpool.R;

import static android.R.id.list;
import static garbagecollectors.com.snucabpool.R.styleable.View;

public class RequestActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button reqButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        
        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        reqButton = (Button) findViewById(R.id.reqButton);

        //DataSnapshot dataSnapshot = new DataSnapshot();
        /*for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
        {
            User u = dataSnapshot1.getValue(User.class);
            // FireModel fire = new FireModel();
            User fire = new User();
            String user_Id = u.getUserId();
            String name = u.getName();

            fire.setUserId(user_Id);
            fire.setName(name);
            mDataset.add(fire);

        }*/

        ArrayList list = new ArrayList<User>();
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){

                    User value = dataSnapshot1.getValue(User.class);
                    User user = new User();

                    String user_id = value.getUserId();

                    String name = value.getName();
                    user.setUserId(user_id);
                    user.setName(name);
                    list.add(user);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Hello", "Failed to read value.", databaseError.toException());
            }

            // Get Post object and use the values to update the UI
            /*    User user = dataSnapshot.getValue(User.class);

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Hello", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mPostReference.addValueEventListener(postListener);
        */
        //ArrayList<User> myDataset = new ArrayList<User>();
        //mAdapter = new RequestAdapter(myDataset);

        });

        reqButton.setOnClickListener(v -> {

        RequestAdapter recyclerAdapter = new RequestAdapter(list);
        RecyclerView.LayoutManager recyce = new GridLayoutManager(RequestActivity.this,2);
        /// RecyclerView.LayoutManager recyce = new LinearLayoutManager(MainActivity.this);
        // recycle.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mRecyclerView.setLayoutManager(recyce);
        mRecyclerView.setItemAnimator( new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

    });

    }

    @Override
    int getContentViewId() {
        return R.layout.activity_request;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_requests;
    }
}
