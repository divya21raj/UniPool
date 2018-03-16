package garbagecollectors.com.unipool.activities;

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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import garbagecollectors.com.unipool.Message;
import garbagecollectors.com.unipool.PairUp;
import garbagecollectors.com.unipool.R;
import garbagecollectors.com.unipool.User;
import garbagecollectors.com.unipool.UtilityMethods;


public class MessageListActivity extends AppCompatActivity
{
	LinearLayout messagesLayout;
	ImageView sendButton;
	EditText messageArea;
	ScrollView scrollView;

	private static List<Message> personalMessageList;
	private static User chatUser;

	private static PairUp pairUp;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_list);

		messagesLayout = (LinearLayout) findViewById(R.id.layout1);
		sendButton = (ImageView)findViewById(R.id.sendButton);
		messageArea = (EditText)findViewById(R.id.message_edit_text);
		scrollView = (ScrollView)findViewById(R.id.scrollView);

		setTitle(chatUser.getName());

		for(Message message: personalMessageList)
			showMessage(message);

		setScrollViewToBottom();

		DatabaseReference userMessageDatabaseReference = FirebaseDatabase.getInstance().
								getReference("messages/" + BaseActivity.getFinalCurrentUser().getUserId());

		userMessageDatabaseReference.addChildEventListener(new ChildEventListener()
		{
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s)
			{
				Message message = dataSnapshot.getValue(Message.class);

				//Toast.makeText(getApplicationContext(), "Got it in ML activity", Toast.LENGTH_SHORT).show();

				if(message != null)
				{
					UtilityMethods.putMessageInMap(BaseActivity.getMessages(), message);

					//Toast.makeText(getApplicationContext(), "Is receiver", Toast.LENGTH_SHORT).show();
					if (!message.getMessageId().equals("def@ult"))
					{
						personalMessageList.add(message);
						showMessage(message);
					}
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
			String typedMessage = messageArea.getText().toString();

			if(!typedMessage.isEmpty())
			{
				Message message = new Message("", pairUp.getPairUpId(), typedMessage,
						BaseActivity.getFinalCurrentUser().getUserId(), chatUser.getUserId(), UtilityMethods.getCurrentTime());

				UtilityMethods.putMessageOnDB(message, chatUser, BaseActivity.getFinalCurrentUser());  //online update

				UtilityMethods.putMessageInMap(BaseActivity.getMessages(), message);  //local update

				messageArea.setText("");
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

	private void setScrollViewToBottom()
	{
		View lastChild = scrollView.getChildAt(scrollView.getChildCount() - 1);
		int bottom = lastChild.getBottom() + scrollView.getPaddingBottom();
		int sy = scrollView.getScrollY();
		int sh = scrollView.getHeight();
		int delta = bottom - (sy + sh);

		scrollView.smoothScrollBy(0, delta);
	}

	private void showMessage(Message message)
	{
		if(message.getSenderId().equals(BaseActivity.getFinalCurrentUser().getUserId()))
		{
			addMessageBox(message.getMessage(), 1);
		}
		else if(message.getSenderId().equals(chatUser.getUserId()))
		{
			addMessageBox(message.getMessage(), 2);
		}
	}

	public void addMessageBox(String message, int type)
	{
		TextView textView = new TextView(MessageListActivity.this);
		textView.setText(message);

		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
																		ViewGroup.LayoutParams.WRAP_CONTENT);
		lp2.weight = 1.0f;

		if(type == 1)
		{
			lp2.gravity = Gravity.RIGHT;
			textView.setBackgroundResource(R.drawable.bubble_in);
		}
		else
		{
			lp2.gravity = Gravity.LEFT;
			textView.setBackgroundResource(R.drawable.bubble_out);
		}

		textView.setLayoutParams(lp2);
		messagesLayout.addView(textView);

		//scrollView.fullScroll(View.FOCUS_DOWN);
		setScrollViewToBottom();
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

	public static List<Message> getPersonalMessageList()
	{
		return personalMessageList;
	}

	public static User getChatUser()
	{
		return chatUser;
	}

	public static PairUp getPairUp()
	{
		return pairUp;
	}

	public static void refresh()
	{

	}
}