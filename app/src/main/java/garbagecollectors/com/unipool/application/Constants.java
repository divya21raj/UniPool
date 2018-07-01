package garbagecollectors.com.unipool.application;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Constants
{
	public static final String UNI = "";
	public static final String CHANNEL_ID = "unipool";

	public static DatabaseReference userDatabaseReference;
	public static DatabaseReference userMessageDatabaseReference;
	public static DatabaseReference entryDatabaseReference = FirebaseDatabase.getInstance().getReference(UNI + "entries");
	public static DatabaseReference pairUpDatabaseReference = FirebaseDatabase.getInstance().getReference(UNI + "pairUps");
	public static DatabaseReference notificationDatabaseReference = FirebaseDatabase.getInstance().getReference(UNI + "notifications");
	public static DatabaseReference messageDatabaseReference = FirebaseDatabase.getInstance().getReference(UNI + "messages");
	public static DatabaseReference expiryDatabaseReference = FirebaseDatabase.getInstance().getReference(UNI + "deleteExpired");

	public static DatabaseReference sentRequestsDatabaseReference;
	public static DatabaseReference receivedRequestsDatabaseReference;

	public static final String OPEN_ACTIVITY = "";
	//HOME, REQUESTS

	public static HashMap<String, String> uniInfo;

	public static void init()
	{
		uniInfo = new HashMap<>();
		uniInfo.put("name", "Shiv Nadar University");
		uniInfo.put("address", "NH91, Tehsil Dadri, Gautam Buddha Nagar, Greater Noida, Uttar Pradesh 201314, India");
		uniInfo.put("latitude", "28.525752899999997");
		uniInfo.put("longitude", "77.57445179999999");
	}
}
