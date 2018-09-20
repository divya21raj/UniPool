package garbagecollectors.com.unipool.application;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.common.util.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;


public class LocalStorageHelper
{
	public static void storeLocally(String fileName, String key, Object value, Context context)
	{
		Gson gson = new Gson();
		String valString = gson.toJson(value);

		if(!Strings.nullToEmpty(key).equals("") && !Strings.nullToEmpty(valString).equals(""))  // strings are neither null nor empty
		{
			SharedPreferences.Editor editor = context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit();

			editor.putString(key, valString);

			editor.apply();
		}
	}

	public static Object loadFromLocal(String key, Context context, Type typeOfT)
	{
		//returns "", if some problem, else returns required object

		try
		{
			if(!Strings.nullToEmpty(key).equals(""))
			{
				Gson gson = new Gson();

				SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.USER_SP_FILE, Context.MODE_PRIVATE);
				String valString =  sharedPreferences.getString(key, "");

				return Strings.nullToEmpty(gson.fromJson(valString, typeOfT));
			}
			else return "";
		} catch (JsonSyntaxException e)
		{
			return "";
		}
	}
}
