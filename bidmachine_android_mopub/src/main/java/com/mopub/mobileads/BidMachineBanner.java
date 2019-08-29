package com.mopub.mobileads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mopub.common.DataKeys;
import com.mopub.common.logging.MoPubLog;
import com.mopub.common.util.Views;

import java.util.Map;

import io.bidmachine.AdsType;
import io.bidmachine.BidMachineFetcher;
import io.bidmachine.banner.BannerListener;
import io.bidmachine.banner.BannerRequest;
import io.bidmachine.banner.BannerSize;
import io.bidmachine.banner.BannerView;
import io.bidmachine.utils.BMError;

public class BidMachineBanner extends CustomEventBanner {

    private static final String ADAPTER_NAME = BidMachineBanner.class.getSimpleName();
    private static final String BANNER_WIDTH = "banner_width";

    private CustomEventBannerListener customBannerListener;
    private BannerView bannerView;

    @Override
    protected void loadBanner(Context context,
                              CustomEventBannerListener customEventBannerListener,
                              Map<String, Object> localExtras,
                              Map<String, String> serverExtras) {
        setAutomaticImpressionAndClickTracking(false);
        customBannerListener = customEventBannerListener;

        Map<String, Object> fusedMap = BidMachineUtils.getFusedMap(serverExtras, localExtras);
        BidMachineUtils.prepareBidMachine(context, fusedMap, true);
        BannerRequest request = null;
        BannerSize bannerSize = null;
        MoPubErrorCode errorCode = null;
        if (fusedMap.containsKey(BidMachineFetcher.KEY_ID)) {
            request = BidMachineUtils.obtainCachedRequest(AdsType.Banner, fusedMap);
            if (request == null) {
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM,
                             ADAPTER_NAME,
                             "Fetched AdRequest not found");
                MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_FAILED,
                             ADAPTER_NAME,
                             MoPubErrorCode.NO_FILL.getIntCode(),
                             MoPubErrorCode.NO_FILL);
                errorCode = MoPubErrorCode.NO_FILL;
            } else {
                bannerSize = request.getSize();
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM,
                             ADAPTER_NAME,
                             "Fetched request resolved: " + request.getAuctionResult());
            }
        } else {
            bannerSize = findBannerSize(fusedMap, BANNER_WIDTH);
            if (bannerSize == null) {
                bannerSize = findBannerSize(fusedMap, DataKeys.AD_WIDTH);
            }
            if (bannerSize == null) {
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM,
                             ADAPTER_NAME,
                             "Unsupported banner size");
                MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_FAILED,
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

            MoPubLog.log(
                    MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED,
                    ADAPTER_NAME,
                    ", with size: ",
                    bannerSize);
        } else if (customBannerListener != null) {
            customBannerListener.onBannerFailed(errorCode != null
                                                        ? errorCode
                                                        : MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        }
    }

    @Override
    protected void onInvalidate() {
        if (bannerView != null) {
            Views.removeFromParent(bannerView);
            bannerView.setListener(null);
            bannerView.destroy();
            bannerView = null;
        }
        customBannerListener = null;
    }

    private <T> BannerSize findBannerSize(@Nullable Map<String, T> extras, String key) {
        if (extras == null) {
            return null;
        }

        int width = BidMachineUtils.parseInteger(extras.get(key));
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

    private class BidMachineAdListener implements BannerListener {

        @Override
        public void onAdLoaded(@NonNull BannerView bannerView) {
            MoPubLog.log(
                    MoPubLog.AdapterLogEvent.LOAD_SUCCESS,
                    ADAPTER_NAME);
            MoPubLog.log(
                    MoPubLog.AdapterLogEvent.SHOW_ATTEMPTED,
                    ADAPTER_NAME);
            MoPubLog.log(
                    MoPubLog.AdapterLogEvent.SHOW_SUCCESS,
                    ADAPTER_NAME);
            if (customBannerListener != null) {
                customBannerListener.onBannerLoaded(bannerView);
            }
        }

        @Override
        public void onAdLoadFailed(@NonNull BannerView bannerView, @NonNull BMError bmError) {
            MoPubErrorCode moPubErrorCode = BidMachineUtils.transformToMoPubErrorCode(bmError);
            MoPubLog.log(
                    MoPubLog.AdapterLogEvent.LOAD_FAILED,
                    ADAPTER_NAME,
                    moPubErrorCode.getIntCode(),
                    moPubErrorCode);
            if (customBannerListener != null) {
                customBannerListener.onBannerFailed(moPubErrorCode);
            }
        }

        @Override
        public void onAdShown(@NonNull BannerView bannerView) {
            MoPubLog.log(
                    MoPubLog.AdapterLogEvent.SHOW_SUCCESS,
                    ADAPTER_NAME);
        }

        @Override
        public void onAdImpression(@NonNull BannerView bannerView) {
            if (customBannerListener != null) {
                customBannerListener.onBannerImpression();
            }
        }

        @Override
        public void onAdClicked(@NonNull BannerView bannerView) {
            MoPubLog.log(
                    MoPubLog.AdapterLogEvent.CLICKED,
                    ADAPTER_NAME);
            if (customBannerListener != null) {
                customBannerListener.onBannerClicked();
            }
        }

        @Override
        public void onAdExpired(@NonNull BannerView bannerView) {
            MoPubLog.log(
                    MoPubLog.AdapterLogEvent.CUSTOM,
                    ADAPTER_NAME,
                    "Ad was expired");
            if (customBannerListener != null) {
                customBannerListener.onBannerFailed(MoPubErrorCode.EXPIRED);
            }
        }

    }

}
