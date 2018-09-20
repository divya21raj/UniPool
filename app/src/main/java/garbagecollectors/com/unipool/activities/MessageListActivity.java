package garbagecollectors.com.unipool.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import garbagecollectors.com.unipool.R;
import garbagecollectors.com.unipool.application.Globals;
import garbagecollectors.com.unipool.application.UtilityMethods;
import garbagecollectors.com.unipool.models.Message;
import garbagecollectors.com.unipool.models.PairUp;
import garbagecollectors.com.unipool.models.User;


public class MessageListActivity extends AppCompatActivity
{
	LinearLayout messagesLayout;
	ImageView sendButton;
	EditText messageArea;
	ScrollView scrollView;

	ProgressDialog messageProgressDialog;

	private static TreeMap<Long, Message> personalMessageMap;   //key = CreatedAtTime
	private static User chatUser;

	private static PairUp pairUp;

	private ArrayList<String> messagesOnScreen;  //list of messageIds displayed
	private static String name;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_list);

		messagesLayout = findViewById(R.id.layout1);
		sendButton = findViewById(R.id.sendButton);
		messageArea = findViewById(R.id.message_edit_text);
		scrollView = findViewById(R.id.scrollView);

		personalMessageMap = new TreeMap<>();

		messagesOnScreen = new ArrayList<>();

		messageProgressDialog = new ProgressDialog(this);
		messageProgressDialog.setCanceledOnTouchOutside(false);
		messageProgressDialog.setMessage("Fetching your messages...");
		messageProgressDialog.show();

		setScrollViewToBottom();

		DatabaseReference userMessageDatabaseReference = null;
		try
		{
			userMessageDatabaseReference = FirebaseDatabase.getInstance().
					getReference("messages/" + BaseActivity.getFinalCurrentUser().getUserId());
		} catch (Exception e)
		{
			//Could be NPE, fall back to locally stored USER_ID
			userMessageDatabaseReference = FirebaseDatabase.getInstance().
					getReference("messages/" + Globals.USER_ID);
		}

		/*load from local*/

		userMessageDatabaseReference.addChildEventListener(new ChildEventListener()
		{
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s)
			{
				Message message = dataSnapshot.getValue(Message.class);

				if (message != null)
				{
					UtilityMethods.putMessageInMap(BaseActivity.getMessages(), message);

					if (!message.getMessageId().equals("def@ult") && (message.getSenderId().equals(chatUser.getUserId())
							|| message.getReceiverId().equals(chatUser.getUserId())))
					{
						personalMessageMap.containsKey(message.getCreatedAtTime());

						if(!messagesOnScreen.contains(message.getMessageId()))
						{
							showMessage(message);
						}
					}

					else if(message.getMessageId().equals("def@ult"))
						messageProgressDialog.dismiss();
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
				Toast.makeText(getApplicationContext(), "Problems! Couldn't fetch messages...", Toast.LENGTH_LONG).show();
			}
		});

		sendButton.setOnClickListener(view ->
		{
			String typedMessage = messageArea.getText().toString();

			if (!typedMessage.isEmpty())
			{
				Message message = new Message("", pairUp.getPairUpId(), typedMessage,
						BaseActivity.getFinalCurrentUser().getUserId(), chatUser.getUserId(), UtilityMethods.getCurrentTime());

				UtilityMethods.putMessageOnDB(message, chatUser, BaseActivity.getFinalCurrentUser());  //online update

				UtilityMethods.putMessageInMap(BaseActivity.getMessages(), message);  //local update

				HashMap<String, String> notificationObject = new HashMap<>();
				notificationObject.put("from", BaseActivity.getFinalCurrentUser().getUserId());
				notificationObject.put("type", "chat");

				Globals.notificationDatabaseReference.child(chatUser.getUserId()).push().setValue(notificationObject);

				messageArea.setText("");
				personalMessageMap.put(message.getCreatedAtTime(), message);
				showMessage(message);
			}
		});

		//detecting if keyboard on-screen
		final View activityRootView = findViewById(R.id.activity_message_list);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() ->
		{
			int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();

			if (heightDiff > UtilityMethods.dpToPx(MessageListActivity.this, 200))
				setScrollViewToBottom();
		});

	}

	@Override
	protected void onStart()
	{
		super.onStart();
		setTitle(UtilityMethods.sanitizeName(chatUser.getName()));
		setName(chatUser.getName());
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		setName("asdsafasde");
	}

	private void showMessage(Message message)
	{
		messagesOnScreen.add(message.getMessageId());

		//messageProgressDialog.dismiss();

		if(message.getSenderId().equals(BaseActivity.getFinalCurrentUser().getUserId()))
		{
			addMessageBox(message.getMessage(), message.getCreatedAtTime(), 1);
		}
		else if(message.getSenderId().equals(chatUser.getUserId()))
		{
			addMessageBox(message.getMessage(), message.getCreatedAtTime(), 2);
		}
	}

	public void addMessageBox(String message, Long createdAtTime, int type)
	{
		TextView messageView = new TextView(MessageListActivity.this);
		messageView.setText(message);

		TextView timeView = new TextView(MessageListActivity.this);
		timeView.setTextSize(10);
		timeView.setText(UtilityMethods.formatDateTime(createdAtTime));

		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
																		ViewGroup.LayoutParams.WRAP_CONTENT);
		lp2.weight = 1.0f;

		if(type == 1)
		{
			lp2.gravity = Gravity.RIGHT;
			messageView.setBackgroundResource(R.drawable.bubble_in);
			timeView.setTextColor(getResources().getColor(R.color.colorPrimary));
			timeView.setPadding(0,0, 5, 0);
		}
		else
		{
			lp2.gravity = Gravity.LEFT;
			timeView.setPadding(5,0, 0, 0);
			timeView.setTextColor(getResources().getColor(R.color.orange));
			messageView.setBackgroundResource(R.drawable.bubble_out);
		}

		messageView.setLayoutParams(lp2);
		timeView.setLayoutParams(lp2);

		messagesLayout.addView(messageView);
		messagesLayout.addView(timeView);

		//scrollView.fullScroll(View.FOCUS_DOWN);
		setScrollViewToBottom();
	}

	private void setScrollViewToBottom()
	{
		View lastChild = scrollView.getChildAt(scrollView.getChildCount() - 1);
		int bottom = lastChild.getBottom() + scrollView.getPaddingBottom();
		int sy = scrollView.getScrollY();
		int sh = scrollView.getHeight();
		int delta = bottom - (sy + sh);

		scrollView.smoothScrollBy(0, delta);
	}



	public static void setPairUp(PairUp pairUp)
	{
		MessageListActivity.pairUp = pairUp;
	}

	public static void setChatUser(User chatUser)
	{
		MessageListActivity.chatUser = chatUser;
	}

	public static String getName()
	{
		return name;
	}

	public static void setName(String name)
	{
		MessageListActivity.name = name;
	}

	public static TreeMap<Long, Message> getPersonalMessageMap()
	{
		return personalMessageMap;
	}

	public static void setPersonalMessageMap(TreeMap<Long, Message> personalMessageMap)
	{
		MessageListActivity.personalMessageMap = personalMessageMap;
	}

	public static User getChatUser()
	{
		return chatUser;
	}

	public static PairUp getPairUp()
	{
		return pairUp;
	}

}