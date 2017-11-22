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
}
