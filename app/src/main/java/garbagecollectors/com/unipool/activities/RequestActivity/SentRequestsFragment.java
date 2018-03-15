package garbagecollectors.com.unipool.activities.RequestActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import garbagecollectors.com.unipool.R;
import garbagecollectors.com.unipool.TripEntry;
import garbagecollectors.com.unipool.User;
import garbagecollectors.com.unipool.UtilityMethods;
import garbagecollectors.com.unipool.adapters.SentRequestsTEA;

public class SentRequestsFragment extends Fragment
{
    RecyclerView recycle;
    static SentRequestsTEA recyclerAdapter;

    static User finalCurrentUser;
    static ArrayList<TripEntry> sentRequests;

    public SentRequestsFragment()
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
        View view =  inflater.inflate(R.layout.fragment_sent_requests, container, false);

        finalCurrentUser = RequestActivity.getFinalCurrentUser();

        sentRequests = finalCurrentUser.getRequestSent();

        recycle = (RecyclerView) view.findViewById(R.id.recycle_requests);

        if(sentRequests.size() >= 1)
        {
            sentRequests = UtilityMethods.removeFromList(sentRequests, "0");

            recyclerAdapter = new SentRequestsTEA(sentRequests,getContext());

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),1);

            recycle.setLayoutManager(layoutManager);
            recycle.setItemAnimator( new DefaultItemAnimator());
            recycle.setAdapter(recyclerAdapter);
        }

        return view;
    }


    public static void refreshRecycler()
    {
        finalCurrentUser = RequestActivity.getFinalCurrentUser();

        sentRequests = finalCurrentUser.getRequestSent();

        sentRequests = UtilityMethods.removeFromList(sentRequests, "0");

        if(recyclerAdapter != null)
            recyclerAdapter.notifyDataSetChanged();
    }
}