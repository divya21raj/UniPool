package garbagecollectors.com.snucabpool.activities;

import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import garbagecollectors.com.snucabpool.R;

public class ChatMainActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    int getContentViewId() {

        return R.layout.activity_request;
    }

    @Override
    int getNavigationMenuItemId() {
        return 0;
    }
}
