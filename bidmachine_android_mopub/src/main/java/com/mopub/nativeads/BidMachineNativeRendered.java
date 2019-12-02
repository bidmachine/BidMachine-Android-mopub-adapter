package com.mopub.nativeads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mopub.common.logging.MoPubLog;

import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import io.bidmachine.nativead.view.NativeAdContentLayout;

public class BidMachineNativeRendered implements MoPubAdRenderer<BidMachineNativeAd> {

    private final BidMachineViewBinder viewBinder;
    private final WeakHashMap<View, BidMachineNativeViewHolder> viewHolderMap;

    public BidMachineNativeRendered(@NonNull BidMachineViewBinder viewBinder) {
        this.viewBinder = viewBinder;
        viewHolderMap = new WeakHashMap<>();
    }

    @NonNull
    @Override
    public View createAdView(@NonNull Context context, @Nullable ViewGroup parent) {
        return LayoutInflater.from(context).inflate(viewBinder.layoutId, parent, false);
    }

    @Override
    public void renderAdView(@NonNull View view, @NonNull BidMachineNativeAd nativeAd) {
        BidMachineNativeViewHolder viewHolder = viewHolderMap.get(view);
        if (viewHolder == null) {
            viewHolder = BidMachineNativeViewHolder.fromViewBinder(view, viewBinder);
            viewHolderMap.put(view, viewHolder);
        }
        update(viewHolder, nativeAd);
    }

    @Override
    public boolean supports(@NonNull BaseNativeAd nativeAd) {
        return nativeAd instanceof BidMachineNativeAd;
    }

    private void update(@NonNull BidMachineNativeViewHolder viewHolder,
                        @NonNull BidMachineNativeAd bidMachineNativeAd) {
        if (viewHolder.nativeAdContentLayout == null) {
            MoPubLog.log(MoPubLog.AdapterLogEvent.SHOW_FAILED,
                         "Error during registering native ad: NativeAdContentLayout don't found on layout");
            return;
        }
        viewHolder.nativeAdContentLayout.bind(bidMachineNativeAd.getNativeAd());
        viewHolder.nativeAdContentLayout.registerViewForInteraction(
                bidMachineNativeAd.getNativeAd(),
                viewHolder.clickableViews);
    }

    private static final class BidMachineNativeViewHolder {

        private NativeAdContentLayout nativeAdContentLayout;
        private Set<View> clickableViews;

        static BidMachineNativeViewHolder fromViewBinder(@NonNull final View view,
                                                         @NonNull BidMachineViewBinder viewBinder) {
            BidMachineNativeViewHolder viewHolder = new BidMachineNativeViewHolder();
            try {
                viewHolder.nativeAdContentLayout = view.findViewById(viewBinder.nativeAdContentId);
                viewHolder.clickableViews = findClickableViews(view, viewBinder.clickableViewIds);
                return viewHolder;
            } catch (Exception e) {
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM_WITH_THROWABLE,
                             "Error during filling ViewHolder ",
                             e);
                return new BidMachineNativeViewHolder();
            }
        }

        @Nullable
        private static Set<View> findClickableViews(@NonNull View container,
                                                    @Nullable Set<Integer> clickableViewIds) {
            if (clickableViewIds == null || clickableViewIds.isEmpty()) {
                return null;
            }
            Set<View> clickableViews = new HashSet<>();
            for (int id : clickableViewIds) {
                View view = container.findViewById(id);
                if (view != null) {
                    clickableViews.add(view);
                }
            }
            return clickableViews;
        }

    }

}