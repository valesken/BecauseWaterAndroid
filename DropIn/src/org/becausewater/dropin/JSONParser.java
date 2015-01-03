package org.becausewater.dropin;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class JSONParser {
	
	private String urlString, data = "";
	private ArrayList<JSONObject> objs;
	private ArrayList<Double> lats, lngs;
	private ArrayList<String> names, details;
	
	public JSONParser(String url) {
		this.urlString = url;
		objs = new ArrayList<JSONObject>();
		lats = new ArrayList<Double>();
		lngs = new ArrayList<Double>();
		names = new ArrayList<String>();
		details = new ArrayList<String>();
	}
	
	public void getLats(ArrayList<Double> dest) {
		for(int i = 0; i < lats.size(); ++i)
			dest.add(lats.get(i));
	}
	
	public void getLngs(ArrayList<Double> dest) {
		for(int i = 0; i < lngs.size(); ++i)
			dest.add(lngs.get(i));
	}
	
	public void getNames(ArrayList<String> dest) {
		for(int i = 0; i < lngs.size(); ++i)
			dest.add(names.get(i));
	}
	
	public void getDetails(ArrayList<String> dest) {
		for(int i = 0; i < lngs.size(); ++i)
			dest.add(details.get(i));
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
					s.useDelimiter("<br/>|<br />|&nbsp;");
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
		try {
			JSONObject reader = new JSONObject(in);
			for(int i = 0; i < 30; ++i) {
				index = Integer.toString(i);
				if(reader.has(index)) {
					objs.add(reader.getJSONObject(index));
					obj = objs.get(i);
					lats.add(obj.getDouble("lat"));
					lngs.add(obj.getDouble("lng"));
					names.add(obj.getString("name"));
					details.add(obj.getString("details"));
				}
				else
					break;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
