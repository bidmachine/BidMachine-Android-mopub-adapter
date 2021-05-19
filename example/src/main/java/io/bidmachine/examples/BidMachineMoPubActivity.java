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
import com.mopub.mobileads.MoPubRewardedAdListener;
import com.mopub.mobileads.MoPubRewardedAds;
import com.mopub.mobileads.MoPubView;
import com.mopub.nativeads.AdapterHelper;
import com.mopub.nativeads.BidMachineNativeRendered;
import com.mopub.nativeads.BidMachineViewBinder;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BidMachineMoPubActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final String BID_MACHINE_SELLER_ID = "5";
    private static final String AD_UNIT_ID = "bc55ee5faf30480d8cec2d514c775199";
    private static final String BANNER_KEY = "bc55ee5faf30480d8cec2d514c775199";
    private static final String MREC_KEY = "5db812ce0913448e8a392e2955912da3";
    private static final String INTERSTITIAL_KEY = "323f925a727e45038404717b47556297";
    private static final String REWARDED_KEY = "f74775a1c3bc44e09a5f7ae80f57a9b0";
    private static final String NATIVE_KEY = "6e82ccc177f54fab97e47ef9786e1d0d";

    private Button bInitialize;
    private Button bLoadBanner;
    private Button bShowBanner;
    private Button bLoadMrec;
    private Button bShowMrec;
    private Button bLoadInterstitial;
    private Button bShowInterstitial;
    private Button bLoadRewarded;
    private Button bShowRewarded;
    private Button bLoadNative;
    private Button bShowNative;
    private FrameLayout adContainer;

    private MoPubView bannerMoPubView;
    private MoPubView mrecMoPubView;
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
        bLoadMrec = findViewById(R.id.bLoadMrec);
        bLoadMrec.setOnClickListener(v -> loadMrec());
        bShowMrec = findViewById(R.id.bShowMrec);
        bShowMrec.setOnClickListener(v -> showMrec());
        bLoadInterstitial = findViewById(R.id.bLoadInterstitial);
        bLoadInterstitial.setOnClickListener(v -> loadInterstitial());
        bShowInterstitial = findViewById(R.id.bShowInterstitial);
        bShowInterstitial.setOnClickListener(v -> showInterstitial());
        bLoadRewarded = findViewById(R.id.bLoadRewarded);
        bLoadRewarded.setOnClickListener(v -> loadRewarded());
        bShowRewarded = findViewById(R.id.bShowRewarded);
        bShowRewarded.setOnClickListener(v -> showRewarded());
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
        destroyMrec();
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
        bLoadMrec.setEnabled(true);
        bLoadInterstitial.setEnabled(true);
        bLoadRewarded.setEnabled(true);
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
        bShowBanner.setEnabled(false);

        // Destroy previous MoPubView
        destroyBanner();

        // Prepare localExtras for set to MoPubView
        Map<String, Object> localExtras = new HashMap<>();
        localExtras.put(BidMachineUtils.BANNER_WIDTH, 320);

        // Create new MoPubView instance and load
        bannerMoPubView = new MoPubView(this);
        bannerMoPubView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                   ViewGroup.LayoutParams.MATCH_PARENT));
        bannerMoPubView.setLocalExtras(localExtras);
        bannerMoPubView.setAutorefreshEnabled(false);
        bannerMoPubView.setAdUnitId(BANNER_KEY);
        bannerMoPubView.setBannerAdListener(new BannerViewListener());
        bannerMoPubView.loadAd(MoPubView.MoPubAdSize.HEIGHT_50);

        Log.d(TAG, "loadBanner");
    }

    /**
     * Method for show MoPubView
     */
    private void showBanner() {
        Log.d(TAG, "showBanner");

        bShowBanner.setEnabled(false);

        if (bannerMoPubView != null) {
            addAdView(bannerMoPubView);
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
        if (bannerMoPubView != null) {
            bannerMoPubView.setBannerAdListener(null);
            bannerMoPubView.destroy();
        }
    }

    /**
     * Method for load MoPubView
     */
    private void loadMrec() {
        bShowMrec.setEnabled(false);

        // Destroy previous MoPubView
        destroyMrec();

        // Prepare localExtras for set to MoPubView
        Map<String, Object> localExtras = new HashMap<>();
        localExtras.put(BidMachineUtils.BANNER_WIDTH, 300);

        // Create new MoPubView instance and load
        mrecMoPubView = new MoPubView(this);
        mrecMoPubView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                 ViewGroup.LayoutParams.MATCH_PARENT));
        mrecMoPubView.setLocalExtras(localExtras);
        mrecMoPubView.setAutorefreshEnabled(false);
        mrecMoPubView.setAdUnitId(MREC_KEY);
        mrecMoPubView.setBannerAdListener(new MrecViewListener());
        mrecMoPubView.loadAd(MoPubView.MoPubAdSize.HEIGHT_250);

        Log.d(TAG, "loadMrec");
    }

    /**
     * Method for show MoPubView
     */
    private void showMrec() {
        Log.d(TAG, "showMrec");

        bShowMrec.setEnabled(false);

        if (mrecMoPubView != null) {
            addAdView(mrecMoPubView);
        } else {
            Log.d(TAG, "show error - mrec object is null");
        }
    }

    /**
     * Method for destroy MoPubView
     */
    private void destroyMrec() {
        Log.d(TAG, "destroyMrec");

        adContainer.removeAllViews();
        if (mrecMoPubView != null) {
            mrecMoPubView.setBannerAdListener(null);
            mrecMoPubView.destroy();
        }
    }

    /**
     * Method for load MoPubInterstitial
     */
    private void loadInterstitial() {
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

        Log.d(TAG, "loadInterstitial");
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
     * Method for load MoPubRewardedAds
     */
    private void loadRewarded() {
        bShowRewarded.setEnabled(false);

        JSONArray priceFloors = new JSONArray();
        try {
            priceFloors.put(0.01);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONArray externalUserIds = new JSONArray();
        try {
            JSONObject externalUserId = new JSONObject()
                    .put(BidMachineUtils.EXTERNAL_USER_SOURCE_ID, "source_id")
                    .put(BidMachineUtils.EXTERNAL_USER_VALUE, "value");
            externalUserIds.put(externalUserId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Prepare localExtras for set to MoPubRewardedAds
        Map<String, Object> localExtras = new HashMap<>();
        localExtras.put(BidMachineUtils.PRICE_FLOORS, priceFloors.toString());
        localExtras.put(BidMachineUtils.EXTERNAL_USER_IDS, externalUserIds.toString());

        // Create BidMachineMediationSettings instance with local extras
        MediationSettings mediationSettings = new BidMachineMediationSettings()
                .withLocalExtras(localExtras);

        // Load MoPubRewardedAds
        MoPubRewardedAds.setRewardedAdListener(new RewardedAdListener());
        MoPubRewardedAds.loadRewardedAd(REWARDED_KEY, mediationSettings);

        Log.d(TAG, "loadRewarded");
    }

    /**
     * Method for show MoPubRewardedAds
     */
    private void showRewarded() {
        Log.d(TAG, "showRewarded");

        bShowRewarded.setEnabled(false);

        // Checking for can show before showing ads
        if (MoPubRewardedAds.hasRewardedAd(REWARDED_KEY)) {
            MoPubRewardedAds.showRewardedAd(REWARDED_KEY);
        } else {
            Log.d(TAG, "show error - rewarded object not loaded");
        }
    }

    /**
     * Method for load MoPubNative
     */
    private void loadNative() {
        bShowNative.setEnabled(false);

        // Destroy previous MoPubNative
        destroyNative();

        JSONArray priceFloors = new JSONArray();
        try {
            priceFloors.put(0.01);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONArray externalUserIds = new JSONArray();
        try {
            JSONObject externalUserId = new JSONObject()
                    .put(BidMachineUtils.EXTERNAL_USER_SOURCE_ID, "source_id")
                    .put(BidMachineUtils.EXTERNAL_USER_VALUE, "value");
            externalUserIds.put(externalUserId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Prepare localExtras for set to MoPubNative
        Map<String, Object> localExtras = new HashMap<>();
        localExtras.put(BidMachineUtils.PRICE_FLOORS, priceFloors.toString());
        localExtras.put(BidMachineUtils.EXTERNAL_USER_IDS, externalUserIds.toString());

        // Create a new instance of BidMachineViewBinder with layout which contains NativeAdContentLayout and its ID
        BidMachineViewBinder viewBinder = new BidMachineViewBinder(R.layout.native_ad,
                                                                   R.id.native_ad_container);

        // Create new MoPubNative instance and load
        moPubNative = new MoPubNative(this, NATIVE_KEY, new NativeListener());
        moPubNative.registerAdRenderer(new BidMachineNativeRendered(viewBinder));
        moPubNative.setLocalExtras(localExtras);
        moPubNative.makeRequest();

        Log.d(TAG, "loadNative");
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
     * Class for definition behavior MoPubView
     */
    private class MrecViewListener implements MoPubView.BannerAdListener {

        @Override
        public void onBannerLoaded(@NonNull MoPubView banner) {
            bShowMrec.setEnabled(true);

            Log.d(TAG, "MrecViewListener - onBannerLoaded");
            Toast.makeText(BidMachineMoPubActivity.this,
                           "MrecLoaded",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
            Log.d(TAG,
                  String.format(
                          "MrecViewListener - onBannerFailed with errorCode: %s (%s)",
                          errorCode.getIntCode(),
                          errorCode.toString()));
            Toast.makeText(BidMachineMoPubActivity.this,
                           "MrecFailedToLoad",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBannerClicked(MoPubView banner) {
            Log.d(TAG, "MrecViewListener - onBannerClicked");
        }

        @Override
        public void onBannerExpanded(MoPubView banner) {
            Log.d(TAG, "MrecViewListener - onBannerExpanded");
        }

        @Override
        public void onBannerCollapsed(MoPubView banner) {
            Log.d(TAG, "MrecViewListener - onBannerCollapsed");
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
     * Class for definition behavior MoPubRewardedAds
     */
    private class RewardedAdListener implements MoPubRewardedAdListener {

        @Override
        public void onRewardedAdLoadSuccess(@NonNull String s) {
            bShowRewarded.setEnabled(true);

            Log.d(TAG, "RewardedAdListener - onRewardedAdLoadSuccess");
            Toast.makeText(BidMachineMoPubActivity.this,
                           "RewardedAdLoaded",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRewardedAdLoadFailure(@NonNull String s,
                                            @NonNull MoPubErrorCode moPubErrorCode) {
            Log.d(TAG,
                  String.format(
                          "RewardedAdListener - onRewardedAdLoadFailure with errorCode: %s (%s)",
                          moPubErrorCode.getIntCode(),
                          moPubErrorCode.toString()));
            Toast.makeText(BidMachineMoPubActivity.this,
                           "RewardedAdFailedToLoad",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRewardedAdStarted(@NonNull String s) {
            Log.d(TAG, "RewardedAdListener - onRewardedAdStarted");
        }

        @Override
        public void onRewardedAdShowError(@NonNull String s,
                                          @NonNull MoPubErrorCode moPubErrorCode) {
            Log.d(TAG,
                  String.format(
                          "RewardedAdListener - onRewardedAdShowError with errorCode: %s (%s)",
                          moPubErrorCode.getIntCode(),
                          moPubErrorCode.toString()));
        }

        @Override
        public void onRewardedAdClicked(@NonNull String s) {
            Log.d(TAG, "RewardedAdListener - onRewardedAdClicked");
        }

        @Override
        public void onRewardedAdClosed(@NonNull String s) {
            Log.d(TAG, "RewardedAdListener - onRewardedAdClosed");
        }

        @Override
        public void onRewardedAdCompleted(@NonNull Set<String> set,
                                          @NonNull MoPubReward moPubReward) {
            Log.d(TAG, "RewardedAdListener - onRewardedAdCompleted");
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