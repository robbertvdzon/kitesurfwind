package com.vdzon.windapp.pojo;

import java.io.Serializable;

import android.graphics.Bitmap;

public class SpotData  implements Serializable {

	private int locationDirection;
	private int siteID;
	private String title;
	private String spotBitmapLocation;

	public int getLocationDirection() {
		return locationDirection;
	}
	public void setLocationDirection(int locationDirection) {
		this.locationDirection = locationDirection;
	}
	public int getSiteID() {
		return siteID;
	}
	public void setSiteID(int siteID) {
		this.siteID = siteID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSpotBitmapLocation() {
		return spotBitmapLocation;
	}
	public void setSpotBitmapLocation(String spotBitmapLocation) {
		this.spotBitmapLocation = spotBitmapLocation;
	}

	public String toString(){
		return title+" "+siteID+" "+locationDirection;

	}
	@Override
	public boolean equals(Object o){
		SpotData sd = (SpotData)o;
		if (sd.locationDirection!=locationDirection) return false;
		if (sd.siteID!=siteID) return false;
		if (!sd.title.equals(title)) return false;
		if (!sd.spotBitmapLocation.equals(spotBitmapLocation)) return false;
		return true;
	}

}
