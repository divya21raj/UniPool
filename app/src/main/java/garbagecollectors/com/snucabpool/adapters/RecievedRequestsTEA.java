//TEA = TripEntryAdapter

package garbagecollectors.com.snucabpool.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import garbagecollectors.com.snucabpool.Message;
import garbagecollectors.com.snucabpool.PairUps;
import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.TripEntry;
import garbagecollectors.com.snucabpool.User;
import garbagecollectors.com.snucabpool.UtilityMethods;
import garbagecollectors.com.snucabpool.activities.BaseActivity;
import garbagecollectors.com.snucabpool.activities.RequestActivity.RecievedRequestsFragment;
import garbagecollectors.com.snucabpool.activities.RequestActivity.RequestActivity;

public class RecievedRequestsTEA extends TripEntryAdapter
{
    private List<TripEntry> list;
    private Context context;
    private boolean isAlreadyInList = false;
    private boolean requestSent = false;

    private ProgressDialog progressDialog;

    public RecievedRequestsTEA(Context context)
    {
        super(context);
    }

    public RecievedRequestsTEA(List<TripEntry> list, Context context)
    {
        this.list = list;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.card, parent, false);

        return new MyHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyHolder holder, int position)
    {
        holder.itemView.setOnClickListener(view ->
        {
            progressDialog = new ProgressDialog(view.getContext());
            progressDialog.setMessage("Please wait...");

            DatabaseReference userDatabaseReference = BaseActivity.getUserDatabaseReference();

            TripEntry tripEntry = list.get(position);

            User finalCurrentUser = BaseActivity.getFinalCurrentUser();

            ArrayList<PairUps> currentUserPairUps = finalCurrentUser.getPairUps();

            HashMap<String, ArrayList<String>> recievedRequests = finalCurrentUser.getRequestsRecieved();

            RecievedRequestsFragment.alertDialogBuilder.setPositiveButton("YES", (dialog, which) ->
            {
                progressDialog.show();

                User tripEntryUser = UtilityMethods.getUserFromDatabase(tripEntry.getUser_id());

                ArrayList<PairUps> tripEntryUserPairUps = tripEntryUser.getPairUps();
                ArrayList<TripEntry> sentRequests = tripEntryUser.getRequestSent();

                ArrayList<String> tripEntriesPairedOver = UtilityMethods.getTripEntriesPairedOver(tripEntryUserPairUps, finalCurrentUser.getUserId());
                tripEntriesPairedOver.add(tripEntry.getEntry_id());

                PairUps pairUp = new PairUps(finalCurrentUser.getUserId(), tripEntryUser.getUserId(), new ArrayList<>(), tripEntriesPairedOver);
                pairUp.getMessages().add(new Message("Your request was accepted :)", pairUp.getCreatorId(), UtilityMethods.getCurrentTime()));

                isAlreadyInList = UtilityMethods.addPairUpInList(currentUserPairUps, pairUp, tripEntry.getEntry_id());

                if(!isAlreadyInList)
                {
                    tripEntryUserPairUps.add(pairUp);

                    UtilityMethods.removeFromMap(recievedRequests, tripEntry.getEntry_id(), tripEntryUser.getUserId());
                    UtilityMethods.removeFromList(sentRequests, tripEntry.getEntry_id());

                    Task<Void> task1 = userDatabaseReference.child(finalCurrentUser.getUserId()).child("pairUps").setValue(currentUserPairUps);
                    Task<Void> task2 = userDatabaseReference.child(tripEntryUser.getUserId()).child("pairUps").setValue(tripEntryUserPairUps);

                    Task<Void> task3 = userDatabaseReference.child(finalCurrentUser.getUserId()).child("requestsRecieved").setValue(recievedRequests);
                    Task<Void> task4 = userDatabaseReference.child(tripEntryUser.getUserId()).child("requestSent").setValue(sentRequests);

                    Task<Void> allTask = Tasks.whenAll(task1, task2, task3, task4);
                    allTask.addOnSuccessListener(aVoid ->
                    {
                        progressDialog.dismiss();

                        if(context instanceof RequestActivity)
                            ((RequestActivity)context).setRefresh(true);

                    });

                    allTask.addOnFailureListener(e ->
                    {
                        progressDialog.dismiss();
                        // apologize profusely to the user!
                        Toast.makeText(view.getContext(), "FAIL", Toast.LENGTH_LONG).show();
                    });
                }

                dialog.dismiss();
            });

            RecievedRequestsFragment.alertDialogBuilder.setNegativeButton("NO", (dialog, which) ->
                    dialog.dismiss());

            AlertDialog alert = RecievedRequestsFragment.alertDialogBuilder.create();
            alert.show();

        });

        TripEntry tripEntry = list.get(position);

        UtilityMethods.fillHolder(holder, tripEntry);
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

