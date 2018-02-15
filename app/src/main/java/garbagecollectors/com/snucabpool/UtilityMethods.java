package garbagecollectors.com.snucabpool;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import garbagecollectors.com.snucabpool.activities.BaseActivity;
import garbagecollectors.com.snucabpool.adapters.TripEntryAdapter;
import garbagecollectors.com.snucabpool.adapters.UserAdapter;

public class UtilityMethods
{
    public static Task accessUserDatabase(String userReference)
    {
        TaskCompletionSource<DataSnapshot> userSource = new TaskCompletionSource<>();
        Task userTask = userSource.getTask();

        DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference(userReference);

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

        return userTask;
    }

    public static boolean addRequestInList(ArrayList<TripEntry> requestSent, ArrayList<PairUp> pairUps, TripEntry tripEntry)
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
        {
            for(PairUp pairUp: pairUps)
            {
                if(pairUp.getPairUpId().contains(tripEntry.getUser_id()))
                {
                    flag = true;
                    break;
                }

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

        tripEntryList.add(0, tripEntry);
    }

    public static Task populateReceivedRequestsList(ArrayList<TripEntry> receivedRequestsList, HashMap<String, ArrayList<String>> receivedRequestsMap, ArrayList<TripEntry> tripEntries)
    {
        final TripEntry[] temp = new TripEntry[1];

        Task userTask = accessUserDatabase("users");    //all users
        userTask.addOnSuccessListener(aVoid ->
        {
            DataSnapshot snapshot = (DataSnapshot) userTask.getResult();

            for (Map.Entry<String, ArrayList<String>> entry : receivedRequestsMap.entrySet())
            {
                if (!(entry.getKey().equals("dummy")))
                {
                    TripEntry tripEntry = getTripEntryFromList(entry.getKey(), tripEntries);

                    if (tripEntry != null)
                    {
                        for (String userId : entry.getValue())
                        {
                            final User[] user = new User[1];
                            user[0] = snapshot.child(userId).getValue(User.class);

                            if(user[0] != null)
                            {
                                temp[0] = new TripEntry(tripEntry);
                                temp[0].setName(user[0].getName());
                                temp[0].setUser_id(user[0].getUserId());

                                receivedRequestsList.add(temp[0]);
                            }
                        }
                    }
                }
            }
        });

        return userTask;
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

    public static void fillTripEntryHolder(TripEntryAdapter.MyHolder holder, TripEntry tripEntry)
    {
        holder.date.setText(tripEntry.getDate());
        holder.name_user.setText(tripEntry.getName());
        holder.travel_time.setText(tripEntry.getTime());
        holder.source.setText("From " + tripEntry.getSource().getName());
        holder.destination.setText("to " + tripEntry.getDestination().getName());
    }

    public static void fillUserHolder(UserAdapter.MyHolder holder, User user)
    {
        holder.name.setText(user.getName());
        holder.email.setText("email");
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

    public static boolean addPairUpInList(ArrayList<PairUp> pairUps, PairUp pairUp)
    {
        boolean flag = false;

        for (PairUp pu: pairUps)
        {
            if (pu.getPairUpId().contains(pairUp.getRequesterId()) && pu.getPairUpId().contains((pairUp.getCreatorId())))
            {
                flag = true;
                break;
            }
        }

        if(!flag)
        {
            pairUps.add(pairUp);
        }

        return flag;
    }

    public static Long getCurrentTime()
    {
        Long time;

        Date currentTime = Calendar.getInstance().getTime();

        time = currentTime.getTime();

        return time;
    }

    public static Task populateChatList(DataSnapshot userData)
    {
        final String[] userId = new String[1];

        ArrayList<PairUp> pairUps = new ArrayList<>();

        for(DataSnapshot dataSnapshot: userData.child("pairUps").getChildren())
            pairUps.add(dataSnapshot.getValue(PairUp.class));

        Task task = accessUserDatabase("users");

        ArrayList<User> finalChatList = new ArrayList<>();

        task.addOnCompleteListener(aTask ->
        {
            DataSnapshot snapshot = (DataSnapshot) task.getResult();

            for(PairUp pairUp: pairUps)
            {
                if(!(pairUp.getCreatorId().equals("dummy")))
                {
                    if(pairUp.getCreatorId().equals(BaseActivity.getCurrentUser().getUid()))
                        userId[0] = pairUp.getRequesterId();

                    else
                        userId[0] = pairUp.getCreatorId();

                    finalChatList.add(snapshot.child(userId[0]).getValue(User.class));
                }
            }

            BaseActivity.setChatList(finalChatList);
        });

        return task;
    }

    public static String formatDateTime(Long createdAtTime)
    {
        String formattedTime;

        Date date = new Date(createdAtTime);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

        formattedTime = sdf.format(date);

        return formattedTime;
    }

    public static void putMessageInMap(HashMap<String, ArrayList<Message>> messages, Message targetMessage)
    {
        boolean flag1 = false, flag2 = false;

        if(messages == null)
            messages = new HashMap<>();

        for (Map.Entry<String, ArrayList<Message>> entry : messages.entrySet())
        {
            if (entry.getKey().equals(targetMessage.getPairUpId()))
            {
                for (Message message: entry.getValue())
                {
                    if (message.getMessageId().equals(targetMessage.getMessageId()))
                    {
                        flag1 = true;
                        break;
                    }
                }

                if(!flag1)
                {
                    entry.getValue().add(targetMessage);
                    flag2 = true;
                    break;
                }
            }
        }

        if(!flag1 && !flag2)
        {
            ArrayList<Message> messageList = new ArrayList<>();
            messageList.add(targetMessage);

            messages.put(targetMessage.getPairUpId(), messageList);
        }

    }
}
