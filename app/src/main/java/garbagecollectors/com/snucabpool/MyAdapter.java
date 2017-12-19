package garbagecollectors.com.snucabpool;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import garbagecollectors.com.snucabpool.activities.BaseActivity;

import static garbagecollectors.com.snucabpool.UtilityMethods.addRequestInList;
import static garbagecollectors.com.snucabpool.UtilityMethods.addRequestInMap;
import static garbagecollectors.com.snucabpool.UtilityMethods.getUserFromDatabase;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder>
{
    //private String[] mDataset;
    private LayoutInflater inflater;
    private List<TripEntry> list;
    private Context context;

    private boolean isRequestAlreadyInMap;
    private Boolean isAlreadyRequested;

    public MyAdapter(Context context)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class MyHolder extends RecyclerView.ViewHolder
    {
        // each data item is just a string in this case
        public TextView date;
        TextView user_id;
        TextView source;
        TextView destination;
        TextView name_user;
        TextView travel_time;


        MyHolder(View v)
        {
            super(v);
            date=(TextView)v.findViewById(R.id.vdate);
            user_id=(TextView)v.findViewById(R.id.vuser_id);
            source=(TextView)v.findViewById(R.id.vsource);
            destination=(TextView)v.findViewById(R.id.vdestination);
            name_user=(TextView)v.findViewById(R.id.vname);
            travel_time=(TextView)v.findViewById(R.id.vtime);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<TripEntry> list, Context context)
    {
       this.context=context;
       this.list=list;
    }

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
        holder.itemView.setOnClickListener(view ->
        {
            User user = BaseActivity.getFinalCurrentUser();

            TripEntry tripEntry = list.get(position);

            if(tripEntry.getUser_id().equals(user.getUserId()))
            {
                Toast.makeText(view.getContext(), "Can't pool with yourself, that feature isn't ready yet...", Toast.LENGTH_SHORT).show();
                return;
            }

            User tripEntryUser = getUserFromDatabase(tripEntry.getUser_id());    //the user that created the clicked tripEntry

            DatabaseReference userDatabaseReference = BaseActivity.getUserDatabaseReference();

            ArrayList<TripEntry> requestSent = user.getRequestSent();
            HashMap<String, ArrayList<String>> requestsRecieved = tripEntryUser.getRequestsRecieved();

            isAlreadyRequested = addRequestInList(requestSent, tripEntry);

            if(!isAlreadyRequested)
                isRequestAlreadyInMap = addRequestInMap(requestsRecieved, tripEntry.getEntry_id(), user.getUserId());

            user.setRequestSent(requestSent);
            tripEntryUser.setRequestsRecieved(requestsRecieved);

            if(!isAlreadyRequested && !isRequestAlreadyInMap)
            {
                //update firebase database to include arrayList that contains name of the card clicked in requests sent...
                userDatabaseReference.child(tripEntryUser.getUserId()).removeValue();
                userDatabaseReference.child(tripEntryUser.getUserId()).setValue(tripEntryUser);

                userDatabaseReference.child(user.getUserId()).removeValue();
                userDatabaseReference.child(user.getUserId()).setValue(user);

            /*userDatabaseReference.child("users").child(user.getUserId()).child("friends").setValue(tripEntry.getUser_id()+", ");
            userDatabaseReference.child("users").child(tripEntry.getUser_id()).child("friends").setValue(user.getRequestSent().toString());
            magic over!*/

                Toast.makeText(view.getContext(), "Request Sent!", Toast.LENGTH_LONG).show();
            }

            else
                Toast.makeText(view.getContext(), "Request already sent", Toast.LENGTH_LONG).show();

               //Intent intent = new Intent(view.getContext(), HomeActivity.class);
               //view.getContext().startActivity(intent);

        });
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        // holder.mTextView.setText(mDataset[position]);

        TripEntry myList = list.get(position);
        holder.date.setText(myList.getDate());
//      holder.user_id.setText(myList.getUser_id());
        holder.name_user.setText(myList.getName());
        holder.travel_time.setText(myList.getTime());


        //System.out.println("Time is "+holder.travel_time);
        //Object temp_Source=(Place)myList.getSource();
       // Place temp_Destination=(Place)myList.getDestination();
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

