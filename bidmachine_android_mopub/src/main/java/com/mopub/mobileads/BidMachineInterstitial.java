package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mopub.common.LifecycleListener;
import com.mopub.common.logging.MoPubLog;

import java.util.Map;
import java.util.UUID;

import io.bidmachine.AdContentType;
import io.bidmachine.AdsType;
import io.bidmachine.BidMachineFetcher;
import io.bidmachine.interstitial.InterstitialAd;
import io.bidmachine.interstitial.InterstitialListener;
import io.bidmachine.interstitial.InterstitialRequest;
import io.bidmachine.utils.BMError;

public class BidMachineInterstitial extends BaseAd {

    private static final String ADAPTER_NAME = BidMachineInterstitial.class.getSimpleName();
    private static final String AD_CONTENT_TYPE = "ad_content_type";

    private String adUnitId = "";
    private InterstitialAd interstitialAd;

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

        InterstitialRequest request;
        if (fusedMap.containsKey(BidMachineFetcher.KEY_ID)) {
            request = BidMachineUtils.obtainCachedRequest(AdsType.Interstitial, fusedMap);
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
            } else {
                request.notifyMediationWin();

                MoPubLog.log(getAdNetworkId(),
                             MoPubLog.AdapterLogEvent.CUSTOM,
                             ADAPTER_NAME,
                             "Fetched request resolved: " + request.getAuctionResult());
            }
        } else {
            InterstitialRequest.Builder interstitialRequestBuilder = new InterstitialRequest.Builder()
                    .setTargetingParams(BidMachineUtils.findTargetingParams(fusedMap))
                    .setPriceFloorParams(BidMachineUtils.findPriceFloorParams(fusedMap));
            AdContentType adContentType = findAdContentType(fusedMap);
            if (adContentType != null) {
                interstitialRequestBuilder.setAdContentType(adContentType);
            } else {
                MoPubLog.log(getAdNetworkId(),
                             MoPubLog.AdapterLogEvent.CUSTOM,
                             ADAPTER_NAME,
                             "ad_content_type not found, will be used default AdContentType");
            }
            request = interstitialRequestBuilder.build();
        }

        if (request != null) {
            interstitialAd = new InterstitialAd(context);
            interstitialAd.setListener(new BidMachineAdListener());
            interstitialAd.load(request);

            MoPubLog.log(getAdNetworkId(), MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED, ADAPTER_NAME);
        } else if (mLoadListener != null) {
            mLoadListener.onAdLoadFailed(MoPubErrorCode.NO_FILL);
        }
    }

    @NonNull
    @Override
    protected String getAdNetworkId() {
        return adUnitId;
    }

    @Override
    protected void show() {
        MoPubLog.log(getAdNetworkId(), MoPubLog.AdapterLogEvent.SHOW_ATTEMPTED, ADAPTER_NAME);
        if (interstitialAd != null && interstitialAd.canShow()) {
            interstitialAd.show();
        } else {
            MoPubLog.log(getAdNetworkId(),
                         MoPubLog.AdapterLogEvent.SHOW_FAILED,
                         ADAPTER_NAME,
                         MoPubErrorCode.NETWORK_NO_FILL.getIntCode(),
                         MoPubErrorCode.NETWORK_NO_FILL);
            if (mInteractionListener != null) {
                mInteractionListener.onAdFailed(MoPubErrorCode.NETWORK_NO_FILL);
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
        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
            MoPubLog.log(getAdNetworkId(), MoPubLog.AdapterLogEvent.LOAD_SUCCESS, ADAPTER_NAME);
            if (mLoadListener != null) {
                mLoadListener.onAdLoaded();
            }
        }

        @Override
        public void onAdLoadFailed(@NonNull InterstitialAd interstitialAd,
                                   @NonNull BMError bmError) {
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
        public void onAdShown(@NonNull InterstitialAd interstitialAd) {
            MoPubLog.log(getAdNetworkId(), MoPubLog.AdapterLogEvent.SHOW_SUCCESS, ADAPTER_NAME);
            if (mInteractionListener != null) {
                mInteractionListener.onAdShown();
            }
        }

        @Override
        public void onAdShowFailed(@NonNull InterstitialAd interstitialAd,
                                   @NonNull BMError bmError) {
            MoPubErrorCode moPubErrorCode = BidMachineUtils.transformToMoPubErrorCode(bmError);
            MoPubLog.log(getAdNetworkId(),
                         MoPubLog.AdapterLogEvent.SHOW_FAILED,
                         ADAPTER_NAME,
                         moPubErrorCode.getIntCode(),
                         moPubErrorCode);
            if (mInteractionListener != null) {
                mInteractionListener.onAdFailed(moPubErrorCode);
            }
        }

        @Override
        public void onAdImpression(@NonNull InterstitialAd interstitialAd) {
            if (mInteractionListener != null) {
                mInteractionListener.onAdImpression();
            }
        }

        @Override
        public void onAdClicked(@NonNull InterstitialAd interstitialAd) {
            MoPubLog.log(getAdNetworkId(), MoPubLog.AdapterLogEvent.CLICKED, ADAPTER_NAME);
            if (mInteractionListener != null) {
                mInteractionListener.onAdClicked();
            }
        }

        @Override
        public void onAdClosed(@NonNull InterstitialAd interstitialAd, boolean b) {
            MoPubLog.log(getAdNetworkId(), MoPubLog.AdapterLogEvent.DID_DISAPPEAR, ADAPTER_NAME);
            if (mInteractionListener != null) {
                mInteractionListener.onAdDismissed();
            }
        }

        @Override
        public void onAdExpired(@NonNull InterstitialAd interstitialAd) {
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
