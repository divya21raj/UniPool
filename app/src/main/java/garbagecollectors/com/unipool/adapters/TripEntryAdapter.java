package garbagecollectors.com.unipool.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import garbagecollectors.com.unipool.R;

public abstract class TripEntryAdapter extends RecyclerView.Adapter<TripEntryAdapter.MyHolder>
{
    TripEntryAdapter()
    {}

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyHolder extends RecyclerView.ViewHolder
    {
        public TextView date;
        public TextView source;
        public TextView destination;
        public TextView name_user;
        public TextView travel_time;
        public Button requestButton;

        MyHolder(View v)
        {
            super(v);
            date = v.findViewById(R.id.vdate);
            source = v.findViewById(R.id.vsource);
            destination = v.findViewById(R.id.vdestination);
            name_user = v.findViewById(R.id.vname);
            travel_time = v.findViewById(R.id.vtime);
            requestButton = v.findViewById(R.id.requestButton);
        }
    }

}


