package garbagecollectors.com.snucabpool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import garbagecollectors.com.snucabpool.activities.BaseActivity;

public class UtilityMethods
{
    public static User getUserFromDatabase(String uid)
    {
        User userToBeFound = null;

        for(User user: BaseActivity.getUserList())
        {
            if(user.getUserId().equals(uid))
            {
                userToBeFound = user;
                break;
            }
        }

        return userToBeFound;
    }

    static boolean addRequestInList(ArrayList<TripEntry> requestSent, TripEntry tripEntry)
    {
        boolean flag = false;

        for(TripEntry e: requestSent)
        {
            if(e.getEntry_id().equals(tripEntry.getEntry_id()))
            {
                flag = true;
                break;
            }
        }

        if(!flag)
            requestSent.add(tripEntry);

        return flag;
    }

    static boolean addRequestInMap(Map<String, ArrayList<User>> requestsRecieved, String key, User entryUser)
    {
        boolean flag = false;

        for (Iterator<Map.Entry<String, ArrayList<User>>> entries = requestsRecieved.entrySet().iterator(); entries.hasNext(); )
        {
            Map.Entry<String, ArrayList<User>> entry = entries.next();

            if(entry.getKey().equals(key))
            {
                for(User user: entry.getValue())
                {
                    if(user.getUserId().equals(entryUser.getUserId()))
                    {
                        flag = true;
                        break;
                    }
                }

                if(!flag)
                {
                    entry.getValue().add(entryUser);
                    break;
                }
            }
        }

        return flag;
    }
}
