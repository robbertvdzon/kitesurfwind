package com.vdzon.windapp.pojo;

import java.util.ArrayList;

/**
 * this pojo is used to get filled by GSon
 */
public class WindResult {
	private ArrayList<WindData> values;

	public ArrayList<WindData> getValues() {
		return values;
	}
	public void setValues(ArrayList<WindData> values) {
		this.values = values;
	}

}
