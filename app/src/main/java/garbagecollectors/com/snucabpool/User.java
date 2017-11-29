package garbagecollectors.com.snucabpool;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * Created by D2R on 29/11/2017.
 */

public class User
{
    private String userId;
    private String name;
    private ArrayList<Entry> requestedEntries;
    private ArrayList<User> requestsRecieved;

    public User(String userId, String name, ArrayList<Entry> requestedEntries, ArrayList<User> requestsRecieved)
    {
        this.userId = userId;
        this.name = name;
        this.requestedEntries = requestedEntries;
        this.requestsRecieved = requestsRecieved;
    }

    public User()
    {}

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
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

    public ArrayList<Entry> getRequestedEntries()
    {
        return requestedEntries;
    }

    public void setRequestedEntries(ArrayList<Entry> requestedEntries)
    {
        this.requestedEntries = requestedEntries;
    }

    public ArrayList<User> getRequestsRecieved()
    {
        return requestsRecieved;
    }

    public void setRequestsRecieved(ArrayList<User> requestsRecieved)
    {
        this.requestsRecieved = requestsRecieved;
    }
}
