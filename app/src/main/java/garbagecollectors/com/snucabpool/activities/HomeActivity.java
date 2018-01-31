package garbagecollectors.com.snucabpool.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.adapters.HomeActivityTEA;

public class HomeActivity extends BaseActivity
{
    RecyclerView recycle;
    Button signOutButton;
    static HomeActivityTEA recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
        	actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
        	actionBar.setDisplayHomeAsUpEnabled(true);
        }

	    drawerLayout = (DrawerLayout) findViewById(R.id.container);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
	    navigationView.setNavigationItemSelectedListener(menuItem ->
	    {
		    Toast.makeText(this, menuItem.getTitle() + " pressed", Toast.LENGTH_LONG).show();

		    menuItem.setChecked(true);

		    drawerLayout.closeDrawers();

		    return true;

	    });

	    bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        signOutButton = (Button) findViewById(R.id.sign_out_button);
        recycle = (RecyclerView) findViewById(R.id.recycle);

        recyclerAdapter = new HomeActivityTEA(tripEntryList,HomeActivity.this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(HomeActivity.this,1);

        recycle.setLayoutManager(layoutManager);
        recycle.setItemAnimator( new DefaultItemAnimator());
        recycle.setAdapter(recyclerAdapter);

        signOutButton.setOnClickListener(v ->
        {
            mAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        });

    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				drawerLayout.openDrawer(GravityCompat.START);
				return true;
		}
		return super.onOptionsItemSelected(item);
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