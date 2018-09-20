package garbagecollectors.com.unipool.application;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import garbagecollectors.com.unipool.activities.BaseActivity;
import garbagecollectors.com.unipool.adapters.TripEntryAdapter;
import garbagecollectors.com.unipool.adapters.UserAdapter;
import garbagecollectors.com.unipool.models.Message;
import garbagecollectors.com.unipool.models.PairUp;
import garbagecollectors.com.unipool.models.TripEntry;
import garbagecollectors.com.unipool.models.User;

public class UtilityMethods
{
    public static Task accessUserDatabase(String userReference)
    {
        TaskCompletionSource<DataSnapshot> userSource = new TaskCompletionSource<>();
        Task userTask = userSource.getTask();

        DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference(Globals.UNI + userReference);

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

    public static boolean addRequestInList(HashMap<String, TripEntry> requestSent, HashMap<String, PairUp> pairUps, TripEntry tripEntry)
    {
        boolean flag = false;

        if(requestSent == null)
            requestSent = new HashMap<>();

        if(requestSent.containsKey(tripEntry.getEntry_id()))
            flag = true;

        if(!flag)
        {
            for(Map.Entry<String, PairUp> entry: pairUps.entrySet())
            {
                PairUp pairUp = entry.getValue();
                if(pairUp.getPairUpId().contains(tripEntry.getUser_id()))
                {
                    flag = true;
                    break;
                }
            }
        }

        if(!flag)
            requestSent.put(tripEntry.getEntry_id(), tripEntry);

        return flag;
    }

    public static boolean putInMap(HashMap<String, ArrayList<String>> requestsReceived, String keyId, String valueId)
    {
        boolean flag = false, flag2 = false;

        if(requestsReceived == null)
            requestsReceived = new HashMap<>();

        for (Map.Entry<String, ArrayList<String>> entry : requestsReceived.entrySet())
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

            requestsReceived.put(keyId, IdList);
        }

        return flag;
    }

    public static void updateTripList(ArrayList<TripEntry> tripEntryList, TripEntry tripEntry, boolean addToEnd)
    {
        Iterator<TripEntry> iterator = tripEntryList.iterator();

        while (iterator.hasNext())
        {
            TripEntry tripEntryFromList = iterator.next();

            try
            {
                if(tripEntryFromList.getEntry_id().equals(tripEntry.getEntry_id()))
                {
                    iterator.remove();
                    break;
                }
            } catch (NullPointerException npe)
            {
                npe.printStackTrace();
            }
        }

        if(addToEnd)
            tripEntryList.add(tripEntryList.size(), tripEntry);
        else tripEntryList.add(0, tripEntry);
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

    public static ArrayList<TripEntry> removeFromList(ArrayList<TripEntry> list, String entryId)
    {
        Iterator<TripEntry> iterator = list.iterator();

        while (iterator.hasNext())
        {
            try
            {
                if (iterator.next().getEntry_id().equals(entryId))
                {
                    iterator.remove();
                    break;
                }

            }
            catch (NullPointerException ignored)
            {}
        }

        return list;
    }

    public static void fillTripEntryHolder(TripEntryAdapter.MyHolder holder, TripEntry tripEntry)
    {
    	holder.cardArrow.setVisibility(View.GONE);
	    holder.messageCard.setVisibility(View.GONE);

        holder.date.setText(tripEntry.getDate());
        holder.name_user.setText(sanitizeName(tripEntry.getName()));
        holder.travel_time.setText(tripEntry.getTime());
        holder.source.setText("From " + tripEntry.getSource().getName());
        holder.destination.setText("to " + tripEntry.getDestination().getName());

        if(tripEntry.getMessage() != null)
        {
            holder.cardArrow.setVisibility(View.VISIBLE);
            holder.messageCard.setVisibility(View.VISIBLE);
            holder.messageText.setText(tripEntry.getMessage());
        }
    }

    public static void fillUserHolder(UserAdapter.MyHolder holder, User user)
    {
        holder.name.setText(UtilityMethods.sanitizeName(user.getName()));
        holder.email.setText("");
        Picasso.get().load(user.getPhotoUrl()).into(holder.photo);
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

    public static boolean addPairUpInMap(HashMap<String, PairUp> pairUps, PairUp pairUp)
    {
        boolean flag = false;

        for (Map.Entry<String, PairUp> entry: pairUps.entrySet())
        {
            PairUp pu = entry.getValue();
            if (pu.getPairUpId().contains(pairUp.getRequesterId()) && pu.getPairUpId().contains((pairUp.getCreatorId())))
            {
                flag = true;
                break;
            }
        }

        if(!flag)
        {
            pairUps.put(pairUp.getPairUpId(), pairUp);
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

    public static Task populateChatMap(DataSnapshot pairUpSnapshot)
    {
        final String[] userId = new String[1];

        ArrayList<PairUp> pairUps = new ArrayList<>();

        for(DataSnapshot dataSnapshot: pairUpSnapshot.getChildren())
            pairUps.add(dataSnapshot.getValue(PairUp.class));

        Task task = accessUserDatabase("users");

        HashMap<String, User> finalChatMap = new HashMap<>();

        task.addOnCompleteListener(aTask ->
        {
            DataSnapshot snapshot = (DataSnapshot) task.getResult();

            for(PairUp pairUp: pairUps)
            {
                if(!(pairUp.getCreatorId().equals("dummy")))
                {
                    if(pairUp.getCreatorId().equals(BaseActivity.getFinalCurrentUser().getUserId()))
                        userId[0] = pairUp.getRequesterId();

                    else
                        userId[0] = pairUp.getCreatorId();

                    User user = snapshot.child(userId[0]).getValue(User.class);
                    if (user != null)
                    {
                        finalChatMap.put(user.getUserId(), user);
                    }
                }
            }

            BaseActivity.setChatMap(finalChatMap);
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

    public static void putMessageInMap(HashMap<String, HashMap<String, Message>> messages, Message targetMessage)
    {
        HashMap<String, Message> mapMessage = new HashMap<>();
        mapMessage.put(targetMessage.getMessageId(), targetMessage);

        messages.put(targetMessage.getPairUpId(), mapMessage);
    }

    public static List<Message> getMessageList(HashMap<String, ArrayList<Message>> messages, String userId)
    {
        ArrayList<Message> personalMessageList;
        HashMap<String, PairUp> pairUpList = BaseActivity.getFinalCurrentUser().getPairUps();

        String pairUpId = null;

        for(Map.Entry<String, PairUp> entry: pairUpList.entrySet())
        {
            PairUp pairUp = entry.getValue();
            if(pairUp.getCreatorId().equals(userId)||pairUp.getRequesterId().equals(userId))
            {
                pairUpId = pairUp.getPairUpId();
                break;
            }
        }

        List messageList = messages.get(pairUpId);

        if(messageList != null)
            personalMessageList = new ArrayList<>(messages.get(pairUpId));

        else
        {
            personalMessageList = new ArrayList<>();
            messages.put(pairUpId, personalMessageList);
        }

        return personalMessageList;
    }

    public static PairUp getPairUp(User user, HashMap<String, PairUp> pairUps)
    {
        PairUp pairUp = null;

        for (Map.Entry<String, PairUp> entry: pairUps.entrySet())
        {
            PairUp up = entry.getValue();
            if(up.getRequesterId().equals(user.getUserId()) || up.getCreatorId().equals(user.getUserId()))
            {
                pairUp = up;
                break;
            }
        }
        return pairUp;
    }

    public static void putMessageOnDB(Message message, User chatUser, User user)
    {
        DatabaseReference chatUserMessageReference = FirebaseDatabase.getInstance().getReference(Globals.UNI + "messages/" + chatUser.getUserId());
        DatabaseReference userMessageReference = FirebaseDatabase.getInstance().getReference(Globals.UNI + "messages/" + user.getUserId());

        String messageId = userMessageReference.push().getKey();
        message.setMessageId(messageId);

        userMessageReference.child(messageId).setValue(message);
        chatUserMessageReference.child(messageId).setValue(message);
    }

    public static boolean messageAlreadyInList(Message message, List<Message> personalMessageList)
    {
        boolean flag = false;

        for(Message message1: personalMessageList)
        {
            if(message.getMessageId().equals(message1.getMessageId()))
            {
                flag = true;
                break;
            }
        }

        return flag;
    }

    public static float dpToPx(Context context, float valueInDp)
    {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public static boolean putMessageInList(Message message, List<Message> personalMessageList)
    {
        boolean flag = false;

        for(Message m: personalMessageList)
        {
            if(m.getMessageId().equals(message.getMessageId()))
            {
                flag = true;
                break;
            }
        }

        if(!flag)
            personalMessageList.add(message);

        return flag;
    }

    public static TreeMap<Long, Message> getPersonalMessageMap(HashMap<String, HashMap<String, Message>> messages, String userId)
    {
        TreeMap<Long, Message> messageMap = new TreeMap<>();

        for (Map.Entry<String, HashMap<String, Message>> entry: messages.entrySet())
        {
            if(entry.getKey().contains(userId))
            {
                HashMap<String, Message> messagesHashMap = entry.getValue();
                for(Map.Entry<String, Message> e: messagesHashMap.entrySet())
                {
                    Message message = e.getValue();
                    messageMap.put(message.getCreatedAtTime(), message);
                }
                break;
            }
        }

        return messageMap;
    }

    public static String sanitizeName(String name)
    {
        if(name == null)
            return "";

        final String ACTIONABLE_DELIMITERS = " '-/"; // these cause the character following
        // to be capitalized

        StringBuilder sb = new StringBuilder();
        boolean capNext = true;

        for (char c : name.toCharArray())
        {
            if (capNext)
                c = Character.toUpperCase(c);
            else
                c = Character.toLowerCase(c);

            sb.append(c);
            capNext = (ACTIONABLE_DELIMITERS.indexOf((int) c) >= 0); // explicit cast not needed
        }

        return sb.toString();
    }

    public static void storeUserLocally(FirebaseUser user, Context context)
    {
        LocalStorageHelper.storeLocally(Globals.USER_SP_FILE, Globals.USER_ID_KEY, user.getUid(), context);
        LocalStorageHelper.storeLocally(Globals.USER_SP_FILE, Globals.USER_NAME_KEY, user.getDisplayName(), context);
        LocalStorageHelper.storeLocally(Globals.USER_SP_FILE, Globals.USER_EMAIL_KEY, user.getEmail(), context);
        LocalStorageHelper.storeLocally(Globals.USER_SP_FILE, Globals.USER_PHONE_KEY, user.getPhoneNumber(), context);
        LocalStorageHelper.storeLocally(Globals.USER_SP_FILE, Globals.USER_PHOTO_URL_KEY, user.getPhotoUrl(), context);

    }

    public static void fillGlobalVariables(Context context)
    {
        Globals.USER_ID = (String)LocalStorageHelper.loadFromLocal(Globals.USER_ID_KEY, context, String.class);
        Globals.USER_NAME = (String)LocalStorageHelper.loadFromLocal(Globals.USER_NAME_KEY, context, String.class);
        Globals.USER_EMAIL = (String)LocalStorageHelper.loadFromLocal(Globals.USER_EMAIL_KEY, context, String.class);
        Globals.USER_PHONE = (String)LocalStorageHelper.loadFromLocal(Globals.USER_PHONE_KEY, context, String.class);
        Globals.USER_PHOTO_URL = (String)LocalStorageHelper.loadFromLocal(Globals.USER_PHOTO_URL_KEY, context, String.class);
        Globals.USER_TOKEN = (String)LocalStorageHelper.loadFromLocal(Globals.USER_TOKEN_KEY, context, String.class);
    }
}
