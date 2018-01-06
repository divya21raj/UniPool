package garbagecollectors.com.snucabpool;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by D2R on 29/11/2017.
 */

public class User
{
    private String userId;
    private String name;
    private ArrayList<TripEntry> userTripEntries =new ArrayList<>();

    private ArrayList<TripEntry> requestSent;
    private HashMap<String, ArrayList<String>> requestsRecieved;
    //Key is the entryId of entry requested, Value is list of userIDs who've requested that entry
    //We have Map because we're taking TripEntry object of the entry that we have made (that the other person has clicked on)

    ArrayList<PairUp> pairUps;
    //Key is userId paired up with, value is list of tripEntries paired up over.


    public User(String userId, String name, ArrayList<TripEntry> userTripEntries, ArrayList<TripEntry> requestSent, HashMap<String, ArrayList<String>> requestsRecieved, ArrayList<PairUp> pairUps)
    {
        this.userId = userId;
        this.name = name;
        this.userTripEntries = userTripEntries;
        this.requestSent = requestSent;
        this.requestsRecieved = requestsRecieved;
        this.pairUps = pairUps;
    }

    public String getUserId()
    {
        return userId;
    }

    void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ArrayList<TripEntry> getRequestSent()
    {
        return requestSent;
    }

    public void setRequestSent(ArrayList<TripEntry> requestSent)
    {
        this.requestSent = requestSent;
    }

    public HashMap<String, ArrayList<String>> getRequestsRecieved()
    {
        return requestsRecieved;
    }

    public void setRequestsRecieved(HashMap<String, ArrayList<String>> requestsRecieved)
    {
        this.requestsRecieved = requestsRecieved;
    }

    public User()
    {
    }

    public ArrayList<TripEntry> getUserTripEntries()
    {
        return userTripEntries;
    }

    public ArrayList<PairUp> getPairUps()
    {
        return pairUps;
    }
}
