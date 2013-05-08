package com.example.rfid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.Menu;
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
import net.arnx.jsonic.JSON;
import com.google.gson.Gson;

public class MainActivity extends Activity implements OnClickListener, OnDotrEventListener, OnCheckedChangeListener{
	
	private DOTR_Util _reader;
	private String _macAddress;   //= "00:18:9A:05:9C:62";
	private BluetoothAdapter _bt = BluetoothAdapter.getDefaultAdapter();
	private static final String TAG = MainActivity.class.getSimpleName(); // Name of this class 
	private TextView _tv;
	private Handler _handler; // For UI control
	private String _epc; // EPC Tag Name which are read from the reader
	//private ArrayList<Map<String, String>> _tagIDs;
	private MyItem _myItem = new MyItem();
	//private String _url = "http://www.hongo.wide.ad.jp/~tsubo/index.php";
	//private String _url = "http://133.11.236.196:8080/api/update";
	private String _server;
	private String _place;
	private HttpPostTask _hpt = null;
	private MyHttpPostHandler _hph = null;
	private TagAccessParameter _param = new TagAccessParameter();
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _reader = new DOTR_Util();
    	_reader.setOnDotrEventListener(this);
        _handler = new Handler();
		_tv = (TextView)findViewById(R.id.condition);
		//_tagIDs = new ArrayList<Map<String, String>>();
		_myItem.list = new ArrayList<Row>();
        checkBluetooth();
        configureButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private void configureButtons() {
    	Button connectBtn = (Button)findViewById(R.id.connect);
    	Button disconBtn = (Button)findViewById(R.id.disconnect);
    	Button readBtn = (Button)findViewById(R.id.read);
    	Button decBtn = (Button)findViewById(R.id.decrease);
    	Button sendBtn = (Button)findViewById(R.id.send);

    	connectBtn.setOnClickListener(this);
    	disconBtn.setOnClickListener(this);
    	readBtn.setOnClickListener(this);
    	decBtn.setOnClickListener(this);
    	sendBtn.setOnClickListener(this);
    	
    	RadioGroup server_radioGroup = (RadioGroup) findViewById(R.id.server_radiogroup);
        server_radioGroup.setOnCheckedChangeListener(this);
        server_radioGroup.check(R.id.tsubo_server);
        RadioButton serverButton = (RadioButton) findViewById(R.id.tsubo_server);
        _server = (String) serverButton.getText();
        RadioGroup place_radioGroup = (RadioGroup) findViewById(R.id.place_radiogroup);
        place_radioGroup.setOnCheckedChangeListener(this);
        place_radioGroup.check(R.id.elab);
        RadioButton placeButton = (RadioButton) findViewById(R.id.elab);
        _place = (String) placeButton.getText();
    	
    }
    
    private void checkBluetooth() {
    	if (!_bt.equals(null)) { //Bluetooth available
            Log.d(TAG,"Bluetooth available");
        } else { //not available
            Log.d(TAG,"Bluetooth not available");
            finish();
        }
        boolean btEnable = _bt.isEnabled();
        if (btEnable == true) { //case of bluetooth enable 
            Set<BluetoothDevice> pairedDevices = _bt.getBondedDevices();
            if(pairedDevices.size() > 0){
                //There are devices which had connected before.
                for(BluetoothDevice device:pairedDevices){
                    //getName() -> device name
                    //getAddress -> MAC address
                    Log.d(TAG, device.getName() + "\n" + device.getAddress());
                    _macAddress = device.getAddress();
                }
            }
        } else {
            finish();
        }    
    }
    
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		RadioButton radioButton = (RadioButton) findViewById(checkedId);
	    Toast.makeText(MainActivity.this,
                "onCheckedChanged():" + radioButton.getText(),
                Toast.LENGTH_SHORT).show();
		switch (group.getId()) {
		case R.id.server_radiogroup:
			_server = (String) radioButton.getText();
			Log.d(TAG, _server);
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
    			if (_hpt == null) {
    				_hph = new MyHttpPostHandler();
    			}
				_hpt = new HttpPostTask(this, _server, _hph);
    			Row row = new Row();
    			row.tag_id = "aaaaaa";
    			row.place = "todai";
    			_myItem.list.add(row);
    			/*
    			for (Map<String, String> m : _tagIDs) {
    				row = new Row();
    				row.tag_id = m.get("tag_id");
    				row.place = m.get("place");
    				mi.list.add(row);
    			}
    			*/
    			String jsonString = new Gson().toJson(_myItem.list, ArrayList.class);
    			Log.d(TAG, "jsonString   " + jsonString);
    			_hpt.addPostParam("body", jsonString);
    			_hpt.execute();
    			try {
    				_tv.setText(_hph._response);
    				Log.d(TAG, _hph._response);
    			} catch (Exception e) {
    				Log.d(TAG, e.getMessage());
    			}
    			break;
    		case R.id.connect:
    			_tv.setText("connecting...");
    	    	Log.d(TAG, "before connect : " + _macAddress);
    	    	_reader.disconnect();
    	    	if (_reader.connect(_macAddress)) {
    	    		Log.d("RFID", "success");
    	    		_tv.setText("success");
    	    	} else {
    	    		Log.d("RFID", "failed");
    	    		_tv.setText("failed");
    	    	}
    	    	Log.d("RFID", "onClick");
    			break;
    		case R.id.disconnect:
    			if (_reader.disconnect()) {
    				_tv.setText("disconnected");
    			} else {
    				_tv.setText("disconnect failed");
    			}
    			break;
    		case R.id.decrease:
    			_reader.setRadioPower(30);
    			Log.d(TAG, "decrease");
    			break;
    		case R.id.read:
    			Log.d(TAG, "read button pushed");
    			if (_reader.isConnect()) {
    				//_reader.readTag(_param, true, EnMaskFlag.None, 3);
    				_reader.inventoryTag(false, EnMaskFlag.None, 1000);
    				Log.d(TAG, "read");
    			} else {
    				Log.d(TAG, "can't read");
    			}
    			break;
    	}
    }
    
    @Override
    public void onReadTagData(String data, String epc) {
    	Log.d(TAG, "In onReadTagData");
    	_epc = data + " : " + epc;
    	_handler.post(new Runnable() {
			@Override
			public void run() {
				_tv.setText(_epc);
	    		Log.d(TAG, _epc);
			}
		});
    }
    
	@Override
	public void onInventoryEPC(String epc) {
		_epc = epc;
		Row row = new Row();
		row.tag_id = epc;
		row.place = _place;
		_myItem.list.add(row);
		_handler.post(new Runnable() {
			@Override
			public void run() {
				_tv.setText(_epc);
	    		Log.d(TAG, _epc);
			}
		});
	}

	@Override
	public void onConnected() {
		//_reader.inventoryTag(false, EnMaskFlag.None, 1000);
		_param.setMemoryBank(EnMemoryBank.EPC);
		_param.setWordOffset(1);
		_param.setWordCount(1);
		_param.setPassword(0);
		//_reader.readTag(_param, true, EnMaskFlag.None, 3);
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
    	if (_reader.isConnect()) {
	    	if (trigger) {
	    		_reader.inventoryTag(false, EnMaskFlag.None, 0);
	    	} else {
	    		_reader.stop();
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
	
	public class MyItem {
		//List<Row> list;
		ArrayList<Row> list;
		
	}
	public class Row {
		String tag_id;
		String place;
	}

	
    
}
