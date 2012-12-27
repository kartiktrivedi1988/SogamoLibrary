package com.Sogamo.util;

import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class InternetUtil {
	
	public static boolean haveNetworkConnection(Context context) {
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	    }
	    return haveConnectedWifi || haveConnectedMobile;
	}
	
	public static String getServer_Data(String url){		
		try {
			
			String server_data = "";
			DefaultHttpClient client = new DefaultHttpClient();
			URI uri = new URI(url);
			HttpGet method = new HttpGet(uri);
			HttpResponse res = client.execute(method);
			InputStream is = res.getEntity().getContent();	
			server_data = generateString(is);
			Log.i("URL Response", server_data);
			return server_data;
		} catch(SecurityException se) {
			return "error " + se.getMessage();
		} catch(Exception e) {
			return "error " + e.getMessage();
		}
	}
	
	private static String generateString(InputStream stream) {
		try {
			StringBuffer sb = new StringBuffer();
			int cur;
			while ((cur = stream.read()) != -1) {
				sb.append((char) cur);
			}
			return String.valueOf(sb);
		} catch (Exception e) {			
			e.printStackTrace();
			return "0";
		}
	}

}
