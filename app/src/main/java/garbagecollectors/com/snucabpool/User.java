package garbagecollectors.com.snucabpool;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by D2R on 29/11/2017.
 */

public class User
{
    private String userId;
    private String name;

    private ArrayList<Entry> requestedEntries;
    private Map<String, Entry> requestsRecieved;    //String is the userId of requester, Entry is the entry that he requested

    public User(String userId, String name, ArrayList<Entry> requestedEntries, Map<String, Entry> requestsRecieved)
    {
        this.userId = userId;
        this.name = name;
        this.requestedEntries = requestedEntries;
        this.requestsRecieved = requestsRecieved;
    }

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

    public Map<String, Entry> getRequestsRecieved()
    {
        return requestsRecieved;
    }

    public void setRequestsRecieved(Map<String, Entry> requestsRecieved)
    {
        this.requestsRecieved = requestsRecieved;
    }

    public User()
    {
    }
}
