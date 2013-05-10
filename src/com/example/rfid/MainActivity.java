package com.example.rfid;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import jp.co.tss21.uhfrfid.dotr_android.*;

//import net.sf.json.JSONObject;
//import net.arnx.jsonic.JSON;
import com.google.gson.Gson;

public class MainActivity extends Activity implements OnClickListener, OnDotrEventListener, OnCheckedChangeListener {
	
	private static final String TAG = MainActivity.class.getSimpleName(); // Name of this class 
	protected TextView tv;
	private Handler handler; // For UI control
	private String epc; // EPC Tag Name which are read from the reader
	public String server;
	private String _place;
	private HttpPostTask _hpt = null;
	private MyHttpPostHandler _hph = null;
	private SendTimer _timer;
	public Globals globals;
	
	private static final int MENU_TAG_LIST = Menu.FIRST;
	private static final int MENU_NEW_REGISTER = Menu.FIRST + 1;
	private static final int MENU_CONFIG = Menu.FIRST + 2;
	
	private final Context context = this;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		globals = (Globals) this.getApplication();
    	globals.reader.setOnDotrEventListener(this);
        handler = new Handler();
		tv = (TextView)findViewById(R.id.condition);
        globals.checkBluetooth(this);
        configureButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
    	super.onCreateOptionsMenu(menu);
    	menu.add(0, MENU_TAG_LIST, 0, "Tag List");
    	menu.add(0, MENU_NEW_REGISTER, 0, "New Register");
    	menu.add(0, MENU_CONFIG, 0, "Reader Config");
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (!globals.reader.isConnect()) {
    		Toast.makeText(MainActivity.this,
                    "Must Connect Before Transition",
                    Toast.LENGTH_SHORT).show();
    		return false;
    	}
    	Intent intent;
        switch (item.getItemId()) {
        case MENU_TAG_LIST:
            Log.d("Menu","Select Menu tag list");
            intent = new Intent(MainActivity.this, TagListActivity.class);
            startActivity(intent);
            return true;
        case MENU_NEW_REGISTER:
        	intent = new Intent(MainActivity.this, NewRegisterActivity.class);
            startActivity(intent);
            return true;
        case MENU_CONFIG:
        	intent = new Intent(MainActivity.this, ConfigActivity.class);
        	startActivity(intent);
        	return true;
        }
        return false;
    }
    
    private void configureButtons() {
    	Button connectBtn = (Button)findViewById(R.id.connect);
    	Button disconBtn = (Button)findViewById(R.id.disconnect);
    	Button readBtn = (Button)findViewById(R.id.read);
    	Button decBtn = (Button)findViewById(R.id.decrease);
    	Button sendBtn = (Button)findViewById(R.id.send);
    	Button timerBtn = (Button)findViewById(R.id.timer);
    	Button loopBtn = (Button)findViewById(R.id.loop);
    	
    	connectBtn.setOnClickListener(this);
    	disconBtn.setOnClickListener(this);
    	readBtn.setOnClickListener(this);
    	decBtn.setOnClickListener(this);
    	sendBtn.setOnClickListener(this);
    	timerBtn.setOnClickListener(this);
    	loopBtn.setOnClickListener(this);
    	
    	/* server_radio_button */
    	RadioGroup server_radioGroup = (RadioGroup) findViewById(R.id.server_radiogroup);
    	server_radioGroup.check(R.id.tsubo_server);
        server_radioGroup.setOnCheckedChangeListener(this);
        RadioButton serverButton = (RadioButton) findViewById(R.id.tsubo_server);
        server = (String) serverButton.getText();
        /* place_radio_button */
        RadioGroup place_radioGroup = (RadioGroup) findViewById(R.id.place_radiogroup);
        place_radioGroup.check(R.id.elab);
        place_radioGroup.setOnCheckedChangeListener(this);
        RadioButton placeButton = (RadioButton) findViewById(R.id.elab);
        _place = (String) placeButton.getText();
    }
    

    
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		RadioButton radioButton = (RadioButton) findViewById(checkedId);
	    Toast.makeText(MainActivity.this,
                "onCheckedChanged():" + radioButton.getText(),
                Toast.LENGTH_SHORT).show();
		switch (group.getId()) {
		case R.id.server_radiogroup:
			server = (String) radioButton.getText();
			Log.d(TAG, server);
			break;
		case R.id.place_radiogroup:
			_place = (String) radioButton.getText();
			Log.d(TAG, _place);
			break;
		}            
	}
    
    @Override
    public void onClick(View v) {
    	switch(v.getId()) {
    		case R.id.send:
    			if (_hph == null) {
    				_hph = new MyHttpPostHandler(this);
    			}
				_hpt = new HttpPostTask(this, server, _hph);
    			Row row = new Row();
    			row.tag_id = "aaaaaa";
    			row.place = "todai";
    			if (globals == null) {
    				Log.d(TAG, "globals null");
    				globals = (Globals) this.getApplication(); 
    			}
    			if (globals.list == null) {
    				Log.d(TAG, "list null");
    			}
    			globals.list.add(row);
    			String jsonString = new Gson().toJson(globals.list, ArrayList.class);
    			Log.d(TAG, "jsonString   " + jsonString);
    			_hpt.addPostParam("body", jsonString);
    			_hpt.execute();
    			try {
    				tv.setText(_hph._response);
    				Log.d(TAG, _hph._response);
    			} catch (Exception e) {
    				Log.d(TAG, e.getMessage());
    			}
    			break;
    		case R.id.connect:
    			asyncConnect();
    			/*
    			tv.setText("connecting...");
    	    	Log.d(TAG, "before connect : " + globals.macAddress);
    	    	globals.reader.disconnect();
    	    	if (globals.reader.connect(globals.macAddress)) {
    	    		Log.d("RFID", "success");
    	    		tv.setText("success");
    	    	} else {
    	    		Log.d("RFID", "failed");
    	    		tv.setText("failed");
    	    	}
    	    	*/
    	    	Log.d("RFID", "connect onClick");
    			break;
    		case R.id.disconnect:
    			if (globals.reader.disconnect()) {
    				tv.setText("disconnected");
    			} else {
    				tv.setText("disconnect failed");
    			}
    			break;
    		case R.id.decrease:
    			globals.reader.setRadioPower(30);
    			Log.d(TAG, "decrease");
    			break;
    		case R.id.read:
    			Log.d(TAG, "read button pushed");
    			if (globals.reader.isConnect()) {
    				//globals.reader.readTag(_param, true, EnMaskFlag.None, 3);
    				globals.reader.inventoryTag(false, EnMaskFlag.None, 1000);
    				Log.d(TAG, "read");
    			} else {
    				Log.d(TAG, "can't read");
    			}
    			break;
    		case R.id.timer:
    			_timer = new SendTimer(this, this);
    			_timer.execute("timer");
    			break;
    		case R.id.loop:
    			Log.d(TAG, "loop");    			
    			// 3秒ごとにタグ読み取り
    			Timer mTimer = new Timer(true);
    			mTimer.schedule(new TimerTask() {
    				public void run() {
        				globals.reader.inventoryTag(false, EnMaskFlag.None, 1000);
    				}
    			}, 1000, 3000);
    			break;
    	}
    }
    
    public void asyncConnect() {
    	AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
    		ProgressDialog dialog;
    		@Override
    		protected void onPreExecute() {
    			 dialog = new ProgressDialog(context);
    			 dialog.setTitle("Please wait");
    			 dialog.setMessage("Loading data...");
    			 dialog.setCancelable(true);
    			 dialog.show();
    		}
    		@Override
    		protected Void doInBackground(Void... unused) {
   			 	globals.reader.connect(globals.macAddress);
    			return null;
    		}
    		
			@Override
			protected void onPostExecute(Void result) {
				dialog.dismiss();
				tv.setText("Connect Success!");
			}
    		
    	};
    	task.execute(); // パラメータを渡す
    }
    
    @Override
    public void onReadTagData(String data, final String epc) {
    	Log.d(TAG, "In onReadTagData");
    	this.epc = data + " : " + epc;
    	handler.post(new Runnable() {
			@Override
			public void run() {
				tv.setText(epc);
	    		Log.d(TAG, epc);
			}
		});
    }
    
	@Override
	public void onInventoryEPC(String epc) {
		this.epc = epc;
		Row row = new Row();
		row.tag_id = epc;
		row.place = _place;
		globals.list.add(row);
		handler.post(new Runnable() {
			@Override
			public void run() {
				tv.setText("TagCount: " + globals.list.size());
	    		Log.d(TAG, "" + globals.list.size());
	    		if (_timer == null) {
	    			_timer = new SendTimer(MainActivity.this, MainActivity.this);
		    		_timer.execute("hoge");
	    		} else {
	    			_timer.cancel(true);
	    			_timer = null;
	    			_timer = new SendTimer(MainActivity.this, MainActivity.this);
	    			_timer.execute("hoge");
	    		}
	    		
			}
		});
	}

	@Override
	public void onConnected() {
		globals.setParams();
		//globals.reader.inventoryTag(false, EnMaskFlag.None, 1000);
		//globals.reader.readTag(globals.param, true, EnMaskFlag.None, 3);
	}

	@Override
	public void onDisconnected() {		
	}

	@Override
	public void onLinkLost() {
	}


	@Override
	public void onTriggerChaned(boolean trigger) {
    	Log.d(TAG, "ontriggerchanged");
    	if (globals.reader.isConnect()) {
	    	if (trigger) {
	    		globals.reader.inventoryTag(false, EnMaskFlag.None, 0);
	    	} else {
	    		globals.reader.stop();
	    	}
    	} else {
    		Log.d(TAG, "not connected");
    	}
	}


	@Override
	public void onUploadTagData(String arg0) {
	}


	@Override
	public void onWriteTagData(String arg0) {
	}  
}
