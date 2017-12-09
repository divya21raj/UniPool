package garbagecollectors.com.snucabpool;

import com.google.android.gms.location.places.Place;

import java.text.ParseException;
import java.util.HashMap;

public class Entry
{
    private String entry_id;
    private String user_id;

    Sorting_Filtering sf = new Sorting_Filtering();

    String time, date;

    Object source, destination;

    private HashMap<String, Float> lambdaMap = new HashMap<>(); //HashMap contains entry_id(String value) as key and lambda(Float value) as value

    public Entry(String entry_id, String user_id, String time, String date, Place source, Place destination, HashMap<String, Float> lambdaMap) throws ParseException {
        this.entry_id = entry_id;
        this.user_id = user_id;
        this.time = time;
        this.date = date;
        this.source = source;
        this.destination = destination;
        this.setLambdaMap();
    }

    void setLambdaMap() throws ParseException {
        for(Entry e : sf.entry_list)
        {
            this.lambdaMap.put(e.getEntry_id(), sf.calc_lambda(this, e));
        }
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
