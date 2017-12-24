//TEA = TripEntryAdapter

package garbagecollectors.com.snucabpool.adapters;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.TripEntry;
import garbagecollectors.com.snucabpool.User;
import garbagecollectors.com.snucabpool.UtilityMethods;
import garbagecollectors.com.snucabpool.activities.BaseActivity;
import garbagecollectors.com.snucabpool.activities.RequestActivity.RecievedRequestsFragment;

public class RecievedRequestsTEA extends TripEntryAdapter
{
    private List<TripEntry> list;
    private Context context;
    private boolean isAlreadyInMap = false;

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
            DatabaseReference userDatabaseReference = BaseActivity.getUserDatabaseReference();

            TripEntry tripEntry = list.get(position);

            User tripEntryUser = UtilityMethods.getUserFromDatabase(tripEntry.getUser_id());
            User finalCurrentUser = BaseActivity.getFinalCurrentUser();

            HashMap<String, ArrayList<String>> currentUserPairUps = finalCurrentUser.getPairUps();
            HashMap<String, ArrayList<String>> tripEntryUserPairUps = finalCurrentUser.getPairUps();

            HashMap<String, ArrayList<String>> recievedRequests = finalCurrentUser.getRequestsRecieved();
            ArrayList<TripEntry> sentRequests = tripEntryUser.getRequestSent();

            RecievedRequestsFragment.alertDialogBuilder.setPositiveButton("YES", (dialog, which) ->
            {
                isAlreadyInMap = UtilityMethods.putInMap(currentUserPairUps, tripEntryUser.getUserId(), tripEntry.getEntry_id());

                if(!isAlreadyInMap)
                {
                    UtilityMethods.putInMap(tripEntryUserPairUps, tripEntryUser.getUserId(), tripEntry.getEntry_id());

                    UtilityMethods.removeFromMap(recievedRequests, tripEntry.getEntry_id(), tripEntryUser.getUserId());
                    UtilityMethods.removeFromList(sentRequests, tripEntry.getEntry_id());

                    userDatabaseReference.child(finalCurrentUser.getUserId()).child("pairUps").setValue(currentUserPairUps);
                    userDatabaseReference.child(tripEntryUser.getUserId()).child("pairUps").setValue(tripEntryUserPairUps);

                    userDatabaseReference.child(finalCurrentUser.getUserId()).child("requestsRecieved").setValue(recievedRequests);
                    userDatabaseReference.child(tripEntryUser.getUserId()).child("requestSent").setValue(sentRequests);

                    Toast.makeText(context, "Request accepted!", Toast.LENGTH_SHORT).show();
                }

                else
                    Toast.makeText(context, "Already requested!", Toast.LENGTH_SHORT).show();

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

