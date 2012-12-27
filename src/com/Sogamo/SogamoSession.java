package com.Sogamo;

import java.util.Date;

import android.util.Log;

//Class contain details of the session and it's parameter.
public class SogamoSession {
	
	private String TAG = "SogamoSession"; // Identify Class from Log detail. 
	
	private String _sessionId /* Unique Session Id */, 
		_playerId /* End user's Unique Id */, 
		_logCallectorUrl /* Url to send Log or event data */;
	private int _gameId; /* Unique id to identify Current Game */ 
	private Date _startDate; /* Current Session Starting Date */ 
	private boolean _isOfflineSession; /* Check whether current session is Online or Offline. True - Offline, False - Online */
	
	private SogamoSession(String aSessionId, String aPlayerId, String aGameId, String lcURL, boolean isOffline) {
		// TODO Auto-generated constructor stub
		_sessionId = aSessionId;
		_playerId = aPlayerId;
		
		try {
			_gameId = Integer.parseInt(aGameId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "Number format Exception in Game Id");
		}
		_logCallectorUrl = lcURL;
		_isOfflineSession = isOffline;
		_startDate = new Date();
	}
	
	public SogamoSession() {
		// TODO Auto-generated constructor stub
	}
	
	// Constructor
	public SogamoSession init(String aSessionId, String aPlayerId, String aGameId, String lcURL, boolean isOffline){
		return new SogamoSession(aSessionId, aPlayerId, aGameId, lcURL, isOffline);
	}
	
	

	/**
	 * @return the _sessionId
	 */
	public String get_sessionId() {
		return _sessionId;
	}

	/**
	 * @return the _playerId
	 */
	public String get_playerId() {
		return _playerId;
	}

	/**
	 * @return the _logCallectorUrl
	 */
	public String get_logCallectorUrl() {
		return _logCallectorUrl;
	}

	/**
	 * @return the _gameId
	 */
	public int get_gameId() {
		return _gameId;
	}

	/**
	 * @return the _startDate
	 */
	public Date get_startDate() {
		return _startDate;
	}

	/**
	 * @return the _isOfflineSession
	 */
	public boolean is_isOfflineSession() {
		return _isOfflineSession;
	}
	
	
}
