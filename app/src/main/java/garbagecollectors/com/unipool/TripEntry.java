package garbagecollectors.com.unipool;

import java.text.ParseException;
import java.util.HashMap;

public class TripEntry
{
    private String entry_id;

    private String user_id;     //Data type could be changed to long

    private String name;

    private String time, date;

    private GenLocation source, destination;

    public TripEntry(String name, String entry_id, String user_id, String time, String date, GenLocation source, GenLocation destination, HashMap<String, Float> lambdaMap) throws ParseException
    {
        this.entry_id = entry_id;
        this.user_id = user_id;
        this.time = time;
        this.date = date;
        this.source = source;
        this.destination = destination;
        this.name = name;
    }

    public TripEntry(TripEntry tripEntry)   //copyConstructor
    {
        this.entry_id = tripEntry.getEntry_id();
        this.user_id = tripEntry.getUser_id();
        this.time = tripEntry.getTime();
        this.date = tripEntry.getDate();
        this.source = tripEntry.getSource();
        this.destination = tripEntry.getDestination();
        this.name = tripEntry.getName();
    }

    public TripEntry()
    {}

    public String getEntry_id()
    {
        return entry_id;
    }

    public void setEntry_id(String entry_id)
    {
        this.entry_id = entry_id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUser_id()
    {
        return user_id;
    }

    public void setUser_id(String user_id)
    {
        this.user_id = user_id;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getDate()
    {
        return date;
    }

    public GenLocation getSource()
    {
        return source;
    }

    public GenLocation getDestination()
    {
        return destination;
    }

}
