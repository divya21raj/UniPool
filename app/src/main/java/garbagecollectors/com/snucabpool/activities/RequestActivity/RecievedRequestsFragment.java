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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.TripEntry;
import garbagecollectors.com.snucabpool.User;
import garbagecollectors.com.snucabpool.UtilityMethods;
import garbagecollectors.com.snucabpool.activities.HomeActivity;
import garbagecollectors.com.snucabpool.adapters.RecievedRequestsTEA;

public class RecievedRequestsFragment extends Fragment
{
    RecyclerView recycle;
    static RecievedRequestsTEA recyclerAdapter;

    User user;
    HashMap<String, ArrayList<String>> recievedRequestsMap;
    static ArrayList<TripEntry> recievedRequestsList;

    ArrayList<TripEntry> tripEntries;

    public static AlertDialog.Builder alertDialogBuilder;

    public RecievedRequestsFragment()
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
        View view = inflater.inflate(R.layout.fragment_recieved_requests, container, false);

        alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        alertDialogBuilder.setTitle("Confirm");
        alertDialogBuilder.setMessage("Are you sure you want to accept?");

        user = RequestActivity.getFinalCurrentUser();

        recievedRequestsMap = user.getRequestsRecieved();

        tripEntries = HomeActivity.getTripEntryList();

        recievedRequestsList = new ArrayList<>();
        Task task = UtilityMethods.populateRecievedRequestsList(recievedRequestsList, recievedRequestsMap, tripEntries);

        recycle = (RecyclerView) view.findViewById(R.id.recycle_requests);

        task.addOnSuccessListener(o ->
        {
            recyclerAdapter = new RecievedRequestsTEA(recievedRequestsList, getContext());

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),1);

            recycle.setLayoutManager(layoutManager);
            recycle.setItemAnimator( new DefaultItemAnimator());
            recycle.setAdapter(recyclerAdapter);
        });

        return view;
    }

    public static ArrayList<TripEntry> getRecievedRequestsList()
    {
        return recievedRequestsList;
    }

    public static void refreshRecycler()
    {
        if(recyclerAdapter != null)
            recyclerAdapter.notifyDataSetChanged();
    }
}