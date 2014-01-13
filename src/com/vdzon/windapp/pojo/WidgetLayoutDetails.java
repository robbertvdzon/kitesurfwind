package com.vdzon.windapp.pojo;

/**
 * Created by robbert on 1-12-13.
 */
public class WidgetLayoutDetails {
	public int widgetHeight = 400;
	public int widgetWidth = 400;
	public int textPos = 10;

	public int arrowImageHeight = 400;
	public int spotImageHeight = 400;
	public int yOffsetImage = 0;
	public int xOffsetImage = 0;
	public int statusImageHeight = 150;
	public int textHeight = 70;

	public int timeTextLeft = 310;
	public int timeTextTop = 85;


	public int windTextLeft = 410;
	public int windTextTop = 120+85;

	public int angleTextLeft = 410;
	public int angleTextTop = 120+120+85;

	public int windSpeedCircelX = 150/2+10;
	public int windSpeedCircelY = 150/2+10;
	public int windSpeedCircelDiameter = 150;
	public int strokeWidth = 10;

	public int windSpeedCircelTextLeftBelow10 = 60;
	public int windSpeedCircelTextLeftMoreThen10 = 35;
	public int windSpeedCircelTextTop = 115;


	public int adviceStatusIconLeft = widgetHeight-statusImageHeight-10;
	public int adviceStatusIconTop = 10;

	public boolean drawTimeline = true;
	public boolean drawGraph = true;
	public boolean drawImage = true;
	public boolean drawImageText = true;
	public boolean drawWhiteBackground = false;
	public boolean drawWindInCorner = false;



}
