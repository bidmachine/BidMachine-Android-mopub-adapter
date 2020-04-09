package com.mopub.nativeads;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.BidMachineUtils;

import io.bidmachine.nativead.NativeAd;
import io.bidmachine.nativead.NativeListener;
import io.bidmachine.nativead.NativeRequest;
import io.bidmachine.utils.BMError;

import static com.mopub.nativeads.BidMachineNative.ADAPTER_NAME;

public class BidMachineNativeAd extends BaseNativeAd {

    private NativeAd nativeAd;

    void load(@NonNull Context context,
              @NonNull NativeRequest nativeRequest,
              @NonNull CustomEventNative.CustomEventNativeListener customEventNativeListener) {
        nativeAd = new NativeAd(context);
        nativeAd.setListener(new BidMachineAdListener(customEventNativeListener));
        nativeAd.load(nativeRequest);
    }

    NativeAd getNativeAd() {
        return nativeAd;
    }

    @Override
    public void prepare(@NonNull View view) {

    }

    @Override
    public void clear(@NonNull View view) {
        nativeAd.unregisterView();
    }

    @Override
    public void destroy() {
        if (nativeAd != null) {
            nativeAd.setListener(null);
            nativeAd.destroy();
            nativeAd = null;
        }
    }

    private final class BidMachineAdListener implements NativeListener {

        private final CustomEventNative.CustomEventNativeListener customEventNativeListener;

        BidMachineAdListener(@NonNull CustomEventNative.CustomEventNativeListener customEventNativeListener) {
            this.customEventNativeListener = customEventNativeListener;
        }

        @Override
        public void onAdLoaded(@NonNull NativeAd nativeAd) {
            customEventNativeListener.onNativeAdLoaded(BidMachineNativeAd.this);
            MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_SUCCESS, ADAPTER_NAME);
        }

        @Override
        public void onAdLoadFailed(@NonNull NativeAd nativeAd, @NonNull BMError bmError) {
            NativeErrorCode errorCode = BidMachineUtils.transformToMoPubNativeErrorCode(bmError);
            MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_FAILED,
                         ADAPTER_NAME,
                         errorCode.getIntCode(),
                         errorCode);
            customEventNativeListener.onNativeAdFailed(errorCode);
        }

        @Override
        public void onAdShown(@NonNull NativeAd nativeAd) {

        }

        @Override
        public void onAdImpression(@NonNull NativeAd nativeAd) {
            notifyAdImpressed();
            MoPubLog.log(MoPubLog.AdapterLogEvent.SHOW_SUCCESS, ADAPTER_NAME);
        }

        @Override
        public void onAdClicked(@NonNull NativeAd nativeAd) {
            notifyAdClicked();
            MoPubLog.log(MoPubLog.AdapterLogEvent.CLICKED, ADAPTER_NAME);
        }

        @Override
        public void onAdExpired(@NonNull NativeAd nativeAd) {
            MoPubLog.log(MoPubLog.AdapterLogEvent.EXPIRED, ADAPTER_NAME);
        }
    }

}
