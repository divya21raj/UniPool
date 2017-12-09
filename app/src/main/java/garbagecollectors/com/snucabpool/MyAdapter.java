package garbagecollectors.com.snucabpool;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
    //private String[] mDataset;

    List<Entry> list;
    Context context;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class MyHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView date;
        public TextView user_id;
        public TextView source;
        public TextView destination;
        public TextView name_user;
        public TextView travel_time;

        public MyHolder(View v) {
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
    public MyAdapter(List<Entry> list,Context context) {
       this.context=context;
       this.list=list;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.myview, parent, false);
        // set the view's size, margins, paddings and layout parameters...

        MyHolder vh = new MyHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        // holder.mTextView.setText(mDataset[position]);
        Entry mylist = list.get(position);

        holder.date.setText(mylist.getDate());
        holder.user_id.setText(mylist.getUser_id());
        holder.name_user.setText(mylist.getName());
        holder.travel_time.setText(mylist.getTime());

        //Toast.makeText(this, "Entry created!", Toast.LENGTH_SHORT).show();
        //System.out.println("Time is "+holder.travel_time);
        //Object temp_Source=(Place)mylist.getSource();
       // Place temp_Destination=(Place)mylist.getDestination();
       // holder.source.setText(temp_Source.getName());
        //holder.destination.setText(temp_Destination.getName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        int arr = 0;

        try{
            if(list.size()==0){

                arr = 0;

            }
            else{

                arr=list.size();
            }



        }catch (Exception e){



        }

        return arr;
    }
}

