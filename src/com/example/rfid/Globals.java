package com.example.rfid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.tss21.uhfrfid.dotr_android.DOTR_Util;
import jp.co.tss21.uhfrfid.dotr_android.EnBuzzerVolume;
import jp.co.tss21.uhfrfid.dotr_android.EnMemoryBank;
import jp.co.tss21.uhfrfid.dotr_android.TagAccessParameter;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

public class Globals extends Application {
	public List<Row> list = new ArrayList<Row>();
	public DOTR_Util reader = new DOTR_Util();
	public String macAddress; // = "00:18:9A:05:9C:62";
	public TagAccessParameter param = new TagAccessParameter();
	public BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
	public EnBuzzerVolume volume = null;
	public int decrease = 0;
	public final String MAX_RADIO_POWER = "1000mW"; 
	public Map<String, EnBuzzerVolume> volMap;
	public Map<String, Integer> decMap; 
	
	private static final String TAG = Globals.class.getSimpleName();
	
	public void setParams() {
		param.setMemoryBank(EnMemoryBank.EPC);
		param.setWordOffset(1);
		param.setWordCount(1);
		param.setPassword(0);
	}
	
	public void checkBluetooth(Activity activity) {
		if (!bt.equals(null)) { //Bluetooth available
			Log.d(TAG,"Bluetooth available");
	    } else { //not available
	    	Log.d(TAG,"Bluetooth not available");
	    	activity.finish();
	    }
		boolean btEnable = bt.isEnabled();
		if (btEnable == true) { //case of bluetooth enable 
			Set<BluetoothDevice> pairedDevices = bt.getBondedDevices();
			if(pairedDevices.size() > 0){
				//There are devices which had connected before.
				for(BluetoothDevice device:pairedDevices){
					//getName() -> device name
					//getAddress -> MAC address
					Log.d(TAG, device.getName() + "\n" + device.getAddress());
					macAddress = device.getAddress();
				}
			}
		} else {
			activity.finish();
		}    
	}
	
	public void compressList() {
		/*
		Set<Row> set = new HashSet<Row>();
		set.addAll(list);
		List<Row> uniqueList = new ArrayList<Row>();
		uniqueList.addAll(set);
		list = uniqueList;
		*/
	}
	
	public void configureMap() {
    	volMap = new HashMap<String, EnBuzzerVolume>();
    	volMap.put("Mute", EnBuzzerVolume.Mute);
    	volMap.put("Low", EnBuzzerVolume.Mute);
    	volMap.put("High", EnBuzzerVolume.Mute);
    	decMap = new HashMap<String, Integer>();
    	decMap.put("1/2", 3); 
    	decMap.put("1/4", 6); 
    	decMap.put("1/8", 9);
	}
}
	//ary = Constants.getInstance().getArray(); シングルトン

