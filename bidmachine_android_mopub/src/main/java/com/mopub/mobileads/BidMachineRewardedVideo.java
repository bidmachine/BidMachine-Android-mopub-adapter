package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mopub.common.LifecycleListener;
import com.mopub.common.MoPubReward;
import com.mopub.common.logging.MoPubLog;

import java.util.Map;
import java.util.UUID;

import io.bidmachine.AdsType;
import io.bidmachine.BidMachineFetcher;
import io.bidmachine.rewarded.RewardedAd;
import io.bidmachine.rewarded.RewardedListener;
import io.bidmachine.rewarded.RewardedRequest;
import io.bidmachine.utils.BMError;

public class BidMachineRewardedVideo extends BaseAd {

    private static final String ADAPTER_NAME = BidMachineRewardedVideo.class.getSimpleName();

    private RewardedAd rewardedAd;
    private String adUnitId = "";

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

        BidMachineMediationSettings settings = getMediationSettings(adData);
        Map<String, Object> localExtras = getBidMachineLocalExtras(settings);
        Map<String, Object> fusedMap = BidMachineUtils.getFusedMap(adData.getExtras(), localExtras);
        BidMachineUtils.prepareBidMachine(context, fusedMap, true);

        String bmRequestId = settings != null ? settings.getRequestId() : null;
        RewardedRequest request;
        if (bmRequestId != null) {
            request = BidMachineUtils.obtainCachedRequest(AdsType.Rewarded, bmRequestId);
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
        } else if (fusedMap.containsKey(BidMachineFetcher.KEY_ID)) {
            request = BidMachineUtils.obtainCachedRequest(AdsType.Rewarded, fusedMap);
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
            request = new RewardedRequest.Builder()
                    .setTargetingParams(BidMachineUtils.findTargetingParams(fusedMap))
                    .setPriceFloorParams(BidMachineUtils.findPriceFloorParams(fusedMap))
                    .build();
        }
        if (request != null) {
            rewardedAd = new RewardedAd(context);
            rewardedAd.setListener(new BidMachineAdListener());
            rewardedAd.load(request);

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
        if (rewardedAd != null && rewardedAd.canShow()) {
            rewardedAd.show();
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
        if (rewardedAd != null) {
            rewardedAd.setListener(null);
            rewardedAd.destroy();
            rewardedAd = null;
        }
    }

    @Nullable
    private BidMachineMediationSettings getMediationSettings(@NonNull AdData adData) {
        BidMachineMediationSettings mediationSettings = null;
        String adUnit = adData.getAdUnit();
        if (adUnit != null) {
            mediationSettings = MoPubRewardedVideoManager.getInstanceMediationSettings(
                    BidMachineMediationSettings.class,
                    adUnit);
        }
        if (mediationSettings == null) {
            mediationSettings = MoPubRewardedVideoManager.getGlobalMediationSettings(
                    BidMachineMediationSettings.class);
        }
        return mediationSettings;
    }

    @Nullable
    private Map<String, Object> getBidMachineLocalExtras(@Nullable BidMachineMediationSettings settings) {
        if (settings == null) {
            return null;
        }
        return settings.getLocalExtras();
    }

    private class BidMachineAdListener implements RewardedListener {

        @Override
        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
            MoPubLog.log(getAdNetworkId(), MoPubLog.AdapterLogEvent.LOAD_SUCCESS, ADAPTER_NAME);
            if (mLoadListener != null) {
                mLoadListener.onAdLoaded();
            }
        }

        @Override
        public void onAdLoadFailed(@NonNull RewardedAd rewardedAd, @NonNull BMError bmError) {
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
        public void onAdShown(@NonNull RewardedAd rewardedAd) {
            MoPubLog.log(getAdNetworkId(), MoPubLog.AdapterLogEvent.SHOW_SUCCESS, ADAPTER_NAME);
            if (mInteractionListener != null) {
                mInteractionListener.onAdShown();
            }
        }

        @Override
        public void onAdShowFailed(@NonNull RewardedAd rewardedAd, @NonNull BMError bmError) {
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
        public void onAdImpression(@NonNull RewardedAd rewardedAd) {
            if (mInteractionListener != null) {
                mInteractionListener.onAdImpression();
            }
        }

        @Override
        public void onAdClicked(@NonNull RewardedAd rewardedAd) {
            MoPubLog.log(getAdNetworkId(), MoPubLog.AdapterLogEvent.CLICKED, ADAPTER_NAME);
            if (mInteractionListener != null) {
                mInteractionListener.onAdClicked();
            }
        }

        @Override
        public void onAdClosed(@NonNull RewardedAd rewardedAd, boolean b) {
            MoPubLog.log(getAdNetworkId(), MoPubLog.AdapterLogEvent.DID_DISAPPEAR, ADAPTER_NAME);
            if (mInteractionListener != null) {
                mInteractionListener.onAdDismissed();
            }
        }

        @Override
        public void onAdRewarded(@NonNull RewardedAd rewardedAd) {
            MoPubReward moPubReward = MoPubReward.success(
                    MoPubReward.NO_REWARD_LABEL,
                    MoPubReward.DEFAULT_REWARD_AMOUNT);
            MoPubLog.log(getAdNetworkId(), MoPubLog.AdapterLogEvent.SHOULD_REWARD,
                         ADAPTER_NAME,
                         moPubReward.getAmount(),
                         moPubReward.getLabel());
            if (mInteractionListener != null) {
                mInteractionListener.onAdComplete(moPubReward);
            }
        }

        @Override
        public void onAdExpired(@NonNull RewardedAd rewardedAd) {
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
