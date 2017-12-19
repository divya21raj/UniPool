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
    private ArrayList<TripEntry> user_entries =new ArrayList<>();

    private ArrayList<TripEntry> requestSent;
    private HashMap<String, ArrayList<String>> requestsRecieved;
    //Key is the entryId of entry requested, Value is list of userIDs who've requested that entry
    //We have Map because we're taking TripEntry object of the entry that we have made (that the other person has clicked on)

    private ArrayList<TripEntry> friends;

    public User(String userId, String name, ArrayList<TripEntry> requestSent, HashMap<String, ArrayList<String>> requestsRecieved, ArrayList<TripEntry> friends)
    {
        this.userId = userId;
        this.name = name;
        this.requestSent = requestSent;
        this.requestsRecieved = requestsRecieved;
        this.friends = friends;
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

    public ArrayList<TripEntry> getUser_entries()
    {
        return user_entries;
    }

    public ArrayList<TripEntry> getFriends()
    {
        return friends;
    }

}
