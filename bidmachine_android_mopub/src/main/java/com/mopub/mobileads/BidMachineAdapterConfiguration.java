package com.mopub.mobileads;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mopub.common.BaseAdapterConfiguration;
import com.mopub.common.OnNetworkInitializationFinishedListener;
import com.mopub.mobileads.bidmachine.BuildConfig;

import java.util.Map;

import io.bidmachine.BidMachine;

public class BidMachineAdapterConfiguration extends BaseAdapterConfiguration {

    private static final String NETWORK_VERSION = BidMachine.VERSION;
    private static final String ADAPTER_VERSION = BuildConfig.VERSION_NAME;
    private static final String MOPUB_NETWORK_NAME = "bid_machine";

    @NonNull
    @Override
    public String getAdapterVersion() {
        return ADAPTER_VERSION;
    }

    @Nullable
    @Override
    public String getBiddingToken(@NonNull Context context) {
        return null;
    }

    @NonNull
    @Override
    public String getMoPubNetworkName() {
        return MOPUB_NETWORK_NAME;
    }

    @NonNull
    @Override
    public String getNetworkSdkVersion() {
        return NETWORK_VERSION;
    }

    @Override
    public void initializeNetwork(@NonNull Context context,
                                  @Nullable Map<String, String> configuration,
                                  @NonNull OnNetworkInitializationFinishedListener listener) {
        if (configuration != null) {
            BidMachineUtils.storeConfiguration(configuration);
            BidMachineUtils.prepareBidMachine(context, configuration, false);
        }
        listener.onNetworkInitializationFinished(BidMachineAdapterConfiguration.class,
                                                 MoPubErrorCode.ADAPTER_INITIALIZATION_SUCCESS);
    }
}
