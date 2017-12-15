package garbagecollectors.com.snucabpool;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import garbagecollectors.com.snucabpool.activities.BaseActivity;

public class UtilityMethods
{
    static DatabaseReference userDatabaseReference = BaseActivity.getUserDatabaseReference();
    static DatabaseReference entryDatabaseReference = BaseActivity.getEntryDatabaseReference();

    public static User getUserFromDatabase(String uid)
    {
        final User[] entryUser = {null};

        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren())
                {
                    User user = dataSnapshot1.getValue(User.class);

                    if(user.getUserId().equals(uid))
                    {
                        entryUser[0] = user;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                // ...
            }
        });

        return entryUser[0];
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
