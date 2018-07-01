//TEA = TripEntryAdapter

package garbagecollectors.com.unipool.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import garbagecollectors.com.unipool.R;
import garbagecollectors.com.unipool.activities.HomeActivity;
import garbagecollectors.com.unipool.application.UtilityMethods;
import garbagecollectors.com.unipool.models.TripEntry;

public class SentRequestsTEA extends TripEntryAdapter
{
    private List<TripEntry> list;
    private Context context;

    int isExpanded = -1;

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
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.item_trip_entry, parent, false);
        // set the view's size, margins, padding and layout parameters...

        return new MyHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(MyHolder holder, int position)
    {
        TripEntry tripEntry = list.get(position);

        UtilityMethods.fillTripEntryHolder(holder, tripEntry);

        if(list.get(position).getMessage() != null)
        {
            final boolean isExpanded = position== this.isExpanded;
            holder.messageCard.setVisibility(isExpanded?View.VISIBLE:View.GONE);
            holder.itemView.setActivated(isExpanded);
            holder.cardArrow.setOnClickListener(v -> {
                if (!isExpanded)
                    holder.cardArrow.setImageResource(R.drawable.ic_arrow_drop_down_circle_24px);
                else
                    holder.cardArrow.setImageResource(R.drawable.ic_arrow_left_24px);
                this.isExpanded = isExpanded ? -1:position;
                TransitionManager.beginDelayedTransition(HomeActivity.getRecycle());
                notifyDataSetChanged();
            });
        }

        holder.requestButton.setVisibility(View.INVISIBLE);
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

