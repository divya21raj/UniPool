package garbagecollectors.com.snucabpool.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import garbagecollectors.com.snucabpool.Message;
import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.User;
import garbagecollectors.com.snucabpool.adapters.MessageListAdapter;


public class MessageListActivity extends AppCompatActivity
{
	private RecyclerView mMessageRecycler;
	private MessageListAdapter mMessageAdapter;
	private static List<Message> personalMessageList;
	private static User chatUser;

	@Override

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_list);

		getActionBar().setTitle(chatUser.getName());
		getSupportActionBar().setTitle(chatUser.getName());

		mMessageRecycler = (RecyclerView) findViewById(R.id.recyclerView_message_list);
		mMessageAdapter = new MessageListAdapter(this, personalMessageList);
		mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));

	}

	public static void setChatUser(User chatUser)
	{
		MessageListActivity.chatUser = chatUser;
	}

	public static void setPersonalMessageList(List<Message> personalMessageList)
	{
		MessageListActivity.personalMessageList = personalMessageList;
	}

}