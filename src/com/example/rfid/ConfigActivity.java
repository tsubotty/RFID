package com.example.rfid;

import java.util.HashMap;
import java.util.Map;

import jp.co.tss21.uhfrfid.dotr_android.DOTR_Util;
import jp.co.tss21.uhfrfid.dotr_android.EnBuzzerVolume;
import jp.co.tss21.uhfrfid.dotr_android.EnMaskFlag;
import jp.co.tss21.uhfrfid.dotr_android.OnDotrEventListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
//import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ConfigActivity extends MainActivity implements OnClickListener, OnDotrEventListener, OnCheckedChangeListener{
	//private DOTR_Util reader;
	private static final String TAG = ConfigActivity.class.getSimpleName(); // Name of this class 
	//private EnBuzzerVolume volume;
	private int decrease;
	private Map<String, EnBuzzerVolume> volumeMap;
	private Map<String, Integer> decMap; 
	private String volume_string = "Mute";
	private Globals globals;
	private TextView debug;
	private Handler handler = new Handler();
	private String epc;
	
	private static final int MENU_MAIN = Menu.FIRST;
	private static final int MENU_TAG_LIST = Menu.FIRST + 1;
	private static final int MENU_NEW_REGISTER = Menu.FIRST + 2;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);
        globals = (Globals) this.getApplication();
        globals.reader.setOnDotrEventListener(this);
        debug = (TextView) findViewById(R.id.debug);
    	volumeMap = new HashMap<String, EnBuzzerVolume>();
    	volumeMap.put("Mute", EnBuzzerVolume.Mute);
    	volumeMap.put("Low", EnBuzzerVolume.Mute);
    	volumeMap.put("High", EnBuzzerVolume.Mute);
    	decMap = new HashMap<String, Integer>();
    	decMap.put("1/2", 3); 
    	decMap.put("1/4", 6); 
    	decMap.put("1/8", 9);
		configureButtons();
		checkBluetooth();
    }
	
	@Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	    	super.onCreateOptionsMenu(menu);
	    	menu.add(0, MENU_MAIN, 0, "Main");
	    	menu.add(0, MENU_TAG_LIST, 0, "Tag List");
	    	menu.add(0, MENU_NEW_REGISTER, 0, "New Register");
	        return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent;
        switch (item.getItemId()) {
        case MENU_MAIN:
        	intent = new Intent(ConfigActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        case MENU_TAG_LIST:
        	intent = new Intent(ConfigActivity.this, TagListActivity.class);
            startActivity(intent);
            return true;
        case MENU_NEW_REGISTER:
        	intent = new Intent(ConfigActivity.this, NewRegisterActivity.class);
        	startActivity(intent);
        	return true;
        }
        return false;
    }
	
	
	 protected void configureButtons() {
	    Button volumeChangeBtn = (Button)findViewById(R.id.volume_change);
	    Button decreaseBtn = (Button)findViewById(R.id.decrease);
	    	
	    volumeChangeBtn.setOnClickListener(this);
	    decreaseBtn.setOnClickListener(this);
	    	
	    /* volume_change_radio_button */
	    RadioGroup volume_group = (RadioGroup) findViewById(R.id.volume_group);
	    volume_group.check(R.id.mute);
	    volume_group.setOnCheckedChangeListener(this);
	    //volume = decMap.get("1/4");
	    /* radio_power_radio_button */
	    RadioGroup power_group = (RadioGroup) findViewById(R.id.radio_power_group);
	    power_group.check(R.id.four);
	    power_group.setOnCheckedChangeListener(this);
		decrease = decMap.get("1/4");
	 }

	@Override
	public void onClick(View v) {
	// TODO 自動生成されたメソッド・スタブ
		if (!globals.reader.isConnect()) {
			if (globals.reader.connect(globals.macAddress)) {
				Log.d(TAG, "connect success");
				debug.setText("reader is not connected");
			}
		} else {
			debug.setText("reader is connected");
		}
		switch(v.getId()) {
		case R.id.volume_change:
			try {
				Log.d(TAG, "volume_string  " + volume_string);
				Log.d(TAG, "volume  " + volumeMap.get(volume_string));
				if (globals.reader.setBuzzerVolume(volumeMap.get(volume_string), false)) {
					Log.d(TAG, "volume changed to " + volume_string);
					debug.setText("volume change success");
				} else {
					debug.setText("valume change failed");
				}
			} catch (Exception e) {
				Log.d(TAG, "volume change failed");
				Log.d(TAG, e.getMessage());
				debug.setText("volume change failed catch");
			}
			
			
			break;			
		case R.id.decrease:
			try {
				if (globals.reader.setRadioPower(decrease)) {
					Log.d(TAG, "decrease:  " + decrease);
					Log.d(TAG, "power decreased by " + decrease + " decibel");
					debug.setText("decrease success");
				} else {
					debug.setText("decrease failed");
				}
			} catch (Exception e) {
				Log.d(TAG, "radio power failed");
				Log.d(TAG, e.getMessage());
				debug.setText("decrease failed catch");
			}
			break;
		}			
	}
	 
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		RadioButton radioButton = (RadioButton) findViewById(checkedId);
	    Toast.makeText(ConfigActivity.this,
                "onCheckedChanged():" + radioButton.getText(),
                Toast.LENGTH_SHORT).show();
		switch (group.getId()) {
		case R.id.volume_group:
			volume_string = (String) radioButton.getText();
			Log.d(TAG, volume_string);
			break;
		case R.id.radio_power_group:
			String decStr = (String) radioButton.getText();
			Log.d(TAG, "decStr   " + decStr);
			decrease = decMap.get(decStr);
			Log.d(TAG, decStr);
			break;
		}
	}

	@Override
	public void onConnected() {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onDisconnected() {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onInventoryEPC(final String epc) {
		// TODO 自動生成されたメソッド・スタブ
		//this.epc = epc;
		handler.post(new Runnable() {
			public void run() {
				debug.setText(epc);
			}
		});
	}

	@Override
	public void onLinkLost() {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onReadTagData(String arg0, String arg1) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onTriggerChaned(boolean arg0) {
		// TODO 自動生成されたメソッド・スタブ
		handler.post(new Runnable() {
			public void run() {
				debug.setText("onTriggerChanged");
			}
		});
		globals.reader.inventoryTag(true, EnMaskFlag.None, 100);
	}

	@Override
	public void onUploadTagData(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onWriteTagData(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

}
