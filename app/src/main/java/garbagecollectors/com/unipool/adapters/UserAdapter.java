package garbagecollectors.com.unipool.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import garbagecollectors.com.unipool.R;
import garbagecollectors.com.unipool.activities.BaseActivity;
import garbagecollectors.com.unipool.activities.MessageListActivity;
import garbagecollectors.com.unipool.application.UtilityMethods;
import garbagecollectors.com.unipool.models.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyHolder>
{
	private List<User> userList;
	private Context context;

	public UserAdapter(List<User> userList, Context context)
	{
		this.userList = userList;
		this.context = context;
	}

	@Override
	public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		View v = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
		// set the view's size, margins, padding and layout parameters...

		return new UserAdapter.MyHolder(v);
	}

	@Override
	public void onBindViewHolder(@NonNull MyHolder holder, int position)
	{
		User user = userList.get(position);
		UtilityMethods.fillUserHolder(holder, user);

		holder.itemView.setOnClickListener(view ->
		{
			//TreeMap<Long, Message> messageMap = UtilityMethods.getPersonalMessageMap(BaseActivity.getMessages(), user.getUserId());

			MessageListActivity.setChatUser(user);
			MessageListActivity.setName(UtilityMethods.sanitizeName(user.getName()));
			//MessageListActivity.setPersonalMessageMap(messageMap);
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

		MyHolder(View itemView)
		{
			super(itemView);

			name = itemView.findViewById(R.id.item_friend_name_text_view);
			email = itemView.findViewById(R.id.item_friend_email_text_view);
			photo = itemView.findViewById(R.id.item_user_image_view);
		}
	}
}
