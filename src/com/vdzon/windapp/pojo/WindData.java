package com.vdzon.windapp.pojo;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * this pojo is used to get filled by GSon
 * The serialized names are are compressed to decrease the data to be transmitted from the web call 
 */
public class WindData implements Serializable{

	@SerializedName("w") private float wind;
	@SerializedName("g") private float gust;
	@SerializedName("a") private int angle;
	@SerializedName("d") private int day;
	@SerializedName("h2") private int endHour;
	@SerializedName("m2") private int endMinute;
	@SerializedName("h1") private int startHour;
	@SerializedName("m1") private int startMinute;
	@SerializedName("t") private long lastModified;
	@SerializedName("s") private int spotID;

	public long getLastModified() {
		return lastModified;
	}
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public int getSpotID() {
		return spotID;
	}
	public void setSpotID(int spotID) {
		this.spotID = spotID;
	}
	public float getWind() {
		return wind;
	}
	public void setWind(float wind) {
		this.wind = wind;
	}
	public float getGust() {
		return gust;
	}
	public void setGust(float gust) {
		this.gust = gust;
	}
	public int getAngle() {
		return angle;
	}
	public void setAngle(int angle) {
		this.angle = angle;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getEndHour() {
		return endHour;
	}
	public void setendHour(int hour) {
		this.endHour = hour;
	}
	public int getEndMinute() {
		return endMinute;
	}
	public void setEndMinute(int minute) {
		this.endMinute = minute;
	}
	public int getStartHour() {
		return startHour;
	}
	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}
	public int getStartMinute() {
		return startMinute;
	}
	public void setStartMinute(int startMinute) {
		this.startMinute = startMinute;
	}

	public String getTimeStr(){
		return getStartTimeStr()+"-"+getEndTimeStr();
	}

	public String getStartTimeStr(){
		String time = startHour+":"+startMinute;
		if (startMinute==0) time+="0";
		if (startHour<10) time = "0"+time;
		return time;
	}

	public String getEndTimeStr(){
		String time = endHour+":"+endMinute;
		if (endMinute==0) time+="0";
		if (endHour<10) time = "0"+time;
		return time;
	}

	public float getStartTime(){
		return startHour+(float)startMinute/60;
	}

	public float getEndTime(){
		return endHour+(float)endMinute/60;
	}


	public String toString(){
		return "spotID="+spotID+" day="+day+" "+getTimeStr() +" wind="+wind+" gust="+gust+" angle="+angle ;
	}

}
