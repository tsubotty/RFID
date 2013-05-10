package com.example.rfid;

import java.util.HashMap;
import java.util.Map;

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
	private String volStr;
	private String decStr;
	private Globals globals;
	private Handler handler = new Handler();
		
	private static final int MENU_MAIN = Menu.FIRST;
	private static final int MENU_TAG_LIST = Menu.FIRST + 1;
	private static final int MENU_NEW_REGISTER = Menu.FIRST + 2;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);
        globals = (Globals) this.getApplication();
        globals.reader.setOnDotrEventListener(this);
        tv = (TextView) findViewById(R.id.debug);
        
        globals.configureMap();
		configureButtons();
		globals.checkBluetooth(this);
    }

	@Override
	 public boolean onCreateOptionsMenu(Menu menu) {
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
	    Button defaultBtn = (Button)findViewById(R.id.default_button);
	    
	    volumeChangeBtn.setOnClickListener(this);
	    decreaseBtn.setOnClickListener(this);
	    defaultBtn.setOnClickListener(this);
	    /* volume_change_radio_button */
	    RadioGroup volumeGroup = (RadioGroup) findViewById(R.id.volume_group);
	    volumeGroup.check(R.id.mute);
	    volumeGroup.setOnCheckedChangeListener(this);
	    RadioButton volumeButton = (RadioButton) findViewById(volumeGroup.getCheckedRadioButtonId());
	    volStr = (String) volumeButton.getText();
	    //volume = globals.decMap.get("1/4");
	    /* radio_power_radio_button */
	    RadioGroup powerGroup = (RadioGroup) findViewById(R.id.radio_power_group);
	    powerGroup.check(R.id.four);
	    powerGroup.setOnCheckedChangeListener(this);
	    RadioButton powerButton = (RadioButton) findViewById(powerGroup.getCheckedRadioButtonId());
	    decStr = (String) powerButton.getText();
	 }

	@Override
	public void onClick(View v) {
	// TODO 自動生成されたメソッド・スタブ
		if (!globals.reader.isConnect()) {
			if (globals.reader.connect(globals.macAddress)) {
				Log.d(TAG, "connect success");
				tv.setText("reader is not connected");
			}
		} else {
			tv.setText("reader is connected");
		}
		switch(v.getId()) {
		case R.id.volume_change:
			try {				
				Log.d(TAG, "volStr  " + volStr);
				Log.d(TAG, "volume  " + globals.volMap.get(volStr));
				if (globals.reader.setBuzzerVolume(globals.volMap.get(volStr), false)) {
					globals.volume = globals.volMap.get(volStr);
					Log.d(TAG, "volume changed to " + volStr);
					tv.setText("volume change success");
				} else {
					tv.setText("valume change failed");
				}
			} catch (Exception e) {
				Log.d(TAG, "volume change failed");
				Log.d(TAG, e.getMessage());
				tv.setText("volume change failed catch");
			}
			break;			
		case R.id.decrease:
			try {
				if (globals.reader.setRadioPower(globals.decMap.get(decStr))) {
					globals.decrease = globals.decMap.get(decStr);
					Log.d(TAG, "decrease:  " + globals.decrease);
					Log.d(TAG, "power decreased by " + globals.decrease + " decibel");
					tv.setText("decrease success");
				} else {
					tv.setText("decrease failed");
				}
			} catch (Exception e) {
				Log.d(TAG, "radio power failed");
				tv.setText("decrease failed catch");
			}
			break;
		case R.id.default_button:
			try {
				if (globals.reader.setDefaultParameter()) {
					tv.setText("set default success");
					
					// globalsの変数を既定値に戻しておく
					globals.decrease = 0;
					globals.volume = null;
				}
			} catch (Exception e) {
				Log.d(TAG, "set default failed");
				tv.setText("set default failed catch");
			}
			break;
		}			
	}
	 
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		RadioButton radioButton = (RadioButton) findViewById(checkedId);
		switch (group.getId()) {
		case R.id.volume_group:
			volStr = (String) radioButton.getText();
			Toast.makeText(ConfigActivity.this,
	                "Volume will be " + volStr,
	                Toast.LENGTH_SHORT).show();
			Log.d(TAG, "volStr    " + volStr);
			break;
		case R.id.radio_power_group:
			decStr = (String) radioButton.getText();
			Toast.makeText(ConfigActivity.this,
	                "Radio Power will be " + decStr + " ✕ " + globals.MAX_RADIO_POWER,
	                Toast.LENGTH_SHORT).show();
			Log.d(TAG, "decStr   " + decStr);
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
				tv.setText(epc);
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
				tv.setText("onTriggerChanged");
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
