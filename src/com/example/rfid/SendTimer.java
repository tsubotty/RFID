package com.example.rfid;

import java.util.ArrayList;

import com.google.gson.Gson;

import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.util.Log;

public class SendTimer extends AsyncTask<String, Integer, Long> implements OnCancelListener{

  final String TAG = "SendTimer";
  ProgressDialog _dialog;
  Context _context;
  //ArrayList<Row> _list;
  //String _server;
  MainActivity _activity;
  HttpPostTask _hpt = null;
  MyHttpPostHandler _hph = null;
    
  public SendTimer(Context context, MainActivity activity) {
    this._context = context;
    this._activity = activity;
  }
  
  @Override
  protected void onPreExecute() {
    Log.d(TAG, "onPreExecute");
    _dialog = new ProgressDialog(_context);
    _dialog.setTitle("Please wait");
    _dialog.setMessage("Loading data...");
    _dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    _dialog.setCancelable(true);
    _dialog.setOnCancelListener(this);
    _dialog.setMax(100);
    _dialog.setProgress(0);
    _dialog.show();		
  }

  @Override
  protected Long doInBackground(String... params) {
    Log.d(TAG, "doInBackground - " + params[0]);
    
    try {
      for(int i=0; i<10; i++){
        if(isCancelled()){
          Log.d(TAG, "Cancelled!");
          break;
        }
        Thread.sleep(1000);
        publishProgress((i+1) * 10);
      }
    } catch (InterruptedException e) {
      Log.d(TAG, "InterruptedException in doInBackground");
    }  
    return 123L;
  }
  
  @Override
  protected void onProgressUpdate(Integer... values) {
    Log.d(TAG, "onProgressUpdate - " + values[0]);
    _dialog.setProgress(values[0]);
  }
  
  @Override
  protected void onCancelled() {
    Log.d(TAG, "onCancelled");
    _dialog.dismiss();
  }

  @Override
  protected void onPostExecute(Long result) {
    Log.d(TAG, "onPostExecute - " + result);
    _dialog.dismiss();
    
	if (_hpt == null) {
		_hph = new MyHttpPostHandler(this._activity);
	}
	_hpt = new HttpPostTask(this._activity, _activity._server, _hph);
	String jsonString = new Gson().toJson(_activity._list, ArrayList.class);
	Log.d(TAG, "Count: " + _activity._list.size() + "  jsonString: " + jsonString);
	_hpt.addPostParam("body", jsonString);
	_hpt.execute();
  }

  @Override
  public void onCancel(DialogInterface dialog) {
    Log.d(TAG, "Dialog onCancell... calling cancel(true)");
    this.cancel(true);
  }
}
