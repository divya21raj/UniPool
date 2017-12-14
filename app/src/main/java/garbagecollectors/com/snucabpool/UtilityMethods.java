package garbagecollectors.com.snucabpool;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import static garbagecollectors.com.snucabpool.RequestAdapter.userDatabaseReference;

public class UtilityMethods
{
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

    static boolean checkEntryInEntryList(ArrayList<Entry> requestSent, Entry entry)
    {
        boolean flag = false;

        for(Entry e: requestSent)
        {
            if(e.getEntry_id().equals(entry.getEntry_id()))
            {
                flag = true;
                break;
            }
        }

        return flag;
    }

    static boolean checkRequestInMap(Map<Entry, User> requestsRecieved, Entry entry, User entryUser)
    {
        boolean flag = false;

        return flag;
    }
}
