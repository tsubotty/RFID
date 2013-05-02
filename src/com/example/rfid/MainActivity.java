package com.example.rfid;

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
import android.widget.TextView;
import android.os.Handler;

import jp.co.tss21.uhfrfid.dotr_android.*;


public class MainActivity extends Activity implements OnClickListener, OnDotrEventListener{
	
	private DOTR_Util _reader;
	private String _macAddress;   //= "00:18:9A:05:9C:62";
	private BluetoothAdapter _bt = BluetoothAdapter.getDefaultAdapter();
	private static final String TAG = MainActivity.class.getSimpleName(); // Name of this class 
	private TextView _tv;
	private Handler _handler; // For UI control
	private String _epc; // EPC Tag Name which are read from the reader
	private String _url = "http://www.hongo.wide.ad.jp/~tsubo/index.php";
	private HttpPostTask hpt = null;
	private MyHttpPostHandler hph = null;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _reader = new DOTR_Util();
        _handler = new Handler();
		_tv = (TextView)findViewById(R.id.textView1);
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
    	Button getBtn = (Button)findViewById(R.id.getButton);
    	connectBtn.setOnClickListener(this);
    	disconBtn.setOnClickListener(this);
    	getBtn.setOnClickListener(this);
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
    public void onClick(View v) {
    	switch(v.getId()) {
    		case R.id.getButton:
    			if (hpt == null) {
    				hph = new MyHttpPostHandler();
    				hpt = new HttpPostTask(this, _url, hph);
    				hpt.addPostParam("id", _epc);
    				//hpt.addPostParam("place", );
    				hpt.execute();
    				_tv.setText(hph._response);
    			}
    			break;
    		case R.id.connect:
    			_tv.setText("hoge");
    	    	_reader.setOnDotrEventListener(this);
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
    	}
    }
    
    @Override
    public void onReadTagData(String data, String epc) {
    	_tv.setText(data + ":	" + epc );
    	Log.d(TAG, data + ":" + epc);
    }

	@Override
	public void onConnected() {
		_reader.inventoryTag(false, EnMaskFlag.None, 1000);
	}

	@Override
	public void onDisconnected() {		
	}

	@Override
	public void onInventoryEPC(String epc) {
		_epc = epc;
		_handler.post(new Runnable() {
			@Override
			public void run() {
				_tv.setText(_epc);
	    		Log.d(TAG, _epc);
			}
		});
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
    
}
