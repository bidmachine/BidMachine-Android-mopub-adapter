package com.mopub.nativeads;

import java.util.HashSet;
import java.util.Set;

public class BidMachineViewBinder {

    final int layoutId;
    final int nativeAdContentId;
    Set<Integer> clickableViewIds;

    public BidMachineViewBinder(int layoutId, int nativeAdContentId) {
        this.layoutId = layoutId;
        this.nativeAdContentId = nativeAdContentId;
        this.clickableViewIds = new HashSet<>();
    }

    public BidMachineViewBinder addClickableViewId(int viewId) {
        clickableViewIds.add(viewId);
        return this;
    }

}