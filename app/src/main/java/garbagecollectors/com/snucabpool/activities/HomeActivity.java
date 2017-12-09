package garbagecollectors.com.snucabpool.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import garbagecollectors.com.snucabpool.Entry;
import garbagecollectors.com.snucabpool.MyAdapter;
import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.User;

public class HomeActivity extends BaseActivity {

    private TextView mTextMessage;

    @Override
    int getContentViewId()
    {
        return R.layout.activity_home;
    }
   // static ArrayList<Entry> entry_list = new ArrayList<>();                            //To store all the entries
    List<Entry> list;
    RecyclerView recycle;
    Button view;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        view = (Button) findViewById(R.id.view);
        recycle = (RecyclerView) findViewById(R.id.recycle);
        //mTextMessage = (TextView) findViewById(R.id.info_text);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        entryDatabaseReference = FirebaseDatabase.getInstance().getReference("entries");

      /*  if(currentUser != null)
        {
            mTextMessage.setText(currentUser.getDisplayName());
        }
        else
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }*/

        entryDatabaseReference.addValueEventListener(new ValueEventListener() {
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

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });




    /*public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.sign_out_button:
                mAuth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
        }
    }*/
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
    @Override
    int getNavigationMenuItemId()
    {
        return R.id.navigation_home;
    }
}