package garbagecollectors.com.snucabpool.Actvities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import garbagecollectors.com.snucabpool.Entry;
import garbagecollectors.com.snucabpool.R;

public class HomeActivity extends AppCompatActivity {

    private TextView mTextMessage;
    static ArrayList<Entry> entry_list = new ArrayList<>();                            //To store all the entries

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.info_text);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {
            mTextMessage.setText(currentUser.getEmail());
        }
        else
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }


    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.sign_out_button:
                mAuth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
