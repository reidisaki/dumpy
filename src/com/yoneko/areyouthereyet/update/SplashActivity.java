package com.yoneko.areyouthereyet.update;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class SplashActivity extends Activity {

    InterstitialAd mInterstitialAd;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        // Here, thisActivity is the current activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED){

                // Should we show an explanation?

                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_NETWORK_STATE,
                                android.Manifest.permission.READ_CONTACTS,
                                android.Manifest.permission.READ_PHONE_STATE,
                                android.Manifest.permission.SEND_SMS,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.RECEIVE_BOOT_COMPLETED},
                        1);
            } else {
                Log.i("ty", "starting map activity already have permissions");
                startActivityNoHistory();
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Log.i("ty", "starting map activity less than M");
            startActivityNoHistory();
        }
        /*
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(final int errorCode) {
                super.onAdFailedToLoad(errorCode);
            	Intent i = new Intent(SplashActivity.this, MapActivity.class);
            	startActivity(i);
            }

            @Override
            public void onAdClosed() {
            	Intent i = new Intent(SplashActivity.this, MapActivity.class);
            	startActivity(i);
            }
        });

        //disable ads for now
        requestNewInterstitial();
        */
    }

    // Create the interstitial.
//    final InterstitialAd interstitialAd = new InterstitialAd(this);
//    AdRegistration.setAppKey("ebbbcbf8ca734a10aa32cffb9f2c4971");
//    AdRegistration.enableLogging(true);

    // Set the listener to use the callbacks below.
//    interstitialAd.setListener(new AdListener() {
//        @Override
//        public void onAdLoaded(final Ad ad, final AdProperties adProperties) {
//            interstitialAd.showAd();
//        }
//
//        @Override
//        public void onAdFailedToLoad(final Ad ad, final AdError adError) {
//            Log.i("mc", "ad failed: " + adError.getMessage());                
//        }
//
//        @Override
//        public void onAdExpanded(final Ad ad) {
//
//        }
//
//        @Override
//        public void onAdCollapsed(final Ad ad) {
//
//        }
//
//        @Override
//        public void onAdDismissed(final Ad ad) {
//        	Intent i = new Intent(SplashActivity.this, MapActivity.class);        	
//        	startActivity(i);
//        }
//    });
//
//    interstitialAd.loadAd();
//    
//    
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("ty", "starting map activity perm result");
        startActivityNoHistory();
    }

    public void startActivityNoHistory() {
        Log.i("ty", "starting map activity perm result");
        Intent i = new Intent(SplashActivity.this, MapActivity.class);
        startActivity(i);
    }
}
