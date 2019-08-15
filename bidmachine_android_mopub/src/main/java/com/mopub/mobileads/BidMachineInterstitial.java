package com.mopub.mobileads;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mopub.common.logging.MoPubLog;

import java.util.Map;

import io.bidmachine.AdContentType;
import io.bidmachine.interstitial.InterstitialAd;
import io.bidmachine.interstitial.InterstitialListener;
import io.bidmachine.interstitial.InterstitialRequest;
import io.bidmachine.utils.BMError;

public class BidMachineInterstitial extends CustomEventInterstitial {

    private static final String ADAPTER_NAME = BidMachineInterstitial.class.getSimpleName();
    private static final String AD_CONTENT_TYPE = "ad_content_type";

    private CustomEventInterstitialListener customInterstitialListener;
    private InterstitialAd interstitialAd;

    @Override
    protected void loadInterstitial(Context context,
                                    CustomEventInterstitialListener customEventInterstitialListener,
                                    Map<String, Object> localExtras,
                                    Map<String, String> serverExtras) {
        setAutomaticImpressionAndClickTracking(false);
        customInterstitialListener = customEventInterstitialListener;

        Map<String, Object> fusedMap = BidMachineUtils.getFusedMap(serverExtras, localExtras);
        BidMachineUtils.prepareBidMachine(context, fusedMap, true);
        InterstitialRequest.Builder interstitialRequestBuilder = new InterstitialRequest.Builder()
                .setTargetingParams(BidMachineUtils.findTargetingParams(fusedMap))
                .setPriceFloorParams(BidMachineUtils.findPriceFloorParams(fusedMap));
        AdContentType adContentType = findAdContentType(fusedMap);
        if (adContentType != null) {
            interstitialRequestBuilder.setAdContentType(adContentType);
        } else {
            MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM,
                    ADAPTER_NAME,
                    "ad_content_type not found, will be used default AdContentType");
        }

        interstitialAd = new InterstitialAd(context);
        interstitialAd.setListener(new BidMachineAdListener());
        interstitialAd.load(interstitialRequestBuilder.build());

        MoPubLog.log(
                MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED,
                ADAPTER_NAME);
    }

    @Override
    protected void showInterstitial() {
        MoPubLog.log(
                MoPubLog.AdapterLogEvent.SHOW_ATTEMPTED,
                ADAPTER_NAME);
        if (interstitialAd != null && interstitialAd.canShow()) {
            interstitialAd.show();
        } else {
            MoPubLog.log(
                    MoPubLog.AdapterLogEvent.SHOW_FAILED,
                    ADAPTER_NAME,
                    MoPubErrorCode.NETWORK_NO_FILL.getIntCode(),
                    MoPubErrorCode.NETWORK_NO_FILL);
            if (customInterstitialListener != null) {
                customInterstitialListener.onInterstitialFailed(MoPubErrorCode.INTERNAL_ERROR);
            }
        }
    }

    @Override
    protected void onInvalidate() {
        if (interstitialAd != null) {
            interstitialAd.setListener(null);
            interstitialAd.destroy();
            interstitialAd = null;
        }
        customInterstitialListener = null;
    }

    private <T> AdContentType findAdContentType(@NonNull Map<String, T> extras) {
        try {
            Object value = extras.get(AD_CONTENT_TYPE);
            if (value instanceof String) {
                return AdContentType.valueOf((String) value);
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    private class BidMachineAdListener implements InterstitialListener {

        @Override
        public void onAdShowFailed(@NonNull InterstitialAd interstitialAd, @NonNull BMError bmError) {
            MoPubLog.log(
                    MoPubLog.AdapterLogEvent.SHOW_FAILED,
                    ADAPTER_NAME);
            if (customInterstitialListener != null) {
                customInterstitialListener.onInterstitialFailed(MoPubErrorCode.INTERNAL_ERROR);
            }
        }

        @Override
        public void onAdClosed(@NonNull InterstitialAd interstitialAd, boolean b) {
            MoPubLog.log(
                    MoPubLog.AdapterLogEvent.DID_DISAPPEAR,
                    ADAPTER_NAME);
            if (customInterstitialListener != null) {
                customInterstitialListener.onInterstitialDismissed();
            }
        }

        @Override
        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
            MoPubLog.log(
                    MoPubLog.AdapterLogEvent.LOAD_SUCCESS,
                    ADAPTER_NAME);
            if (customInterstitialListener != null) {
                customInterstitialListener.onInterstitialLoaded();
            }
        }

        @Override
        public void onAdLoadFailed(@NonNull InterstitialAd interstitialAd, @NonNull BMError bmError) {
            MoPubErrorCode moPubErrorCode = BidMachineUtils.transformToMoPubErrorCode(bmError);
            MoPubLog.log(
                    MoPubLog.AdapterLogEvent.LOAD_FAILED,
                    ADAPTER_NAME,
                    moPubErrorCode.getIntCode(),
                    moPubErrorCode);
            if (customInterstitialListener != null) {
                customInterstitialListener.onInterstitialFailed(moPubErrorCode);
            }
        }

        @Override
        public void onAdShown(@NonNull InterstitialAd interstitialAd) {
            MoPubLog.log(
                    MoPubLog.AdapterLogEvent.SHOW_SUCCESS,
                    ADAPTER_NAME);
            if (customInterstitialListener != null) {
                customInterstitialListener.onInterstitialShown();
            }
        }

        @Override
        public void onAdImpression(@NonNull InterstitialAd interstitialAd) {
            if (customInterstitialListener != null) {
                customInterstitialListener.onInterstitialImpression();
            }
        }

        @Override
        public void onAdClicked(@NonNull InterstitialAd interstitialAd) {
            MoPubLog.log(
                    MoPubLog.AdapterLogEvent.CLICKED,
                    ADAPTER_NAME);
            if (customInterstitialListener != null) {
                customInterstitialListener.onInterstitialClicked();
            }
        }

        @Override
        public void onAdExpired(@NonNull InterstitialAd interstitialAd) {
            MoPubLog.log(
                    MoPubLog.AdapterLogEvent.CUSTOM,
                    ADAPTER_NAME,
                    "Ad was expired");
            if (customInterstitialListener != null) {
                customInterstitialListener.onInterstitialFailed(MoPubErrorCode.EXPIRED);
            }
        }
    }

}
