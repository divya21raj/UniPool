//TEA = TripEntryAdapter

package garbagecollectors.com.snucabpool.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.TripEntry;
import garbagecollectors.com.snucabpool.User;
import garbagecollectors.com.snucabpool.UtilityMethods;
import garbagecollectors.com.snucabpool.activities.BaseActivity;

import static garbagecollectors.com.snucabpool.UtilityMethods.accessUserDatabase;
import static garbagecollectors.com.snucabpool.UtilityMethods.addRequestInList;
import static garbagecollectors.com.snucabpool.UtilityMethods.putInMap;

public class HomeActivityTEA extends TripEntryAdapter
{
    private List<TripEntry> list;
    private List<TripEntry> listCopy;
    private Context context;

    private boolean isRequestAlreadyInMap;
    private Boolean isAlreadyRequested;

    ProgressDialog progressDialog;

    public HomeActivityTEA(Context context)
    {
        super(context);
    }

    public HomeActivityTEA(List<TripEntry> list, Context context)
    {
        this.list = list;
        this.context = context;
    }

    public HomeActivityTEA()
    {
        listCopy.addAll(list);
    }
    // Create new views (invoked by the layout manager)
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.item_trip_entry, parent, false);

        return new MyHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyHolder holder, int position)
    {
        UtilityMethods.fillTripEntryHolder(holder, list.get(position));

        holder.itemView.setOnClickListener(view ->
        {
            progressDialog = new ProgressDialog(view.getContext());
            progressDialog.setMessage("Please wait...");

            User user = BaseActivity.getFinalCurrentUser();

            TripEntry tripEntry = list.get(position);

            if(tripEntry.getUser_id().equals(user.getUserId()))
            {
                Toast.makeText(view.getContext(), "Can't pool with yourself, that feature isn't ready yet...", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog.show();

            final User[] tripEntryUser = new User[1];
            Task userTask = accessUserDatabase("users/" + tripEntry.getUser_id());    //the user that created the clicked tripEntry
            userTask.addOnSuccessListener(aVoid ->
            {
                DataSnapshot snapshot = (DataSnapshot) userTask.getResult();

                tripEntryUser[0] = snapshot.getValue(User.class);

                DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
                DatabaseReference notificationDatabaseReference = BaseActivity.getNotificationDatabaseReference();

                ArrayList<TripEntry> requestSent = user.getRequestSent();
                HashMap<String, ArrayList<String>> requestsReceived = tripEntryUser[0].getRequestsReceived();

                isAlreadyRequested = addRequestInList(requestSent, user.getPairUps(), tripEntry);

                if(!isAlreadyRequested)
                {
                    isRequestAlreadyInMap = putInMap(requestsReceived, tripEntry.getEntry_id(), user.getUserId());
                }

                user.setRequestSent(requestSent);
                tripEntryUser[0].setRequestsReceived(requestsReceived);

                if(!isAlreadyRequested && !isRequestAlreadyInMap)
                {
                    //update firebase database to include arrayList that contains name of the item_trip_entry clicked in requests sent...
                    Task<Void> task1 = userDatabaseReference.child(user.getUserId()).child("requestSent").setValue(requestSent);
                    Task<Void> task2 = userDatabaseReference.child(tripEntryUser[0].getUserId()).child("requestsReceived").setValue(requestsReceived);

                    HashMap<String, String> notificationObject = new HashMap<>();
                    notificationObject.put("from", user.getUserId());
                    notificationObject.put("type", "request");

                    Task<Void> task3 = notificationDatabaseReference.child(tripEntryUser[0].getUserId()).push().setValue(notificationObject);

                    Task<Void> allTask = Tasks.whenAll(task1, task2, task3);
                    allTask.addOnSuccessListener(bVoid ->
                    {
                        progressDialog.dismiss();
                        Toast.makeText(view.getContext(), "Request Sent!", Toast.LENGTH_LONG).show();
                    });

                    allTask.addOnFailureListener(e ->
                    {
                        progressDialog.dismiss();
                        // apologize profusely to the user!
                        Toast.makeText(view.getContext(), "FAIL", Toast.LENGTH_LONG).show();
                    });
                }

                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(view.getContext(), "You two are already paired up!", Toast.LENGTH_LONG).show();
                }

            });
        });
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

    public void filter(String text)
    {
        list.clear();
        if(text.isEmpty())
        {
            list.addAll(listCopy);
        }
        else
        {
            text = text.toLowerCase();
            for(TripEntry trip : listCopy)
            {
                if((trip.getSource().toString().toLowerCase().contains(text))||(trip.getDestination().toString().toLowerCase().contains(text)))
                {
                    list.add(trip);
                }
            }
        }
        notifyDataSetChanged();
    }
}

