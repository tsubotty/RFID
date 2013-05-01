package com.example.rfid;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpGetTask {
	private String host;
	private String url;
	private Map<String, String> map;
	private static final String PROTO = "http"; 
	
	public HttpGetTask(String host, String url, Map<String, String> map) {
		this.host = host;
		this.url = url;
		this.map = map;
	}
    public void executeHttpGet(){
        DefaultHttpClient httpclient = null;
        HttpHost targetHost = null;
        HttpEntity entity = null;
        try {
            httpclient = new DefaultHttpClient();
            targetHost = new HttpHost(host, 80, PROTO);
            String getUrl = url;
            getUrl += "?";
            boolean trimFlag = false;
            for (Map.Entry<String, String> e : map.entrySet()) {
            	getUrl = getUrl + e.getKey() + "=" + e.getValue() + "&";
            	trimFlag = true;
            }
            if (trimFlag) {
            	getUrl = getUrl.substring(0, getUrl.length() - 2); // Trim '&' at end of getUrl;
            }
            HttpGet httpget = new HttpGet(getUrl);
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            httpclient.getConnectionManager().shutdown();
        }
    }
}
