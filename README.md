# BidMachine Android MoPubAdapter

[<img src="https://img.shields.io/badge/SDK%20Version-1.6.1-brightgreen">](https://github.com/bidmachine/BidMachine-Android-SDK)
[<img src="https://img.shields.io/badge/Adapter%20Version-1.6.1.13-green">](https://artifactory.bidmachine.io/bidmachine/io/bidmachine/ads.adapters.mopub/1.6.1.13/)
[<img src="https://img.shields.io/badge/MoPub%20Version-5.14.0-blue">](https://developers.mopub.com/publishers/android/integrate/)

* [Useful links](#useful-links)
* [Integration](#integration)
* [Classic implementation](#classic-implementation)
  * [SDK configuration sample](#sdk-configuration-sample)
  * [Banner implementation](#banner-implementation)
  * [Interstitial implementation](#interstitial-implementation)
  * [RewardedVideo implementation](#rewardedvideo-implementation)
  * [Native implementation](#native-implementation)
* [HeaderBidding implementation](#headerbidding-implementation)
  * [SDK configuration sample](#sdk-configuration-sample-1)
  * [Banner implementation](#banner-implementation-1)
  * [Interstitial implementation](#interstitial-implementation-1)
  * [RewardedVideo implementation](#rewardedvideo-implementation-1)
  * [Native implementation](#native-implementation-1)
  * [Work with price](#work-with-price)
* [What's new in last version](whats-new-in-last-version)

## Useful links
* [BidMachine integration documentation](https://wiki.appodeal.com/display/BID/BidMachine+Android+SDK+Documentation)
* [BidMachine MoPub custom network integration guide](https://wiki.appodeal.com/display/BID/BidMachine+MoPub+custom+network+integration+guide)

## Integration
```gradle
repositories {
    //Add BidMachine maven repository
    maven {
        name 'BidMachine Ads maven repository'
        url 'https://artifactory.bidmachine.io/bidmachine'
    }
    //Add Moat maven repository for MoPub
    maven {
        url "https://s3.amazonaws.com/moat-sdk-builds"
    }
}

dependencies {
    //Add BidMachine SDK dependency
    implementation 'io.bidmachine:ads:1.6.1'
    //Add BidMachine SDK Mopub Adapter dependency
    implementation 'io.bidmachine:ads.adapters.mopub:1.6.1.13'
    //Add Mopub SDK dependency
    implementation('com.mopub:mopub-sdk:5.14.0@aar') {
        transitive = true
    }
}
```

## Classic implementation
#### SDK configuration sample
```java
//Prepare price_floors for BidMachine
JSONArray jsonArray = new JSONArray();
try {
    jsonArray.put(new JSONObject().put("id1", 300.006));
    jsonArray.put(new JSONObject().put("id2", 1000));
    jsonArray.put(302.006);
    jsonArray.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

//Prepare configuration map for BidMachineAdapterConfiguration
Map<String, String> configuration = new HashMap<>();
configuration.put("seller_id", "YOUR_SELLER_ID");
configuration.put("mediation_config", "YOUR_MEDIATION_CONFIG");
configuration.put("coppa", "true");
configuration.put("logging_enabled", "true");
configuration.put("test_mode", "true");
configuration.put("consent_string", "YOUR_GDPR_CONSENT_STRING");
configuration.put("endpoint", "YOUR_ENDPOINT");
configuration.put("banner_width", "320");
configuration.put("user_id", "YOUR_USER_ID");
configuration.put("gender", "F");
configuration.put("yob", "2000");
configuration.put("keywords", "Keyword_1,Keyword_2,Keyword_3,Keyword_4");
configuration.put("country", "YOUR_COUNTRY");
configuration.put("city", "YOUR_CITY");
configuration.put("zip", "YOUR_ZIP");
configuration.put("sturl", "https://store_url.com");
configuration.put("paid", "true");
configuration.put("bcat", "IAB-1,IAB-3,IAB-5");
configuration.put("badv", "https://domain_1.com,https://domain_2.org");
configuration.put("bapps", "com.test.application_1,com.test.application_2,com.test.application_3");
configuration.put("price_floors", jsonArray.toString());

//Prepare SdkConfiguration for initialize MoPub with BidMachineAdapterConfiguration
SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(AD_UNIT_ID)
        .withAdditionalNetwork(BidMachineAdapterConfiguration.class.getName())
        .withMediatedNetworkConfiguration(
                BidMachineAdapterConfiguration.class.getName(),
                configuration)
        .build();

//Initialize MoPub SDK
MoPub.initializeSdk(this, sdkConfiguration, new InitializationListener());
```
[*Example*](example/src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L104)

#### Banner implementation
Server configuration sample:
```json
{
    "seller_id": "YOUR_SELLER_ID",
    "mediation_config": "YOUR_MEDIATION_CONFIG",
    "coppa": "true",
    "logging_enabled": "true",
    "test_mode": "true",
    "consent_string": "YOUR_GDPR_CONSENT_STRING",
    "endpoint": "YOUR_ENDPOINT",
    "banner_width": "320",
    "user_id": "YOUR_USER_ID",
    "gender": "F",
    "yob": "2000",
    "keywords": "Keyword_1,Keyword_2,Keyword_3,Keyword_4",
    "country": "YOUR_COUNTRY",
    "city": "YOUR_CITY",
    "zip": "YOUR_ZIP",
    "sturl": "https://store_url.com",
    "paid": "true",
    "bcat": "IAB-1,IAB-3,IAB-5",
    "badv": "https://domain_1.com,https://domain_2.org",
    "bapps": "com.test.application_1,com.test.application_2,com.test.application_3",
    "price_floors": [{
            "id_1": 300.06
        }, {
            "id_2": 1000
        },
        302.006,
        1002
    ]
}
```

Local configuration sample:
```java
//Prepare priceFloors for BidMachine
JSONArray jsonArray = new JSONArray();
try {
    jsonArray.put(new JSONObject().put("id1", 300.006));
    jsonArray.put(new JSONObject().put("id2", 1000));
    jsonArray.put(302.006);
    jsonArray.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

//Prepare localExtras for MoPubView
Map<String, Object> localExtras = new HashMap<>();
localExtras.put("seller_id", "YOUR_SELLER_ID");
localExtras.put("mediation_config", "YOUR_MEDIATION_CONFIG");
localExtras.put("coppa", "true");
localExtras.put("logging_enabled", "true");
localExtras.put("test_mode", "true");
localExtras.put("consent_string", "YOUR_GDPR_CONSENT_STRING");
localExtras.put("endpoint", "YOUR_ENDPOINT");
localExtras.put("banner_width", "320");
localExtras.put("user_id", "YOUR_USER_ID");
localExtras.put("gender", "F");
localExtras.put("yob", "2000");
localExtras.put("keywords", "Keyword_1,Keyword_2,Keyword_3,Keyword_4");
localExtras.put("country", "YOUR_COUNTRY");
localExtras.put("city", "YOUR_CITY");
localExtras.put("zip", "YOUR_ZIP");
localExtras.put("sturl", "https://store_url.com");
localExtras.put("paid", "true");
localExtras.put("bcat", "IAB-1,IAB-3,IAB-5");
localExtras.put("badv", "https://domain_1.com,https://domain_2.org");
localExtras.put("bapps", "com.test.application_1,com.test.application_2,com.test.application_3");
localExtras.put("price_floors", jsonArray.toString());

//Create new MoPubView instance and load
MoPubView moPubView = new MoPubView(this);
moPubView.setLayoutParams(new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));
moPubView.setLocalExtras(localExtras);
moPubView.setAutorefreshEnabled(false);
moPubView.setAdUnitId(BANNER_KEY);
moPubView.setBannerAdListener(new BannerViewListener());
moPubView.loadAd();
```
[*Example*](example/src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L154)

#### Interstitial implementation
Server configuration sample:
```json
{
    "seller_id": "YOUR_SELLER_ID",
    "mediation_config": "YOUR_MEDIATION_CONFIG",
    "coppa": "true",
    "logging_enabled": "true",
    "test_mode": "true",
    "consent_string": "YOUR_GDPR_CONSENT_STRING",
    "endpoint": "YOUR_ENDPOINT",
    "ad_content_type": "All",
    "user_id": "YOUR_USER_ID",
    "gender": "F",
    "yob": "2000",
    "keywords": "Keyword_1,Keyword_2,Keyword_3,Keyword_4",
    "country": "YOUR_COUNTRY",
    "city": "YOUR_CITY",
    "zip": "YOUR_ZIP",
    "sturl": "https://store_url.com",
    "paid": "true",
    "bcat": "IAB-1,IAB-3,IAB-5",
    "badv": "https://domain_1.com,https://domain_2.org",
    "bapps": "com.test.application_1,com.test.application_2,com.test.application_3",
    "price_floors": [{
            "id_1": 300.06
        }, {
            "id_2": 1000
        },
        302.006,
        1002
    ]
}
```

Local configuration sample:
```java
//Prepare priceFloors for BidMachine
JSONArray jsonArray = new JSONArray();
try {
    jsonArray.put(new JSONObject().put("id1", 300.006));
    jsonArray.put(new JSONObject().put("id2", 1000));
    jsonArray.put(302.006);
    jsonArray.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

//Prepare localExtras for MoPubInterstitial
Map<String, Object> localExtras = new HashMap<>();
localExtras.put("seller_id", "YOUR_SELLER_ID");
localExtras.put("mediation_config", "YOUR_MEDIATION_CONFIG");
localExtras.put("coppa", "true");
localExtras.put("logging_enabled", "true");
localExtras.put("test_mode", "true");
localExtras.put("consent_string", "YOUR_GDPR_CONSENT_STRING");
localExtras.put("endpoint", "YOUR_ENDPOINT");
localExtras.put("ad_content_type", "All");
localExtras.put("user_id", "YOUR_USER_ID");
localExtras.put("gender", "F");
localExtras.put("yob", "2000");
localExtras.put("keywords", "Keyword_1,Keyword_2,Keyword_3,Keyword_4");
localExtras.put("country", "YOUR_COUNTRY");
localExtras.put("city", "YOUR_CITY");
localExtras.put("zip", "YOUR_ZIP");
localExtras.put("sturl", "https://store_url.com");
localExtras.put("paid", "true");
localExtras.put("bcat", "IAB-1,IAB-3,IAB-5");
localExtras.put("badv", "https://domain_1.com,https://domain_2.org");
localExtras.put("bapps", "com.test.application_1,com.test.application_2,com.test.application_3");
localExtras.put("price_floors", jsonArray.toString());

//Create new MoPubInterstitial instance and load
MoPubInterstitial moPubInterstitial = new MoPubInterstitial(this, INTERSTITIAL_KEY);
moPubInterstitial.setLocalExtras(localExtras);
moPubInterstitial.setInterstitialAdListener(new InterstitialListener());
moPubInterstitial.load();
```
[*Example*](example/src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L209)

#### RewardedVideo implementation
Server configuration sample:
```json
{
    "seller_id": "YOUR_SELLER_ID",
    "mediation_config": "YOUR_MEDIATION_CONFIG",
    "coppa": "true",
    "logging_enabled": "true",
    "test_mode": "true",
    "consent_string": "YOUR_GDPR_CONSENT_STRING",
    "endpoint": "YOUR_ENDPOINT",
    "user_id": "YOUR_USER_ID",
    "gender": "F",
    "yob": "2000",
    "keywords": "Keyword_1,Keyword_2,Keyword_3,Keyword_4",
    "country": "YOUR_COUNTRY",
    "city": "YOUR_CITY",
    "zip": "YOUR_ZIP",
    "sturl": "https://store_url.com",
    "paid": "true",
    "bcat": "IAB-1,IAB-3,IAB-5",
    "badv": "https://domain_1.com,https://domain_2.org",
    "bapps": "com.test.application_1,com.test.application_2,com.test.application_3",
    "price_floors": [{
            "id_1": 300.06
        }, {
            "id_2": 1000
        },
        302.006,
        1002
    ]
}
```
Local configuration sample:
```java
//Prepare priceFloors for BidMachine
JSONArray jsonArray = new JSONArray();
try {
    jsonArray.put(new JSONObject().put("id1", 300.006));
    jsonArray.put(new JSONObject().put("id2", 1000));
    jsonArray.put(302.006);
    jsonArray.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

//Prepare localExtras for MoPubRewardedVideos
Map<String, Object> localExtras = new HashMap<>();
localExtras.put("seller_id", "YOUR_SELLER_ID");
localExtras.put("mediation_config", "YOUR_MEDIATION_CONFIG");
localExtras.put("coppa", "true");
localExtras.put("logging_enabled", "true");
localExtras.put("test_mode", "true");
localExtras.put("consent_string", "YOUR_GDPR_CONSENT_STRING");
localExtras.put("endpoint", "YOUR_ENDPOINT");
localExtras.put("ad_content_type", "All");
localExtras.put("user_id", "YOUR_USER_ID");
localExtras.put("gender", "F");
localExtras.put("yob", "2000");
localExtras.put("keywords", "Keyword_1,Keyword_2,Keyword_3,Keyword_4");
localExtras.put("country", "YOUR_COUNTRY");
localExtras.put("city", "YOUR_CITY");
localExtras.put("zip", "YOUR_ZIP");
localExtras.put("sturl", "https://store_url.com");
localExtras.put("paid", "true");
localExtras.put("bcat", "IAB-1,IAB-3,IAB-5");
localExtras.put("badv", "https://domain_1.com,https://domain_2.org");
localExtras.put("bapps", "com.test.application_1,com.test.application_2,com.test.application_3");
localExtras.put("price_floors", jsonArray.toString());

//Create BidMachineMediationSettings instance with local extras
MediationSettings mediationSettings = new BidMachineMediationSettings()
        .withLocalExtras(localExtras);

//Load MoPubRewardedVideos
MoPubRewardedVideos.setRewardedVideoListener(new RewardedVideoListener());
MoPubRewardedVideos.loadRewardedVideo(REWARDED_KEY, mediationSettings);
```
[*Example*](example/src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L254)

#### Native implementation
Server configuration sample:
```json
{
    "seller_id": "YOUR_SELLER_ID",
    "mediation_config": "YOUR_MEDIATION_CONFIG",
    "coppa": "true",
    "logging_enabled": "true",
    "test_mode": "true",
    "consent_string": "YOUR_GDPR_CONSENT_STRING",
    "endpoint": "YOUR_ENDPOINT",
    "user_id": "YOUR_USER_ID",
    "gender": "F",
    "yob": "2000",
    "keywords": "Keyword_1,Keyword_2,Keyword_3,Keyword_4",
    "country": "YOUR_COUNTRY",
    "city": "YOUR_CITY",
    "zip": "YOUR_ZIP",
    "sturl": "https://store_url.com",
    "paid": "true",
    "bcat": "IAB-1,IAB-3,IAB-5",
    "badv": "https://domain_1.com,https://domain_2.org",
    "bapps": "com.test.application_1,com.test.application_2,com.test.application_3",
    "price_floors": [{
            "id_1": 300.06
        }, {
            "id_2": 1000
        },
        302.006,
        1002
    ]
}
```
Local configuration sample:
```java
//Prepare priceFloors for BidMachine
JSONArray jsonArray = new JSONArray();
try {
    jsonArray.put(new JSONObject().put("id1", 300.006));
    jsonArray.put(new JSONObject().put("id2", 1000));
    jsonArray.put(302.006);
    jsonArray.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

//Prepare localExtras for MoPubNative
Map<String, Object> localExtras = new HashMap<>();
localExtras.put("seller_id", "YOUR_SELLER_ID");
localExtras.put("mediation_config", "YOUR_MEDIATION_CONFIG");
localExtras.put("coppa", "true");
localExtras.put("logging_enabled", "true");
localExtras.put("test_mode", "true");
localExtras.put("consent_string", "YOUR_GDPR_CONSENT_STRING");
localExtras.put("endpoint", "YOUR_ENDPOINT");
localExtras.put("user_id", "YOUR_USER_ID");
localExtras.put("gender", "F");
localExtras.put("yob", "2000");
localExtras.put("keywords", "Keyword_1,Keyword_2,Keyword_3,Keyword_4");
localExtras.put("country", "YOUR_COUNTRY");
localExtras.put("city", "YOUR_CITY");
localExtras.put("zip", "YOUR_ZIP");
localExtras.put("sturl", "https://store_url.com");
localExtras.put("paid", "true");
localExtras.put("bcat", "IAB-1,IAB-3,IAB-5");
localExtras.put("badv", "https://domain_1.com,https://domain_2.org");
localExtras.put("bapps", "com.test.application_1,com.test.application_2,com.test.application_3");
localExtras.put("price_floors", jsonArray.toString());

//Create a new instance of BidMachineViewBinder with layout which contains NativeAdContentLayout and its ID
BidMachineViewBinder viewBinder = new BidMachineViewBinder(R.layout.native_ad,
                                                           R.id.native_ad_container);
viewBinder.addClickableViewId(R.id.view_id_1);
viewBinder.addClickableViewId(R.id.view_id_2);

//Create new MoPubNative instance and load
MoPubNative moPubNative = new MoPubNative(this, NATIVE_KEY, new NativeListener());
moPubNative.registerAdRenderer(new BidMachineNativeRendered(viewBinder));
moPubNative.setLocalExtras(localExtras);
moPubNative.makeRequest();
```
[*Example*](example/src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L277)


## HeaderBidding implementation
#### SDK configuration sample
```java
//Initialize BidMachine SDK
BidMachine.initialize(this, <YOUR_SELLER_ID>);

//Prepare SdkConfiguration for initialize MoPub with BidMachineAdapterConfiguration
SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(AD_UNIT_ID)
        .withAdditionalNetwork(BidMachineAdapterConfiguration.class.getName())
        .withMediatedNetworkConfiguration(
                BidMachineAdapterConfiguration.class.getName(),
                configuration)
        .build();

//Initialize MoPub SDK
MoPub.initializeSdk(this, sdkConfiguration, new InitializationListener());
```
[*Example*](example/src/main/java/io/bidmachine/examples/BidMachineMoPubFetchActivity.java#L113)

#### Banner implementation
```java
//Create new BidMachine request
BannerRequest bannerRequest = new BannerRequest.Builder()
        .setSize(BannerSize.Size_320x50)
        .setTargetingParams(...)
        .setPriceFloorParams(...)
        .setListener(new AdRequest.AdRequestListener<BannerRequest>() {
            @Override
            public void onRequestSuccess(@NonNull BannerRequest bannerRequest,
                                         @NonNull AuctionResult auctionResult) {
                // Fetch BidMachine Ads params
                Map<String, String> fetchParams = BidMachineFetcher.fetch(bannerRequest);
                if (fetchParams != null) {
                    //Prepare MoPub keywords
                    String keywords = BidMachineFetcher.MoPub.toKeywords(fetchParams);

                    //Request callbacks run in background thread, but you should call MoPub load methods on UI thread
                    runOnUiThread(() -> {
                        //Prepare MoPubView
                        MoPubView moPubView = ...;

                        //Set MoPub Banner keywords
                        moPubView.setKeywords(keywords);

                        //Prepare localExtras for set to MoPubView with additional fetching parameters
                        Map<String, Object> localExtras = new HashMap<>(fetchParams);

                        //Set MoPub local extras
                        moPubView.setLocalExtras(localExtras);

                        //Load MoPub Ads
                        moPubView.loadAd();
                    });
                } else {
                    //Params fetching failed
                }
            }

            @Override
            public void onRequestFailed(@NonNull BannerRequest bannerRequest,
                                        @NonNull BMError bmError) {
                //Request failed, additional info can be found in "bmError" object
            }

            @Override
            public void onRequestExpired(@NonNull BannerRequest bannerRequest) {
                //ignore
            }
        })
        .build();

//Request BidMachine Ads without load it
bannerRequest.request(this);
```
[*Example*](example/src/main/java/io/bidmachine/examples/BidMachineMoPubFetchActivity.java#L158)

#### Interstitial implementation
```java
//Create new BidMachine request
InterstitialRequest interstitialRequest = new InterstitialRequest.Builder()
        .setTargetingParams(...)
        .setPriceFloorParams(...)
        .setListener(new AdRequest.AdRequestListener<InterstitialRequest>() {
            @Override
            public void onRequestSuccess(@NonNull InterstitialRequest interstitialRequest,
                                         @NonNull AuctionResult auctionResult) {
                // Fetch BidMachine Ads params
                Map<String, String> fetchParams = BidMachineFetcher.fetch(interstitialRequest);
                if (fetchParams != null) {
                    //Prepare MoPub keywords
                    String keywords = BidMachineFetcher.MoPub.toKeywords(fetchParams);

                    //Request callbacks run in background thread, but you should call MoPub load methods on UI thread
                    runOnUiThread(() -> {
                        //Prepare MoPubInterstitial
                        MoPubInterstitial moPubInterstitial = ...;

                        // Set MoPub Interstitial keywords
                        moPubInterstitial.setKeywords(keywords);

                        //Prepare localExtras for set to MoPubInterstitial with additional fetching parameters
                        Map<String, Object> localExtras = new HashMap<>(fetchParams);

                        //Set MoPub local extras
                        moPubInterstitial.setLocalExtras(localExtras);

                        //Load MoPub Ads
                        moPubInterstitial.load();
                    });
                } else {
                    //Params fetching failed
                }
            }

            @Override
            public void onRequestFailed(@NonNull InterstitialRequest interstitialRequest,
                                        @NonNull BMError bmError) {
                //Request failed, additional info can be found in "bmError" object
            }

            @Override
            public void onRequestExpired(@NonNull InterstitialRequest interstitialRequest) {
                //ignore
            }
        })
        .build();

//Request BidMachine Ads without load it
interstitialRequest.request(this);
```
[*Example*](example/src/main/java/io/bidmachine/examples/BidMachineMoPubFetchActivity.java#L260)

#### RewardedVideo implementation
```java
//Create new BidMachine request
RewardedRequest request = new RewardedRequest.Builder()
        .setTargetingParams(...)
        .setPriceFloorParams(...)
        .setListener(new AdRequest.AdRequestListener<RewardedRequest>() {
            @Override
            public void onRequestSuccess(@NonNull RewardedRequest rewardedRequest,
                                         @NonNull AuctionResult auctionResult) {
                //Fetch BidMachine Ads params
                Map<String, String> fetchParams = BidMachineFetcher.fetch(rewardedRequest);
                if (fetchParams != null) {
                    //Prepare MoPub keywords
                    String keywords = BidMachineFetcher.MoPub.toKeywords(fetchParams);

                    //Request callbacks run in background thread, but you should call MoPub load methods on UI thread
                    runOnUiThread(() -> {
                        //Load MoPub Rewarded video
                        MoPubRewardedVideos.loadRewardedVideo(
                                REWARDED_KEY,
                                //Set MoPub Rewarded keywords
                                new MoPubRewardedVideoManager.RequestParameters(keywords),
                                //Create BidMachineMediationSettings with fetched request id
                                new BidMachineMediationSettings(fetchParams));
                    });
                } else {
                    //Params fetching failed
                }
            }

            @Override
            public void onRequestFailed(@NonNull RewardedRequest rewardedRequest,
                                        @NonNull BMError bmError) {
                //Request failed, additional info can be found in "bmError" object
            }

            @Override
            public void onRequestExpired(@NonNull RewardedRequest rewardedRequest) {
                //ignore
            }
        })
        .build();

//Request BidMachine Ads without load it
request.request(this);
```

[*Example*](example/src/main/java/io/bidmachine/examples/BidMachineMoPubFetchActivity.java#L356)

#### Native implementation
```java
NativeRequest request = new NativeRequest.Builder()
        .setTargetingParams(...)
        .setPriceFloorParams(...)
        .setListener(new AdRequest.AdRequestListener<NativeRequest>() {
            @Override
            public void onRequestSuccess(@NonNull NativeRequest nativeRequest,
                                         @NonNull AuctionResult auctionResult) {
                //Fetch BidMachine Ads
                Map<String, String> fetchParams = BidMachineFetcher.fetch(nativeRequest);
                if (fetchParams != null) {
                    //Prepare MoPub keywords
                    String keywords = BidMachineFetcher.MoPub.toKeywords(fetchParams);

                    //Request callbacks run in background thread, but you should call MoPub load methods on UI thread
                    runOnUiThread(() -> {
                        //Prepare MoPubNative
                        MoPubNative moPubNative = ...;

                        //Prepare localExtras for set to MoPubNative with additional fetching parameters
                        Map<String, Object> localExtras = new HashMap<>(fetchParams);

                        //Set MoPub local extras
                        moPubNative.setLocalExtras(localExtras);

                        // Set MoPub Native keywords
                        RequestParameters requestParameters = new RequestParameters.Builder()
                                .keywords(keywords)
                                .build();

                        //Load MoPub Ads
                        moPubNative.makeRequest(requestParameters);
                    });
                } else {
                    //Params fetching failed
                }
            }

            @Override
            public void onRequestFailed(@NonNull NativeRequest nativeRequest,
                                        @NonNull BMError bmError) {
                //Request failed, additional info can be found in "bmError" object
            }

            @Override
            public void onRequestExpired(@NonNull NativeRequest nativeRequest) {
                //ignore
            }
        })
        .build();

//Request BidMachine Ads without load it
request.request(this);
```
[*Example*](example/src/main/java/io/bidmachine/examples/BidMachineMoPubFetchActivity.java#L427)

#### Work with price
When **BidMachineFetcher.fetch(...)** is called, price rounding occurs. By default, RoundingMode is **RoundingMode.CEILING**, but if you want specific RoundingMode, you can change it with help **BidMachineFetcher.setPriceRounding(...)**. You can try your rounding configuration via call **BidMachineFetcher.roundPrice(...)**. More info about RoundingMode [here](https://developer.android.com/reference/java/math/RoundingMode).

**Attention**:  RoundingMode.UNNECESSARY is not supported.

Price rounding examples:

| Round mode | Result |
| ---------- | ------ |
| BidMachineFetcher.setPriceRounding(0.01) | 0.01 -> 0.01 <br> 0.99 -> 0.99 <br> 1.212323 -> 1.22 <br> 1.34538483 -> 1.35 <br> 1.4 -> 1.40 <br> 1.58538483 -> 1.59 |
| BidMachineFetcher.setPriceRounding(0.1) | 0.01 -> 0.1 <br> 0.99 -> 1.0 <br> 1.212323 -> 1.3 <br> 1.34538483 -> 1.4 <br> 1.4 -> 1.4 <br> 1.58538483 -> 1.6 |
| BidMachineFetcher.setPriceRounding(0.01, RoundingMode.FLOOR) | 0.01 -> 0.01 <br> 0.99 -> 0.99 <br> 1.212323 -> 1.21 <br> 1.34538483 -> 1.34 <br> 1.4 -> 1.40 <br> 1.58538483 -> 1.58 |

## What's new in last version

Please view the [changelog](CHANGELOG.md) for details.
