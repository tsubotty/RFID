package com.example.rfid;

public class MyHttpPostHandler extends HttpPostHandler {
	public String _response;
	public String _pro = "hoge";
	@Override
	public void onPostCompleted(String response) {
		this._response = response;
	}

	@Override
	public void onPostFailed(String response) {
		this._response = response;
	}

}
