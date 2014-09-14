package com.yoneko.areyouthereyet.update;

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

import com.google.android.gms.drive.query.Filters;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	private ArrayList<String> resultList;
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	private static final String API_KEY ="AIzaSyBc1oS-wYDEOxYiFd3sMk5kt5CZQ4SQzJs"; //places auto complete
	
	private int timeThresholdMilliseconds = 200;
	public long startTime = System.currentTimeMillis() + timeThresholdMilliseconds;
	private Runnable myTask;
	public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	@Override
	public int getCount() {
		return resultList.size();
	}

	@Override
	public String getItem(int index) {
		return resultList.get(index);
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
					Log.i("Reid","time passed: " + (System.currentTimeMillis()-startTime));
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

	private ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

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
			Log.i("Reid","Error processing Places API URL" + e);
			return resultList;
		} catch (IOException e) {
			Log.i("Reid","Error connecting to Places API" + e);
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
			resultList = new ArrayList<String>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
			}
		} catch (JSONException e) {
			Log.i("Reid","Cannot process JSON results" +e);
		}

		return resultList;
	}
}