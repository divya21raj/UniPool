package garbagecollectors.com.snucabpool.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import garbagecollectors.com.snucabpool.GenLocation;
import garbagecollectors.com.snucabpool.Message;
import garbagecollectors.com.snucabpool.PairUp;
import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.TripEntry;
import garbagecollectors.com.snucabpool.User;

import static garbagecollectors.com.snucabpool.activities.BaseActivity.finalCurrentUser;

public class LoginActivity extends Activity implements View.OnClickListener
{
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";

    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;
    private static FirebaseUser currentUser;

    protected static DatabaseReference userDatabaseReference;

    private ProgressDialog progressDialog;

    SignInButton signInButton;

    public static boolean userNewOnDatabase = false;

    TaskCompletionSource<DataSnapshot> userDBSource = new TaskCompletionSource();
    Task userDBTask = userDBSource.getTask();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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

        userDatabaseReference = FirebaseDatabase.getInstance().getReference("users");

        progressDialog = new ProgressDialog(this);

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
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
        progressDialog.setMessage("Please Wait!");
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

                        try
                        {
                            createUserOnDatabase(user);
                        } catch (ParseException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                    // ...
                });
    }

    private void createUserOnDatabase(FirebaseUser user) throws ParseException
    {
        dummyInitFinalCurrentUser(user);

        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener()
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
                Toast.makeText(getApplicationContext(), "Couldn't make it!", Toast.LENGTH_SHORT).show();
            }
        });

        userDBTask.addOnSuccessListener(o ->
        {
           DataSnapshot userDataSnapshot = (DataSnapshot) userDBTask.getResult();

            if (!userDataSnapshot.child(finalCurrentUser.getUserId()).exists())
            {
                userNewOnDatabase = true;

                userDatabaseReference.child(finalCurrentUser.getUserId()).setValue(finalCurrentUser);

                Toast.makeText(getApplicationContext(), "User added to database!", Toast.LENGTH_SHORT).show();

                updateUI(user);
            }

            else
            {
                Toast.makeText(getApplicationContext(), "User already there, no need to add!", Toast.LENGTH_SHORT).show();
                userNewOnDatabase = false;

                updateUI(user);
            }

        });
    }

    private void dummyInitFinalCurrentUser(FirebaseUser user) throws ParseException
    {
        HashMap<String, Float> dummyLambdaMap = new HashMap<>();
        dummyLambdaMap.put("123", 0f);

        GenLocation dummyGenLocation = new GenLocation("dummy", "dummy", 0d, 0d);

        TripEntry dummyTripEntry = new TripEntry("dummy", "0", "DummyUser", "12:00", "1/11/12", dummyGenLocation, dummyGenLocation, dummyLambdaMap);

        ArrayList<TripEntry> dummyUserEntries = new ArrayList<>();
        dummyUserEntries.add(dummyTripEntry);

        ArrayList<TripEntry> dummyRequestSent = new ArrayList<>();
        dummyRequestSent.add(dummyTripEntry);

        ArrayList<String> dummyUserIdList = new ArrayList<>();
        dummyUserIdList.add("dummy");

        HashMap<String, ArrayList<String>> dummyRequestReceived = new HashMap<>();
        dummyRequestReceived.put("dummy", dummyUserIdList);

        Message dummyMessage = new Message("", "dummy", 1L);
        ArrayList<Message> dummyMessages = new ArrayList<>();
        dummyMessages.add(dummyMessage);

        PairUp dummyPairUp = new PairUp("dummydummy", "dummy", "dummy", dummyMessages);
        ArrayList<PairUp> dummyPairUps = new ArrayList<>();
        dummyPairUps.add(dummyPairUp);

        finalCurrentUser = new User(user.getUid(), user.getDisplayName(), dummyUserEntries, dummyRequestSent, dummyRequestReceived, dummyPairUps);
    }

    private void updateUI(FirebaseUser currentUser)
    {
        progressDialog.dismiss();

        if(currentUser != null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(), SplashActivity.class));
        }
    }


}
