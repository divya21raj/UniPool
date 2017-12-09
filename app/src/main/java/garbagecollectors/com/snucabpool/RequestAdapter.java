package garbagecollectors.com.snucabpool;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Atishay Arjav on 29-11-2017.
 */

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private ArrayList<User> mDataset;

    protected static DatabaseReference userDatabaseReference;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }
    //TODO accepting requests: aded to frineds list
    //TODO display requests receieved
    // Provide a suitable constructor (depends on the kind of dataset)
    public RequestAdapter(ArrayList<User> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_request, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            /*@Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
            }*/
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    //ArrayList<User> list = new ArrayList<User>();
                    Map<String, Object> td = (HashMap<String,Object>) dataSnapshot.getValue();

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                         User u = dataSnapshot1.getValue(User.class);
                        // FireModel fire = new FireModel();
                        String user_Id = u.getUserId();
                        String name = u.getName();

                        u.setUserId(user_Id);
                        u.setName(name);
                        mDataset.add(u);

                    }

                }

                @Override
                public void onCancelled(DatabaseError error)
                {
                    // Failed to read value
                    Log.w("Hello", "Failed to read value.", error.toException());
                }
            });

            /*
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });*/
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position).getName());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
