package io.bidmachine.examples;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mopub.common.MediationSettings;
import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.BidMachineAdapterConfiguration;
import com.mopub.mobileads.BidMachineMediationSettings;
import com.mopub.mobileads.BidMachineUtils;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideos;
import com.mopub.mobileads.MoPubView;
import com.mopub.nativeads.AdapterHelper;
import com.mopub.nativeads.BidMachineNativeRendered;
import com.mopub.nativeads.BidMachineViewBinder;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BidMachineMoPubActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final String BID_MACHINE_SELLER_ID = "5";
    private static final String AD_UNIT_ID = "417666f425234aedb2bb786486bca869";
    private static final String BANNER_KEY = "417666f425234aedb2bb786486bca869";
    private static final String INTERSTITIAL_KEY = "d91dbddf7eeb402596c33a3a96764b90";
    private static final String REWARDED_KEY = "8325aa1038424e2a8af2b4a121abf29e";
    private static final String NATIVE_KEY = "f1dd482e0eb34806a2a5ec995fe95c9c";

    private Button bInitialize;
    private Button bLoadBanner;
    private Button bShowBanner;
    private Button bLoadInterstitial;
    private Button bShowInterstitial;
    private Button bLoadRewardedVideo;
    private Button bShowRewardedVideo;
    private Button bLoadNative;
    private Button bShowNative;
    private FrameLayout adContainer;

    private MoPubView moPubView;
    private MoPubInterstitial moPubInterstitial;
    private MoPubNative moPubNative;
    private NativeAd nativeAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bInitialize = findViewById(R.id.bInitialize);
        bInitialize.setOnClickListener(v -> initialize());
        bLoadBanner = findViewById(R.id.bLoadBanner);
        bLoadBanner.setOnClickListener(v -> loadBanner());
        bShowBanner = findViewById(R.id.bShowBanner);
        bShowBanner.setOnClickListener(v -> showBanner());
        bLoadInterstitial = findViewById(R.id.bLoadInterstitial);
        bLoadInterstitial.setOnClickListener(v -> loadInterstitial());
        bShowInterstitial = findViewById(R.id.bShowInterstitial);
        bShowInterstitial.setOnClickListener(v -> showInterstitial());
        bLoadRewardedVideo = findViewById(R.id.bLoadRewarded);
        bLoadRewardedVideo.setOnClickListener(v -> loadRewardedVideo());
        bShowRewardedVideo = findViewById(R.id.bShowRewarded);
        bShowRewardedVideo.setOnClickListener(v -> showRewardedVideo());
        bLoadNative = findViewById(R.id.bLoadNative);
        bLoadNative.setOnClickListener(v -> loadNative());
        bShowNative = findViewById(R.id.bShowNative);
        bShowNative.setOnClickListener(v -> showNative());

        adContainer = findViewById(R.id.adContainer);

        if (MoPub.isSdkInitialized()) {
            bInitialize.setEnabled(false);
            enableLoadButton();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        destroyBanner();
        destroyInterstitial();
        destroyNative();
    }

    /**
     * Initialize MoPub SDK with BidMachineAdapterConfiguration
     */
    private void initialize() {
        Log.d(TAG, "initialize");

        // Prepare configuration map for BidMachineAdapterConfiguration
        Map<String, String> configuration = new HashMap<>();
        configuration.put(BidMachineUtils.SELLER_ID, BID_MACHINE_SELLER_ID);
        configuration.put(BidMachineUtils.COPPA, "true");
        configuration.put(BidMachineUtils.LOGGING_ENABLED, "true");
        configuration.put(BidMachineUtils.TEST_MODE, "true");

        // Prepare SdkConfiguration for initialize MoPub with BidMachineAdapterConfiguration
        SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(AD_UNIT_ID)
                .withLogLevel(MoPubLog.LogLevel.DEBUG)
                .withAdditionalNetwork(BidMachineAdapterConfiguration.class.getName())
                .withMediatedNetworkConfiguration(BidMachineAdapterConfiguration.class.getName(),
                                                  configuration)
                .build();

        // Initialize MoPub SDK
        MoPub.initializeSdk(this, sdkConfiguration, new InitializationListener());
    }

    /**
     * Enable buttons for user interaction
     */
    private void enableLoadButton() {
        bLoadBanner.setEnabled(true);
        bLoadInterstitial.setEnabled(true);
        bLoadRewardedVideo.setEnabled(true);
        bLoadNative.setEnabled(true);
    }

    private void addAdView(View view) {
        adContainer.removeAllViews();
        adContainer.addView(view);
    }

    /**
     * Method for load MoPubView
     */
    private void loadBanner() {
        Log.d(TAG, "loadBanner");

        bShowBanner.setEnabled(false);

        // Destroy previous MoPubView
        destroyBanner();

        // Prepare localExtras for set to MoPubView
        Map<String, Object> localExtras = new HashMap<>();
        localExtras.put(BidMachineUtils.BANNER_WIDTH, 320);

        // Create new MoPubView instance and load
        moPubView = new MoPubView(this);
        moPubView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                             ViewGroup.LayoutParams.MATCH_PARENT));
        moPubView.setLocalExtras(localExtras);
        moPubView.setAutorefreshEnabled(false);
        moPubView.setAdUnitId(BANNER_KEY);
        moPubView.setBannerAdListener(new BannerViewListener());
        moPubView.loadAd();
    }

    /**
     * Method for show MoPubView
     */
    private void showBanner() {
        Log.d(TAG, "showBanner");

        bShowBanner.setEnabled(false);

        if (moPubView != null) {
            addAdView(moPubView);
        } else {
            Log.d(TAG, "show error - banner object is null");
        }
    }

    /**
     * Method for destroy MoPubView
     */
    private void destroyBanner() {
        Log.d(TAG, "destroyBanner");

        adContainer.removeAllViews();
        if (moPubView != null) {
            moPubView.setBannerAdListener(null);
            moPubView.destroy();
        }
    }

    /**
     * Method for load MoPubInterstitial
     */
    private void loadInterstitial() {
        Log.d(TAG, "loadInterstitial");

        bShowInterstitial.setEnabled(false);

        // Destroy previous MoPubInterstitial
        destroyInterstitial();

        // Prepare localExtras for set to MoPubInterstitial
        Map<String, Object> localExtras = new HashMap<>();
        localExtras.put(BidMachineUtils.AD_CONTENT_TYPE, "All");

        // Create new MoPubInterstitial instance and load
        moPubInterstitial = new MoPubInterstitial(this, INTERSTITIAL_KEY);
        moPubInterstitial.setLocalExtras(localExtras);
        moPubInterstitial.setInterstitialAdListener(new InterstitialListener());
        moPubInterstitial.load();
    }

    /**
     * Method for show MoPubInterstitial
     */
    private void showInterstitial() {
        Log.d(TAG, "showInterstitial");

        bShowInterstitial.setEnabled(false);

        // Checking for can show before showing ads
        if (moPubInterstitial != null && moPubInterstitial.isReady()) {
            moPubInterstitial.show();
        } else {
            Log.d(TAG, "show error - interstitial object not loaded");
        }
    }

    /**
     * Method for destroy MoPubInterstitial
     */
    private void destroyInterstitial() {
        Log.d(TAG, "destroyInterstitial");

        if (moPubInterstitial != null) {
            moPubInterstitial.setInterstitialAdListener(null);
            moPubInterstitial.destroy();
        }
    }

    /**
     * Method for load MoPubRewardedVideos
     */
    private void loadRewardedVideo() {
        Log.d(TAG, "loadRewardedVideo");

        bShowRewardedVideo.setEnabled(false);

        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(0.01);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Prepare localExtras for set to MoPubRewardedVideos
        Map<String, Object> localExtras = new HashMap<>();
        localExtras.put(BidMachineUtils.PRICE_FLOORS, jsonArray.toString());

        // Create BidMachineMediationSettings instance with local extras
        MediationSettings mediationSettings = new BidMachineMediationSettings()
                .withLocalExtras(localExtras);

        // Load MoPubRewardedVideos
        MoPubRewardedVideos.setRewardedVideoListener(new RewardedVideoListener());
        MoPubRewardedVideos.loadRewardedVideo(REWARDED_KEY, mediationSettings);
    }

    /**
     * Method for show MoPubRewardedVideos
     */
    private void showRewardedVideo() {
        Log.d(TAG, "showRewardedVideo");

        bShowRewardedVideo.setEnabled(false);

        // Checking for can show before showing ads
        if (MoPubRewardedVideos.hasRewardedVideo(REWARDED_KEY)) {
            MoPubRewardedVideos.showRewardedVideo(REWARDED_KEY);
        } else {
            Log.d(TAG, "show error - rewarded object not loaded");
        }
    }

    /**
     * Method for load MoPubNative
     */
    private void loadNative() {
        Log.d(TAG, "loadNative");

        bShowNative.setEnabled(false);

        // Destroy previous MoPubNative
        destroyNative();

        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(0.01);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Prepare localExtras for MoPubNative
        Map<String, Object> localExtras = new HashMap<>();
        localExtras.put(BidMachineUtils.PRICE_FLOORS, jsonArray.toString());

        // Create a new instance of BidMachineViewBinder with layout which contains NativeAdContentLayout and its ID
        BidMachineViewBinder viewBinder = new BidMachineViewBinder(R.layout.native_ad,
                                                                   R.id.native_ad_container);

        // Create new MoPubNative instance and load
        moPubNative = new MoPubNative(this, NATIVE_KEY, new NativeListener());
        moPubNative.registerAdRenderer(new BidMachineNativeRendered(viewBinder));
        moPubNative.setLocalExtras(localExtras);
        moPubNative.makeRequest();
    }

    /**
     * Method for show MoPubNative
     */
    private void showNative() {
        Log.d(TAG, "showNative");

        bShowNative.setEnabled(false);

        if (nativeAd == null) {
            Log.d(TAG, "show error - native object not loaded");
            return;
        }
        nativeAd.setMoPubNativeEventListener(new NativeDisplayListener());

        AdapterHelper adapterHelper = new AdapterHelper(this, 0, 2);
        View adView = adapterHelper.getAdView(null, adContainer, nativeAd);
        addAdView(adView);
    }

    /**
     * Method for destroy MoPubNative
     */
    private void destroyNative() {
        Log.d(TAG, "destroyNative");

        adContainer.removeAllViews();
        if (nativeAd != null) {
            nativeAd.destroy();
            nativeAd = null;
        }
        if (moPubNative != null) {
            moPubNative.destroy();
            moPubNative = null;
        }
    }

    /**
     * Class for definition behavior after initialize finished
     */
    private class InitializationListener implements SdkInitializationListener {

        @Override
        public void onInitializationFinished() {
            bInitialize.setEnabled(false);
            enableLoadButton();

            Log.d(TAG, "InitializationListener - onInitializationFinished");
        }

    }

    /**
     * Class for definition behavior MoPubView
     */
    private class BannerViewListener implements MoPubView.BannerAdListener {

        @Override
        public void onBannerLoaded(@NonNull MoPubView banner) {
            bShowBanner.setEnabled(true);

            Log.d(TAG, "BannerViewListener - onBannerLoaded");
            Toast.makeText(BidMachineMoPubActivity.this,
                           "BannerLoaded",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
            Log.d(TAG,
                  String.format(
                          "BannerViewListener - onBannerFailed with errorCode: %s (%s)",
                          errorCode.getIntCode(),
                          errorCode.toString()));
            Toast.makeText(BidMachineMoPubActivity.this,
                           "BannerFailedToLoad",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBannerClicked(MoPubView banner) {
            Log.d(TAG, "BannerViewListener - onBannerClicked");
        }

        @Override
        public void onBannerExpanded(MoPubView banner) {
            Log.d(TAG, "BannerViewListener - onBannerExpanded");
        }

        @Override
        public void onBannerCollapsed(MoPubView banner) {
            Log.d(TAG, "BannerViewListener - onBannerCollapsed");
        }

    }

    /**
     * Class for definition behavior MoPubInterstitial
     */
    private class InterstitialListener implements MoPubInterstitial.InterstitialAdListener {

        @Override
        public void onInterstitialLoaded(MoPubInterstitial interstitial) {
            bShowInterstitial.setEnabled(true);

            Log.d(TAG, "InterstitialListener - onInterstitialLoaded");
            Toast.makeText(BidMachineMoPubActivity.this,
                           "InterstitialLoaded",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
            Log.d(TAG,
                  String.format(
                          "InterstitialListener - onInterstitialFailed with errorCode: %s (%s)",
                          errorCode.getIntCode(),
                          errorCode.toString()));
            Toast.makeText(BidMachineMoPubActivity.this,
                           "InterstitialFailedToLoad",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onInterstitialShown(MoPubInterstitial interstitial) {
            Log.d(TAG, "InterstitialListener - onInterstitialShown");
        }

        @Override
        public void onInterstitialClicked(MoPubInterstitial interstitial) {
            Log.d(TAG, "InterstitialListener - onInterstitialClicked");
        }

        @Override
        public void onInterstitialDismissed(MoPubInterstitial interstitial) {
            Log.d(TAG, "InterstitialListener - onInterstitialDismissed");
        }

    }

    /**
     * Class for definition behavior MoPubRewardedVideos
     */
    private class RewardedVideoListener implements MoPubRewardedVideoListener {

        @Override
        public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
            bShowRewardedVideo.setEnabled(true);

            Log.d(TAG, "RewardedVideoListener - onRewardedVideoLoadSuccess");
            Toast.makeText(BidMachineMoPubActivity.this,
                           "RewardedVideoLoaded",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRewardedVideoLoadFailure(@NonNull String adUnitId,
                                               @NonNull MoPubErrorCode errorCode) {
            Log.d(TAG,
                  String.format(
                          "RewardedVideoListener - onRewardedVideoLoadFailure with errorCode: %s (%s)",
                          errorCode.getIntCode(),
                          errorCode.toString()));
            Toast.makeText(BidMachineMoPubActivity.this,
                           "RewardedVideoFailedToLoad",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRewardedVideoStarted(@NonNull String adUnitId) {
            Log.d(TAG, "RewardedVideoListener - onRewardedVideoStarted");
        }

        @Override
        public void onRewardedVideoPlaybackError(@NonNull String adUnitId,
                                                 @NonNull MoPubErrorCode errorCode) {
            Log.d(TAG,
                  String.format(
                          "RewardedVideoListener - onRewardedVideoPlaybackError with errorCode: %s (%s)",
                          errorCode.getIntCode(),
                          errorCode.toString()));
        }

        @Override
        public void onRewardedVideoClicked(@NonNull String adUnitId) {
            Log.d(TAG, "RewardedVideoListener - onRewardedVideoClicked");
        }

        @Override
        public void onRewardedVideoClosed(@NonNull String adUnitId) {
            Log.d(TAG, "RewardedVideoListener - onRewardedVideoClosed");
        }

        @Override
        public void onRewardedVideoCompleted(@NonNull Set<String> adUnitIds,
                                             @NonNull MoPubReward reward) {
            Log.d(TAG, "RewardedVideoListener - onRewardedVideoCompleted");
        }

    }

    /**
     * Class for definition behavior MoPubNative
     */
    private class NativeListener implements MoPubNative.MoPubNativeNetworkListener {

        @Override
        public void onNativeLoad(NativeAd nativeAd) {
            BidMachineMoPubActivity.this.nativeAd = nativeAd;

            bShowNative.setEnabled(true);

            Log.d(TAG, "NativeListener - onNativeLoad");
            Toast.makeText(BidMachineMoPubActivity.this,
                           "NativeAdLoad",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNativeFail(NativeErrorCode errorCode) {
            Log.d(TAG, "NativeListener - onNativeFail");
            Toast.makeText(BidMachineMoPubActivity.this,
                           "NativeAdFailedToLoad",
                           Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Class for definition behavior NativeAd
     */
    private class NativeDisplayListener implements NativeAd.MoPubNativeEventListener {

        @Override
        public void onImpression(View view) {
            Log.d(TAG, "NativeDisplayListener - onImpression");
            Toast.makeText(BidMachineMoPubActivity.this,
                           "NativeAdOnImpression",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "NativeDisplayListener - onClick");
            Toast.makeText(BidMachineMoPubActivity.this,
                           "NativeAdOnClick",
                           Toast.LENGTH_SHORT).show();
        }

    }

}