package garbagecollectors.com.snucabpool;

import com.google.android.gms.location.places.Place;

import java.util.HashMap;

public class Entry
{
    private String entry_id;
    String user_id;                               //Data type could be changed to long
    String name;
    String time, date;
    Object source, destination;

    private HashMap<String, Float> lambdaMap = new HashMap<>(); //HashMap contains entry_id(String value) as key and lambda(Float value) as value

    public Entry(String name, String entry_id, String user_id, String time, String date, Object source, Object destination, HashMap<String, Float> lambdaMap)
    {
        this.entry_id = entry_id;
        this.user_id = user_id;
        this.time = time;
        this.date = date;
        this.source = source;
        this.destination = destination;
        this.lambdaMap = lambdaMap;
        this.name = name;
    }

    public Entry()
    {
    }

    public void setName(String name)
    {
        this.name=name;
    }
    public void setEntry_id(String entry_id) {
        this.entry_id = entry_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public void setDestination(Object destination) {
        this.destination = destination;
    }

    public void setLambdaMap(HashMap<String, Float> lambdaMap) {
        this.lambdaMap = lambdaMap;
    }

    public String getName()
    {
        return name;
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

    public Object getSource()
    {
        return source;
    }

    public Object getDestination()
    {
        return destination;
    }

    public HashMap<String, Float> getLambdaMap()
    {
        return lambdaMap;
    }
}
