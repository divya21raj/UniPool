package garbagecollectors.com.unipool;

import java.util.HashMap;

public class Constants
{
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
