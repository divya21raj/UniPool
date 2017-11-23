package garbagecollectors.com.snucabpool;

import java.util.HashMap;

/**
 * Created by Rohan on 22-11-2017.
 */

public class Entry
{
    private long user_id;                                                 //Data type could be changed to long
    String source, destination,time;
    private HashMap<Long, Float> map = new HashMap<>();                   //HashMap contains entry_id(Long value) and lambda(Float value)

    public Entry(long user_id, String source, String destination, String time, HashMap<Long, Float> map)
    {
        this.user_id = user_id;
        this.source = source;
        this.destination = destination;
        this.time = time;
        this.map = map;
    }

    public Entry()
    {}

    public long getUser_id()
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

    public HashMap<Long, Float> getMap()
    {
        return map;
    }
}
