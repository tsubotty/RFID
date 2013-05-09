package com.example.rfid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import android.os.AsyncTask;
import android.util.Log;

public class HttpGetter extends AsyncTask<String, Void, Void> {
	NewRegisterActivity activity;
	Map<String, String> map;
	String http_ret_msg;
	String http_err_msg = null;
	
	public HttpGetter(NewRegisterActivity activity, Map<String, String> map) {
		this.activity = activity;
		this.map = map;
	}

    @Override
    protected Void doInBackground(String... urls) {
            // TODO Auto-generated method stub
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(urls[0]);
            
            try {
                    HttpResponse response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    if (statusCode == 200) {
                            HttpEntity entity = response.getEntity();
                            InputStream content = entity.getContent();
                            BufferedReader reader = new BufferedReader(
                                            new InputStreamReader(content));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                    builder.append(line);
                            }
                            Log.v("Getter", "Your data: " + builder.toString()); //response data
                            http_ret_msg = builder.toString();
                    } else {
                            Log.e("Getter", "Failed to download file");
                            http_err_msg = "error occured";
                    }
            } catch (ClientProtocolException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            }       
            return null;
    }
    
    protected void onPostExecute(Void unused) {
    	/*
    	Message message = new Message();
        Bundle bundle = new Bundle();
        if (http_err_msg != null) {
          // ÉGÉâÅ[î≠ê∂éû
          bundle.putBoolean("http_get_success", false);
          bundle.putString("http_response", http_err_msg);
        } else {
          // í êMê¨å˜éû
          bundle.putBoolean("http_get_success", true);
          bundle.putString("http_response", http_ret_msg);
        }
        message.setData(bundle);
        Log.d("MainActivity.java", "http_ret_message : " + http_ret_msg);
    	.sendMessage(message);
    	*/
    	activity.handleResponse(http_ret_msg);
      }
      
      
}
