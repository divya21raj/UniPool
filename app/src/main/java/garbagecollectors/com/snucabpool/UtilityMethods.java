package garbagecollectors.com.snucabpool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import garbagecollectors.com.snucabpool.activities.BaseActivity;
import garbagecollectors.com.snucabpool.adapters.TripEntryAdapter;

public class UtilityMethods
{
    public static User getUserFromDatabase(String uid)
    {
        User userToBeFound = null;
        ArrayList<User> userList = BaseActivity.getUserList();

        for(User user: userList)
        {
            if(user.getUserId().equals(uid))
            {
                userToBeFound = user;
                break;
            }
        }

        return userToBeFound;
    }

    public static boolean addRequestInList(ArrayList<TripEntry> requestSent, TripEntry tripEntry)
    {
        boolean flag = false;

        if(requestSent == null)
            requestSent = new ArrayList<>();

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

    public static boolean addRequestInMap(HashMap<String, ArrayList<String>> requestsRecieved, String key, String entryUserId)
    {
        boolean flag = false, flag2 = false;

        if(requestsRecieved == null)
            requestsRecieved = new HashMap<>();

        for (Map.Entry<String, ArrayList<String>> entry : requestsRecieved.entrySet())
        {
            if (entry.getKey().equals(key))
            {
                for (String userId : entry.getValue())
                {
                    if (userId.equals(entryUserId))
                    {
                        flag = true;
                        break;
                    }
                }

                if(!flag)
                {
                    entry.getValue().add(entryUserId);
                    flag2 = true;
                    break;
                }
            }
        }

        if(!flag && !flag2)
        {
            ArrayList<String> userIdList = new ArrayList<>();
            userIdList.add(entryUserId);

            requestsRecieved.put(key, userIdList);
        }

        return flag;
    }

    public static void updateTripList(ArrayList<TripEntry> tripEntryList, TripEntry tripEntry)
    {
        Iterator<TripEntry> iterator = tripEntryList.iterator();

        while (iterator.hasNext())
        {
            TripEntry tripEntryFromList = iterator.next();

            if(tripEntryFromList.getEntry_id().equals(tripEntry.getEntry_id()))
            {
                iterator.remove();
                break;
            }
        }

        tripEntryList.add(tripEntry);
    }

    public static void updateUserList(ArrayList<User> userList, User user)
    {
        Iterator<User> iterator = userList.iterator();

        while (iterator.hasNext())
        {
            User userFromList = iterator.next();

            if(userFromList.getUserId().equals(user.getUserId()))
            {
                iterator.remove();
                break;
            }
        }

        userList.add(user);
    }

    public static ArrayList<TripEntry> populateRecievedRequestsList(HashMap<String, ArrayList<String>> recievedRequestsMap, ArrayList<TripEntry> tripEntries)
    {
        TripEntry temp;

        ArrayList<TripEntry> recievedRequestsList = new ArrayList<>();

        for (Map.Entry<String, ArrayList<String>> entry : recievedRequestsMap.entrySet())
        {
            if(!entry.getKey().equals("dummy"))
            {
                TripEntry tripEntry = getTripEntryFromList(entry.getKey(), tripEntries);

                if(tripEntry != null)
                {
                    for(String userId : entry.getValue())
                    {
                        User user = getUserFromDatabase(userId);

                        temp = new TripEntry(tripEntry);
                        temp.setName(user.getName());
                        temp.setUser_id(user.getUserId());

                        recievedRequestsList.add(temp);
                    }
                }
            }
        }

        return recievedRequestsList;
    }

    private static TripEntry getTripEntryFromList(String key, ArrayList<TripEntry> tripEntries)
    {
        TripEntry tripEntry = null;

        for(TripEntry entry : tripEntries)
        {
            if(entry.getEntry_id().equals(key))
            {
                tripEntry = entry;
                break;
            }
        }

        return tripEntry;
    }

    public static ArrayList<TripEntry> removeNullEntry(ArrayList<TripEntry> sentRequests)
    {
        Iterator<TripEntry> iterator = sentRequests.iterator();

        while (iterator.hasNext())
        {
            if (iterator.next().getName().equals("dummy"))
            {
                iterator.remove();
                break;
            }
        }

        return sentRequests;
    }

    public static void fillHolder(TripEntryAdapter.MyHolder holder, TripEntry tripEntry)
    {
        holder.date.setText(tripEntry.getDate());
        holder.name_user.setText(tripEntry.getName());
        holder.travel_time.setText(tripEntry.getTime());
    }
}
