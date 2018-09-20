package garbagecollectors.com.unipool.application;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Globals
{
	public static final String UNI = "";
	public static final String CHANNEL_ID = "unipool";

	// SharedPreference Keys
	public static final String USER_SP_FILE = "USER_SP_FILE";
	public static final String USER_ID_KEY = "USER_ID";
	public static final String USER_NAME_KEY = "USER_NAME";
	public static final String USER_EMAIL_KEY = "USER_EMAIL";
	public static final String USER_PHONE_KEY = "USER_PHONE";
	public static final String USER_PHOTO_URL_KEY = "USER_PHOTO_URL";
	public static final String USER_TOKEN_KEY = "USER_TOKEN";

	// User Stuff
	public static String USER_ID = "";
	public static String USER_NAME = "";
	public static String USER_EMAIL = "";
	public static String USER_PHONE = "";
	public static String USER_PHOTO_URL = "";
	public static String USER_TOKEN = "";


	public static DatabaseReference userDatabaseReference;
	public static DatabaseReference userMessageDatabaseReference;
	public static DatabaseReference entryDatabaseReference = FirebaseDatabase.getInstance().getReference(UNI + "entries");
	public static DatabaseReference megaEntryDatabaseReference = FirebaseDatabase.getInstance().getReference(UNI + "mega_entries");
	public static DatabaseReference pairUpDatabaseReference = FirebaseDatabase.getInstance().getReference(UNI + "pairUps");
	public static DatabaseReference notificationDatabaseReference = FirebaseDatabase.getInstance().getReference(UNI + "notifications");
	public static DatabaseReference messageDatabaseReference = FirebaseDatabase.getInstance().getReference(UNI + "messages");
	public static DatabaseReference expiryDatabaseReference = FirebaseDatabase.getInstance().getReference(UNI + "deleteExpired");

	public static DatabaseReference sentRequestsDatabaseReference;
	public static DatabaseReference receivedRequestsDatabaseReference;
	public static DatabaseReference userPairUpDatabaseReference;

	public static String OPEN_ACTIVITY = "";
	//HOME, REQUESTS, CHAT, ABOUT

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
