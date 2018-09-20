package garbagecollectors.com.unipool.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import garbagecollectors.com.unipool.R;
import garbagecollectors.com.unipool.application.ForceUpdateChecker;
import garbagecollectors.com.unipool.application.Globals;
import garbagecollectors.com.unipool.application.UtilityMethods;
import garbagecollectors.com.unipool.models.TripEntry;
import garbagecollectors.com.unipool.models.User;

public class SplashActivity extends AppCompatActivity implements ForceUpdateChecker.OnUpdateNeededListener
{
    private static ArrayList<TripEntry> tripEntryList = new ArrayList<>();

    FirebaseAuth mAuth;
    static FirebaseUser currentUser;

    private boolean canProceed = true;

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

        //check if update required
        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //For Oreo and above
        createNotificationChannel();

        UtilityMethods.fillGlobalVariables(getApplicationContext());

        BaseActivity.setCurrentUser(currentUser);

        Globals.userDatabaseReference = FirebaseDatabase.getInstance().getReference(Globals.UNI + "users/" + currentUser.getUid());

        Globals.messageDatabaseReference = FirebaseDatabase.getInstance().getReference(Globals.UNI + "messages/" + currentUser.getUid());

        Handler handler = new Handler();
        handler.postDelayed(() -> timerSource.setResult(null), 12345);

        Globals.userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener()
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

        Globals.entryDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener()
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
                UtilityMethods.updateTripList(tripEntryList, tripEntry, true);
            }

            if (!(LoginActivity.userNewOnDatabase))
                BaseActivity.setFinalCurrentUser(userData.getValue(User.class));

            Task chatListTask = UtilityMethods.populateChatMap(userData.child("pairUps"));

            Globals.messageDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener()
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
                if(canProceed)
                {
                    finish();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
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

    @Override
    public void onUpdateNeeded(String updateUrl)
    {
        canProceed = false;  //don't go beyond splash
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New version available")
                .setMessage("The latest version fixes the problem of phones catching fire randomly, please update")
                .setPositiveButton("Update",
                        (dialog1, which) -> redirectStore(updateUrl)).setNegativeButton("No, thanks",
                        (dialog12, which) -> finish()).create();
        dialog.show();
    }
    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Globals.CHANNEL_ID, "UniPool Channel", importance);
            channel.setDescription("The default notification channel for UniPool");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
