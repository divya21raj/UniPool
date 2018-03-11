package garbagecollectors.com.snucabpool;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService
{

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage)
	{
		super.onMessageReceived(remoteMessage);

		String notificationTitle = remoteMessage.getNotification().getTitle();
		String notificationBody = remoteMessage.getNotification().getBody();

		String clickAction = remoteMessage.getNotification().getClickAction();

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle(notificationTitle)
				.setContentText(notificationBody)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT);

		//clicking on the notification
		Intent resultIntent = new Intent(clickAction);

		PendingIntent resultPendingIntent =
				PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(resultPendingIntent);

		//issuing
		int notificationId = (int) System.currentTimeMillis();
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

		// Builds notification and issues it
		notificationManager.notify(notificationId, mBuilder.build());


	}
}
