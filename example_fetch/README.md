# HeaderBidding MoPub implementation

* [Overview](#overview)
* [SDK configuration sample](#sdk-configuration-sample)
* [Banner implementation](#banner-implementation)
* [MREC implementation](#mrec-implementation)
* [Interstitial implementation](#interstitial-implementation)
* [Rewarded implementation](#rewarded-implementation)
* [Native implementation](#native-implementation)

## Overview
For this integration method to work correctly, you must:
1. Load the BidMachine ad request
2. If the BidMachine ad request loaded successfully, the MoPub ad object should be configured:
   * Save BidMachine ad request via BidMachineFetcher.fetch
   * Append parameters to keywords
   * Append local extras
3. Load the MoPub ad object

## SDK configuration sample
```java
// Initialize BidMachine SDK first
BidMachine.setTestMode(true);
BidMachine.setLoggingEnabled(true);
BidMachine.initialize(this, BID_MACHINE_SELLER_ID);

// Prepare SdkConfiguration for initialize MoPub with BidMachineAdapterConfiguration
SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(AD_UNIT_ID)
        .withLogLevel(MoPubLog.LogLevel.DEBUG)
        .withAdditionalNetwork(BidMachineAdapterConfiguration.class.getName())
        .build();

// Initialize MoPub SDK
MoPub.initializeSdk(this, sdkConfiguration, new InitializationListener());
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubFetchActivity.java#L131)

## Banner implementation
```java
private void loadBanner() {
    // Create new MoPubView instance
    bannerMoPubView = new MoPubView(this);
    bannerMoPubView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                               ViewGroup.LayoutParams.MATCH_PARENT));
    bannerMoPubView.setAutorefreshEnabled(false);
    bannerMoPubView.setAdUnitId(BANNER_KEY);
    bannerMoPubView.setBannerAdListener(new BannerViewListener());

    // Create new BidMachine request
    BannerRequest bannerRequest = new BannerRequest.Builder()
            .setSize(BannerSize.Size_320x50)
            .setListener(new AdRequest.AdRequestListener<BannerRequest>() {
                @Override
                public void onRequestSuccess(@NonNull BannerRequest bannerRequest,
                                             @NonNull AuctionResult auctionResult) {
                    runOnUiThread(() -> loadMoPubBanner(bannerRequest));
                }
            })
            .build();

    // Request BidMachine Ads without load it
    bannerRequest.request(this);
}

private void loadMoPubBanner(@NonNull BannerRequest bannerRequest) {
    // Fetch parameters from BidMachine AdRequest and append to MoPubView before load
    Map<String, String> fetchParams = BidMachineFetcher.fetch(bannerRequest);
    if (fetchParams != null) {
        BidMachineUtils.appendRequest(bannerMoPubView, fetchParams);
    }

    // or
    // Append BidMachine AdRequest to MoPubView before load
    // BidMachineUtils.appendRequest(bannerMoPubView, bannerRequest);

    bannerMoPubView.loadAd(MoPubView.MoPubAdSize.HEIGHT_50);
}
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubFetchActivity.java#L173)

### Auto Refresh
To use BidMachine with enabled autorefresh on MoPub SDK please use following MoPub callbacks
to clear BidMachine keywords within MoPubView and then make another BidMachine ad request.
```java
MoPubView.BannerAdListener#onBannerLoaded(MoPubView)
```
```java
MoPubView.BannerAdListener#onBannerFailed(MoPubView, MoPubErrorCode)
```

[*Example*](src/main/java/io/bidmachine/examples/BannerAutoRefreshActivity.java)

## MREC implementation
```java
private void loadMrec() {
    // Create new MoPubView instance
    mrecMoPubView = new MoPubView(this);
    mrecMoPubView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                             ViewGroup.LayoutParams.MATCH_PARENT));
    mrecMoPubView.setAutorefreshEnabled(false);
    mrecMoPubView.setAdUnitId(MREC_KEY);
    mrecMoPubView.setBannerAdListener(new MrecViewListener());

    // Create new BidMachine request
    BannerRequest bannerRequest = new BannerRequest.Builder()
            .setSize(BannerSize.Size_300x250)
            .setListener(new AdRequest.AdRequestListener<BannerRequest>() {
                @Override
                public void onRequestSuccess(@NonNull BannerRequest bannerRequest,
                                             @NonNull AuctionResult auctionResult) {
                    runOnUiThread(() -> loadMoPubMrec(bannerRequest));
                }
            })
            .build();

    // Request BidMachine Ads without load it
    bannerRequest.request(this);
}

private void loadMoPubMrec(@NonNull BannerRequest bannerRequest) {
    // Fetch parameters from BidMachine AdRequest and append to MoPubView before load
    Map<String, String> fetchParams = BidMachineFetcher.fetch(bannerRequest);
    if (fetchParams != null) {
        BidMachineUtils.appendRequest(mrecMoPubView, fetchParams);
    }

    // or
    // Append BidMachine AdRequest to MoPubView before load
    // BidMachineUtils.appendRequest(mrecMoPubView, bannerRequest);

    mrecMoPubView.loadAd(MoPubView.MoPubAdSize.HEIGHT_250);
}
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubFetchActivity.java#L263)

## Interstitial implementation
```java
private void loadInterstitial() {
    // Create new MoPubInterstitial instance
    moPubInterstitial = new MoPubInterstitial(this, INTERSTITIAL_KEY);
    moPubInterstitial.setInterstitialAdListener(new InterstitialListener());

    // Create new BidMachine request
    InterstitialRequest interstitialRequest = new InterstitialRequest.Builder()
            .setListener(new AdRequest.AdRequestListener<InterstitialRequest>() {
                @Override
                public void onRequestSuccess(@NonNull InterstitialRequest interstitialRequest,
                                             @NonNull AuctionResult auctionResult) {
                    runOnUiThread(() -> loadMoPubInterstitial(interstitialRequest));
                }
            })
            .build();

    // Request BidMachine Ads without load it
    interstitialRequest.request(this);
}
    
private void loadMoPubInterstitial(@NonNull InterstitialRequest interstitialRequest) {
    // Fetch parameters from BidMachine AdRequest and append to MoPubInterstitial before load
    Map<String, String> fetchParams = BidMachineFetcher.fetch(interstitialRequest);
    if (fetchParams != null) {
        BidMachineUtils.appendRequest(moPubInterstitial, fetchParams);
    }

    // or
    // Append BidMachine AdRequest to MoPubInterstitial before load
    // BidMachineUtils.appendRequest(moPubInterstitial, interstitialRequest);

    moPubInterstitial.load();
}
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubFetchActivity.java#L353)

## Rewarded implementation
```java
private void loadRewarded() {
    // Create new BidMachine request
    RewardedRequest request = new RewardedRequest.Builder()
            .setListener(new AdRequest.AdRequestListener<RewardedRequest>() {
                @Override
                public void onRequestSuccess(@NonNull RewardedRequest rewardedRequest,
                                             @NonNull AuctionResult auctionResult) {
                    runOnUiThread(() -> loadMoPubRewarded(rewardedRequest));
                }
            })
            .build();

    // Request BidMachine Ads without load it
    request.request(this);
}

private void loadMoPubRewarded(@NonNull RewardedRequest rewardedRequest) {
    // Fetch BidMachine Ads
    Map<String, String> fetchParams = BidMachineFetcher.fetch(rewardedRequest);
    if (fetchParams != null) {
        // Prepare MoPub keywords
        String keywords = BidMachineUtils.toKeywords(fetchParams);

        // Set MoPub Rewarded listener if required
        MoPubRewardedAds.setRewardedAdListener(new RewardedAdListener());

        // Load MoPub Rewarded
        MoPubRewardedAds.loadRewardedAd(REWARDED_KEY,
                                        // Set MoPub Rewarded keywords
                                        new MoPubRewardedAdManager.RequestParameters(keywords),
                                        // Create BidMachine MediationSettings with fetched request id
                                        new BidMachineMediationSettings(fetchParams));
    } else {
        Toast.makeText(this, "RewardedFetchFailed", Toast.LENGTH_SHORT).show();
    }
}
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubFetchActivity.java#L438)

## Native implementation
```java
private void loadNative() {
    // Create new MoPubNative instance
    BidMachineViewBinder viewBinder = new BidMachineViewBinder(R.layout.native_ad,
                                                               R.id.native_ad_container);
    moPubNative = new MoPubNative(this, NATIVE_KEY, new NativeListener());
    moPubNative.registerAdRenderer(new BidMachineNativeRendered(viewBinder));

    // Create new BidMachine request
    NativeRequest request = new NativeRequest.Builder()
            .setListener(new AdRequest.AdRequestListener<NativeRequest>() {
                @Override
                public void onRequestSuccess(@NonNull NativeRequest nativeRequest,
                                             @NonNull AuctionResult auctionResult) {
                    runOnUiThread(() -> loadMoPubNative(nativeRequest));
                }
            })
            .build();

    // Request BidMachine Ads without load it
    request.request(this);
}

private void loadMoPubNative(@NonNull NativeRequest nativeRequest) {
    // Fetch BidMachine Ads
    Map<String, String> fetchParams = BidMachineFetcher.fetch(nativeRequest);
    if (fetchParams != null) {
        // Prepare MoPub keywords
        String keywords = BidMachineUtils.toKeywords(fetchParams);

        // Prepare localExtras for set to MoPubNative with additional fetching parameters
        Map<String, Object> localExtras = new HashMap<>(fetchParams);

        // Set MoPub local extras
        moPubNative.setLocalExtras(localExtras);

        // Set MoPub Native keywords
        RequestParameters requestParameters = new RequestParameters.Builder()
                .keywords(keywords)
                .build();

        // Load MoPub Ads
        moPubNative.makeRequest(requestParameters);
    } else {
        Toast.makeText(this, "NativeFetchFailed", Toast.LENGTH_SHORT).show();
    }
}
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubFetchActivity.java#L518)