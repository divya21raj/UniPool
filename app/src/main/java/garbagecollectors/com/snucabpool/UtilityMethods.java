package garbagecollectors.com.snucabpool;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import garbagecollectors.com.snucabpool.activities.BaseActivity;
import garbagecollectors.com.snucabpool.adapters.TripEntryAdapter;

public class UtilityMethods
{
    public static User getUserFromDatabase(String uid)
    {
        final User[] userToBeFound = {null};

        TaskCompletionSource<DataSnapshot> userSource = new TaskCompletionSource<>();
        Task userTask = userSource.getTask();

        DatabaseReference userDatabaseReference = BaseActivity.getUserDatabaseReference();

        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                userSource.setResult(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                //whoopsie!
            }
        });

        userTask.addOnSuccessListener(aVoid ->
        {
            DataSnapshot snapshot = (DataSnapshot) userTask.getResult();

            userToBeFound[0] = snapshot.child(uid).getValue(User.class);
        });

        return userToBeFound[0];
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

    public static boolean putInMap(HashMap<String, ArrayList<String>> map, String keyId, String valueId)
    {
        boolean flag = false, flag2 = false;

        if(map == null)
            map = new HashMap<>();

        for (Map.Entry<String, ArrayList<String>> entry : map.entrySet())
        {
            if (entry.getKey().equals(keyId))
            {
                for (String Id : entry.getValue())
                {
                    if (Id.equals(valueId))
                    {
                        flag = true;
                        break;
                    }
                }

                if(!flag)
                {
                    entry.getValue().add(valueId);
                    flag2 = true;
                    break;
                }
            }
        }

        if(!flag && !flag2)
        {
            ArrayList<String> IdList = new ArrayList<>();
            IdList.add(valueId);

            map.put(keyId, IdList);
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

    public static ArrayList<TripEntry> removeFromList(ArrayList<TripEntry> list, String id)
    {
        Iterator<TripEntry> iterator = list.iterator();

        while (iterator.hasNext())
        {
            if (iterator.next().getEntry_id().equals(id))
            {
                iterator.remove();
                break;
            }
        }

        return list;
    }

    public static void fillHolder(TripEntryAdapter.MyHolder holder, TripEntry tripEntry)
    {
        holder.date.setText(tripEntry.getDate());
        holder.name_user.setText(tripEntry.getName());
        holder.travel_time.setText(tripEntry.getTime());
        holder.source.setText("From " + tripEntry.getSource().getName());
        holder.destination.setText("to " + tripEntry.getDestination().getName());
    }


    public static void removeFromMap(HashMap<String, ArrayList<String>> map, String keyId, String valueId)
    {
        boolean flag = false;
        String keyToBeRemoved = "";

        for (Map.Entry<String, ArrayList<String>> entry : map.entrySet())
        {
            if(entry.getKey().equals(keyId))
            {
                Iterator<String> iterator = entry.getValue().iterator();

                while (iterator.hasNext())
                {
                    if (iterator.next().equals(valueId))
                    {
                        iterator.remove();
                        break;
                    }
                }

                if(entry.getValue().size() == 0)
                {
                    flag = true;
                    keyToBeRemoved = entry.getKey();
                }
            }
        }

        if(flag)
            map.remove(keyToBeRemoved);
    }

    public static boolean addPairUpInList(ArrayList<PairUps> pairUps, PairUps pairUp, String tripEntryId)
    {
        boolean flag = false, flag1 = false;
        PairUps temp = new PairUps();

        for (PairUps pu: pairUps)
        {
            if (pu.getRequesterId().equals(pairUp.requesterId))
            {
                flag = true;
                temp = pu;
                break;
            }
        }

        if(!flag)
        {
            pairUps.add(pairUp);
        }

        else
        {
            ArrayList<String> tripEntriesPairedOver = temp.getTripEntriesPairedUpOver();
            for(String entryId: tripEntriesPairedOver)
            {
                if(entryId.equals(tripEntryId))
                {
                    flag1 = true;
                    break;
                }
            }

            if(!flag1)
            {
                tripEntriesPairedOver.add(tripEntryId);
                flag = false;
            }
        }

        return flag;
    }

    public static Long getCurrentTime()
    {
        Long time = null;

        Date currentTime = Calendar.getInstance().getTime();

        time = currentTime.getTime();

        return time;
    }

    public static ArrayList<String> getTripEntriesPairedOver(ArrayList<PairUps> pairUps, String userId)
    {
        ArrayList<String> tripEntriesPairedOver = new ArrayList<>();

        for(PairUps pairUp: pairUps)
        {
            if(pairUp.getCreatorId().equals(userId))
            {
                tripEntriesPairedOver = pairUp.getTripEntriesPairedUpOver();
                break;
            }
        }

        return tripEntriesPairedOver;
    }
}
