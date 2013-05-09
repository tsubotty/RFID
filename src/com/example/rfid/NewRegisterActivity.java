package com.example.rfid;


import jp.co.tss21.uhfrfid.dotr_android.DOTR_Util;
import jp.co.tss21.uhfrfid.dotr_android.EnMaskFlag;
import jp.co.tss21.uhfrfid.dotr_android.EnMemoryBank;
import jp.co.tss21.uhfrfid.dotr_android.OnDotrEventListener;
import jp.co.tss21.uhfrfid.dotr_android.TagAccessParameter;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class NewRegisterActivity extends Activity implements OnDotrEventListener, OnClickListener, OnCheckedChangeListener{
	/*
	 * publish id で new_id を取ってきて
	 * register で登録
	 * 登録の際にコネクトしていなければ、コネクトし、書き込み
	 */
	DOTR_Util reader;
	Handler handler;
	String tag_id;
	TextView tag_id_tv;
	String url ;//= "http://133.11.236.196:8080/api/new";
	private static final String TAG = "NewRegisterActivity";
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_register);
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
    	
    	RadioGroup server_radioGroup = (RadioGroup) findViewById(R.id.server_radiogroup);
        server_radioGroup.setOnCheckedChangeListener(this);
        server_radioGroup.check(R.id.tsubo_new);
	}

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		switch(v.getId()) {
		case R.id.publish:
			HttpGetter getter = new HttpGetter(NewRegisterActivity.this, null);
			getter.execute(url);
			break;
		case R.id.register:
			if (tag_id != null) { // tag_id の validation した方が良いかも
				TagAccessParameter param = new TagAccessParameter();
				param.setMemoryBank(EnMemoryBank.EPC);
				param.setWordOffset(1);
				param.setWordCount(1);
				param.setPassword(0);
				reader.writeTag(param, tag_id, true, EnMaskFlag.None, 100);
			}
			break;
		}
		
	}
	
	public void handleResponse(String str) {
		tag_id_tv.setText(str);
		tag_id = str;
	}
	public void handleMessage(Message msg)
	  {
	    boolean isSuccess = msg.getData().getBoolean("http_post_success");
	    String http_response = msg.getData().get("http_response").toString();

	    if( isSuccess )
	    {
	      tag_id_tv.setText(http_response);
	    }
	    else
	    {
	      tag_id_tv.setText("HTTP GET REQUEST FAILED");
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
	public void onWriteTagData(String epc) {
		// TODO 自動生成されたメソッド・スタブ
		Log.d(TAG, "onWriteTagData");
		Log.d(TAG, "epc:   " + epc);
		Log.d(TAG, "tag_id:    " + tag_id);
		if (epc == tag_id) {
			Log.d(TAG, "matched");
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO 自動生成されたメソッド・スタブ
		RadioButton radioButton = (RadioButton) findViewById(checkedId);
	    Toast.makeText(NewRegisterActivity.this,
                "onCheckedChanged():" + radioButton.getText(),
                Toast.LENGTH_SHORT).show();
		switch (group.getId()) {
		case R.id.server_radiogroup:
			url = (String) radioButton.getText();
			Log.d(TAG, url);
			break;
		}        
	}
}
