package garbagecollectors.com.unipool.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import garbagecollectors.com.unipool.R;
import garbagecollectors.com.unipool.TripEntry;
import garbagecollectors.com.unipool.User;
import garbagecollectors.com.unipool.UtilityMethods;

public class SplashActivity extends AppCompatActivity
{
    private static ArrayList<TripEntry> tripEntryList = new ArrayList<>();

    private static DatabaseReference userDatabaseReference;
    private static DatabaseReference entryDatabaseReference = FirebaseDatabase.getInstance().getReference("entries");
    private static DatabaseReference messageDatabaseReference;

    FirebaseAuth mAuth;
    static FirebaseUser currentUser;

    private TaskCompletionSource<Void> timerSource = new TaskCompletionSource<>();
    private Task<Void> timerTask = timerSource.getTask();

    private TaskCompletionSource<DataSnapshot> UserDBSource = new TaskCompletionSource<>();
    private Task UserDBTask = UserDBSource.getTask();

    private TaskCompletionSource<DataSnapshot> EntryDBSource = new TaskCompletionSource<>();
    private Task EntryDBTask = EntryDBSource.getTask();

    private static TaskCompletionSource<DataSnapshot> MessageDBSource = new TaskCompletionSource<>();
    public static Task MessageDBTask = MessageDBSource.getTask();

    AnimationDrawable loadingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView splashLogo = findViewById(R.id.splashLogo);
        splashLogo.setBackgroundResource(R.drawable.loading_animation);
        loadingAnimation = (AnimationDrawable) splashLogo.getBackground();

        loadingAnimation.start();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        BaseActivity.setCurrentUser(currentUser);

        userDatabaseReference = FirebaseDatabase.getInstance().getReference("users/" + currentUser.getUid());

        messageDatabaseReference = FirebaseDatabase.getInstance().getReference("messages/" + currentUser.getUid());

        Handler handler = new Handler();
        handler.postDelayed(() -> timerSource.setResult(null), 12350);

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
                Toast.makeText(getApplicationContext(), "Network Issues!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "Network Issues!", Toast.LENGTH_SHORT).show();
            }
        });

        Task<Void> allTask = Tasks.whenAll(UserDBTask, EntryDBTask);
        allTask.addOnSuccessListener(aVoid ->
        {
            handler.removeCallbacksAndMessages(null);

            DataSnapshot userData = (DataSnapshot) UserDBTask.getResult();
            DataSnapshot entryData = (DataSnapshot) EntryDBTask.getResult();

            for (DataSnapshot dataSnapshot : entryData.getChildren())
            {
                TripEntry tripEntry = dataSnapshot.getValue(TripEntry.class);
                UtilityMethods.updateTripList(tripEntryList, tripEntry);
            }

            if (!(LoginActivity.userNewOnDatabase))
                BaseActivity.setFinalCurrentUser(userData.getValue(User.class));

            Task chatListTask = UtilityMethods.populateChatMap(userData);

            messageDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    try
                    {
                        MessageDBSource.setResult(dataSnapshot);
                    } catch (IllegalStateException ignored)
                    {}
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                    MessageDBSource.setException(databaseError.toException());
                    Toast.makeText(getApplicationContext(), "Network Issues!", Toast.LENGTH_SHORT).show();
                }
            });

            chatListTask.addOnCompleteListener(task1 ->
            {
                finish();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            });
        });

        allTask.addOnFailureListener(e ->
        {
            // apologize profusely to the user!
            Toast.makeText(getApplicationContext(), "Network Issues!", Toast.LENGTH_SHORT).show();
        });

        timerTask.addOnCompleteListener(task ->
        {
            if(timerTask.getResult() == null)
                Toast.makeText(this, "Network not that good, I'm trying...", Toast.LENGTH_LONG).show();
        });
    }

    public static ArrayList<TripEntry> getTripEntryList()
    {
        return tripEntryList;
    }

}
