package com.example.rfid;

import java.util.ArrayList;

public class MyHttpPostHandler extends HttpPostHandler {
	public String _response;
	public String _pro = "hoge";
	public MainActivity _activity;
	public Globals globals;
	
	public MyHttpPostHandler(MainActivity activity) {
		_activity = activity;
	}
	
	@Override
	public void onPostCompleted(String response) {
		this._response = response;
		_activity.globals.list = null; // Reset tag list
		_activity.globals.list = new ArrayList<Row>();
	}

	@Override
	public void onPostFailed(String response) {
		this._response = response;
	}

}
