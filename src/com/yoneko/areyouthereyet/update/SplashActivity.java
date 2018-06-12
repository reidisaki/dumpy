package com.yoneko.areyouthereyet.update;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.widget.ImageView;

public class SplashActivity extends Activity implements OnRequestPermissionsResultCallback {

    InterstitialAd mInterstitialAd;
    /**
     * StartAppAd object declaration
     */
//    private StartAppAd startAppAd = new StartAppAd(this);

    /**
     * StartApp Native Ad declaration
     */
//    private StartAppNativeAd startAppNativeAd = new StartAppNativeAd(this);
//    private NativeAdDetails nativeAd = null;
    private ImageView imgFreeApp;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

//        initIronSource();
//        initStartApp();

        // Here, thisActivity is the current activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?

                requestPermissions(new String[]{permission.ACCESS_COARSE_LOCATION,
                                permission.ACCESS_FINE_LOCATION,
                                permission.ACCESS_NETWORK_STATE,
                                permission.READ_CONTACTS,
                                permission.SEND_SMS,
                                permission.READ_PHONE_STATE,
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

        //disable ads for now
//        requestNewInterstitial();
    }

    private void initIronSource() {
        IronSource.init(this, "729b96d5", IronSource.AD_UNIT.INTERSTITIAL);
        IronSource.setInterstitialListener(new InterstitialListener() {
            @Override
            public void onInterstitialAdReady() {

                if (IronSource.isInterstitialReady()) {
                    //show the interstitial
                    IronSource.showInterstitial();
                }
            }

            @Override
            public void onInterstitialAdLoadFailed(final IronSourceError ironSourceError) {
                int x = 9;
            }

            @Override
            public void onInterstitialAdOpened() {
                int x = 9;
            }

            @Override
            public void onInterstitialAdClosed() {

            }

            @Override
            public void onInterstitialAdShowSucceeded() {

            }

            @Override
            public void onInterstitialAdShowFailed(final IronSourceError ironSourceError) {

            }

            @Override
            public void onInterstitialAdClicked() {

            }
        });
        IronSource.loadInterstitial();
    }

    // Create the interstitial.

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
        boolean allPermissionsGranted = true;
        if (requestCode == PERMISSION_REQUEST_BLOCK_INTERNAL) {

            for (int iGranting : grantResults) {
                if (iGranting != PermissionChecker.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                startActivityNoHistory();
                requestNewInterstitial();
            } else {
                requestPermissions(new String[]{permission.ACCESS_COARSE_LOCATION,
                                permission.ACCESS_FINE_LOCATION,
                                permission.ACCESS_NETWORK_STATE,
                                permission.READ_CONTACTS,
                                permission.SEND_SMS,
                                permission.READ_PHONE_STATE,
                                permission.RECEIVE_BOOT_COMPLETED},
                        PERMISSION_REQUEST_BLOCK_INTERNAL);
            }
        }
        if (allPermissionsGranted) {
            requestNewInterstitial();
            startActivityNoHistory();
        }
    }

    protected void onResume() {
        super.onResume();

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(final int i) {
                super.onAdFailedToLoad(i);
                startActivityNoHistory();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }

            @Override
            public void onAdClosed() {
                startActivityNoHistory();
            }
        });
        requestNewInterstitial();
    }
//        IronSource.onResume(this);


    protected void onPause() {
        super.onPause();
//        IronSource.onPause(this);
    }

    /**
     * Native Ad Callback
     */
//    private AdEventListener nativeAdListener = new AdEventListener() {
//
//        @Override
//        public void onReceiveAd(Ad ad) {
//
//            // Get the native ad
//            ArrayList<NativeAdDetails> nativeAdsList = startAppNativeAd.getNativeAds();
//            if (nativeAdsList.size() > 0) {
//                nativeAd = nativeAdsList.get(0);
//            }
//
//            // Verify that an ad was retrieved
//            if (nativeAd != null) {
//
//                // When ad is received and displayed - we MUST send impression
//                nativeAd.sendImpression(SplashActivity.this);
//
//                if (imgFreeApp != null) {
//
//                    // Set button as enabled
//                    imgFreeApp.setEnabled(true);
//
//                    // Set ad's image
//                    imgFreeApp.setImageBitmap(nativeAd.getImageBitmap());
//
//                    // Set ad's title
//                }
//            }
//        }
//
//        @Override
//        public void onFailedToReceiveAd(Ad ad) {
//            int x = 9;
//        }
//    };

//    @Override
//    public void onBackPressed() {
//        StartAppAd.onBackPressed(this);
//        super.onBackPressed();
//    }

    //    AdRegistration.enableLogging(true);
//    AdRegistration.setAppKey("ebbbcbf8ca734a10aa32cffb9f2c4971");
    final InterstitialAd interstitialAd = new InterstitialAd(this);
//    private void initStartApp() {
//        StartAppSDK.init(this, "204578934", true); //TODO: Replace with your Application ID
//        /** Initialize Native Ad views **/
//        imgFreeApp = (ImageView) findViewById(R.id.imgFreeApp);
//
//        /**
//         * Load Native Ad with the following parameters:
//         * 1. Only 1 Ad
//         * 2. Download ad image automatically
//         * 3. Image size of 150x150px
//         */
//
//        StartAppAd.showAd(this);
////        startAppNativeAd.loadAd(
////                new NativeAdPreferences()
////                        .setAdsNumber(1)
////                        .setAutoBitmapDownload(true)
////                        .setPrimaryImageSize(2),
////                nativeAdListener);
//    }
}
