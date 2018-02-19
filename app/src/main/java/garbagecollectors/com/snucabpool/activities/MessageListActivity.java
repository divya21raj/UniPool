package garbagecollectors.com.snucabpool.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import garbagecollectors.com.snucabpool.Message;
import garbagecollectors.com.snucabpool.PairUp;
import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.User;
import garbagecollectors.com.snucabpool.UtilityMethods;
import garbagecollectors.com.snucabpool.adapters.MessageListAdapter;


public class MessageListActivity extends AppCompatActivity
{
	private RecyclerView messageRecycler;
	private MessageListAdapter messageAdapter;

	private Button sendButton;
	private EditText chatBoxEditText;

	private static List<Message> personalMessageList;
	private static User chatUser;

	private static PairUp pairUp;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_list);

		/*getActionBar().setTitle(chatUser.getName());
		getSupportActionBar().setTitle(chatUser.getName());*/

		sendButton = (Button) findViewById(R.id.button_chatbox_send);
		chatBoxEditText = (EditText) findViewById(R.id.editText_chatbox);

		messageRecycler = (RecyclerView) findViewById(R.id.recyclerView_message_list);

		messageAdapter = new MessageListAdapter(personalMessageList, this);

		messageRecycler.setLayoutManager(new LinearLayoutManager(this));
		messageRecycler.setAdapter(messageAdapter);

		DatabaseReference userMessageDatabaseReference = BaseActivity.getUserMessageDatabaseReference();

		userMessageDatabaseReference.addChildEventListener(new ChildEventListener()
		{
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s)
			{
				Message message = dataSnapshot.getValue(Message.class);
				UtilityMethods.putMessageInMap(BaseActivity.getMessages(), message);

				if (message != null &&
						!UtilityMethods.messageAlreadyInList(message, personalMessageList) && !message.getMessageId().equals("def@ult"))
				{
					personalMessageList.add(message);
					messageAdapter.notifyDataSetChanged();
				}

			}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s)
			{}

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot)
			{}

			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String s)
			{}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{
				// Failed to read value
				Log.w("Hello", "Failed to read value.", databaseError.toException());
			}
		});

		sendButton.setOnClickListener(view ->
		{
			String typedMessage = chatBoxEditText.getText().toString();

			if(!typedMessage.isEmpty())
			{
				Message message = new Message("", pairUp.getPairUpId(), typedMessage,
						BaseActivity.getFinalCurrentUser().getUserId(), chatUser.getUserId(), UtilityMethods.getCurrentTime());

				UtilityMethods.putMessageOnDB(message, chatUser, BaseActivity.getFinalCurrentUser());  //online update

				UtilityMethods.putMessageInMap(BaseActivity.getMessages(), message);  //local update

				personalMessageList.add(message);
				messageAdapter.notifyDataSetChanged();

				chatBoxEditText.setText("");
			}
		});

	}

	public static PairUp getPairUp()
	{
		return pairUp;
	}

	public static void setPairUp(PairUp pairUp)
	{
		MessageListActivity.pairUp = pairUp;
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