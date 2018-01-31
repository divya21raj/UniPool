package garbagecollectors.com.snucabpool.activities;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.adapters.HomeActivityTEA;

public class HomeActivity extends BaseActivity
{
    RecyclerView recycle;
    static HomeActivityTEA recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
        	actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
        	actionBar.setDisplayHomeAsUpEnabled(true);
        }

	    drawerLayout = (DrawerLayout) findViewById(R.id.home_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_drawer);
	    navigationView.setNavigationItemSelectedListener(menuItem ->
	    {
		    dealWithSelectedMenuItem(menuItem);
		    drawerLayout.closeDrawers();

		    return true;
	    });

	    bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        recycle = (RecyclerView) findViewById(R.id.recycle);

        recyclerAdapter = new HomeActivityTEA(tripEntryList,HomeActivity.this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(HomeActivity.this,1);

        recycle.setLayoutManager(layoutManager);
        recycle.setItemAnimator( new DefaultItemAnimator());
        recycle.setAdapter(recyclerAdapter);

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

    public static void updateRecycleAdapter()
    {
        recyclerAdapter.notifyDataSetChanged();
    }
}