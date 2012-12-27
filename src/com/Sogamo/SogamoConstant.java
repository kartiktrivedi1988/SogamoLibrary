package com.Sogamo;

public class SogamoConstant {
	public static String IOS_LOG_COLLECTOR ="sogamo-data-collector-test.herokuapp.com/ios";
	public static String AUTHENTICATION_SERVER_URL ="http://sogamo-suggestion.herokuapp.com";
	public static String SESSIONS_DATA_FILE_NAME ="sogamo_sessions_data.bin";
	public static String API_DEFINITIONS_FILE_NAME ="sogamo_api_definitions.plist";

	public static String AUTHENTICATION_RESPONSE_GAME_ID_KEY ="game_id";
	public static String AUTHENTICATION_RESPONSE_SESSION_ID_KEY ="session_id";
	public static String AUTHENTICATION_RESPONSE_LOG_COLLECTOR_URL_KEY ="lc_url";
	public static String AUTHENTICATION_RESPONSE_PLAYER_ID_KEY ="player_id";
	public static String AUTHENTICATION_RESPONSE_IS_OFFLINE_SESSION_KEY ="is_offline_session";

	public static String SESSIONS_DATA_EVENTS_KEY ="events";
	public static String SESSIONS_DATA_GAME_ID_KEY ="game_id";
	public static String SESSIONS_DATA_SESSIONS_KEY ="sessions";
	public static String SESSIONS_DATA_PLAYER_ID_KEY ="player_id";
	public static String SESSIONS_DATA_SESSION_ID_KEY ="session_id";
	public static String SESSIONS_DATA_LOG_COLLECTOR_URL_KEY ="lc_url";
	public static String SESSIONS_DATA_LATEST_SESSION_KEY ="latest_session";
	public static String SESSIONS_DATA_IS_OFFLINE_SESSION ="is_offline_session";
	public static String SESSIONS_DATA_SESSION_START_DATE_KEY ="session_start_date";
	public static String SESSIONS_DATA_LATEST_SESSION_START_DATE_KEY ="latest_session_start_date";

	public static String DEFINITIONS_DATA_REQUIRED_PARAMETERS_KEY ="required_parameters";
	public static String DEFINITIONS_DATA_API_DEFINITIONS_KEY ="api_definitions";
	public static String DEFINITIONS_DATA_PARAMETERS_KEY ="parameters";
	public static String DEFINITIONS_EVENT_INDEX_KEY ="event_index";
	public static String DEFINITIONS_DATA_REQUIRED_KEY ="required";
	public static String DEFINITIONS_DATA_VERSION_KEY ="version";

	public static String DEFINITIONS_DATA_TYPE_KEY ="type";

	public static int SESSION_TIME_OUT_PERIOD = 43200;
	public static int SESSION_EXPIRED_TIME_HOURS = 12;
	public static String UUID_SALT ="com.sogamo.api";
	public static int MIN_FLUSH_INTERVAL =  0;
	public static int MAX_FLUSH_INTERVAL = 3600;


}
