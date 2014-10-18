package com.yoneko.areyouthereyet.update;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.yoneko.models.SimpleGeofence;

public class DrawerItemAdapter extends ArrayAdapter<SimpleGeofence> {
	Context mContext;
	int layoutResourceId;
	List<SimpleGeofence> data = null;
	public DrawerItemAdapter(Context context, int resource) {
		super(context, resource);
	}
	public DrawerItemAdapter(Context mContext, int layoutResourceId, List<SimpleGeofence> data) {
		super(mContext, layoutResourceId, data);

		this.layoutResourceId = layoutResourceId;
		this.mContext = mContext;
		this.data = data;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if(convertView==null){
			// inflate the layout
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);
		}

		// object item based on the position
		Log.i("Reid","data size: " + data.size() + " position: " + position);
		final SimpleGeofence objectItem = data.get(position);

		// get the TextView and then set the text (item name) and tag (item ID) values
		TextView textViewItem = (TextView) convertView.findViewById(R.id.drawer_text);
		textViewItem.setText(objectItem.getTitle() + " - " + (objectItem.getTransitionType() == 1 ? "enter" : "exit") ); // this could be 4 which is enter and exit

		final CheckBox checkbox = (CheckBox)convertView.findViewById(R.id.drawer_check_box);
		checkbox.setChecked(objectItem.isChecked());
		checkbox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(((CheckBox)v).isChecked())
				{
					v.setSelected(true);
					objectItem.setChecked(true);
//					objectItem.setTitle("THIS IS FUCKED UP");
				} else {
					v.setSelected(false);
					objectItem.setChecked(false);
				}
				Log.i("Reid","Setting new title: " + objectItem.getTitle());
				data.set(position, objectItem);
			}
		});
		if(objectItem.getTitle().equals(mContext.getResources().getString(R.string.clear_all_text))) {
			checkbox.setVisibility(View.GONE);
		} else {
			checkbox.setVisibility(View.VISIBLE);
		}
		
		return convertView;

	}

}
