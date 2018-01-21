package garbagecollectors.com.snucabpool.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.widget.Button;

import java.text.ParseException;

import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.TripEntry;
import garbagecollectors.com.snucabpool.adapters.HomeActivityTEA;

public class HomeActivity extends BaseActivity
{
    RecyclerView recycle;

    Button viewButton, signOutButton;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        viewButton = (Button) findViewById(R.id.viewButtonHome);
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