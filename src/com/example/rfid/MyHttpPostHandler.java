package com.example.rfid;

import java.util.ArrayList;

public class MyHttpPostHandler extends HttpPostHandler {
	public String response;
	public MainActivity activity;
		
	public MyHttpPostHandler(MainActivity activity) {
		this.activity = activity;
	}
	
	@Override
	public void onPostCompleted(String response) {
		this.response = response;		
		activity.tv.setText("Post Success! (Compressed to " + activity.globals.list.size() + ")");
		activity.globals.list = null; // Reset tag list
		activity.globals.list = new ArrayList<Row>();
	}

	@Override
	public void onPostFailed(String response) {
		this.response = response;
		activity.tv.setText("Post Failed... Tag Count: " + activity.globals.list.size());
	}

}
