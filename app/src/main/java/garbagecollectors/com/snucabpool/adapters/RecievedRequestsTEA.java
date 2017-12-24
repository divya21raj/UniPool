//TEA = TripEntryAdapter

package garbagecollectors.com.snucabpool.adapters;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
            TripEntry tripEntry = list.get(position);

            User tripEntryUser = UtilityMethods.getUserFromDatabase(tripEntry.getUser_id());
            User finalCurrentUser = BaseActivity.getFinalCurrentUser();

            HashMap<String, ArrayList<String>> pairUps = finalCurrentUser.getPairUps();

            RecievedRequestsFragment.alertDialogBuilder.setPositiveButton("YES", (dialog, which) ->
            {
                UtilityMethods.putInMap(pairUps, tripEntryUser.getUserId(), tripEntry.getEntry_id());

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

