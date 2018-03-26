package garbagecollectors.com.unipool.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.RelativeLayout;

import garbagecollectors.com.unipool.R;
import garbagecollectors.com.unipool.adapters.HomeActivityTEA;

public class HomeActivity extends BaseActivity
{
    RecyclerView recycle;
    static HomeActivityTEA recyclerAdapter;

    static public RelativeLayout noEntryRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

	    handleIntent(getIntent());

	    final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
        	actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        	actionBar.setDisplayHomeAsUpEnabled(true);
        }

	    drawerLayout = findViewById(R.id.home_layout);

        noEntryRelativeLayout = findViewById(R.id.no_entry_message);
        noEntryRelativeLayout.setVisibility(View.INVISIBLE);

        navDrawerStateListener();

        navigationView = findViewById(R.id.nav_drawer);
	    navigationView.setNavigationItemSelectedListener(menuItem ->
	    {
		    dealWithSelectedMenuItem(menuItem);
		    drawerLayout.closeDrawers();

		    return true;
	    });

	    bottomNavigationView = findViewById(R.id.bottom_navigation);
	    bottomNavigationView.setOnNavigationItemSelectedListener(this);

	    recycle = findViewById(R.id.recycle);

	    recyclerAdapter = new HomeActivityTEA(tripEntryList,HomeActivity.this);
	    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(HomeActivity.this,1);

	    recycle.setLayoutManager(layoutManager);
	    recycle.setItemAnimator( new DefaultItemAnimator());
	    recycle.setAdapter(recyclerAdapter);

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager =
				(SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView =
				(SearchView) menu.findItem(R.id.search).getActionView();

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
		{
			@Override
			public boolean onQueryTextSubmit(String query)
			{
				recyclerAdapter.filter(query);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText)
			{
				recyclerAdapter.filter(newText);
				return true;
			}
		});

		if (searchManager != null)
		{
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		}

		return true;
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		handleIntent(intent);
	}

	private void handleIntent(Intent intent)
	{

		if (Intent.ACTION_SEARCH.equals(intent.getAction()))
		{
			String query = intent.getStringExtra(SearchManager.QUERY);
			//use the query to search your data somehow
			recyclerAdapter.filter(query);
		}
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
	    if(tripEntryList.isEmpty())
		    noEntryRelativeLayout.setVisibility(View.VISIBLE);

	    else
	    	noEntryRelativeLayout.setVisibility(View.INVISIBLE);

    	recyclerAdapter.notifyDataSetChanged();
    }
}