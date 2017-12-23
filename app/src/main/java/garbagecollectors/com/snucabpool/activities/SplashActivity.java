package garbagecollectors.com.snucabpool.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.TripEntry;
import garbagecollectors.com.snucabpool.User;
import garbagecollectors.com.snucabpool.UtilityMethods;

public class SplashActivity extends AppCompatActivity
{
    private static ArrayList<TripEntry> tripEntryList = new ArrayList<>();
    private static ArrayList<User> userList = new ArrayList<>();

    private static DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
    private static DatabaseReference entryDatabaseReference = FirebaseDatabase.getInstance().getReference("entries");

    private TaskCompletionSource<DataSnapshot> UserDBSource = new TaskCompletionSource<>();
    private Task UserDBTask = UserDBSource.getTask();

    private TaskCompletionSource<DataSnapshot> EntryDBSource = new TaskCompletionSource<>();
    private Task EntryDBTask = EntryDBSource.getTask();

    private TaskCompletionSource<Void> delaySource = new TaskCompletionSource<>();
    private Task<Void> delayTask = delaySource.getTask();

    private Task<Void> allTask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                UserDBSource.setResult(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                UserDBSource.setException(databaseError.toException());
            }
        });

        entryDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                EntryDBSource.setResult(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                EntryDBSource.setException(databaseError.toException());
            }
        });

        new Handler().postDelayed(() -> delaySource.setResult(null), 2000);

        allTask = Tasks.whenAll(UserDBTask, EntryDBTask, delayTask);
        allTask.addOnSuccessListener(aVoid ->
        {
            DataSnapshot userData = (DataSnapshot) UserDBTask.getResult();
            DataSnapshot entryData = (DataSnapshot) EntryDBTask.getResult();

            for(DataSnapshot dataSnapshot :entryData.getChildren())
            {
                TripEntry tripEntry = dataSnapshot.getValue(TripEntry.class);
                UtilityMethods.updateTripList(tripEntryList, tripEntry);
            }

            for(DataSnapshot dataSnapshot1 : userData.getChildren())
            {
                User user = dataSnapshot1.getValue(User.class);
                UtilityMethods.updateUserList(userList, user);
            }

            finish();
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));

        });

        allTask.addOnFailureListener(e ->
        {
            // apologize profusely to the user!
            Toast.makeText(this, "FAIL", Toast.LENGTH_LONG).show();
        });
    }

    public static ArrayList<TripEntry> getTripEntryList()
    {
        return tripEntryList;
    }

    public static ArrayList<User> getUserList()
    {
        return userList;
    }
}
