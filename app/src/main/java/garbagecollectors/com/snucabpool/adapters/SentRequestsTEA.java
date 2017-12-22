//TEA = TripEntryAdapter

package garbagecollectors.com.snucabpool.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.TripEntry;
import garbagecollectors.com.snucabpool.UtilityMethods;

public class SentRequestsTEA extends TripEntryAdapter
{
    //private String[] mDataset;
    private LayoutInflater inflater;
    private List<TripEntry> list;
    private Context context;

    private boolean isRequestAlreadyInMap;
    private Boolean isAlreadyRequested;

    public SentRequestsTEA(Context context)
    {
        super(context);
    }

    public SentRequestsTEA(List<TripEntry> list, Context context)
    {
        this.list = list;
        this.context = context;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    // Create new views (invoked by the layout manager)
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType)
    {
        // create a new view

        View v = LayoutInflater.from(context).inflate(R.layout.card, parent, false);
        // set the view's size, margins, padding and layout parameters...

        return new MyHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyHolder holder, int position)
    {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        // holder.mTextView.setText(mDataset[position]);

        TripEntry tripEntry = list.get(position);

        UtilityMethods.fillHolder(holder, tripEntry);
        
        //System.out.println("Time is "+holder.travel_time);
        //Object temp_Source=(Place)tripEntry.getSource();
        // Place temp_Destination=(Place)tripEntry.getDestination();
        // holder.source.setText(temp_Source.getName());
        //holder.destination.setText(temp_Destination.getName());
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

