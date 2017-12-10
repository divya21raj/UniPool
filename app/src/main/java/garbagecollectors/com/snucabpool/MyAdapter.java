package garbagecollectors.com.snucabpool;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
    //private String[] mDataset;
    private LayoutInflater inflater;
    private List<Entry> list;
    private Context context;

    public MyAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class MyHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView date;
        TextView user_id;
        TextView source;
        TextView destination;
        TextView name_user;
        TextView travel_time;


        MyHolder(View v) {
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
    public MyAdapter(List<Entry> list, Context context) {
       this.context=context;
       this.list=list;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view

        View v = LayoutInflater.from(context).inflate(R.layout.card, parent, false);
        // set the view's size, margins, paddings and layout parameters...

        MyHolder holder = new MyHolder(v);
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {

        @Override
            public void onClick(View view) {
            FirebaseAuth mAuth;
            FirebaseUser currentUser;
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            Entry entry = list.get(position);

            //NOTES: mylist has the entry being displayed on the card. currentUSer has teh user
            User user = new User(currentUser.getUid(), currentUser.getDisplayName(), new ArrayList<Entry>(), new HashMap<String, String>(), new ArrayList<Entry>());
            user.getRequestSent().add(entry);
            DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference();
            //update firebase database to include arrayList that contains name of the card clicked in requests sent
            mDatabase.child("users").child(entry.getUser_id()).child("requestReceived").setValue(user.getRequestSent().toString());
            mDatabase.child("users").child(user.getUserId()).child("requestSent").setValue(entry.getUser_id()+", ");

            mDatabase.child("users").child(user.getUserId()).child("friends").setValue(entry.getUser_id()+", ");
            mDatabase.child("users").child(entry.getUser_id()).child("friends").setValue(user.getRequestSent().toString());
            //magic over!

            Toast.makeText(view.getContext(), "Request Sent!", Toast.LENGTH_LONG).show();

               //Intent intent = new Intent(view.getContext(), HomeActivity.class);
               //view.getContext().startActivity(intent);
               }
           });
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        // holder.mTextView.setText(mDataset[position]);

        Entry mylist = list.get(position);
        holder.date.setText(mylist.getDate());
        holder.user_id.setText(mylist.getUser_id());
        holder.name_user.setText(mylist.getName());
        holder.travel_time.setText(mylist.getTime());


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

