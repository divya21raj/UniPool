package garbagecollectors.com.snucabpool;

import java.util.HashMap;

public class Entry
{
    private String entry_id;
    private String user_id;                               //Data type could be changed to long

    String source, destination,time;

    private HashMap<String, Float> lambdaMap = new HashMap<>(); //HashMap contains entry_id(String value) as key and lambda(Float value) as value

    public Entry(String entry_id, String user_id, String source, String destination, String time, HashMap<String, Float> map)
    {
        this.entry_id = entry_id;
        this.user_id = user_id;
        this.source = source;
        this.destination = destination;
        this.time = time;
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

    public String getSource()
    {
        return source;
    }

    public String getDestination()
    {
        return destination;
    }

    public String getTime()
    {
        return time;
    }

    public HashMap<String, Float> getMap()
    {
        return lambdaMap;
    }
}
