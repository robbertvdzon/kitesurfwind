package com.vdzon.windapp.db.callback;

import java.util.List;

import com.vdzon.windapp.pojo.SpotData;

public interface SpotsLoadedCallback {
	public void spotsLoaded(boolean fromCache, List<SpotData> spots);
}
