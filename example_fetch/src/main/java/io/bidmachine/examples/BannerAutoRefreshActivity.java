package io.bidmachine.examples;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.BidMachineAdapterConfiguration;
import com.mopub.mobileads.BidMachineUtils;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import io.bidmachine.AdRequest;
import io.bidmachine.BidMachine;
import io.bidmachine.banner.BannerRequest;
import io.bidmachine.banner.BannerSize;
import io.bidmachine.models.AuctionResult;
import io.bidmachine.utils.BMError;

public class BannerAutoRefreshActivity extends Activity {

    private static final String TAG = BannerAutoRefreshActivity.class.getSimpleName();
    private static final String BID_MACHINE_SELLER_ID = "5";
    private static final String AD_UNIT_ID = "bc2b6a2f74f141069e8de6e1e046b9c2";
    private static final String BANNER_KEY = "bc2b6a2f74f141069e8de6e1e046b9c2";

    private Button bInitialize;
    private Button bLoadBanner;
    private Button bShowBanner;
    private FrameLayout adContainer;

    private MoPubView bannerMoPubView;

    static Intent createIntent(Context context) {
        return new Intent(context, BannerAutoRefreshActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_auto_refresh);

        bInitialize = findViewById(R.id.bInitialize);
        bInitialize.setOnClickListener(v -> initialize());
        bLoadBanner = findViewById(R.id.bLoadBanner);
        bLoadBanner.setOnClickListener(v -> loadBanner());
        bShowBanner = findViewById(R.id.bShowBanner);
        bShowBanner.setOnClickListener(v -> showBanner());

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

    /**
     * Enable buttons for user interaction
     */
    private void enableLoadButton() {
        bLoadBanner.setEnabled(true);
    }

    private void addAdView(View view) {
        adContainer.removeAllViews();
        adContainer.addView(view);
    }

    /**
     * Method for prepare MoPubView
     */
    private void loadBanner() {
        bShowBanner.setEnabled(false);

        // Destroy previous MoPubView
        destroyBanner();

        // Create new MoPubView instance
        bannerMoPubView = new MoPubView(this);
        bannerMoPubView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                   ViewGroup.LayoutParams.MATCH_PARENT));
        bannerMoPubView.setAutorefreshEnabled(true);
        bannerMoPubView.setAdUnitId(BANNER_KEY);
        bannerMoPubView.setBannerAdListener(new BannerViewListener());

        loadBidMachineRequest(true);

        Log.d(TAG, "loadBanner");
    }

    /**
     * Method for load BannerRequest
     *
     * @param forceMoPubLoad - if true, then {@link MoPubView#loadAd(MoPubView.MoPubAdSize)}
     *                       will be executed on the {@link MoPubView}
     */
    private void loadBidMachineRequest(boolean forceMoPubLoad) {
        // Clear previous MoPubView keywords
        bannerMoPubView.setKeywords(null);

        // Create new BidMachine request
        BannerRequest bannerRequest = new BannerRequest.Builder()
                .setSize(BannerSize.Size_320x50)
                .setListener(new AdRequest.AdRequestListener<BannerRequest>() {
                    @Override
                    public void onRequestSuccess(@NonNull BannerRequest bannerRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        runOnUiThread(() -> loadMoPubBanner(bannerRequest, forceMoPubLoad));
                    }

                    @Override
                    public void onRequestFailed(@NonNull BannerRequest bannerRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(() -> Toast.makeText(BannerAutoRefreshActivity.this,
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
    }

    /**
     * Method for load MoPubView
     *
     * @param bannerRequest  - loaded {@link io.bidmachine.banner.BannerRequest}
     * @param forceMoPubLoad - if true, then {@link MoPubView#loadAd(MoPubView.MoPubAdSize)}
     *                       will be executed on the {@link MoPubView}
     */
    private void loadMoPubBanner(@NonNull BannerRequest bannerRequest, boolean forceMoPubLoad) {
        Log.d(TAG, "loadMoPubBanner");

        // Append BidMachine AdRequest to MoPubView before load
        BidMachineUtils.appendRequest(bannerMoPubView, bannerRequest);

        if (forceMoPubLoad) {
            bannerMoPubView.loadAd(MoPubView.MoPubAdSize.HEIGHT_50);
        }
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

            // Prepare BidMachine for next MoPub auction
            loadBidMachineRequest(false);

            Log.d(TAG, "BannerViewListener - onBannerLoaded");
            Toast.makeText(BannerAutoRefreshActivity.this,
                           "BannerLoaded",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
            // Prepare BidMachine for next MoPub auction
            loadBidMachineRequest(false);

            Log.d(TAG,
                  String.format("BannerViewListener - onBannerFailed with errorCode: %s (%s)",
                                errorCode.getIntCode(),
                                errorCode.toString()));
            Toast.makeText(BannerAutoRefreshActivity.this,
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

}