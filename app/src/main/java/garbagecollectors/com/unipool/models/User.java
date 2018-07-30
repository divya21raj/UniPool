package garbagecollectors.com.unipool.models;

import java.util.ArrayList;
import java.util.HashMap;

public class User
{
    private String userId;
    private String name;
    private String photoUrl;
    private String email;

    private HashMap<String, TripEntry> userTripEntries =new HashMap<>();

    private HashMap<String, TripEntry> requestSent;  //key = tripEntryId
    private HashMap<String, ArrayList<String>> requestsReceived;
    //Key is the entryId of entry requested, Value is list of userIDs who've requested that entry
    //We have Map because we're taking TripEntry object of the entry that we have made (that the other person has clicked on)

    private String deviceToken;

    private boolean isOnline;

    private HashMap<String, PairUp> pairUps;

    public User(String userId, String name, String photoUrl, String email, HashMap<String, TripEntry> userTripEntries, HashMap<String, TripEntry> requestSent, HashMap<String, ArrayList<String>> requestsReceived, String deviceToken, boolean isOnline, HashMap<String, PairUp> pairUps)
    {
        this.userId = userId;
        this.name = name;
        this.photoUrl = photoUrl;
        this.email = email;
        this.userTripEntries = userTripEntries;
        this.requestSent = requestSent;
        this.requestsReceived = requestsReceived;
        this.deviceToken = deviceToken;
        this.isOnline = isOnline;
        this.pairUps = pairUps;
    }

    public User()
    {}

    public String getUserId()
    {
        return userId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public HashMap<String, TripEntry> getRequestSent()
    {
        return requestSent;
    }

    public void setRequestSent(HashMap<String, TripEntry> requestSent)
    {
        this.requestSent = requestSent;
    }

    public HashMap<String, ArrayList<String>> getRequestsReceived()
    {
        return requestsReceived;
    }

    public void setRequestsReceived(HashMap<String, ArrayList<String>> requestsReceived)
    {
        this.requestsReceived = requestsReceived;
    }

    public HashMap<String, TripEntry> getUserTripEntries()
    {
        return userTripEntries;
    }

    public HashMap<String, PairUp> getPairUps()
    {
        return pairUps;
    }

    public void setPairUps(HashMap<String, PairUp> pairUps)
    {
        this.pairUps = pairUps;
    }

    public String getDeviceToken()
    {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken)
    {
        this.deviceToken = deviceToken;
    }

    public String getPhotoUrl()
    {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl)
    {
        this.photoUrl = photoUrl;
    }

    public boolean isOnline()
    {
        return isOnline;
    }

    public void setOnline(boolean online)
    {
        isOnline = online;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
}
