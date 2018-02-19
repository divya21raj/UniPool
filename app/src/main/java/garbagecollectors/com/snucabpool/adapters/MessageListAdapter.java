package garbagecollectors.com.snucabpool.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import garbagecollectors.com.snucabpool.Message;
import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.UtilityMethods;
import garbagecollectors.com.snucabpool.activities.BaseActivity;


public class MessageListAdapter extends RecyclerView.Adapter
{
	private static final int VIEW_TYPE_MESSAGE_SENT = 1;
	private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

	private Context context;
	private LayoutInflater inflater;
	private List<Message> mMessageList;

	public MessageListAdapter(List<Message> messageList, Context context)
	{
		this.context = context;
		mMessageList = messageList;
	}

	public MessageListAdapter(Context context)
	{
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getItemCount()
	{
		return mMessageList.size();
	}

	// Determines the appropriate ViewType according to the sender of the message.
	@Override
	public int getItemViewType(int position)
	{
		Message message = (Message) mMessageList.get(position);

		if (message.getSenderId().equals(BaseActivity.getFinalCurrentUser().getUserId()))
		{
			// If the current user is the sender of the message
			return VIEW_TYPE_MESSAGE_SENT;
		} else
			{
			// If some other user sent the message
			return VIEW_TYPE_MESSAGE_RECEIVED;
		}
	}

	// Inflates the appropriate layout according to the ViewType.
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view;

		if (viewType == VIEW_TYPE_MESSAGE_SENT)
		{
			view = LayoutInflater.from(context)
					.inflate(R.layout.item_sent_message, parent, false);
			return new SentMessageHolder(view);
		} else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED)
		{
			view = LayoutInflater.from(context)
					.inflate(R.layout.item_received_message, parent, false);
			return new ReceivedMessageHolder(view);
		}

		return null;
	}

	// Passes the message object to a ViewHolder so that the contents can be bound to UI.
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
	{
		Message message = (Message) mMessageList.get(position);

		switch (holder.getItemViewType())
		{
			case VIEW_TYPE_MESSAGE_SENT:
				((SentMessageHolder) holder).bind(message);
				break;
			case VIEW_TYPE_MESSAGE_RECEIVED:
				((ReceivedMessageHolder) holder).bind(message);
		}
	}

	private class SentMessageHolder extends RecyclerView.ViewHolder
	{
		TextView messageText, timeText;

		SentMessageHolder(View itemView)
		{
			super(itemView);

			messageText = (TextView) itemView.findViewById(R.id.text_message_body);
			timeText = (TextView) itemView.findViewById(R.id.text_message_time);
		}

		void bind(Message message)
		{
			messageText.setText(message.getMessage());

			// Format the stored timestamp into a readable String using method.
			timeText.setText(UtilityMethods.formatDateTime(message.getCreatedAtTime()));
		}
	}

	private class ReceivedMessageHolder extends RecyclerView.ViewHolder
	{
		TextView messageText, timeText;

		ReceivedMessageHolder(View itemView)
		{
			super(itemView);

			messageText = (TextView) itemView.findViewById(R.id.text_message_body);
			timeText = (TextView) itemView.findViewById(R.id.text_message_time);
		}

		void bind(Message message)
		{
			messageText.setText(message.getMessage());

			// Format the stored timestamp into a readable String using method.
			timeText.setText(UtilityMethods.formatDateTime(message.getCreatedAtTime()));
		}
	}
}