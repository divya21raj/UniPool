package garbagecollectors.com.snucabpool;

import com.google.android.gms.location.places.Place;

import java.text.ParseException;
import java.util.HashMap;

public class Entry
{
    private String entry_id;

    private String user_id;     //Data type could be changed to long

    String name;

    String time, date;

    Object source, destination;

    public HashMap<String, Float> lambdaMap = new HashMap<>(); //HashMap contains entry_id(String value) as key and lambda(Float value) as value

    public Entry(String name, String entry_id, String user_id, String time, String date, Object source, Object destination, HashMap<String, Float> lambdaMap) throws ParseException
    {
        this.entry_id = entry_id;
        this.user_id = user_id;
        this.time = time;
        this.date = date;
        this.source = source;
        this.destination = destination;
        this.name = name;
    }



    public HashMap<String, Float> getLambdaMap()
    {
        return lambdaMap;
    }

    public Entry()
    {
    }

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

    public void setDate(String date)
    {
        this.date = date;
    }

    public Object getSource()
    {
        return source;
    }

    public void setSource(Object source)
    {
        this.source = source;
    }

    public Object getDestination()
    {
        return destination;
    }

    public void setDestination(Object destination)
    {
        this.destination = destination;
    }
}
