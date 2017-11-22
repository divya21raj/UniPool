package garbagecollectors.com.snucabpool;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;

/**
 * Created by Rohan on 22-11-2017.
 */

public class Sorting_Filtering
{
    Place place;
    final int src_wt = 100;
    final int dest_wt = 50;
    final int time_wt = 25;

    static ArrayList<Entry> entry_list = entry_list = new ArrayList<>();

    float calc_lambda(Entry e1, Entry e2)
    {
        
        float lambda = 0;

        //lambda = (src_wt/dist(e1.source, e2.source))+(dest_wt/dist(e1.destination, e2.destination))+(time_wt/diff(e1.time,e2.time));

        return lambda;
    }
}
