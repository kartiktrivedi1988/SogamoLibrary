package com.Sogamo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.Sogamo.database.Database;
import com.Sogamo.database.SessionMaster;
import com.Sogamo.util.InternetUtil;
import com.Sogamo.util.Plist;
import com.Sogamo.util.XmlParseException;

public class SogamoAPI {

	private String TAG = "SogamoAPI"; // Identify Class from Log detail.

	// uniquely identify player
	private String _playerId = null;

	// stores flash interval
	private int _flashInterval;

	// store API Key
	private String _apiKey;

	// store current session data
	private static SogamoSession _currentSession = null;

	// stores API Definition Version
	private static float _apiDefinitionsVersion = -1;

	// Store context object
	private Context context;

	// contains all plist data in hashMap object.
	private HashMap<?, ?> _plistData = null;

	// Constructor which initialize context object
	public SogamoAPI(Context mContext) {
		// TODO Auto-generated constructor stub
		context = mContext;
	}

	public void startSessionWithAPIKey(String anAPIKey) {
		_apiKey = anAPIKey;
		loadSessionsData();
	}

	// Loads Session data in current session object
	private void loadSessionsData() {

		// Crate Database to store Session Data
		Database database = new Database(context);
		database.createTable(SessionMaster.CREATE_TABLE);

		// Fetch Session data from Database
		ArrayList<ContentValues> _sessionList = database
				.SelectData("Select * from " + SessionMaster.TABLE_NAME);

		// Check if data stored in Database or not
		if (_sessionList.size() > 0) {

			// get Previous session object
			ContentValues values = _sessionList.get(0);

			// Initialize current session object from previous session object
			// data
			_currentSession = new SogamoSession();
			_currentSession = _currentSession.init(values
					.getAsString(SessionMaster.SESSION_ID), values
					.getAsString(SessionMaster.PLAYER_ID), values
					.getAsString(SessionMaster.GAME_ID), values
					.getAsString(SessionMaster.LOG_URL), values.getAsString(
					SessionMaster.OFFLINE).equals("true"));

			/*
			 * Check whether session is expired or not
			 * hasCurrentSessionExpired() returns false if previous session data
			 * is not expired otherwise create new object
			 */
			if (hasCurrentSessionExpired() == false)
				return;
		} else {
			// Create New Session object
			getNewSessionIfNeeded();
		}
	}

	// Covert current session object as offline session object
	private boolean convertOfflineSessions() {

		// Check whether Internet connection available or not
		if (InternetUtil.haveNetworkConnection(context)) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					String url = SogamoConstant.AUTHENTICATION_SERVER_URL
							+ "?apiKey=" + _apiKey;
					if (_playerId != null)
						url += "&playerId=" + _playerId; // Coding is Left

					// get current session data
					String res = InternetUtil.getServer_Data(url);
					if (res.startsWith("error"))
						Log.d(TAG, res.substring(res.indexOf(" ")));
					else
						parseAuthenticationResponse(res);
				}
			}).start();
			return true;
		}
		return false;
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
		} 
		else // Create Offline Session
		{
			
		}
	}

	// Check whether session object is expired or not
	private boolean hasCurrentSessionExpired() {
		if (_currentSession != null) {
			long difference = _currentSession.get_startDate().getTime()
					- new Date().getTime();
			int days = (int) (difference / (1000 * 60 * 60 * 24));
			int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
			return hours >= SogamoConstant.SESSION_EXPIRED_TIME_HOURS;
		}
		return false;
	}

	// Parse Session data
	private void parseAuthenticationResponse(String res) {
		try {
			if (_currentSession == null)
				_currentSession = new SogamoSession();
			JSONObject jsonObject = new JSONObject(res);
			// {"game_id":121,"session_id":"84FA3465-7F0E-4267-A87E-D80C32E76542","lc_url":"sogamo-data-collector.herokuapp.com/","js_url":"https://s3.amazonaws.com/sogamo-js/sogamo_js.js"}
			String _gameId = jsonObject
					.getString(SogamoConstant.SESSIONS_DATA_GAME_ID_KEY);
			String _sessionId = jsonObject
					.getString(SogamoConstant.SESSIONS_DATA_SESSION_ID_KEY);
			String _lcUrl = jsonObject
					.getString(SogamoConstant.SESSIONS_DATA_LOG_COLLECTOR_URL_KEY);
			_currentSession = _currentSession.init(_sessionId, _currentSession
					.get_playerId(), _gameId, _lcUrl, false);
			saveCurrentSesstion();
		} catch (Exception e) {
			// TODO: handle exception
			Log.d(TAG, "Parse Authentication " + e.toString());
		}
	}

	// store current session in database
	private void saveCurrentSesstion() {
		ContentValues values = new ContentValues();
		values.put(SessionMaster.GAME_ID, _currentSession.get_gameId());
		values
				.put(SessionMaster.LOG_URL, _currentSession
						.get_logCallectorUrl());
		values
				.put(SessionMaster.OFFLINE, _currentSession
						.is_isOfflineSession());
		values.put(SessionMaster.PLAYER_ID, _currentSession.get_playerId());
		values.put(SessionMaster.SESSION_ID, _currentSession.get_sessionId());
		values.put(SessionMaster.STARTDATE, _currentSession.get_startDate()
				+ "");

		Database database = new Database(context);
		database.createTable(SessionMaster.CREATE_TABLE);
		if (database.count("Select * from " + SessionMaster.TABLE_NAME) == 0)
			database.insert(values, SessionMaster.TABLE_NAME);
		else
			database.update(values, SessionMaster.TABLE_NAME, null);
	}

	public void trackEventWithName(String _eventName,
			HashMap<String, Object> _param) {
		try {
			if (_plistData == null)
				loadAPIDefinitionsData();
			Log.d(TAG, "" + validateEvent(_eventName, _param));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "Error in parsing plist file");
		}
	}

	private boolean validateEvent(String _eventName,
			HashMap<String, Object> _param) {
		try {
			// Load all event detail in "_envent"
			Map<?, ?> _event = (Map<?, ?>) _plistData.get("api_definitions");

			// Check event name is valid or not
			if (_event.containsKey(_eventName)) {

				// If valid then check for parameter e.g. "event_index"
				Iterator _event_detail_key = ((Map<?, ?>) _event
						.get(_eventName)).keySet().iterator();

				while (_event_detail_key.hasNext()) {
					String _param_key_name = (String) _event_detail_key.next();
					if (_param.containsKey(_param_key_name))
						;
					else {
						Log.e(TAG, "Invalid event detail");
						return false;
					}
				}

				// Load all parameter in one object
				Map<?, ?> _event_parameters = (Map<?, ?>) _param
						.get("parameters");
				Iterator _event_parameters_key = ((Map<?, ?>) ((Map<?, ?>) _event
						.get(_eventName)).get("parameters")).keySet()
						.iterator();

				while (_event_parameters_key.hasNext()) {
					// Get Parameter name or identifier in "type"
					String type = (String) _event_parameters_key.next();

					/*// Auto fill Parameter set automatically.
					autofillParameter(_param, ((Map<?, ?>) ((Map<?, ?>) _event
							.get(_eventName)).get("parameters")));*/					
					
					// Load parameter's attributes in one object
					Map<?, ?> _event_parameters_detail = (Map<?, ?>) _event_parameters
							.get(type);
					Map<?, ?> _plist_paramters_detail = ((Map<?, ?>) ((Map<?, ?>) ((Map<?, ?>) _event
							.get(_eventName)).get("parameters")).get(type));

					// Check particular parameter is required or optional
					if (((Boolean) _plist_paramters_detail.get("required"))
							.booleanValue()) {
						if (!_event_parameters.containsKey(type)) {
							Log.e(TAG, "given event's "
									+ "required paramter not found");
							return false;
						}
					}

					// Check for parameter's data type
					String datatype = (String) _plist_paramters_detail
							.get("type");
					if (_event_parameters_detail != null) {
						Object obj = _event_parameters_detail.get("type");
						boolean flagDatatype = false;
						if (datatype.equals("NSString")
								&& obj instanceof String)
							flagDatatype = true;
						else if (datatype.equals("NSDate")
								&& (obj instanceof Date || obj instanceof java.sql.Date))
							flagDatatype = true;
						else if (datatype.equals("NSNumber")
								&& (obj instanceof Integer || obj instanceof Long))
							flagDatatype = true;

						if (flagDatatype == false) {
							Log.e(TAG, "paramter's data type mismatch");
							return false;
						}

					}
				}
			} else {
				Log.e(TAG, "event name not found or invalid event");
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, "Error in event validation");
			return false;
		}
		return true;
	}

	private void autofillParameter(HashMap<String, Object> _param)
	{
		
	}
	
	// Load plist file in hashmap object
	private void loadAPIDefinitionsData() throws Exception {
		String _plistXML = converXMLtoString();
		// If null then error generate in reading plist file.
		if (_plistXML != null) {
			_plistData = (HashMap<?, ?>) Plist.objectFromXml(_plistXML);
		} else
			Log.d(TAG, "Error in reading plist file");

	}

	// convert plist file into string
	private String converXMLtoString() {
		try {
			// load plist file from asset to input stream
			InputStream _inputStream = context.getAssets().open(
					"sogamo_api_definitions.plist");

			// Create BufferReader object to convert inputstream to String
			BufferedReader _reader = new BufferedReader(new InputStreamReader(
					_inputStream));

			// Load Plist XML data in one string buffer which convert in string
			// easily.
			StringBuffer _plistXML = new StringBuffer();

			// Read one line at a time from BufferReader
			String _plistXMLLine;

			/**
			 * Read the BufferReader line by line and append into "_plistXML"
			 * String
			 **/
			while ((_plistXMLLine = _reader.readLine()) != null) {
				_plistXML.append(_plistXMLLine);
			}

			/** Close the BufferReader connection **/
			_reader.close();

			// Close the InputSreaj Connection
			_inputStream.close();

			// Return Xml from String if successfully read plist file.
			return _plistXML.toString();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "Error in reading plist file");
		}

		// return null if error generate in reading plist file.
		return null;
	}
}
