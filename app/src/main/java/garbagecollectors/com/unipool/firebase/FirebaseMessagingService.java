package garbagecollectors.com.unipool.firebase;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import garbagecollectors.com.unipool.R;
import garbagecollectors.com.unipool.activities.MessageListActivity;
import garbagecollectors.com.unipool.application.Globals;
import garbagecollectors.com.unipool.application.UtilityMethods;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService
{
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage)
	{
		super.onMessageReceived(remoteMessage);

		Log.d("D2R", remoteMessage.getNotification().getBody());

		String notificationTitle = UtilityMethods.sanitizeName(remoteMessage.getNotification().getTitle());
		String notificationBody = UtilityMethods.sanitizeName(remoteMessage.getNotification().getBody());

		String clickAction = remoteMessage.getNotification().getClickAction();

		Uri sound = Uri.parse(remoteMessage.getNotification().getSound());

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, Globals.CHANNEL_ID)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle(notificationTitle)
				.setContentText(notificationBody)
				.setAutoCancel(true)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setSound(sound);

		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mBuilder.setSmallIcon(R.drawable.logo_trans);
			mBuilder.setColor(getResources().getColor(R.color.colorTextBlack));
		} else {
			mBuilder.setSmallIcon(R.mipmap.ic_launcher);
		}

		//clicking on the notification
		Intent resultIntent = new Intent(clickAction);

		assert notificationBody != null;
		if(notificationBody.contains("accepted"))
			resultIntent.putExtra("openingTab", 2);
		else if(notificationBody.contains("sent"))
			resultIntent.putExtra("openingTab", 1);
		else if(notificationBody.contains("message"))
		{
			Log.d("D2R", "Yay");
			resultIntent.putExtra("openingTab", 2);
		}

		//resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent resultPendingIntent =
				PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(resultPendingIntent);

		//issuing
		int notificationId = (int) System.currentTimeMillis();

		if(notificationBody.contains("message"))
			notificationId = 0;  // Id stays same for all chat notifs, so that new replaces old

		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

		String name = "sdcsdfiwfef9";
		if(MessageListActivity.getName() != null)
			name = MessageListActivity.getName();

		if(!notificationBody.contains(name) || (notificationBody.contains("request")))
		{
			// Builds notification and issues it
			notificationManager.notify(notificationId, mBuilder.build());
		}
	}
}
