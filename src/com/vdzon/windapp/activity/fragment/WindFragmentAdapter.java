package com.vdzon.windapp.activity.fragment;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vdzon.windapp.pojo.SpotData;
import com.vdzon.windapp.util.Util;

public class WindFragmentAdapter extends FragmentPagerAdapter {
	private List<SpotData> mWindSpots;
	HashMap<Integer, WindFragment> mFragments =new HashMap<Integer, WindFragment>();

	public void setSpots(List<SpotData> _windSpots){
		mWindSpots = _windSpots;
		mFragments = new HashMap<Integer, WindFragment>();// reset the fragments, load them again
		notifyDataSetChanged();
	}

	public WindFragmentAdapter(FragmentManager fm, List<SpotData> _windSpots) {
		super(fm);
		setSpots(_windSpots);
	}

	@Override
	public Fragment getItem(int position) {
		WindFragment fragment = mFragments.get(new Integer(position));
		if (fragment==null){
			int day = Util.calculateDay(new Date());// today
			fragment = WindFragment.newInstance(mWindSpots.get(position % mWindSpots.size()), day);
			mFragments.put(new Integer(position), fragment);
		}
		return fragment;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mWindSpots.get(position % mWindSpots.size()).getTitle();
	}

	@Override
	public int getCount() {
		return mWindSpots.size();
	}
}