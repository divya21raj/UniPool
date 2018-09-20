package garbagecollectors.com.unipool.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;

import garbagecollectors.com.unipool.BuildConfig;
import garbagecollectors.com.unipool.R;
import garbagecollectors.com.unipool.application.Globals;
import garbagecollectors.com.unipool.application.LocalStorageHelper;
import garbagecollectors.com.unipool.application.UtilityMethods;
import garbagecollectors.com.unipool.models.GenLocation;
import garbagecollectors.com.unipool.models.Message;
import garbagecollectors.com.unipool.models.PairUp;
import garbagecollectors.com.unipool.models.TripEntry;
import garbagecollectors.com.unipool.models.User;
import io.fabric.sdk.android.Fabric;

import static garbagecollectors.com.unipool.activities.BaseActivity.finalCurrentUser;

public class LoginActivity extends Activity implements View.OnClickListener
{
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";

    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;
    private static FirebaseUser currentUser;

    Message defaultMessage = BaseActivity.getDefaultMessage();

    private ProgressDialog progressDialog;

    SignInButton signInButton;

    public static boolean userNewOnDatabase = false;

    TaskCompletionSource<DataSnapshot> userDBSource = new TaskCompletionSource();
    Task userDBTask = userDBSource.getTask();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //disableCrashlyticsForDebug();

        startIntro();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* Configure sign-in to request the user's ID, email address, and basic
        profile. ID and basic profile are included in DEFAULT_SIGN_IN.*/
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.WebClientId))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

    }

    private void disableCrashlyticsForDebug()
    {
        // Set up Crashlytics, disabled for debug builds
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();

        // Initialize Fabric with the debug-disabled crashlytics.
        Fabric.with(this, crashlyticsKit, new Crashlytics());
    }

    private void startIntro()
    {
        //  Declare a new thread to do a preference check
        Thread t = new Thread(() ->
        {
            //  Initialize SharedPreferences
            SharedPreferences getPrefs = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());

            //  Create a new boolean and preference and set it to true
            boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

            //  If the activity has never started before...
            if (isFirstStart) {

                //  Launch app intro
                final Intent i = new Intent(LoginActivity.this, IntroActivity.class);

                runOnUiThread(() -> startActivity(i));

                //  Make a new preferences editor
                SharedPreferences.Editor e = getPrefs.edit();

                //  Edit preference to make it false because we don't want this to run again
                e.putBoolean("firstStart", false);

                //  Apply changes
                e.apply();
            }
        });

        // Start the thread
        t.start();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();

        updateUI(currentUser);
    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn()
    {
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try
        {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            firebaseAuthWithGoogle(account);
            // Signed in successfully, show authenticated UI.

        } catch (ApiException e)
        {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getApplicationContext(), "Ok", Toast.LENGTH_SHORT).show();
            updateUI(null);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
    {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task ->
                {
                    if (task.isSuccessful())
                    {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                        FirebaseUser user = mAuth.getCurrentUser();

                        createUserOnDatabase(user);
                    }
                    else
                    {
                        // If sign in fails, display a messageCard to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                });
    }


    private void createUserOnDatabase(FirebaseUser user)
    {
        dummyInitFinalCurrentUser(user);

        Globals.userDatabaseReference = FirebaseDatabase.getInstance().getReference(Globals.UNI + "users/" + user.getUid());

        Globals.userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot snapshot)
            {
                userDBSource.setResult(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                userDBSource.setException(databaseError.toException());
                Toast.makeText(getApplicationContext(), "Couldn't make it, try again...", Toast.LENGTH_SHORT).show();
            }
        });

        userDBTask.addOnSuccessListener(o ->
        {
           DataSnapshot userDataSnapshot = (DataSnapshot) userDBTask.getResult();

            if (!userDataSnapshot.exists())
            {
                userNewOnDatabase = true;

                Globals.userDatabaseReference.setValue(finalCurrentUser);

                Globals.messageDatabaseReference.child(finalCurrentUser.getUserId()).child(defaultMessage.getMessageId()).
                                                                            setValue(defaultMessage);

                //Toast.makeText(getApplicationContext(), "User added to database!", Toast.LENGTH_SHORT).show();

                updateUI(user);
            }

            else
            {
                //Toast.makeText(getApplicationContext(), "User already there, no need to add!", Toast.LENGTH_SHORT).show();
                userNewOnDatabase = false;

                updateUI(user);
            }

        });
    }

    private void dummyInitFinalCurrentUser(FirebaseUser user)
    {
        GenLocation dummyGenLocation = new GenLocation("dummy", "dummy", 0d, 0d);

        TripEntry dummyTripEntry = new TripEntry("dummy", "dummyId", "DummyUser", "12:00",
                                                    "1/11/12", dummyGenLocation, dummyGenLocation, "dummyMessage", "", "", true);

        HashMap<String, TripEntry> dummyUserEntries = new HashMap<>();
        dummyUserEntries.put("dummy", dummyTripEntry);

        HashMap<String, TripEntry> dummyRequestSent = new HashMap<>();
        dummyRequestSent.put(dummyTripEntry.getEntry_id(), dummyTripEntry);

        ArrayList<String> dummyUserIdList = new ArrayList<>();
        dummyUserIdList.add("dummy");

        HashMap<String, ArrayList<String>> dummyRequestReceived = new HashMap<>();
        dummyRequestReceived.put("dummy", dummyUserIdList);

        PairUp dummyPairUp = new PairUp("dummydummy", "dummy", "dummy", "dummy", dummyUserIdList);
        HashMap<String, PairUp> dummyPairUps = new HashMap<>();
        dummyPairUps.put("dummy", dummyPairUp);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String deviceToken = instanceIdResult.getToken();

            String url = "";

            Uri photoUrl = user.getPhotoUrl();
            if(photoUrl != null)
                url = photoUrl.toString();

            finalCurrentUser = new User(user.getUid(), user.getDisplayName(), url,
                    user.getEmail(), dummyUserEntries, dummyRequestSent, dummyRequestReceived, deviceToken, true, dummyPairUps);
        });

    }

    private void updateUI(FirebaseUser user)
    {
        progressDialog.dismiss();

        if(user != null)
        {
            UtilityMethods.storeUserLocally(user, getApplicationContext());

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
                String deviceToken = instanceIdResult.getToken();

                LocalStorageHelper.storeLocally(Globals.USER_SP_FILE, Globals.USER_TOKEN_KEY, deviceToken, getApplicationContext());

                UtilityMethods.fillGlobalVariables(getApplicationContext());

                Globals.userDatabaseReference = FirebaseDatabase.getInstance().getReference(Globals.UNI + "users/" + user.getUid());

                Globals.userDatabaseReference.child("deviceToken").setValue(deviceToken);

                Globals.expiryDatabaseReference.child(user.getUid()).setValue(true);

                finish();
                startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }
}
