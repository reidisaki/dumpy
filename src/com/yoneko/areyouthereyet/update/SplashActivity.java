package com.yoneko.areyouthereyet.update;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import static java.security.AccessController.getContext;

public class SplashActivity extends Activity {

    InterstitialAd mInterstitialAd;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        // Here, thisActivity is the current activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                    permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                    permission.ACCESS_NETWORK_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?

                requestPermissions(new String[]{permission.ACCESS_COARSE_LOCATION,
                                permission.ACCESS_FINE_LOCATION,
                                permission.ACCESS_NETWORK_STATE,
                                permission.READ_CONTACTS,
                                permission.SEND_SMS,
                                permission.RECEIVE_BOOT_COMPLETED},
                        PERMISSION_REQUEST_BLOCK_INTERNAL);
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
//        requestNewInterstitial();
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

    public void startActivityNoHistory() {
        Log.i("ty", "starting map activity perm result");
        Intent i = new Intent(SplashActivity.this, MapActivity.class);
        startActivity(i);
    }

    private static final int PERMISSION_REQUEST_BLOCK_INTERNAL = 555;
    private static final String PERMISSION_SHARED_PREFERENCES = "permissions";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_BLOCK_INTERNAL) {
            boolean allPermissionsGranted = true;

            for (int iGranting : grantResults) {
                if (iGranting != PermissionChecker.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                requestNewInterstitial();
            } else {
                requestPermissions(new String[]{permission.ACCESS_COARSE_LOCATION,
                                permission.ACCESS_FINE_LOCATION,
                                permission.ACCESS_NETWORK_STATE,
                                permission.READ_CONTACTS,
                                permission.SEND_SMS,
                                permission.RECEIVE_BOOT_COMPLETED},
                        1);
            }
        }
    }
}
