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
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;

import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.TripEntry;
import garbagecollectors.com.snucabpool.User;
import garbagecollectors.com.snucabpool.UtilityMethods;
import garbagecollectors.com.snucabpool.activities.HomeActivity;
import garbagecollectors.com.snucabpool.adapters.RecievedRequestsTEA;
import garbagecollectors.com.snucabpool.adapters.SentRequestsTEA;

public class RecievedRequestsFragment extends Fragment
{
    RecyclerView recycle;
    Button viewRecievedRequestsButton;

    User user;
    HashMap<String, ArrayList<String>> recievedRequestsMap;
    ArrayList<TripEntry> recievedRequestsList;

    ArrayList<TripEntry> tripEntries;

    public static AlertDialog.Builder alertDialogBuilder;

    public RecievedRequestsFragment()
    {  }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recieved_requests, container, false);

        alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        alertDialogBuilder.setTitle("Confirm");
        alertDialogBuilder.setMessage("Are you sure you want to accept?");

        user = HomeActivity.getFinalCurrentUser();
        recievedRequestsMap = user.getRequestsRecieved();

        tripEntries = HomeActivity.getTripEntryList();

        recievedRequestsList = UtilityMethods.populateRecievedRequestsList(recievedRequestsMap, tripEntries);

        recycle = (RecyclerView) view.findViewById(R.id.recycle_requests);

        viewRecievedRequestsButton = (Button) view.findViewById(R.id.viewButtonRecievedRequests);

        viewRecievedRequestsButton.setOnClickListener(v ->
        {
            if(recievedRequestsList.size() >= 1)
            {
                RecievedRequestsTEA recyclerAdapter = new RecievedRequestsTEA(recievedRequestsList, getContext());

                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),1);

                recycle.setLayoutManager(layoutManager);
                recycle.setItemAnimator( new DefaultItemAnimator());
                recycle.setAdapter(recyclerAdapter);
            }
        });

        if(recievedRequestsList.size() >= 1)
        {
            RecievedRequestsTEA recyclerAdapter = new RecievedRequestsTEA(recievedRequestsList, getContext());

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),1);

            recycle.setLayoutManager(layoutManager);
            recycle.setItemAnimator( new DefaultItemAnimator());
            recycle.setAdapter(recyclerAdapter);
        }

        return view;
    }

}