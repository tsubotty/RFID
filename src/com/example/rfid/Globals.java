package com.example.rfid;

import java.util.ArrayList;

import jp.co.tss21.uhfrfid.dotr_android.DOTR_Util;

import android.app.Application;

public class Globals extends Application {
	public ArrayList<Row> list = new ArrayList<Row>();
	public DOTR_Util reader = new DOTR_Util();
	public String macAddress;
	//ary = Constants.getInstance().getArray(); ƒVƒ“ƒOƒ‹ƒgƒ“
}
