package garbagecollectors.com.unipool.activities.RequestActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Map;

import garbagecollectors.com.unipool.R;
import garbagecollectors.com.unipool.activities.BaseActivity;
import garbagecollectors.com.unipool.adapters.UserAdapter;
import garbagecollectors.com.unipool.application.Globals;
import garbagecollectors.com.unipool.models.User;

public class ChatFragment extends Fragment
{
    RecyclerView recycle;

    public static UserAdapter recycleAdapter;

    public ChatFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		if (isVisibleToUser) {
			Globals.OPEN_ACTIVITY = "CHATS";
			Log.d("Open", Globals.OPEN_ACTIVITY);
		}
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recycle = view.findViewById(R.id.recycle_users);

        ArrayList<User> chatList = new ArrayList<>();
        for(Map.Entry<String, User> entry: BaseActivity.getChatMap().entrySet())
            chatList.add(entry.getValue());

        recycleAdapter = new UserAdapter(chatList, getContext());

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),1);

        recycle.setLayoutManager(layoutManager);
        recycle.setItemAnimator( new DefaultItemAnimator());
        recycle.setAdapter(recycleAdapter);

        return view;
    }

    public static void refreshRecycler()
    {
        if(recycleAdapter != null)
            recycleAdapter.notifyDataSetChanged();
    }
}