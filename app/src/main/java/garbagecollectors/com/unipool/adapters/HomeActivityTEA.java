//TEA = TripEntryAdapter

package garbagecollectors.com.unipool.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import garbagecollectors.com.unipool.R;
import garbagecollectors.com.unipool.activities.BaseActivity;
import garbagecollectors.com.unipool.application.Globals;
import garbagecollectors.com.unipool.application.UtilityMethods;
import garbagecollectors.com.unipool.models.TripEntry;
import garbagecollectors.com.unipool.models.User;

import static garbagecollectors.com.unipool.application.UtilityMethods.accessUserDatabase;
import static garbagecollectors.com.unipool.application.UtilityMethods.addRequestInList;
import static garbagecollectors.com.unipool.application.UtilityMethods.putInMap;

public class HomeActivityTEA extends TripEntryAdapter
{
    private List<TripEntry> list;
    private List<TripEntry> listCopy = new ArrayList<>();
    private Context context;

    private boolean isRequestAlreadyInMap;
    private Boolean isAlreadyRequested;

    private AlertDialog.Builder alertDialogBuilder;

    private ProgressDialog progressDialog;

    public HomeActivityTEA(List<TripEntry> list, Context context)
    {
        this.list = list;
        this.context = context;
        listCopy.addAll(list);

        alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Confirm");
        alertDialogBuilder.setMessage("Send request?");
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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(MyHolder holder, int position)
    {
        try
        {
            UtilityMethods.fillTripEntryHolder(holder, list.get(position));

            holder.cardArrow.setOnClickListener(v -> {
                if (holder.tripEntryExpand.isExpanded())
                {
                    holder.tripEntryExpand.collapse();
                    holder.cardArrow.setImageResource(R.drawable.ic_arrow_left_24px);
                }
                else
                {
                    holder.tripEntryExpand.expand();
                    holder.cardArrow.setImageResource(R.drawable.ic_arrow_drop_down_circle_24px);
                }
            });

            holder.requestButton.setOnClickListener(view -> onRequestClick(view, position));

            holder.itemView.setOnLongClickListener(v ->
            {
                if(Globals.USER_EMAIL.contains(context.getString(R.string.dev_mail)))  //God mode
                    deleteEntry(v, position);
                else onRequestClick(v, position);
                return true;
            });
        } catch (Exception e)
        {
            //Maybe cause of missing info
            e.printStackTrace();
        }

    }

    private void onRequestClick(View view, int position)
    {
        TripEntry tripEntry = list.get(position);

        if (tripEntry.isNotFromApp())
            if(tripEntry.getPhone() != null && !tripEntry.getPhone().isEmpty())
                addToContacts(tripEntry);
            else
                writeMail(tripEntry);
        else
        {
            if(tripEntry.getUser_id().equals(BaseActivity.getFinalCurrentUser().getUserId()))
                deleteEntry(view, position);
            else sendRequest(view, position);
        }
    }

    private void writeMail(TripEntry tripEntry)
    {
        alertDialogBuilder.setMessage("This person isn't on the app :(  \nDo you want to mail them instead?");

        alertDialogBuilder.setPositiveButton("YES", (dialog, which) ->
        {
            // Open email intent
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto",tripEntry.getEmail(), null));
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
        });

        alertDialogBuilder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void addToContacts(TripEntry tripEntry)
    {
        alertDialogBuilder.setMessage("This person isn't on the app :(\nDo you want to add them to your contacts?");

        alertDialogBuilder.setPositiveButton("YES", (dialog, which) ->
        {
            //open contacts intent with pre-filled stuff

            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

            intent.putExtra(ContactsContract.Intents.Insert.NAME, tripEntry.getName());
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, tripEntry.getPhone());

            context.startActivity(intent);
        });

        alertDialogBuilder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void deleteEntry(View view, int position)
    {
        final Integer[] i = {0};

        TaskCompletionSource<DataSnapshot> userDBSource = new TaskCompletionSource<>();
        Task userDBTask = userDBSource.getTask();

        alertDialogBuilder.setMessage("Delete this entry?");

        alertDialogBuilder.setPositiveButton("YES", (dialog, which) ->
        {
            progressDialog = new ProgressDialog(view.getContext());
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            TripEntry tripEntry = list.get(position);

            Task<Void> task1 = Globals.entryDatabaseReference.child(tripEntry.getEntry_id()).removeValue();

            Globals.userDatabaseReference.child("userTripEntries").addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    userDBSource.setResult(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                    userDBSource.setException(databaseError.toException());
                    Toast.makeText(context, "Network error! Please try again...", Toast.LENGTH_SHORT).show();
                }
            });

            Task<Void> allTasks = Tasks.whenAll(task1, userDBTask);
            allTasks.addOnSuccessListener(aVoid ->
            {
                DataSnapshot userTripEntryData = (DataSnapshot) userDBTask.getResult();

                for (DataSnapshot dataSnapshot : userTripEntryData.getChildren())
                {
                    TripEntry userTripEntry = dataSnapshot.getValue(TripEntry.class);
                    if (userTripEntry != null && userTripEntry.getEntry_id().equals(tripEntry.getEntry_id()))
                        break;

                    i[0]++;
                }

                Task<Void> task3 = Globals.userDatabaseReference.child("userTripEntries").child(tripEntry.getEntry_id()).removeValue();

                task3.addOnSuccessListener(aVoid1 ->
                {
                    UtilityMethods.removeFromList(BaseActivity.getTripEntryList(), tripEntry.getEntry_id());
                    BaseActivity.getFinalCurrentUser().getUserTripEntries().remove(tripEntry.getEntry_id());

                    progressDialog.dismiss();
                });

                task3.addOnFailureListener(e ->
                {
                    progressDialog.dismiss();
                    Toast.makeText(view.getContext(), "Network error! Please try again...", Toast.LENGTH_LONG).show();
                });
            });

            allTasks.addOnFailureListener(e ->
            {
                progressDialog.dismiss();
                Toast.makeText(view.getContext(), "Network error! Please try again...", Toast.LENGTH_LONG).show();
            });
        });

        alertDialogBuilder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void sendRequest(View view, int position)
    {
        alertDialogBuilder.setMessage("Send request to " + list.get(position).getName() + " ?");

        alertDialogBuilder.setPositiveButton("YES", (dialog, which) ->
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

                DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference(Globals.UNI + "users");

                HashMap<String, TripEntry> requestSent = user.getRequestSent();
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
                    notificationObject.put("type", "requestCreated");

                    Task<Void> task3 = Globals.notificationDatabaseReference.child(tripEntryUser[0].getUserId()).push().setValue(notificationObject);

                    Task<Void> allTask = Tasks.whenAll(task1, task2, task3);
                    allTask.addOnSuccessListener(bVoid ->
                    {
                        progressDialog.dismiss();
                        BaseActivity.setFinalCurrentUser(user);
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

        alertDialogBuilder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void filter(String text)
    {
        list.clear();

        if(text.isEmpty())
            list.addAll(listCopy);

        else
        {
            text = text.toLowerCase();

            for(TripEntry tripEntry: listCopy)
            {
                String name = tripEntry.getName().toLowerCase();
                String destination = tripEntry.getDestination().getName().toLowerCase();
                String source = tripEntry.getSource().getName().toLowerCase();

                String time = tripEntry.getTime();
                String date = tripEntry.getDate();

                if(name.contains(text) ||destination.contains(text) || source.contains(text)
                            || time.contains(text) || date.contains(text))
                    list.add(tripEntry);
            }
        }

        notifyDataSetChanged();
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

