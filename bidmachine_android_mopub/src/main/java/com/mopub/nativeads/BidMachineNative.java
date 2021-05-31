package com.mopub.nativeads;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.BidMachineUtils;
import com.mopub.mobileads.MoPubErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.bidmachine.AdsType;
import io.bidmachine.BidMachineFetcher;
import io.bidmachine.MediaAssetType;
import io.bidmachine.core.Utils;
import io.bidmachine.nativead.NativeRequest;

public class BidMachineNative extends CustomEventNative {

    static final String ADAPTER_NAME = BidMachineNative.class.getSimpleName();

    @Override
    protected void loadNativeAd(@NonNull Context context,
                                @NonNull CustomEventNativeListener customEventNativeListener,
                                @NonNull Map<String, Object> localExtras,
                                @NonNull Map<String, String> serverExtras) {
        Map<String, Object> fusedMap = BidMachineUtils.getFusedMap(serverExtras, localExtras);
        BidMachineUtils.prepareBidMachine(context, fusedMap, true);

        NativeRequest request;
        if (fusedMap.containsKey(BidMachineFetcher.KEY_ID)) {
            request = BidMachineUtils.obtainCachedRequest(AdsType.Native, fusedMap);
            if (request == null) {
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM,
                             ADAPTER_NAME,
                             "Fetched AdRequest not found");
                MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_FAILED,
                             ADAPTER_NAME,
                             MoPubErrorCode.NO_FILL.getIntCode(),
                             MoPubErrorCode.NO_FILL);
            } else {
                request.notifyMediationWin();

                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM,
                             ADAPTER_NAME,
                             "Fetched request resolved: " + request.getAuctionResult());
            }
        } else {
            request = new NativeRequest.Builder()
                    .setTargetingParams(BidMachineUtils.findTargetingParams(fusedMap))
                    .setPriceFloorParams(BidMachineUtils.findPriceFloorParams(fusedMap))
                    .setMediaAssetTypes(findMediaAssetTypes(fusedMap))
                    .setPlacementId(BidMachineUtils.findPlacementId(fusedMap))
                    .build();
        }
        if (request != null) {
            BidMachineNativeAd bidMachineNativeAd = new BidMachineNativeAd();
            bidMachineNativeAd.load(context, request, customEventNativeListener);

            MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED,
                         ADAPTER_NAME);
        } else {
            customEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
        }
    }

    @NonNull
    private MediaAssetType[] findMediaAssetTypes(@NonNull Map<String, Object> extras) {
        List<MediaAssetType> mediaAssetTypeList = new ArrayList<>();
        String value = BidMachineUtils.parseString(extras.get(BidMachineUtils.MEDIA_ASSET_TYPES));
        String[] mediaAssetTypeStringArray = BidMachineUtils.splitString(value);
        for (String mediaAssetTypeString : mediaAssetTypeStringArray) {
            if (TextUtils.isEmpty(mediaAssetTypeString)) {
                continue;
            }
            assert mediaAssetTypeString != null;
            try {
                String resultValue = Utils.capitalize(mediaAssetTypeString.trim());
                MediaAssetType mediaAssetType = MediaAssetType.valueOf(resultValue);
                mediaAssetTypeList.add(mediaAssetType);
            } catch (Exception ignore) {
            }
        }
        return mediaAssetTypeList.toArray(new MediaAssetType[0]);
    }

}