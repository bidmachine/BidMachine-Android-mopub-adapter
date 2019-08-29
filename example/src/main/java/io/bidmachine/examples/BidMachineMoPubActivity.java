package io.bidmachine.examples;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.BidMachineAdapterConfiguration;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideos;
import com.mopub.mobileads.MoPubView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BidMachineMoPubActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final String AD_UNIT_ID = "417666f425234aedb2bb786486bca869";
    private static final String BANNER_KEY = "417666f425234aedb2bb786486bca869";
    private static final String INTERSTITIAL_KEY = "d91dbddf7eeb402596c33a3a96764b90";
    private static final String REWARDED_KEY = "8325aa1038424e2a8af2b4a121abf29e";

    private Button btnLoadBanner;
    private Button btnShowBanner;
    private Button btnLoadInterstitial;
    private Button btnShowInterstitial;
    private Button btnLoadRewardedVideo;
    private Button btnShowRewardedVideo;
    private FrameLayout bannerContainer;

    private MoPubView moPubView;
    private MoPubInterstitial moPubInterstitial;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bannerContainer = findViewById(R.id.banner_container);
        btnLoadBanner = findViewById(R.id.load_banner);
        btnLoadBanner.setOnClickListener(v -> loadBanner());
        btnShowBanner = findViewById(R.id.show_banner);
        btnShowBanner.setOnClickListener(v -> showBanner());
        btnLoadInterstitial = findViewById(R.id.load_interstitial);
        btnLoadInterstitial.setOnClickListener(v -> loadInterstitial());
        btnShowInterstitial = findViewById(R.id.show_interstitial);
        btnShowInterstitial.setOnClickListener(v -> showInterstitial());
        btnLoadRewardedVideo = findViewById(R.id.load_rvideo);
        btnLoadRewardedVideo.setOnClickListener(v -> loadRewardedVideo());
        btnShowRewardedVideo = findViewById(R.id.show_rvideo);
        btnShowRewardedVideo.setOnClickListener(v -> showRewardedVideo());
        findViewById(R.id.btn_initialize)
                .setOnClickListener(v -> initialize());
        findViewById(R.id.show_fetch_activity)
                .setOnClickListener(v -> startActivity(new Intent(v.getContext(),
                                                                  BidMachineMoPubFetchActivity.class)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        destroyBanner();
        destroyInterstitial();
    }

    /**
     * Initialize MoPub SDK with BidMachineAdapterConfiguration
     */
    private void initialize() {
        //Check initialized MoPub or not
        if (!MoPub.isSdkInitialized()) {
            Log.d(TAG, "MoPub initialize");

            //Set DEBUG log from MoPub
            MoPubLog.setLogLevel(MoPubLog.LogLevel.DEBUG);

            //Prepare configuration map for BidMachineAdapterConfiguration
            Map<String, String> configuration = new HashMap<>();
            configuration.put("seller_id", "1");
            configuration.put("coppa", "true");
            configuration.put("logging_enabled", "true");
            configuration.put("test_mode", "true");

            //Prepare SdkConfiguration for initialize MoPub with BidMachineAdapterConfiguration
            SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(AD_UNIT_ID)
                    .withAdditionalNetwork(BidMachineAdapterConfiguration.class.getName())
                    .withMediatedNetworkConfiguration(
                            BidMachineAdapterConfiguration.class.getName(),
                            configuration)
                    .build();

            //Initialize MoPub SDK
            MoPub.initializeSdk(this, sdkConfiguration, new InitializationListener());
        } else {
            enableButton();
        }
    }

    /**
     * Enable buttons for user interaction
     */
    private void enableButton() {
        btnLoadBanner.setEnabled(true);
        btnShowBanner.setEnabled(true);
        btnLoadInterstitial.setEnabled(true);
        btnShowInterstitial.setEnabled(true);
        btnLoadRewardedVideo.setEnabled(true);
        btnShowRewardedVideo.setEnabled(true);
    }

    /**
     * Method for load banner from MoPub
     */
    private void loadBanner() {
        //Destroy previous MoPubView
        destroyBanner();

        Log.d(TAG, "MoPubView loadBanner");

        //Prepare localExtras for set to MoPubView
        Map<String, Object> localExtras = new HashMap<>();
        localExtras.put("banner_width", 320);

        //Create new MoPubView instance and load
        moPubView = new MoPubView(this);
        moPubView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        moPubView.setLocalExtras(localExtras);
        moPubView.setAutorefreshEnabled(false);
        moPubView.setAdUnitId(BANNER_KEY);
        moPubView.setBannerAdListener(new BannerViewListener());
        moPubView.setVisibility(View.GONE);

        bannerContainer.addView(moPubView);
        moPubView.loadAd();
    }

    /**
     * Method for show banner from MoPub
     */
    private void showBanner() {
        if (moPubView != null) {
            Log.d(TAG, "MoPubView showBanner");

            //Change MoPubView visibility
            moPubView.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "MoPubView null, load banner first");
        }
    }

    /**
     * Method for destroy MoPubView
     */
    private void destroyBanner() {
        if (moPubView != null) {
            Log.d(TAG, "MoPubView destroyBanner");

            bannerContainer.removeAllViews();
            moPubView.setBannerAdListener(null);
            moPubView.destroy();
        }
    }

    /**
     * Method for load interstitial from MoPub
     */
    private void loadInterstitial() {
        //Destroy previous MoPubInterstitial
        destroyInterstitial();

        Log.d(TAG, "MoPubInterstitial loadInterstitial");

        //Prepare localExtras for set to MoPubInterstitial
        Map<String, Object> localExtras = new HashMap<>();
        localExtras.put("ad_content_type", "All");

        //Create new MoPubInterstitial instance and load
        moPubInterstitial = new MoPubInterstitial(this, INTERSTITIAL_KEY);
        moPubInterstitial.setLocalExtras(localExtras);
        moPubInterstitial.setInterstitialAdListener(new InterstitialListener());
        moPubInterstitial.load();
    }

    /**
     * Method for show interstitial from MoPub
     */
    private void showInterstitial() {
        if (moPubInterstitial != null && moPubInterstitial.isReady()) {
            Log.d(TAG, "MoPubInterstitial showInterstitial");

            moPubInterstitial.show();
        } else {
            Log.d(TAG, "MoPubInterstitial null, load interstitial first");
        }
    }

    /**
     * Method for destroy MoPubInterstitial
     */
    private void destroyInterstitial() {
        if (moPubInterstitial != null) {
            Log.d(TAG, "MoPubInterstitial destroyInterstitial");

            moPubInterstitial.setInterstitialAdListener(null);
            moPubInterstitial.destroy();
        }
    }

    /**
     * Method for load rewarded video from MoPub
     */
    private void loadRewardedVideo() {
        Log.d(TAG, "MoPubRewardedVideos loadRewardedVideo");

        MoPubRewardedVideos.setRewardedVideoListener(new RewardedVideoListener());
        MoPubRewardedVideos.loadRewardedVideo(REWARDED_KEY);
    }

    /**
     * Method for show rewarded video from MoPub
     */
    private void showRewardedVideo() {
        if (MoPubRewardedVideos.hasRewardedVideo(REWARDED_KEY)) {
            Log.d(TAG, "MoPubRewardedVideos showRewardedVideo");

            MoPubRewardedVideos.showRewardedVideo(REWARDED_KEY);
        } else {
            Log.d(TAG, "RewardedVideo not loaded");
        }
    }

    /**
     * Class for definition behavior after initialize finished
     */
    private class InitializationListener implements SdkInitializationListener {

        @Override
        public void onInitializationFinished() {
            Log.d(TAG, "MoPub onInitializationFinished");

            enableButton();
        }

    }

    /**
     * Class for definition behavior MoPubView
     */
    private class BannerViewListener implements MoPubView.BannerAdListener {

        @Override
        public void onBannerLoaded(MoPubView banner) {
            Log.d(TAG, "MoPubView onBannerLoaded");
            Toast.makeText(
                    BidMachineMoPubActivity.this,
                    "BannerLoaded",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
            Log.d(TAG,
                  "MoPubView onBannerFailed with errorCode - " + errorCode.getIntCode() + " (" + errorCode
                          .toString() + ")");
            Toast.makeText(
                    BidMachineMoPubActivity.this,
                    "BannerFailedToLoad",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBannerClicked(MoPubView banner) {
            Log.d(TAG, "MoPubView onBannerClicked");
        }

        @Override
        public void onBannerExpanded(MoPubView banner) {
            Log.d(TAG, "MoPubView onBannerExpanded");
        }

        @Override
        public void onBannerCollapsed(MoPubView banner) {
            Log.d(TAG, "MoPubView onBannerCollapsed");
        }

    }

    /**
     * Class for definition behavior MoPubInterstitial
     */
    private class InterstitialListener implements MoPubInterstitial.InterstitialAdListener {

        @Override
        public void onInterstitialLoaded(MoPubInterstitial interstitial) {
            Log.d(TAG, "MoPubInterstitial onInterstitialLoaded");
            Toast.makeText(
                    BidMachineMoPubActivity.this,
                    "InterstitialLoaded",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
            Log.d(TAG,
                  "MoPubInterstitial onInterstitialFailed with errorCode - " + errorCode.getIntCode() + " (" + errorCode
                          .toString() + ")");
            Toast.makeText(
                    BidMachineMoPubActivity.this,
                    "InterstitialFailedToLoad",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onInterstitialShown(MoPubInterstitial interstitial) {
            Log.d(TAG, "MoPubInterstitial onInterstitialShown");
        }

        @Override
        public void onInterstitialClicked(MoPubInterstitial interstitial) {
            Log.d(TAG, "MoPubInterstitial onInterstitialClicked");
        }

        @Override
        public void onInterstitialDismissed(MoPubInterstitial interstitial) {
            Log.d(TAG, "MoPubInterstitial onInterstitialDismissed");
        }

    }

    /**
     * Class for definition behavior MoPubRewardedVideos
     */
    private class RewardedVideoListener implements MoPubRewardedVideoListener {

        @Override
        public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
            Log.d(TAG, "MoPubRewardedVideos onRewardedVideoLoadSuccess");
            Toast.makeText(
                    BidMachineMoPubActivity.this,
                    "RewardedVideoLoaded",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRewardedVideoLoadFailure(@NonNull String adUnitId,
                                               @NonNull MoPubErrorCode errorCode) {
            Log.d(TAG,
                  "MoPubRewardedVideos onRewardedVideoLoadFailure with errorCode - " + errorCode.getIntCode() + " (" + errorCode
                          .toString() + ")");
            Toast.makeText(
                    BidMachineMoPubActivity.this,
                    "RewardedVideoFailedToLoad",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRewardedVideoStarted(@NonNull String adUnitId) {
            Log.d(TAG, "MoPubRewardedVideos onRewardedVideoStarted");
        }

        @Override
        public void onRewardedVideoPlaybackError(@NonNull String adUnitId,
                                                 @NonNull MoPubErrorCode errorCode) {
            Log.d(TAG,
                  "MoPubRewardedVideos onRewardedVideoPlaybackError with errorCode - " + errorCode.getIntCode() + " (" + errorCode
                          .toString() + ")");
        }

        @Override
        public void onRewardedVideoClicked(@NonNull String adUnitId) {
            Log.d(TAG, "MoPubRewardedVideos onRewardedVideoClicked");
        }

        @Override
        public void onRewardedVideoClosed(@NonNull String adUnitId) {
            Log.d(TAG, "MoPubRewardedVideos onRewardedVideoClosed");
        }

        @Override
        public void onRewardedVideoCompleted(@NonNull Set<String> adUnitIds,
                                             @NonNull MoPubReward reward) {
            Log.d(TAG, "MoPubRewardedVideos onRewardedVideoCompleted");
        }

    }

}