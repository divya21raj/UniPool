package garbagecollectors.com.snucabpool;

import android.location.Location;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static java.lang.Math.abs;

/**
 * Created by Rohan on 22-11-2017.
 */

public class Sorting_Filtering
{
    Location src1, dest1, src2, dest2;
    java.text.DateFormat df = new java.text.SimpleDateFormat("hh:mm:ss");

    final int src_wt = 100;
    final int dest_wt = 50;
    final int time_wt = 25;

    public static ArrayList<Entry> entry_list = entry_list = new ArrayList<>();

    public float calc_lambda(Entry e1, Entry e2) throws ParseException {
        float lambda = 0;

        src1 = (Location)e1.getSource();
        src2 = (Location)e2.getSource();
        dest1 = (Location)e1.getDestination();
        dest2 = (Location)e2.getDestination();

        java.util.Date t1 = df.parse(e1.getDate());
        java.util.Date t2 = df.parse(e2.getDate());

        long time_diff = abs(t2.getTime() - t1.getTime());

        lambda = (src_wt/src1.distanceTo(src2))+(dest_wt/dest1.distanceTo(dest2))+(time_wt/time_diff);

        return lambda;
    }

    private void entry_sort()
    {
        Collections.sort(entry_list, new Comparator<Entry>() {
            @Override
            public int compare(Entry o1, Entry o2) {
                float lambda1 = o1.getLambdaMap().get(o1.getEntry_id());
                float lambda2 = o2.getLambdaMap().get(o2.getEntry_id());

                return lambda1 < lambda2 ? -1 : lambda1 == lambda2 ? 0 : 1;
            }
        });
    }
}