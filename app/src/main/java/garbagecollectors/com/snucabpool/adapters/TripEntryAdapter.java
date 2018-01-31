package garbagecollectors.com.snucabpool.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.TripEntry;

public abstract class TripEntryAdapter extends RecyclerView.Adapter<TripEntryAdapter.MyHolder>
{
    private LayoutInflater inflater;
    private List<TripEntry> list;
    private Context context;

    TripEntryAdapter(Context context)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    TripEntryAdapter()
    {  }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyHolder extends RecyclerView.ViewHolder
    {
        // each data item is just a string in this case
        public TextView date;
        public TextView source;
        public TextView destination;
        public TextView name_user;
        public TextView travel_time;


        MyHolder(View v)
        {
            super(v);
            date = (TextView) v.findViewById(R.id.vdate);
            source = (TextView) v.findViewById(R.id.vsource);
            destination = (TextView) v.findViewById(R.id.vdestination);
            name_user = (TextView) v.findViewById(R.id.vname);
            travel_time = (TextView) v.findViewById(R.id.vtime);
        }
    }
}


