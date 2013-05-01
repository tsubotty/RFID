package com.example.rfid;

import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
//import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import jp.co.tss21.uhfrfid.dotr_android.*;
import android.os.Handler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.Header;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

public class MainActivity extends Activity implements OnClickListener, OnDotrEventListener{
	
	DOTR_Util _reader;
	String _macAddress;   //= "00:18:9A:05:9C:62";
	BluetoothAdapter _bt = BluetoothAdapter.getDefaultAdapter();
	static final String TAG = MainActivity.class.getSimpleName(); // Name of this class 
	TextView _tv;
	Handler _handler; // For UI control
	String _epc; // EPC Tag Name which are read from the reader
	String _url = "http://www.hongo.wide.ad.jp/~tsubo/index.php";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _reader = new DOTR_Util();
        _handler = new Handler();
		_tv = (TextView)findViewById(R.id.textView1);
        checkBluetooth();
        setStartButtonListener();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private void setStartButtonListener() { // Button Settings
    	Button connectBtn = (Button)findViewById(R.id.connect);
    	Button disconBtn = (Button)findViewById(R.id.disconnect);
    	Button getBtn = (Button)findViewById(R.id.getButton);
    	connectBtn.setOnClickListener(this);
    	disconBtn.setOnClickListener(this);
    	getBtn.setOnClickListener(this);
    }
    
    private void checkBluetooth() {
    	if(!_bt.equals(null)){
            //Bluetooth available
            Log.d(TAG,"Bluetooth available");
        }else{
            //not available
            Log.d(TAG,"Bluetooth not available");
            finish();
        }
        boolean btEnable = _bt.isEnabled();
        if(btEnable == true){
            //case of bluetooth 
        }else{
            finish();
        }
        
        Set<BluetoothDevice> pairedDevices = _bt.getBondedDevices();
        if(pairedDevices.size() > 0){
            //There are devices which had connected before.
            for(BluetoothDevice device:pairedDevices){
                //getName()�E�E�Eget device name
                //getAddress()�E�E�Eget MAC address
                Log.d(TAG, device.getName() + "\n" + device.getAddress());
                _macAddress = device.getAddress();
            }
        }
    }
    
    public void executeHttpGet(){
        DefaultHttpClient httpclient = null;
        HttpHost targetHost = null;
        HttpEntity entity = null;
        try {
            httpclient = new DefaultHttpClient();
            targetHost = new HttpHost("203.178.135.39", 80, "http");
            String url = "http://203.178.135.39/~tsubo/index.php?q=" + _epc;
            //HttpGet httpget = new HttpGet("https://news.google.com/news/feeds?q=google&output=rss");
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(targetHost, httpget);
            entity = response.getEntity();
            if(response.getStatusLine().getStatusCode() != 200 ){
                System.out.println("StatusCode:" + response.getStatusLine().getStatusCode());
                return;
            }
            if (entity != null) {
                System.out.println(EntityUtils.toString(entity));
                //System.out.println("length: " + entity.getContentLength());
                //EntityUtils.consume(entity);
                //depriciated
                //entity.consumeContent();
                //System.out.println("hoge");
                _tv.setText("hoge");
            }
        } catch (Exception e) {
            e.printStackTrace();
            _tv.setText("catch error");
        }finally {
            httpclient.getConnectionManager().shutdown();
        }
    }
    
    @Override
    public void onClick(View v) {
    	switch(v.getId()) {
    		case R.id.getButton:
    			if (_epc != "") {
    				executeHttpGet();
    			} else {
    				//executeHttpGet();
    				;
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
    
    public void	onTriggerChanged(boolean trigger) {
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
    
    public void onReadTagData(String data, String epc) {
    	//TextView tv = (TextView)findViewById(R.id.textView1);
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
	public void onTriggerChaned(boolean arg0) {
	}


	@Override
	public void onUploadTagData(String arg0) {
	}


	@Override
	public void onWriteTagData(String arg0) {
	}
    
}
