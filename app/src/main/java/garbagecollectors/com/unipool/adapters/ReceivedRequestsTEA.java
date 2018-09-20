//TEA = TripEntryAdapter

package garbagecollectors.com.unipool.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import garbagecollectors.com.unipool.R;
import garbagecollectors.com.unipool.activities.BaseActivity;
import garbagecollectors.com.unipool.activities.RequestActivity.ReceivedRequestsFragment;
import garbagecollectors.com.unipool.activities.RequestActivity.RequestActivity;
import garbagecollectors.com.unipool.application.Globals;
import garbagecollectors.com.unipool.application.UtilityMethods;
import garbagecollectors.com.unipool.models.PairUp;
import garbagecollectors.com.unipool.models.TripEntry;
import garbagecollectors.com.unipool.models.User;

import static garbagecollectors.com.unipool.activities.SplashActivity.MessageDBTask;
import static garbagecollectors.com.unipool.application.UtilityMethods.accessUserDatabase;

public class ReceivedRequestsTEA extends TripEntryAdapter
{
    private List<TripEntry> list;
    private Context context;
    private boolean isAlreadyInList = false;

    private ProgressDialog requestsProgressDialog;

    public ReceivedRequestsTEA(List<TripEntry> list, Context context)
    {
        this.list = list;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.item_trip_entry, parent, false);

        return new MyHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position)
    {
        requestsProgressDialog = new ProgressDialog(context);
        requestsProgressDialog.setMessage("Please wait...");
        requestsProgressDialog.show();

        holder.cardArrow.setVisibility(View.GONE);
        holder.messageCard.setVisibility(View.GONE);

        MessageDBTask.addOnCompleteListener(o -> requestsProgressDialog.dismiss());
        MessageDBTask.addOnFailureListener(o -> requestsProgressDialog.dismiss());

        holder.requestButton.setOnClickListener(view ->
        {
            DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference(Globals.UNI + "users");
            DatabaseReference pairUpDatabaseReference = Globals.pairUpDatabaseReference;
            DatabaseReference notificationDatabaseReference = Globals.notificationDatabaseReference;

            TripEntry tripEntry = list.get(position);

            User finalCurrentUser = BaseActivity.getFinalCurrentUser();

            HashMap<String, PairUp> currentUserPairUps = finalCurrentUser.getPairUps();

            HashMap<String, ArrayList<String>> finalCurrentUserReceivedRequests = finalCurrentUser.getRequestsReceived();

            ReceivedRequestsFragment.alertDialogBuilder.setPositiveButton("YES", (dialog, which) ->
            {
                requestsProgressDialog.show();

                final User[] tripEntryUser = new User[1];

                Task userTask = accessUserDatabase("users/" + tripEntry.getUser_id());    //the user that created the clicked tripEntry
                userTask.addOnSuccessListener(aVoid ->
                {
                    DataSnapshot snapshot = (DataSnapshot) userTask.getResult();

                    tripEntryUser[0] = snapshot.getValue(User.class);

                    HashMap<String, PairUp> tripEntryUserPairUps = null;
                    if (tripEntryUser[0] != null)
                        tripEntryUserPairUps = tripEntryUser[0].getPairUps();
                    else
                        Toast.makeText(context, "Problems! Please try again...", Toast.LENGTH_LONG);

                    HashMap<String, TripEntry> tripEntryUserSentRequests = tripEntryUser[0].getRequestSent();

                    String pairUpId = finalCurrentUser.getUserId() + tripEntryUser[0].getUserId();

                    String[] timeParts = tripEntry.getTime().split(":");
                    timeParts[0] = String.valueOf((Integer.parseInt(timeParts[0]) + 4)%24);  //give them 4 hours
                    String expiryDate = tripEntry.getDate() + "/" + timeParts[0] + "/" + timeParts[1];

                    PairUp pairUp = new PairUp(pairUpId, finalCurrentUser.getUserId(), tripEntryUser[0].getUserId(), expiryDate, new ArrayList<>());
                    pairUp.getMessages().add("def@ult");

                    isAlreadyInList = UtilityMethods.addPairUpInMap(currentUserPairUps, pairUp);

                    if(!isAlreadyInList)
                    {
                        UtilityMethods.addPairUpInMap(tripEntryUserPairUps, pairUp);

                        UtilityMethods.removeFromMap(finalCurrentUserReceivedRequests, tripEntry.getEntry_id(), tripEntryUser[0].getUserId());
                        tripEntryUserSentRequests.remove(tripEntry.getEntry_id());


                        finalCurrentUser.setRequestsReceived(finalCurrentUserReceivedRequests);
                        finalCurrentUser.setPairUps(currentUserPairUps);
                        BaseActivity.setFinalCurrentUser(finalCurrentUser);

                        HashMap<String, String> notificationObject = new HashMap<>();
                        notificationObject.put("from", finalCurrentUser.getUserId());
                        notificationObject.put("type", "requestAccepted");

                        Task<Void> task1 = userDatabaseReference.child(finalCurrentUser.getUserId()).child("pairUps").setValue(currentUserPairUps);
                        Task<Void> task2 = userDatabaseReference.child(tripEntryUser[0].getUserId()).child("pairUps").setValue(tripEntryUserPairUps);

                        Task<Void> task3 = userDatabaseReference.child(finalCurrentUser.getUserId()).child("requestsReceived").setValue(finalCurrentUserReceivedRequests);
                        Task<Void> task4 = userDatabaseReference.child(tripEntryUser[0].getUserId()).child("requestSent").setValue(tripEntryUserSentRequests);

                        Task<Void> task5 = pairUpDatabaseReference.child(pairUpId).setValue(pairUp);

                        Task<Void> task6 = notificationDatabaseReference.child(tripEntryUser[0].getUserId()).push().setValue(notificationObject);

                        Task<Void> allTask = Tasks.whenAll(task1, task2, task3, task4, task5, task6);
                        allTask.addOnSuccessListener(bVoid ->
                        {
                            tripEntry.setEntry_id("dummyId");
                            userDatabaseReference.child((tripEntryUser[0]).getUserId()).child("requestSent").child("dummyId").setValue(tripEntry);

                            BaseActivity.getChatMap().put(tripEntryUser[0].getUserId(), tripEntryUser[0]);
                            requestsProgressDialog.dismiss();
                            //RequestActivity.refreshRequests(context);

                            Intent intent = new Intent(context, RequestActivity.class);
                            intent.putExtra("openingTab", 2);

                            ((Activity)context).finish();

                            context.startActivity(intent);
                            ((Activity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        });

                        allTask.addOnFailureListener(e ->
                        {
                            requestsProgressDialog.dismiss();
                            // apologize profusely to the user!
                            Toast.makeText(view.getContext(), "FAIL", Toast.LENGTH_LONG).show();
                        });
                    }

                    dialog.dismiss();
                });
            });

            ReceivedRequestsFragment.alertDialogBuilder.setNegativeButton("NO", (dialog, which) ->
                    dialog.dismiss());

            AlertDialog alert = ReceivedRequestsFragment.alertDialogBuilder.create();
            alert.show();

        });

        TripEntry tripEntry = list.get(position);

        UtilityMethods.fillTripEntryHolder(holder, tripEntry);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        int arr = 0;

        try
        {
            if(list.size()==0)
                arr = 0;
            else
                arr = list.size();

        }catch (Exception ignored){}

        return arr;
    }
}

