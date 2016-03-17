package com.yoneko.areyouthereyet.update;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.InterstitialAd;

public class SplashActivity extends Activity {
@Override
protected void onCreate(Bundle savedInstanceState) {	
	super.onCreate(savedInstanceState);
	// Create the interstitial.
    final InterstitialAd interstitialAd = new InterstitialAd(this);
    AdRegistration.setAppKey("ebbbcbf8ca734a10aa32cffb9f2c4971");
    AdRegistration.enableLogging(true);
    
    // Set the listener to use the callbacks below.
    interstitialAd.setListener(new AdListener() {
        @Override
        public void onAdLoaded(final Ad ad, final AdProperties adProperties) {
            interstitialAd.showAd();
        }

        @Override
        public void onAdFailedToLoad(final Ad ad, final AdError adError) {
            Log.i("mc", "ad failed: " + adError.getMessage());                
        }

        @Override
        public void onAdExpanded(final Ad ad) {

        }

        @Override
        public void onAdCollapsed(final Ad ad) {

        }

        @Override
        public void onAdDismissed(final Ad ad) {
        	Intent i = new Intent(SplashActivity.this, MapActivity.class);        	
        	startActivity(i);
        }
    });

    interstitialAd.loadAd();
    
    
}
}
