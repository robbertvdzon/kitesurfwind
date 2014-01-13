package com.vdzon.windapp.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * this pojo is used to get filled by GSon
 */
public class SpotsResult implements Serializable{
	private List<SpotData> spots;

	public List<SpotData> getSpots() {
		return spots;
	}

	public void setSpots(List<SpotData> spots) {
		this.spots = spots;
	}


}
