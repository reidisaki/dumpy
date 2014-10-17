package com.yoneko.areyouthereyet.update;

import java.util.List;

import com.yoneko.areyouthereyet.update.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yoneko.models.SimpleGeofence;

public class GeofenceAdapter extends ArrayAdapter<SimpleGeofence> {

	Context mContext;
	int layoutResourceId;
	List<SimpleGeofence> data;
	public GeofenceAdapter(Context mContext, int layoutResourceId, List<SimpleGeofence> data) {

		super(mContext, layoutResourceId, data);

		this.layoutResourceId = layoutResourceId;
		this.mContext = mContext;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		
		if(convertView==null){
			// inflate the layout
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);
		}
		if(data.size() > 0 ) {
			SimpleGeofence objectItem = data.get(position);

			TextView textViewItem = (TextView) convertView.findViewById(R.id.geofence_name);
			TextView nicknameViewItem = (TextView) convertView.findViewById(R.id.headline_text);
			nicknameViewItem.setText(String.valueOf(objectItem.getTitle()));
			textViewItem.setText(String.valueOf(objectItem.getLatitude()));
		}
		return convertView;
	}
}