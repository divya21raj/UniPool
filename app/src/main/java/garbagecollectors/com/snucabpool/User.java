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
    public ArrayList<Entry> user_entries =new ArrayList<>();

    private ArrayList<Entry> requestSent;//String is the userId of requester, Entry is the entry that he requested
    private Map<String, String> requestsRecieved;//We have Map because we're taking Entry object of the entry that we have made (that the other person has clicked on)
    private ArrayList<Entry> friends;

    public User(String userId, String name, ArrayList<Entry> requestSent, Map<String, String> requestsRecieved, ArrayList<Entry> friends)
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

    public ArrayList<Entry> getRequestSent()
    {
        return requestSent;
    }

    public void setRequestSent(ArrayList<Entry> requestSent)
    {
        this.requestSent = requestSent;
    }

    public Map<String, String> getRequestsRecieved()
    {
        return requestsRecieved;
    }

    public void setRequestsRecieved(Map<String, String> requestsRecieved)
    {
        this.requestsRecieved = requestsRecieved;
    }

    public User()
    {
    }
}
