package garbagecollectors.com.snucabpool.activities.RequestActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    User user;
    HashMap<String, ArrayList<String>> recievedRequestsMap;
    ArrayList<TripEntry> recievedRequestsList;

    ArrayList<TripEntry> tripEntries;

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

        user = HomeActivity.getFinalCurrentUser();
        recievedRequestsMap = user.getRequestsRecieved();

        tripEntries = HomeActivity.getTripEntryList();

        recievedRequestsList = UtilityMethods.populateRecievedRequestsList(recievedRequestsMap, tripEntries);

        recycle = (RecyclerView) view.findViewById(R.id.recycle_requests);

        if(recievedRequestsList.size() > 1)
        {
            RecievedRequestsTEA recyclerAdapter = new RecievedRequestsTEA(recievedRequestsList,getContext());

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),1);
            /// RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
            // recycle.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));

            recycle.setLayoutManager(layoutManager);
            recycle.setItemAnimator( new DefaultItemAnimator());
            recycle.setAdapter(recyclerAdapter);
        }

        return view;
    }

}