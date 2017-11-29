package garbagecollectors.com.snucabpool;

import com.google.android.gms.location.places.Place;

import java.util.HashMap;

public class Entry
{
    private String entry_id;
    private String user_id;                               //Data type could be changed to long

    private Place source, destination;
    private String time, date;

    private HashMap<String, Float> lambdaMap = new HashMap<>(); //HashMap contains entry_id(String value) as key and lambda(Float value) as value

    public Entry(String entry_id, String user_id, Place source, Place destination, String time, String date, HashMap<String, Float> map)
    {
        this.entry_id = entry_id;
        this.user_id = user_id;
        this.source = source;
        this.destination = destination;
        this.time = time;
        this.date = date;
        this.lambdaMap = map;
    }

    public Entry()
    { }

    public String getEntry_id()
    {
        return entry_id;
    }

    public String getUser_id()
    {
        return user_id;
    }

    public Place getSource()
    {
        return source;
    }

    public Place getDestination()
    {
        return destination;
    }

    public String getTime()
    {
        return time;
    }

    public String getDate()
    {
        return date;
    }

    public HashMap<String, Float> getLambdaMap()
    {
        return lambdaMap;
    }
}
