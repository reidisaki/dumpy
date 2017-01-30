package com.yoneko.models;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PhoneContactAdapter extends ArrayAdapter<PhoneContact>{
	Context context; 
	int layoutResourceId;    
	PhoneContact data[] = null;

	public PhoneContactAdapter(Context context, int layoutResourceId, PhoneContact[] data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		PhoneContactHolder holder = null;

		if(row == null)
		{
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new PhoneContactHolder();
			//	            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
			//	            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);

			row.setTag(holder);
		}
		else
		{
			holder = (PhoneContactHolder)row.getTag();
		}

		PhoneContact contact = data[position];
		//	        holder.txtTitle.setText(contact.title);
		//	        holder.imgIcon.setImageResource(contact.icon);

		return row;
	}

	static class PhoneContactHolder
	{
		ImageView imgIcon;
		TextView txtTitle;
	}
}

