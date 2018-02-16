package garbagecollectors.com.snucabpool.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.User;
import garbagecollectors.com.snucabpool.UtilityMethods;
import garbagecollectors.com.snucabpool.activities.BaseActivity;
import garbagecollectors.com.snucabpool.activities.MessageListActivity;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyHolder>
{
	private List<User> userList;
	private LayoutInflater inflater;
	private Context context;

	public UserAdapter(Context context)
	{
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public UserAdapter(List<User> userList, Context context)
	{
		this.userList = userList;
		this.context = context;
	}

	@Override
	public MyHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View v = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
		// set the view's size, margins, padding and layout parameters...

		return new UserAdapter.MyHolder(v);
	}

	@Override
	public void onBindViewHolder(MyHolder holder, int position)
	{
		User user = userList.get(position);
		UtilityMethods.fillUserHolder(holder, user);

		holder.itemView.setOnClickListener(view ->
		{
			MessageListActivity.setChatUser(user);
			MessageListActivity.setPersonalMessageList(UtilityMethods.getMessageList(BaseActivity.getMessages(), user.getUserId()));
			MessageListActivity.setPairUp(UtilityMethods.getPairUp(user, BaseActivity.getFinalCurrentUser().getPairUps()));

			context.startActivity(new Intent(context, MessageListActivity.class));
		});
	}

	@Override
	public int getItemCount()
	{
		return userList.size();
	}

	public class MyHolder extends RecyclerView.ViewHolder
	{
		public TextView name;
		public TextView email;
		public ImageView photo;

		public MyHolder(View itemView)
		{
			super(itemView);

			name = (TextView) itemView.findViewById(R.id.item_friend_name_text_view);
			email = (TextView) itemView.findViewById(R.id.item_friend_email_text_view);
			photo = (ImageView) itemView.findViewById(R.id.item_user_image_view);
		}
	}
}
