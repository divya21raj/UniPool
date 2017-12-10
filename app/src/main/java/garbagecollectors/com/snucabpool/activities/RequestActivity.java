package garbagecollectors.com.snucabpool.activities;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;

import garbagecollectors.com.snucabpool.R;

public class RequestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

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
