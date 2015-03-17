package org.becausewater.dropin;

import com.google.android.gms.maps.model.Marker;

public class Drop {

	/*
	 * Member Variables
	 */
	private double latitude, longitude;
	private String name, details, category, address, user;
	private Marker marker;
	
	/*
	 *  Constructors
	 */
	public Drop() {
		latitude = 0;
		longitude = 0;
		name = "";
		details = "";
		category = "";
		address = "";
		user = "";
	}
	
	/*
	 *  Getters
	 */
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDetails() {
		return details;
	}
	
	public String getCategory() {
		return category;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getUser() {
		return user;
	}
	
	public Marker getMarker() {
		return marker; // Will return null if marker has not been set via setMarker(Marker m)
	}
	
	/*
	 *  Setters
	 */
	public void setLatitude(double lat) {
		this.latitude = lat;
	}
	
	public void setLongitude(double lng) {
		this.longitude = lng;
	}
	
	public void setName(String n) {
		this.name = n;
	}
	
	public void setDetails(String d) {
		this.details = d;
	}
	
	public void setCategory(String c) {
		this.category = c;
	}
	
	public void setAddress(String a) {
		this.address = a;
	}
	
	public void setUser(String u) {
		this.user = u;
	}
	
	public void setMarker(Marker m) {
		this.marker = m;
	}
}
