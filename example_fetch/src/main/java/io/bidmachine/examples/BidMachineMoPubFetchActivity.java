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
import com.mopub.mobileads.MoPubRewardedAdManager;
import com.mopub.mobileads.MoPubRewardedAds;
import com.mopub.mobileads.MoPubView;
import com.mopub.nativeads.AdapterHelper;
import com.mopub.nativeads.BidMachineNativeRendered;
import com.mopub.nativeads.BidMachineViewBinder;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.RequestParameters;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.bidmachine.AdRequest;
import io.bidmachine.BidMachine;
import io.bidmachine.BidMachineFetcher;
import io.bidmachine.banner.BannerRequest;
import io.bidmachine.banner.BannerSize;
import io.bidmachine.interstitial.InterstitialRequest;
import io.bidmachine.models.AuctionResult;
import io.bidmachine.nativead.NativeRequest;
import io.bidmachine.rewarded.RewardedRequest;
import io.bidmachine.utils.BMError;

public class BidMachineMoPubFetchActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final String BID_MACHINE_SELLER_ID = "5";
    private static final String AD_UNIT_ID = "e141f74d6a294b6c91cfbe05534585e4";
    private static final String BANNER_KEY = "e141f74d6a294b6c91cfbe05534585e4";
    private static final String MREC_KEY = "b266629bd11d44fbaeb8ffe9de774135";
    private static final String INTERSTITIAL_KEY = "376c44d6cb114dc194fd016f3b75b424";
    private static final String REWARDED_KEY = "5dceb086be004f1b9f973ae8ebafe42d";
    private static final String NATIVE_KEY = "4eaaaf10237940af9b319c38f28c7ae5";

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
        setContentView(R.layout.activity_fetch);

        bInitialize = findViewById(R.id.bInitialize);
        bInitialize.setOnClickListener(v -> initialize());
        findViewById(R.id.bShowBannerAutoRefreshActivity).setOnClickListener(v -> showBannerAutoRefreshActivity());
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

        if (BidMachine.isInitialized() && MoPub.isSdkInitialized()) {
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

        // Initialize BidMachine SDK first
        BidMachine.setTestMode(true);
        BidMachine.setLoggingEnabled(true);
        BidMachine.initialize(this, BID_MACHINE_SELLER_ID);

        // Prepare SdkConfiguration for initialize MoPub with BidMachineAdapterConfiguration
        SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(AD_UNIT_ID)
                .withLogLevel(MoPubLog.LogLevel.DEBUG)
                .withAdditionalNetwork(BidMachineAdapterConfiguration.class.getName())
                .build();

        // Initialize MoPub SDK
        MoPub.initializeSdk(this, sdkConfiguration, new InitializationListener());
    }

    private void showBannerAutoRefreshActivity() {
        startActivity(BannerAutoRefreshActivity.createIntent(this));
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
     * Method for load BannerRequest
     */
    private void loadBanner() {
        bShowBanner.setEnabled(false);

        // Destroy previous MoPubView
        destroyBanner();

        // Create new MoPubView instance
        bannerMoPubView = new MoPubView(this);
        bannerMoPubView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                   ViewGroup.LayoutParams.MATCH_PARENT));
        bannerMoPubView.setAutorefreshEnabled(false);
        bannerMoPubView.setAdUnitId(BANNER_KEY);
        bannerMoPubView.setBannerAdListener(new BannerViewListener());

        // Create new BidMachine request
        BannerRequest bannerRequest = new BannerRequest.Builder()
                .setSize(BannerSize.Size_320x50)
                .setListener(new AdRequest.AdRequestListener<BannerRequest>() {
                    @Override
                    public void onRequestSuccess(@NonNull BannerRequest bannerRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        runOnUiThread(() -> loadMoPubBanner(bannerRequest));
                    }

                    @Override
                    public void onRequestFailed(@NonNull BannerRequest bannerRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(() -> Toast.makeText(BidMachineMoPubFetchActivity.this,
                                                           "BannerFetchFailed",
                                                           Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onRequestExpired(@NonNull BannerRequest bannerRequest) {
                        //ignore
                    }
                })
                .build();

        // Request BidMachine Ads without load it
        bannerRequest.request(this);

        Log.d(TAG, "loadBanner");
    }

    /**
     * Method for load MoPubView
     *
     * @param bannerRequest - loaded {@link io.bidmachine.banner.BannerRequest}
     */
    private void loadMoPubBanner(@NonNull BannerRequest bannerRequest) {
        Log.d(TAG, "loadMoPubBanner");

        // Append BidMachine AdRequest to MoPubView before load
        BidMachineUtils.appendRequest(bannerMoPubView, bannerRequest);

        bannerMoPubView.loadAd(MoPubView.MoPubAdSize.HEIGHT_50);
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
     * Method for load BannerRequest
     */
    private void loadMrec() {
        bShowMrec.setEnabled(false);

        // Destroy previous MoPubView
        destroyMrec();

        // Create new MoPubView instance
        mrecMoPubView = new MoPubView(this);
        mrecMoPubView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                 ViewGroup.LayoutParams.MATCH_PARENT));
        mrecMoPubView.setAutorefreshEnabled(false);
        mrecMoPubView.setAdUnitId(MREC_KEY);
        mrecMoPubView.setBannerAdListener(new MrecViewListener());

        // Create new BidMachine request
        BannerRequest bannerRequest = new BannerRequest.Builder()
                .setSize(BannerSize.Size_300x250)
                .setListener(new AdRequest.AdRequestListener<BannerRequest>() {
                    @Override
                    public void onRequestSuccess(@NonNull BannerRequest bannerRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        runOnUiThread(() -> loadMoPubMrec(bannerRequest));
                    }

                    @Override
                    public void onRequestFailed(@NonNull BannerRequest bannerRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(() -> Toast.makeText(BidMachineMoPubFetchActivity.this,
                                                           "BannerFetchFailed",
                                                           Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onRequestExpired(@NonNull BannerRequest bannerRequest) {
                        //ignore
                    }
                })
                .build();

        // Request BidMachine Ads without load it
        bannerRequest.request(this);

        Log.d(TAG, "loadMrec");
    }

    /**
     * Method for load MoPubView
     *
     * @param bannerRequest - loaded {@link io.bidmachine.banner.BannerRequest}
     */
    private void loadMoPubMrec(@NonNull BannerRequest bannerRequest) {
        Log.d(TAG, "loadMoPubMrec");

        // Append BidMachine AdRequest to MoPubView before load
        BidMachineUtils.appendRequest(mrecMoPubView, bannerRequest);

        mrecMoPubView.loadAd(MoPubView.MoPubAdSize.HEIGHT_250);
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
     * Method for load InterstitialRequest
     */
    private void loadInterstitial() {
        bShowInterstitial.setEnabled(false);

        // Destroy previous MoPubInterstitial
        destroyInterstitial();

        // Create new MoPubInterstitial instance
        moPubInterstitial = new MoPubInterstitial(this, INTERSTITIAL_KEY);
        moPubInterstitial.setInterstitialAdListener(new InterstitialListener());

        // Create new BidMachine request
        InterstitialRequest interstitialRequest = new InterstitialRequest.Builder()
                .setListener(new AdRequest.AdRequestListener<InterstitialRequest>() {
                    @Override
                    public void onRequestSuccess(@NonNull InterstitialRequest interstitialRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        runOnUiThread(() -> loadMoPubInterstitial(interstitialRequest));
                    }

                    @Override
                    public void onRequestFailed(@NonNull InterstitialRequest interstitialRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(() -> Toast.makeText(BidMachineMoPubFetchActivity.this,
                                                           "InterstitialFetchFailed",
                                                           Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onRequestExpired(@NonNull InterstitialRequest interstitialRequest) {
                        //ignore
                    }
                })
                .build();

        // Request BidMachine Ads without load it
        interstitialRequest.request(this);

        Log.d(TAG, "loadInterstitial");
    }

    /**
     * Method for load MoPubInterstitial
     *
     * @param interstitialRequest - loaded {@link io.bidmachine.interstitial.InterstitialRequest}
     */
    private void loadMoPubInterstitial(@NonNull InterstitialRequest interstitialRequest) {
        Log.d(TAG, "loadMoPubInterstitial");

        // Append BidMachine AdRequest to MoPubInterstitial before load
        BidMachineUtils.appendRequest(moPubInterstitial, interstitialRequest);

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
     * Method for load RewardedRequest
     */
    private void loadRewarded() {
        bShowRewarded.setEnabled(false);

        // Create new BidMachine request
        RewardedRequest request = new RewardedRequest.Builder()
                .setListener(new AdRequest.AdRequestListener<RewardedRequest>() {
                    @Override
                    public void onRequestSuccess(@NonNull RewardedRequest rewardedRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        runOnUiThread(() -> loadMoPubRewarded(rewardedRequest));
                    }

                    @Override
                    public void onRequestFailed(@NonNull RewardedRequest rewardedRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(() -> Toast.makeText(BidMachineMoPubFetchActivity.this,
                                                           "RewardedFetchFailed",
                                                           Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onRequestExpired(@NonNull RewardedRequest rewardedRequest) {
                        //ignore
                    }
                })
                .build();

        // Request BidMachine Ads without load it
        request.request(this);

        Log.d(TAG, "loadRewarded");
    }

    /**
     * Method for load MoPubRewardedAds
     *
     * @param rewardedRequest - loaded {@link io.bidmachine.rewarded.RewardedRequest}
     */
    private void loadMoPubRewarded(@NonNull RewardedRequest rewardedRequest) {
        Log.d(TAG, "loadMoPubRewarded");

        // Fetch BidMachine Ads
        Map<String, String> fetchParams = BidMachineFetcher.fetch(rewardedRequest);
        if (fetchParams != null) {
            // Prepare MoPub keywords
            String keywords = BidMachineUtils.toKeywords(fetchParams);

            // Set MoPub Rewarded listener if required
            MoPubRewardedAds.setRewardedAdListener(new RewardedAdListener());

            // Load MoPub Rewarded
            MoPubRewardedAds.loadRewardedAd(REWARDED_KEY,
                                            // Set MoPub Rewarded keywords
                                            new MoPubRewardedAdManager.RequestParameters(keywords),
                                            // Create BidMachine MediationSettings with fetched request id
                                            new BidMachineMediationSettings(fetchParams));
        } else {
            Toast.makeText(this, "RewardedFetchFailed", Toast.LENGTH_SHORT).show();
        }
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
     * Method for load NativeRequest
     */
    private void loadNative() {
        bShowNative.setEnabled(false);

        // Destroy previous MoPubNative
        destroyNative();

        // Create new MoPubNative instance
        BidMachineViewBinder viewBinder = new BidMachineViewBinder(R.layout.native_ad,
                                                                   R.id.native_ad_container);
        moPubNative = new MoPubNative(this, NATIVE_KEY, new NativeListener());
        moPubNative.registerAdRenderer(new BidMachineNativeRendered(viewBinder));

        // Create new BidMachine request
        NativeRequest request = new NativeRequest.Builder()
                .setListener(new AdRequest.AdRequestListener<NativeRequest>() {
                    @Override
                    public void onRequestSuccess(@NonNull NativeRequest nativeRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        runOnUiThread(() -> loadMoPubNative(nativeRequest));
                    }

                    @Override
                    public void onRequestFailed(@NonNull NativeRequest nativeRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(() -> Toast.makeText(BidMachineMoPubFetchActivity.this,
                                                           "NativeFetchFailed",
                                                           Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onRequestExpired(@NonNull NativeRequest nativeRequest) {
                        //ignore
                    }
                })
                .build();

        // Request BidMachine Ads without load it
        request.request(this);

        Log.d(TAG, "loadNative");
    }

    /**
     * Method for load MoPubNative
     *
     * @param nativeRequest - loaded {@link io.bidmachine.nativead.NativeRequest}
     */
    private void loadMoPubNative(@NonNull NativeRequest nativeRequest) {
        Log.d(TAG, "loadMoPubNative");

        // Fetch BidMachine Ads
        Map<String, String> fetchParams = BidMachineFetcher.fetch(nativeRequest);
        if (fetchParams != null) {
            // Prepare MoPub keywords
            String keywords = BidMachineUtils.toKeywords(fetchParams);

            // Prepare localExtras for set to MoPubNative with additional fetching parameters
            Map<String, Object> localExtras = new HashMap<>(fetchParams);

            // Set MoPub local extras
            moPubNative.setLocalExtras(localExtras);

            // Set MoPub Native keywords
            RequestParameters requestParameters = new RequestParameters.Builder()
                    .keywords(keywords)
                    .build();

            // Load MoPub Ads
            moPubNative.makeRequest(requestParameters);
        } else {
            Toast.makeText(this, "NativeFetchFailed", Toast.LENGTH_SHORT).show();
        }
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
            Toast.makeText(BidMachineMoPubFetchActivity.this,
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
            Toast.makeText(BidMachineMoPubFetchActivity.this,
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
            Toast.makeText(BidMachineMoPubFetchActivity.this,
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
            Toast.makeText(BidMachineMoPubFetchActivity.this,
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
            Toast.makeText(BidMachineMoPubFetchActivity.this,
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
            Toast.makeText(BidMachineMoPubFetchActivity.this,
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
            Toast.makeText(BidMachineMoPubFetchActivity.this,
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
            Toast.makeText(BidMachineMoPubFetchActivity.this,
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
                          "RewardedAdListener - onRewardedAdShowError with errorCode - %s (%s)",
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
            BidMachineMoPubFetchActivity.this.nativeAd = nativeAd;

            bShowNative.setEnabled(true);

            Log.d(TAG, "NativeListener - onNativeLoad");
            Toast.makeText(BidMachineMoPubFetchActivity.this,
                           "NativeAdLoad",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNativeFail(NativeErrorCode errorCode) {
            Log.d(TAG, "NativeListener - onNativeFail");
            Toast.makeText(BidMachineMoPubFetchActivity.this,
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
            Toast.makeText(BidMachineMoPubFetchActivity.this,
                           "NativeAdOnImpression",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "NativeDisplayListener - onClick");
            Toast.makeText(BidMachineMoPubFetchActivity.this,
                           "NativeAdOnClick",
                           Toast.LENGTH_SHORT).show();
        }

    }

}
