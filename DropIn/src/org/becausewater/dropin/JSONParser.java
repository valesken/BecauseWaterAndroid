package org.becausewater.dropin;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;


public class JSONParser {
	
	private String urlString, data = "";
	private ArrayList<JSONObject> objs;
	private ArrayList<Drop> drops;
	
	public JSONParser(String url) {
		this.urlString = url;
		objs = new ArrayList<JSONObject>();
		drops = new ArrayList<Drop>();
	}
	
	public void getDrops(ArrayList<Drop> dest) {
		for(int i = 0; i < drops.size(); ++i)
			dest.add(drops.get(i));
	}
	
	public void fetchJSON() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(urlString);
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
							
					InputStream stream = entity.getContent();
					Scanner s = new Scanner(stream);
					s.useDelimiter("\\n");
					while(s.hasNext())
						data = data.concat(s.next());
					readAndParseJSON(data);
					stream.close();
					s.close();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
		try {
			thread.join();
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void readAndParseJSON(String in) {
		String index = "";
		JSONObject obj;
		Drop drop;
		try {
			JSONObject reader = new JSONObject(in);
			objs.clear();
			for(int i = 0; reader.has(Integer.toString(i)); ++i) {
				index = Integer.toString(i);
				objs.add(reader.getJSONObject(index));
				obj = objs.get(i);
				drop = new Drop();
				drop.setLatitude(obj.getDouble("lat"));
				drop.setLongitude(obj.getDouble("lng"));
				drop.setAddress(obj.getString("address"));
				if(!obj.getString("locationName").isEmpty())
					drop.setName(obj.getString("locationName"));
				else
					drop.setName(obj.getString("name"));
				drop.setDetails(obj.getString("details"));
				drop.setCategory(obj.getString("category"));
				drops.add(drop);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void pushDrop(final String url) {
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				try {
					httpclient.execute(httppost);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
}
