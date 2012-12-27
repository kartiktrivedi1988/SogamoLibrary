package com.Sogamo.database;


public class SessionMaster {
		
	public static final String SESSION_ID = "SessionId";
	public static final String PLAYER_ID = "PlayerId";
	public static final String GAME_ID = "GameId";
	public static final String LOG_URL = "LogUrl";
	public static final String OFFLINE = "Offline";
	public static final String STARTDATE = "StartDate";
		
	public static final String TABLE_NAME = "Session_Master";

	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + 
					"(" + 
					SESSION_ID + " TEXT PRIMARY KEY, " + 
					PLAYER_ID + " TEXT, " +
					GAME_ID + " TEXT, " +
					LOG_URL + " TEXT, " +
					OFFLINE + " TEXT, " +
					STARTDATE + " TEXT" +
				")";
	
	
}
