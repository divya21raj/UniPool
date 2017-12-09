package garbagecollectors.com.snucabpool.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import garbagecollectors.com.snucabpool.RequestAdapter;
import garbagecollectors.com.snucabpool.User;

import garbagecollectors.com.snucabpool.R;

import static garbagecollectors.com.snucabpool.R.styleable.View;

public class RequestActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    User u = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setContentView(R.layout.activity_request);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

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
        ArrayList<User> myDataset = new ArrayList<User>();
        mAdapter = new RequestAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
        }

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
            //@Override
        public void onClick(TextView view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }


    @Override
    int getContentViewId() {
        return 0;
    }

    @Override
    int getNavigationMenuItemId() {
        return 0;
    };
    }
