package com.mopub.mobileads;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mopub.common.DataKeys;
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

public class BidMachineRewardedVideo extends CustomEventRewardedVideo {

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
                                            @NonNull Map<String, Object> moPubLocalExtras,
                                            @NonNull Map<String, String> serverExtras) throws Exception {
        BidMachineMediationSettings settings = getMediationSettings(moPubLocalExtras);
        Map<String, Object> localExtras = getBidMachineLocalExtras(settings);
        return BidMachineUtils.prepareBidMachine(
                launcherActivity,
                BidMachineUtils.getFusedMap(serverExtras, localExtras),
                true);
    }

    @Override
    protected void loadWithSdkInitialized(@NonNull Activity activity,
                                          @NonNull Map<String, Object> moPubLocalExtras,
                                          @NonNull Map<String, String> serverExtras) throws Exception {
        adUnitId = UUID.randomUUID().toString();
        BidMachineMediationSettings settings = getMediationSettings(moPubLocalExtras);
        Map<String, Object> localExtras = getBidMachineLocalExtras(settings);
        Map<String, Object> fusedMap = BidMachineUtils.getFusedMap(serverExtras, localExtras);
        BidMachineUtils.prepareBidMachine(activity, fusedMap, true);
        RewardedRequest request;
        if (settings != null && settings.getRequestId() != null) {
            request = BidMachineUtils.obtainCachedRequest(AdsType.Rewarded,
                                                          settings.getRequestId());
            if (request == null) {
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM,
                             ADAPTER_NAME,
                             "Fetched AdRequest not found");
                MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_FAILED,
                             ADAPTER_NAME,
                             MoPubErrorCode.NO_FILL.getIntCode(),
                             MoPubErrorCode.NO_FILL);

            } else {
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM,
                             ADAPTER_NAME,
                             "Fetched request resolved: " + request.getAuctionResult());
            }
        } else if (fusedMap.containsKey(BidMachineFetcher.KEY_ID)) {
            request = BidMachineUtils.obtainCachedRequest(AdsType.Rewarded, fusedMap);
            if (request == null) {
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM,
                             ADAPTER_NAME,
                             "Fetched AdRequest not found");
                MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_FAILED,
                             ADAPTER_NAME,
                             MoPubErrorCode.NO_FILL.getIntCode(),
                             MoPubErrorCode.NO_FILL);

            } else {
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM,
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
            rewardedAd = new RewardedAd(activity);
            rewardedAd.setListener(new BidMachineAdListener());
            rewardedAd.load(request);

            MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED,
                         ADAPTER_NAME);
        } else {
            MoPubRewardedVideoManager.onRewardedVideoLoadFailure(
                    BidMachineRewardedVideo.class,
                    getAdNetworkId(),
                    MoPubErrorCode.NO_FILL);
        }
    }

    @NonNull
    @Override
    protected String getAdNetworkId() {
        return adUnitId;
    }

    @Override
    protected boolean hasVideoAvailable() {
        return rewardedAd != null && rewardedAd.canShow();
    }

    @Override
    protected boolean isReady() {
        return rewardedAd != null && rewardedAd.canShow();
    }

    @Override
    protected void showVideo() {
        MoPubLog.log(MoPubLog.AdapterLogEvent.SHOW_ATTEMPTED,
                     ADAPTER_NAME);
        if (rewardedAd != null && rewardedAd.canShow()) {
            rewardedAd.show();
        } else {
            MoPubLog.log(MoPubLog.AdapterLogEvent.SHOW_FAILED,
                         ADAPTER_NAME,
                         MoPubErrorCode.NETWORK_NO_FILL.getIntCode(),
                         MoPubErrorCode.NETWORK_NO_FILL);
            MoPubRewardedVideoManager.onRewardedVideoPlaybackError(
                    BidMachineRewardedVideo.class,
                    getAdNetworkId(),
                    MoPubErrorCode.NETWORK_NO_FILL);
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
    private BidMachineMediationSettings getMediationSettings(@NonNull Map<String, Object> localExtras) {
        BidMachineMediationSettings mediationSettings = MoPubRewardedVideoManager.getInstanceMediationSettings(
                BidMachineMediationSettings.class,
                String.valueOf(localExtras.get(DataKeys.AD_UNIT_ID_KEY)));
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
        public void onAdShowFailed(@NonNull RewardedAd rewardedAd, @NonNull BMError bmError) {
            MoPubLog.log(MoPubLog.AdapterLogEvent.SHOW_FAILED,
                         ADAPTER_NAME);
            MoPubRewardedVideoManager.onRewardedVideoPlaybackError(
                    BidMachineRewardedVideo.class,
                    getAdNetworkId(),
                    MoPubErrorCode.VIDEO_PLAYBACK_ERROR);
        }

        @Override
        public void onAdClosed(@NonNull RewardedAd rewardedAd, boolean b) {
            MoPubLog.log(MoPubLog.AdapterLogEvent.DID_DISAPPEAR,
                         ADAPTER_NAME);
            MoPubRewardedVideoManager.onRewardedVideoClosed(
                    BidMachineRewardedVideo.class,
                    getAdNetworkId());
        }

        @Override
        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
            MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_SUCCESS,
                         ADAPTER_NAME);
            MoPubRewardedVideoManager.onRewardedVideoLoadSuccess(
                    BidMachineRewardedVideo.class,
                    getAdNetworkId());
        }

        @Override
        public void onAdLoadFailed(@NonNull RewardedAd rewardedAd, @NonNull BMError bmError) {
            MoPubErrorCode moPubErrorCode = BidMachineUtils.transformToMoPubErrorCode(bmError);
            MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_FAILED,
                         ADAPTER_NAME,
                         moPubErrorCode.getIntCode(),
                         moPubErrorCode);
            MoPubRewardedVideoManager.onRewardedVideoLoadFailure(
                    BidMachineRewardedVideo.class,
                    getAdNetworkId(),
                    moPubErrorCode);
        }

        @Override
        public void onAdShown(@NonNull RewardedAd rewardedAd) {
            MoPubLog.log(MoPubLog.AdapterLogEvent.SHOW_SUCCESS,
                         ADAPTER_NAME);
            MoPubRewardedVideoManager.onRewardedVideoStarted(
                    BidMachineRewardedVideo.class,
                    getAdNetworkId());
        }

        @Override
        public void onAdImpression(@NonNull RewardedAd rewardedAd) {
            //ignore
        }

        @Override
        public void onAdClicked(@NonNull RewardedAd rewardedAd) {
            MoPubLog.log(MoPubLog.AdapterLogEvent.CLICKED,
                         ADAPTER_NAME);
            MoPubRewardedVideoManager.onRewardedVideoClicked(
                    BidMachineRewardedVideo.class,
                    getAdNetworkId());
        }

        @Override
        public void onAdExpired(@NonNull RewardedAd rewardedAd) {
            MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM,
                         ADAPTER_NAME,
                         "Ad was expired");
            MoPubRewardedVideoManager.onRewardedVideoLoadFailure(
                    BidMachineRewardedVideo.class,
                    getAdNetworkId(),
                    MoPubErrorCode.EXPIRED);
        }

        @Override
        public void onAdRewarded(@NonNull RewardedAd rewardedAd) {
            MoPubReward moPubReward = MoPubReward.success(
                    MoPubReward.NO_REWARD_LABEL,
                    MoPubReward.DEFAULT_REWARD_AMOUNT);
            MoPubLog.log(MoPubLog.AdapterLogEvent.SHOULD_REWARD,
                         ADAPTER_NAME,
                         moPubReward.getAmount(),
                         moPubReward.getLabel());
            MoPubRewardedVideoManager.onRewardedVideoCompleted(
                    BidMachineRewardedVideo.class,
                    getAdNetworkId(),
                    moPubReward);
        }
    }

}
