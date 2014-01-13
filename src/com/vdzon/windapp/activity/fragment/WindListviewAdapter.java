package com.vdzon.windapp.activity.fragment;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vdzon.windapp.R;
import com.vdzon.windapp.activity.listener.SwipeListener;
import com.vdzon.windapp.pojo.SpotWindData;
import com.vdzon.windapp.pojo.WindData;
import com.vdzon.windapp.view.WindGraphView;
import com.vdzon.windapp.view.WindImageView;

/**
 * The listview contains the wind statistics
 * The first 2 views are the spotImage and the graph
 * The third contains the titles of the remaining rows 
 */
public class WindListviewAdapter extends BaseAdapter {
	private static final int TYPE_IMAGE = 0;
	private static final int TYPE_GRAPH = 1;
	private static final int TYPE_LISTITEM = 2;
	private static final int TYPE_MAX_COUNT = 3;
	private List<WindData> mData = new LinkedList<WindData>();
	private LayoutInflater mInflater;
	private SpotWindData mSpotWindData; 
	private WindFragment mWindFragment;


	public WindListviewAdapter(Context context, WindFragment mWindFragment) {
		this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mWindFragment = mWindFragment;
	}

	public void init(SpotWindData spotWindData){
		this.mSpotWindData = spotWindData;
	}

	public void setData(List<WindData> data){
		mData = data;
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		if (position==0) return TYPE_IMAGE;
		if (position==1) return TYPE_GRAPH;
		return TYPE_LISTITEM;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	@Override
	public int getCount() {
		if (mData.size()==0) return 2; // when there is now statistics data, only show the spotImage and the (empty)graph
		return mData.size()+3;// first row is the image, second is the graph, third are the titles
	}

	@Override
	public Object getItem(int position) {
		if (position==0) return null;
		if (position==1) return null;
		int index = mData.size()-position+2;// in reverse order but with regards to the fact that the first 3 rows contain other information 
		return mData.get(index);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int type = getItemViewType(position);
		// first populate the holder
		if (convertView == null) {
			holder = new ViewHolder();
			switch (type) {
			case TYPE_IMAGE:
				convertView = mInflater.inflate(R.layout.listview_image, null);
				holder.windImageView = (WindImageView)convertView.findViewById(R.id.list_item_image);
				// store a reference of the view to the fragment. There is only ony instance of the windImageView in the list
				mWindFragment.setImageWindView(holder.windImageView);
				break;
			case TYPE_GRAPH:
				convertView = mInflater.inflate(R.layout.listview_graph, null);
				holder.windGraphView = (WindGraphView)convertView.findViewById(R.id.list_item_windview);
				// store a reference of the view to the fragment. There is only ony instance of the windGraphView in the list
				mWindFragment.setGraphWindView(holder.windGraphView);
				break;
			case TYPE_LISTITEM:
				convertView = mInflater.inflate(R.layout.listview_listitem, null);
				holder.textView = (TextView)convertView.findViewById(R.id.list_item);
				holder.textView2= (TextView)convertView.findViewById(R.id.list_item2);
				holder.textView3= (TextView)convertView.findViewById(R.id.list_item3);
				break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}

		// update the holder with new data
		switch (type) {
		case TYPE_IMAGE:
			holder.windImageView.init(mSpotWindData);
			mWindFragment.setImageWindView(holder.windImageView);
			break;
		case TYPE_GRAPH:
			mWindFragment.setGraphWindView(holder.windGraphView);
			holder.windGraphView.init(mSpotWindData);
			holder.windGraphView.setOnTouchListener(new OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						mWindFragment.setGraphWindView((WindGraphView)v);
						Activity activity = mWindFragment.getActivity();
						// when the activity implemented the SwipeListener, then disable swipe. On the windGraphView we do not want to be able to swipe
						if (activity instanceof SwipeListener){
							SwipeListener swipeListener = (SwipeListener)activity;
							swipeListener.disableSwipe();
						}
						break;
					} // end switch

					return true;
				}
			});						
			break;
		case TYPE_LISTITEM:
			WindData windData = null;
			if (position==2){
				// position 2 is the titles row
				if (mData.size()==0){
					holder.textView.setText("");
					holder.textView2.setText("");
					holder.textView3.setText("");
				}
				else{
					holder.textView.setText(R.string.windlistviewadapter_title_time);
					holder.textView2.setText(R.string.windlistviewadapter_title_wind);
					holder.textView3.setText(R.string.windlistviewadapter_title_gust);
				}
				break;
			}

			windData = (WindData)getItem(position);
			if (windData!=null && holder!=null && holder.textView!=null){
				String datetime = windData.getTimeStr();
				holder.textView.setText(datetime);
				holder.textView2.setText(""+windData.getWind());
				holder.textView3.setText(""+windData.getGust());
			}
			break;
		}
		return convertView;
	}


	static class ViewHolder {
		public TextView textView;
		public TextView textView2;
		public TextView textView3;
		public WindGraphView windGraphView;
		public WindImageView windImageView;

	}	

}