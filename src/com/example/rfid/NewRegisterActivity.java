package com.example.rfid;

import jp.co.tss21.uhfrfid.dotr_android.DOTR_Util;
import jp.co.tss21.uhfrfid.dotr_android.OnDotrEventListener;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

public class NewRegisterActivity extends Activity implements OnDotrEventListener, OnClickListener{
	/*
	 * publish id で new_id を取ってきて
	 * register で登録
	 * 登録の際にコネクトしていなければ、コネクトし、書き込み
	 */
	DOTR_Util reader;
	Handler handler;
	String tag_id;
	TextView tag_id_tv;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        reader = new DOTR_Util();
    	reader.setOnDotrEventListener(this);
    	tag_id_tv = (TextView)findViewById(R.id.tag_id_blank);
    	configureButtons();
    }
	
	public void configureButtons() {
		Button publishBtn = (Button)findViewById(R.id.publish);
		publishBtn.setOnClickListener(this);
    	Button registerBtn = (Button)findViewById(R.id.register);
    	registerBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		switch(v.getId()) {
		case R.id.publish:
			
			break;
		case R.id.register:
		
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
	public void onInventoryEPC(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
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
