package com.Sogamo;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.Sogamo.database.Database;
import com.Sogamo.database.SessionMaster;
import com.Sogamo.util.InternetUtil;

public class SogamoAPI {

	private String TAG = "SogamoAPI"; // Identify Class from Log detail.

	private String _playerId = null;
	private int _flashInterval;
	private String _apiKey;
	private static SogamoSession _currentSession = null;
	private static float _apiDefinitionsVersion;
	private Context context;

	public SogamoAPI(Context mContext) {
		// TODO Auto-generated constructor stub
		context = mContext;
	}

	public void startSessionWithAPIKey(String anAPIKey) {
		_apiKey = anAPIKey;
		loadSessionsData();
	}

	private void loadSessionsData() {
		Database database = new Database(context);
		database.createTable(SessionMaster.CREATE_TABLE);
		ArrayList<ContentValues> _sessionList = database
				.SelectData("Select * from " + SessionMaster.TABLE_NAME);
		if (_sessionList.size() > 0) {
			ContentValues values = _sessionList.get(0);
			_currentSession = new SogamoSession();
			_currentSession = _currentSession.init(values
					.getAsString(SessionMaster.SESSION_ID), values
					.getAsString(SessionMaster.PLAYER_ID), values
					.getAsString(SessionMaster.GAME_ID), values
					.getAsString(SessionMaster.LOG_URL), values.getAsString(
					SessionMaster.OFFLINE).equals("true"));
			if(hasCurrentSessionExpired() == false)// Check whether session expired or not
				return;
		} else {
			getNewSessionIfNeeded(); // Create New Session
		}
	}

	private void getNewSessionIfNeeded() {
		if (_currentSession != null) // Check Previous Session exist
		{
			if (hasCurrentSessionExpired() == false) // Current session not
				// expired
				return;
		}
		if (InternetUtil.haveNetworkConnection(context)) // Check whether
															// Internet
															// available or not
		{
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					String url = SogamoConstant.AUTHENTICATION_SERVER_URL
							+ "?apiKey=" + _apiKey;
					if (_playerId != null)
						url += "&playerId=" + _playerId; // Coding is Left
					String res = InternetUtil.getServer_Data(url);
					if (res.startsWith("error"))
						Log.d(TAG, res.substring(res.indexOf(" ")));
					else
						parseAuthenticationResponse(res);
				}
			}).start();
		} else // Create Offline Session
		{

		}
	}

	private boolean hasCurrentSessionExpired() {
		if (_currentSession != null)
		{
			long difference = _currentSession.get_startDate().getTime() - new Date().getTime(); 
			int days = (int) (difference / (1000*60*60*24));  
			int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60)); 
			return hours >= SogamoConstant.SESSION_EXPIRED_TIME_HOURS;
		}
		return false;
	}

	private void parseAuthenticationResponse(String res) {
		try
		{
			if(_currentSession == null)
				_currentSession = new SogamoSession();
			JSONObject jsonObject = new JSONObject(res);
			//{"game_id":121,"session_id":"84FA3465-7F0E-4267-A87E-D80C32E76542","lc_url":"sogamo-data-collector.herokuapp.com/","js_url":"https://s3.amazonaws.com/sogamo-js/sogamo_js.js"}
			String _gameId = jsonObject.getString(SogamoConstant.SESSIONS_DATA_GAME_ID_KEY);
			String _sessionId = jsonObject.getString(SogamoConstant.SESSIONS_DATA_SESSION_ID_KEY);
			String _lcUrl = jsonObject.getString(SogamoConstant.SESSIONS_DATA_LOG_COLLECTOR_URL_KEY);
			_currentSession = _currentSession.init(_sessionId, _currentSession.get_playerId(), _gameId, _lcUrl, false);
			saveCurrentSesstion();
		}
		catch (Exception e) {
			// TODO: handle exception
			Log.d(TAG, "Parse Authentication " + e.toString());
		}
	}
	
	private void saveCurrentSesstion()
	{
		ContentValues values = new ContentValues();
		values.put(SessionMaster.GAME_ID, _currentSession.get_gameId());
		values.put(SessionMaster.LOG_URL, _currentSession.get_logCallectorUrl());
		values.put(SessionMaster.OFFLINE, _currentSession.is_isOfflineSession());
		values.put(SessionMaster.PLAYER_ID, _currentSession.get_playerId());
		values.put(SessionMaster.SESSION_ID, _currentSession.get_sessionId());
		values.put(SessionMaster.STARTDATE, _currentSession.get_startDate() + "");
		
		Database database = new Database(context);
		database.createTable(SessionMaster.CREATE_TABLE);
		if(database.count("Select * from " + SessionMaster.TABLE_NAME) == 0)
			database.insert(values, SessionMaster.TABLE_NAME);
		else
			database.update(values, SessionMaster.TABLE_NAME, null);
	}

}
