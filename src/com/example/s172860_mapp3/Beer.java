package com.example.s172860_mapp3;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;

/**
 * Classic java representation of a Beer object with implementet getters and setters
 * @author audunlarsen
 *
 */


public class Beer {
	
	String TAG = "CLASS: BEER";
	private String name;
	private String adress;
	private String price;
	private String close_indoor;
	private String close_outdoor;
	private Double lon;
	private Double lat;
	private LatLng latlong;
	private Boolean erUpdate;
	
	public Beer(){
		
		this.erUpdate = false;
		
	}
	
	Context context;
	
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	

	public String getAdress() {
		return this.adress;
	}
	
	public void setAdress(String adress){
		this.adress = adress;
	}
	
	public String getCloseIndoor() {
		return this.close_indoor;
	}
	
	public void setCloseIndoor(String close_indoor){
		this.close_indoor = close_indoor;
	}
	
	public String getCloseoutdoor() {
		return this.close_outdoor;
	}
	
	public void setCloseOutdoor(String close_outdoor){
		this.close_outdoor = close_outdoor;
	}
	
	public String getPrice() {
		return this.price;
	}
	
	public void setPrice(String price){
		this.price = price;
	}
	
	public Double getLon() {
		return this.lon;
		
	}
	
	public void setLon(Double lon){
		this.lon = lon;
	}
	
	public Double getLat() {
		return this.lat;
		
	}
	
	public void setLat(Double lat){
		this.lat = lat;
		this.erUpdate = true;
	}
	
	public void setLatLong(LatLng latlong){
	this.latlong = latlong;
	}
	
	public LatLng getLatLong() {
		return this.latlong;
	}
	
	public Boolean geterUpdate() {
		return this.erUpdate;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
