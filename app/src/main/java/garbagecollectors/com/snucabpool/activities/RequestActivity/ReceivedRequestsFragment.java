package garbagecollectors.com.snucabpool.activities.RequestActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.TripEntry;
import garbagecollectors.com.snucabpool.User;
import garbagecollectors.com.snucabpool.UtilityMethods;
import garbagecollectors.com.snucabpool.activities.HomeActivity;
import garbagecollectors.com.snucabpool.adapters.ReceivedRequestsTEA;

public class ReceivedRequestsFragment extends Fragment
{
    RecyclerView recycle;
    static ReceivedRequestsTEA recyclerAdapter;

    User user;
    HashMap<String, ArrayList<String>> receivedRequestsMap;
    static ArrayList<TripEntry> receivedRequestsList;

    ArrayList<TripEntry> tripEntries;

    public static AlertDialog.Builder alertDialogBuilder;

    public ReceivedRequestsFragment()
    {  }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_received_requests, container, false);

        alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        alertDialogBuilder.setTitle("Confirm");
        alertDialogBuilder.setMessage("Are you sure you want to accept?");

        user = RequestActivity.getFinalCurrentUser();

        receivedRequestsMap = user.getRequestsReceived();

        tripEntries = HomeActivity.getTripEntryList();

        receivedRequestsList = new ArrayList<>();
        Task task = UtilityMethods.populateReceivedRequestsList(receivedRequestsList, receivedRequestsMap, tripEntries);

        recycle = (RecyclerView) view.findViewById(R.id.recycle_requests);

        task.addOnSuccessListener(o ->
        {
            recyclerAdapter = new ReceivedRequestsTEA(receivedRequestsList, getContext());

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),1);

            recycle.setLayoutManager(layoutManager);
            recycle.setItemAnimator( new DefaultItemAnimator());
            recycle.setAdapter(recyclerAdapter);
        });

        return view;
    }

    public static ArrayList<TripEntry> getReceivedRequestsList()
    {
        return receivedRequestsList;
    }

    public static void refreshRecycler()
    {
        if(recyclerAdapter != null)
            recyclerAdapter.notifyDataSetChanged();
    }
}