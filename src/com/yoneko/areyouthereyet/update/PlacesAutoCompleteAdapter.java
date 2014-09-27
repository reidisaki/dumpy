package com.yoneko.areyouthereyet.update;
import com.yoneko.areyouthereyet.update.debug.R;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.yoneko.models.Prediction;

class PlacesAutoCompleteAdapter extends ArrayAdapter<Prediction> implements Filterable {
	private ArrayList<Prediction> resultList;
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	public static final String API_KEY ="AIzaSyBc1oS-wYDEOxYiFd3sMk5kt5CZQ4SQzJs"; //places auto complete
	
	private int timeThresholdMilliseconds = 0;
	public long startTime = System.currentTimeMillis() + timeThresholdMilliseconds;
	public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	@Override
	public int getCount() {
		return resultList.size();
	}

	@Override
	public Prediction getItem(int index) {
		return resultList.get(index);
	}

	@Override
	public View getView(int p, View v, ViewGroup group) {
		if(v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.places_list_item, null);
		}
		TextView placeText = (TextView)v.findViewById(R.id.place_text);
		placeText.setText(getItem(p).getDescription());
		return v;
	}
	
	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(final CharSequence constraint) {
				final FilterResults filterResults = new FilterResults();

				
				if (constraint != null) {
					
					final Handler mHandler = new Handler();
					// Retrieve the autocomplete results.
//					Log.i("Reid","PlacesAUtoCompleteAdapter  " + constraint.toString());
//					Log.i("Reid","time passed: " + (System.currentTimeMillis()-startTime));
					if(System.currentTimeMillis()-startTime >= timeThresholdMilliseconds) {
						resultList = autocomplete(constraint.toString());
						startTime = System.currentTimeMillis();
					}
					
					
					// Assign the data to the FilterResults
					filterResults.values = resultList;
					filterResults.count = resultList.size();
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results != null && results.count > 0) {
					notifyDataSetChanged();
				}
				else {
					notifyDataSetInvalidated();
				}
			}};
			return filter;
	}

	private ArrayList<Prediction> autocomplete(String input) {
		ArrayList<Prediction> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?key=" + API_KEY);
			//	        sb.append("&components=country:uk");
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			return resultList;
		} catch (IOException e) {
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<Prediction>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				Prediction p = new Prediction(predsJsonArray.getJSONObject(i).getString("description"), predsJsonArray.getJSONObject(i).getString("place_id"));
				resultList.add(p);
			}
		} catch (JSONException e) {
		}

		return resultList;
	}
}