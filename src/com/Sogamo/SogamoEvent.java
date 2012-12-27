package com.Sogamo;

import java.util.HashMap;

public class SogamoEvent {

	private String _eventName /* Specify a name of Event */, _eventId /* Uniquely identify Event */;
	private HashMap<String, Object> _params /* Contains List of Parameters for particular Event  */;
	
	private SogamoEvent(String name, String index, HashMap<String, Object> params) {
		// TODO Auto-generated constructor stub
		_eventId = index;
		_eventName = name;
		_params = params;
	}
	
	//Constructor
	public SogamoEvent init(String name, String index, HashMap<String, Object> params ) {
		return new SogamoEvent(name, index, params);
	}
}
