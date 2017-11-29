package garbagecollectors.com.snucabpool.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import garbagecollectors.com.snucabpool.Entry;
import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.User;

public class HomeActivity extends BaseActivity {

    private TextView mTextMessage;

    static ArrayList<Entry> entry_list = new ArrayList<>();                            //To store all the entries
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        mTextMessage = (TextView) findViewById(R.id.info_text);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        entryDatabaseReference = FirebaseDatabase.getInstance().getReference("entries");

        if(currentUser != null)
        {
            mTextMessage.setText(currentUser.getDisplayName());
        }
        else
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    int getContentViewId()
    {
        return R.layout.activity_home;
    }

    @Override
    int getNavigationMenuItemId()
    {
        return R.id.navigation_home;
    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.sign_out_button:
                mAuth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.navigation:
                startActivity(new Intent(this,RequestActivity.class));
        }
    }
}
