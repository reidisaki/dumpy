package com.yoneko.areyouthereyet.update.debug;

import android.content.Context;

public class YonekoUtils {

	public static double distanceBetweenTwoPoints(
	        double lat1, double lng1, double lat2, double lng2) {
	    int r = 6371; // average radius of the earth in km
	    double dLat = Math.toRadians(lat2 - lat1);
	    double dLon = Math.toRadians(lng2 - lng1);
	    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
	       Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) 
	      * Math.sin(dLon / 2) * Math.sin(dLon / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double d = r * c;
	    return d;
	}
	///helper methods
	public static float getDensity(Context context){
		float scale = context.getResources().getDisplayMetrics().density;       
		return scale;
	}

	public static  int convertDiptoPix(int dip, Context context){
		float scale = getDensity(context);
		return (int) (dip * scale + 0.5f);
	}
	public static int convertPixtoDip(int pixel, Context context){
		float scale = getDensity(context);
		return (int)((pixel - 0.5f)/scale);
	}
}
