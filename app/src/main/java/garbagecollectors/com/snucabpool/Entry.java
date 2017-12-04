package garbagecollectors.com.snucabpool;

import com.google.android.gms.location.places.Place;

import java.util.HashMap;

public class Entry
{
    private String entry_id;
    private String user_id;

    String time, date;

    Place source, destination;

    private HashMap<String, Float> lambdaMap = new HashMap<>(); //HashMap contains entry_id(String value) as key and lambda(Float value) as value

    public Entry(String entry_id, String user_id, String time, String date, Place source, Place destination, HashMap<String, Float> lambdaMap)
    {
        this.entry_id = entry_id;
        this.user_id = user_id;
        this.time = time;
        this.date = date;
        this.source = source;
        this.destination = destination;
        this.lambdaMap = lambdaMap;
    }

    public Entry()
    {
    }

    public String getEntry_id()
    {
        return entry_id;
    }

    public String getUser_id()
    {
        return user_id;
    }

    public String getTime()
    {
        return time;
    }

    public String getDate()
    {
        return date;
    }

    public Place getSource()
    {
        return source;
    }

    public Place getDestination()
    {
        return destination;
    }

    public HashMap<String, Float> getLambdaMap()
    {
        return lambdaMap;
    }
}
