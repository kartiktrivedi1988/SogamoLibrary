package com.Sogamo;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SogamoDeviceUtilities {
	
	private static String TAG = "SogamoDeviceUtilities";

	//Use this method when you need a unique identifier in one app
	public static String generateUUISWithSalt(String salt)
	{
		return "";
	}
	
	//Returns local MAC Address like IMEI Number
	public static String macAddress(Context context)
	{
		String macAddress = "";
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			macAddress = telephonyManager.getDeviceId();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "Error in getting MAC Address");
		}
		return macAddress;
	}
}
