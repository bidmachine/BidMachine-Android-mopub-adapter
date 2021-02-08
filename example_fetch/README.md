# HeaderBidding MoPub implementation

* [Overview](#overview)
* [SDK configuration sample](#sdk-configuration-sample)
* [Banner implementation](#banner-implementation)
* [Interstitial implementation](#interstitial-implementation)
* [RewardedVideo implementation](#rewardedvideo-implementation)
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
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubFetchActivity.java#L122)

## Banner implementation
```java
// Create new BidMachine request
BannerRequest bannerRequest = new BannerRequest.Builder()
        .setSize(...)
        .setListener(new AdRequest.AdRequestListener<BannerRequest>() {
            @Override
            public void onRequestSuccess(@NonNull BannerRequest bannerRequest,
                                         @NonNull AuctionResult auctionResult) {
                runOnUiThread(() -> {
                    // Fetch parameters from BidMachine AdRequest and append to MoPubView before load
                    Map<String, String> fetchParams = BidMachineFetcher.fetch(bannerRequest);
                    if (fetchParams != null) {
                        BidMachineUtils.appendRequest(moPubView, fetchParams);
                    }

                    // or
                    // Append BidMachine AdRequest to MoPubView before load
                    // BidMachineUtils.appendRequest(moPubView, bannerRequest);

                    moPubView.loadAd();
                });
            }
        })
        .build();

// Request BidMachine Ads without load it
bannerRequest.request(this);
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubFetchActivity.java#L158)

## Interstitial implementation
```java
// Create new BidMachine request
InterstitialRequest interstitialRequest = new InterstitialRequest.Builder()
        .setListener(new AdRequest.AdRequestListener<InterstitialRequest>() {
            @Override
            public void onRequestSuccess(@NonNull InterstitialRequest interstitialRequest,
                                         @NonNull AuctionResult auctionResult) {
                runOnUiThread(() -> {
                    // Fetch parameters from BidMachine AdRequest and append to MoPubInterstitial before load
                    Map<String, String> fetchParams = BidMachineFetcher.fetch(interstitialRequest);
                    if (fetchParams != null) {
                        BidMachineUtils.appendRequest(moPubInterstitial, fetchParams);
                    }

                    // or
                    // Append BidMachine AdRequest to MoPubInterstitial before load
                    // BidMachineUtils.appendRequest(moPubInterstitial, interstitialRequest);

                    moPubInterstitial.load();
                });
            }
        })
        .build();

// Request BidMachine Ads without load it
interstitialRequest.request(this);
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubFetchActivity.java#L241)

## RewardedVideo implementation
```java
// Create new BidMachine request
RewardedRequest request = new RewardedRequest.Builder()
        .setListener(new AdRequest.AdRequestListener<RewardedRequest>() {
            @Override
            public void onRequestSuccess(@NonNull RewardedRequest rewardedRequest,
                                         @NonNull AuctionResult auctionResult) {
                // Fetch BidMachine Ads params
                Map<String, String> fetchParams = BidMachineFetcher.fetch(rewardedRequest);
                if (fetchParams != null) {
                    // Prepare MoPub keywords
                    String keywords = BidMachineUtils.toKeywords(fetchParams);
                    
                    // Request callbacks run in background thread, but you should call MoPub load methods on UI thread
                    runOnUiThread(() -> {
                        // Set MoPub Rewarded listener if required
                        MoPubRewardedVideos.setRewardedVideoListener(new RewardedVideoListener());
                    
                        // Load MoPub Rewarded video
                        MoPubRewardedVideos.loadRewardedVideo(
                                REWARDED_KEY,
                                // Set MoPub Rewarded keywords
                                new MoPubRewardedVideoManager.RequestParameters(keywords),
                                // Create BidMachine MediationSettings with fetched request id
                                new BidMachineMediationSettings(fetchParams));
                    });
                }
            }
        })
        .build();

// Request BidMachine Ads without load it
request.request(this);
```

[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubFetchActivity.java#L319)

## Native implementation
```java
// Create new BidMachine request
NativeRequest request = new NativeRequest.Builder()
        .setListener(new AdRequest.AdRequestListener<NativeRequest>() {
            @Override
            public void onRequestSuccess(@NonNull NativeRequest nativeRequest,
                                         @NonNull AuctionResult auctionResult) {
                // Fetch BidMachine Ads
                Map<String, String> fetchParams = BidMachineFetcher.fetch(nativeRequest);
                if (fetchParams != null) {
                    // Prepare MoPub keywords
                    String keywords = BidMachineUtils.toKeywords(fetchParams);

                    // Request callbacks run in background thread, but you should call MoPub load methods on UI thread
                    runOnUiThread(() -> {
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
                    });
                }
            }
        })
        .build();

// Request BidMachine Ads without load it
request.request(this);
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubFetchActivity.java#L396)