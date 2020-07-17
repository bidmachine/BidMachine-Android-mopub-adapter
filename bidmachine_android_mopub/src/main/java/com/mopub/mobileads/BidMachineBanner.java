package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mopub.common.LifecycleListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.common.util.Views;

import java.util.Map;
import java.util.UUID;

import io.bidmachine.AdsType;
import io.bidmachine.BidMachineFetcher;
import io.bidmachine.banner.BannerListener;
import io.bidmachine.banner.BannerRequest;
import io.bidmachine.banner.BannerSize;
import io.bidmachine.banner.BannerView;
import io.bidmachine.utils.BMError;

public class BidMachineBanner extends BaseAd {

    private static final String ADAPTER_NAME = BidMachineBanner.class.getSimpleName();
    private static final String BANNER_WIDTH = "banner_width";

    private String adUnitId = "";
    private BannerView bannerView;

    @Nullable
    @Override
    protected LifecycleListener getLifecycleListener() {
        return null;
    }

    @Override
    protected boolean checkAndInitializeSdk(@NonNull Activity launcherActivity,
                                            @NonNull AdData adData) throws Exception {
        return false;
    }

    @Override
    protected void load(@NonNull Context context, @NonNull AdData adData) throws Exception {
        adUnitId = UUID.randomUUID().toString();
        setAutomaticImpressionAndClickTracking(false);

        Map<String, Object> fusedMap = BidMachineUtils.getFusedMap(adData.getExtras());
        BidMachineUtils.prepareBidMachine(context, fusedMap, true);

        BannerRequest request = null;
        BannerSize bannerSize = null;
        MoPubErrorCode errorCode = null;
        if (fusedMap.containsKey(BidMachineFetcher.KEY_ID)) {
            request = BidMachineUtils.obtainCachedRequest(AdsType.Banner, fusedMap);
            if (request == null) {
                MoPubLog.log(getAdNetworkId(),
                             MoPubLog.AdapterLogEvent.CUSTOM,
                             ADAPTER_NAME,
                             "Fetched AdRequest not found");
                MoPubLog.log(getAdNetworkId(),
                             MoPubLog.AdapterLogEvent.LOAD_FAILED,
                             ADAPTER_NAME,
                             MoPubErrorCode.NO_FILL.getIntCode(),
                             MoPubErrorCode.NO_FILL);
                errorCode = MoPubErrorCode.NO_FILL;
            } else {
                request.notifyMediationWin();

                bannerSize = request.getSize();
                MoPubLog.log(getAdNetworkId(),
                             MoPubLog.AdapterLogEvent.CUSTOM,
                             ADAPTER_NAME,
                             "Fetched request resolved: " + request.getAuctionResult());
            }
        } else {
            bannerSize = findBannerSize(fusedMap);
            if (bannerSize == null) {
                bannerSize = findBannerSize(adData);
            }
            if (bannerSize == null) {
                MoPubLog.log(getAdNetworkId(),
                             MoPubLog.AdapterLogEvent.CUSTOM,
                             ADAPTER_NAME,
                             "Unsupported banner size");
                MoPubLog.log(getAdNetworkId(),
                             MoPubLog.AdapterLogEvent.LOAD_FAILED,
                             ADAPTER_NAME,
                             MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR.getIntCode(),
                             MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
                errorCode = MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR;
            } else {
                request = new BannerRequest.Builder()
                        .setSize(bannerSize)
                        .setTargetingParams(BidMachineUtils.findTargetingParams(fusedMap))
                        .setPriceFloorParams(BidMachineUtils.findPriceFloorParams(fusedMap))
                        .build();
            }
        }
        if (request != null) {
            bannerView = new BannerView(context);
            bannerView.setListener(new BidMachineAdListener());
            bannerView.load(request);

            MoPubLog.log(getAdNetworkId(),
                         MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED,
                         ADAPTER_NAME,
                         ", with size: ",
                         bannerSize);
        } else if (mLoadListener != null) {
            mLoadListener.onAdLoadFailed(errorCode != null
                                                 ? errorCode
                                                 : MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        }
    }

    @NonNull
    @Override
    protected String getAdNetworkId() {
        return adUnitId;
    }

    @Nullable
    @Override
    protected View getAdView() {
        return bannerView;
    }

    @Override
    protected void onInvalidate() {
        if (bannerView != null) {
            Views.removeFromParent(bannerView);
            bannerView.setListener(null);
            bannerView.destroy();
            bannerView = null;
        }
    }

    private <T> BannerSize findBannerSize(@Nullable Map<String, T> extras) {
        if (extras == null) {
            return null;
        }

        int width = BidMachineUtils.parseInteger(extras.get(BANNER_WIDTH));
        switch (width) {
            case 300:
                return BannerSize.Size_300x250;
            case 320:
                return BannerSize.Size_320x50;
            case 728:
                return BannerSize.Size_728x90;
            default:
                return null;
        }
    }

    private BannerSize findBannerSize(@NonNull AdData adData) {
        int width = adData.getAdWidth() != null ? adData.getAdWidth() : 0;
        int height = adData.getAdHeight() != null ? adData.getAdHeight() : 0;
        if (width >= 728 && height >= 90) {
            return BannerSize.Size_728x90;
        } else if (width >= 300 && height >= 250) {
            return BannerSize.Size_300x250;
        } else {
            return BannerSize.Size_320x50;
        }
    }

    private class BidMachineAdListener implements BannerListener {

        @Override
        public void onAdLoaded(@NonNull BannerView bannerView) {
            MoPubLog.log(getAdNetworkId(), MoPubLog.AdapterLogEvent.LOAD_SUCCESS, ADAPTER_NAME);
            MoPubLog.log(getAdNetworkId(), MoPubLog.AdapterLogEvent.SHOW_ATTEMPTED, ADAPTER_NAME);
            if (mLoadListener != null) {
                mLoadListener.onAdLoaded();
            }
        }

        @Override
        public void onAdLoadFailed(@NonNull BannerView bannerView, @NonNull BMError bmError) {
            MoPubErrorCode moPubErrorCode = BidMachineUtils.transformToMoPubErrorCode(bmError);
            MoPubLog.log(getAdNetworkId(),
                         MoPubLog.AdapterLogEvent.LOAD_FAILED,
                         ADAPTER_NAME,
                         moPubErrorCode.getIntCode(),
                         moPubErrorCode);
            if (mLoadListener != null) {
                mLoadListener.onAdLoadFailed(moPubErrorCode);
            }
        }

        @Override
        public void onAdShown(@NonNull BannerView bannerView) {
            MoPubLog.log(getAdNetworkId(), MoPubLog.AdapterLogEvent.SHOW_SUCCESS, ADAPTER_NAME);
        }

        @Override
        public void onAdImpression(@NonNull BannerView bannerView) {
            if (mInteractionListener != null) {
                mInteractionListener.onAdShown();
            }
        }

        @Override
        public void onAdClicked(@NonNull BannerView bannerView) {
            MoPubLog.log(getAdNetworkId(), MoPubLog.AdapterLogEvent.CLICKED, ADAPTER_NAME);
            if (mInteractionListener != null) {
                mInteractionListener.onAdClicked();
            }
        }

        @Override
        public void onAdExpired(@NonNull BannerView bannerView) {
            MoPubLog.log(getAdNetworkId(),
                         MoPubLog.AdapterLogEvent.CUSTOM,
                         ADAPTER_NAME,
                         "Ad was expired");
            if (mLoadListener != null) {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.EXPIRED);
            }
        }

    }

}
