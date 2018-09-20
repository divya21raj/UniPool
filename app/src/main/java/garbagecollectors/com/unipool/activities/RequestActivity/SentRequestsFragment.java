package garbagecollectors.com.unipool.activities.RequestActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import garbagecollectors.com.unipool.R;
import garbagecollectors.com.unipool.adapters.SentRequestsTEA;
import garbagecollectors.com.unipool.application.Globals;
import garbagecollectors.com.unipool.models.TripEntry;
import garbagecollectors.com.unipool.models.User;

public class SentRequestsFragment extends Fragment
{
    RecyclerView recycle;
    static SentRequestsTEA recyclerAdapter;

    static User finalCurrentUser;
    static HashMap<String, TripEntry> sentRequests;

    public SentRequestsFragment()
    {  }

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		if (isVisibleToUser) {
			Globals.OPEN_ACTIVITY = "REQUESTS";
            Log.d("Open", Globals.OPEN_ACTIVITY);
		}
	}

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

        recycle = view.findViewById(R.id.recycle_requests);

        if(sentRequests.size() >= 1)
        {
            ArrayList<TripEntry> sentRequestsList = new ArrayList<>();
            for(Map.Entry<String, TripEntry> entry: sentRequests.entrySet())
            {
                if(!entry.getKey().equals("dummyId"))
                    sentRequestsList.add(entry.getValue());
            }

            recyclerAdapter = new SentRequestsTEA(sentRequestsList,getContext());

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),1);

            recycle.setLayoutManager(layoutManager);
            recycle.setItemAnimator( new DefaultItemAnimator());
            recycle.setAdapter(recyclerAdapter);
        }

        return view;
    }


    public static void refreshRecycler()
    {
        try
        {
            finalCurrentUser = RequestActivity.getFinalCurrentUser();

            sentRequests = finalCurrentUser.getRequestSent();

            ArrayList<TripEntry> sentRequestsList = new ArrayList<>();
            for(Map.Entry<String, TripEntry> entry: sentRequests.entrySet())
            {
                if(!entry.getKey().equals("dummyId"))
                    sentRequestsList.add(entry.getValue());
            }

            recyclerAdapter.notifyDataSetChanged();

        }catch (NullPointerException ignored){}
    }
}