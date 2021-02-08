# Classic MoPub implementation

* [SDK configuration sample](#sdk-configuration-sample)
* [Banner implementation](#banner-implementation)
* [Interstitial implementation](#interstitial-implementation)
* [RewardedVideo implementation](#rewardedvideo-implementation)
* [Native implementation](#native-implementation)

## SDK configuration sample
```java
// Prepare price_floors for BidMachine
JSONArray jsonArray = new JSONArray();
try {
    jsonArray.put(new JSONObject().put("id1", 300.006));
    jsonArray.put(new JSONObject().put("id2", 1000));
    jsonArray.put(302.006);
    jsonArray.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

// Prepare configuration map for BidMachineAdapterConfiguration
Map<String, String> configuration = new HashMap<>();
configuration.put(BidMachineUtils.SELLER_ID, "YOUR_SELLER_ID");
configuration.put(BidMachineUtils.MEDIATION_CONFIG, "YOUR_MEDIATION_CONFIG");
configuration.put(BidMachineUtils.COPPA, "true");
configuration.put(BidMachineUtils.LOGGING_ENABLED, "true");
configuration.put(BidMachineUtils.TEST_MODE, "true");
configuration.put(BidMachineUtils.CONSENT_STRING, "YOUR_GDPR_CONSENT_STRING");
configuration.put(BidMachineUtils.ENDPOINT, "YOUR_ENDPOINT");
configuration.put(BidMachineUtils.BANNER_WIDTH, "320");
configuration.put(BidMachineUtils.USER_ID, "YOUR_USER_ID");
configuration.put(BidMachineUtils.GENDER, "F");
configuration.put(BidMachineUtils.YOB, "2000");
configuration.put(BidMachineUtils.KEYWORDS, "Keyword_1,Keyword_2,Keyword_3,Keyword_4");
configuration.put(BidMachineUtils.COUNTRY, "YOUR_COUNTRY");
configuration.put(BidMachineUtils.CITY, "YOUR_CITY");
configuration.put(BidMachineUtils.ZIP, "YOUR_ZIP");
configuration.put(BidMachineUtils.STURL, "https://store_url.com");
configuration.put(BidMachineUtils.PAID, "true");
configuration.put(BidMachineUtils.BCAT, "IAB-1,IAB-3,IAB-5");
configuration.put(BidMachineUtils.BADV, "https://domain_1.com,https://domain_2.org");
configuration.put(BidMachineUtils.BAPPS, "com.test.application_1,com.test.application_2,com.test.application_3");
configuration.put(BidMachineUtils.PRICE_FLOORS, jsonArray.toString());

// Prepare SdkConfiguration for initialize MoPub with BidMachineAdapterConfiguration
SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(AD_UNIT_ID)
        .withLogLevel(MoPubLog.LogLevel.DEBUG)
        .withAdditionalNetwork(BidMachineAdapterConfiguration.class.getName())
        .withMediatedNetworkConfiguration(BidMachineAdapterConfiguration.class.getName(), configuration)
        .build();

// Initialize MoPub SDK
MoPub.initializeSdk(this, sdkConfiguration, new InitializationListener());
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L112)

## Banner implementation
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
// Prepare priceFloors for BidMachine
JSONArray jsonArray = new JSONArray();
try {
    jsonArray.put(new JSONObject().put("id1", 300.006));
    jsonArray.put(new JSONObject().put("id2", 1000));
    jsonArray.put(302.006);
    jsonArray.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

// Prepare localExtras for MoPubView
Map<String, Object> localExtras = new HashMap<>();
localExtras.put(BidMachineUtils.SELLER_ID, "YOUR_SELLER_ID");
localExtras.put(BidMachineUtils.MEDIATION_CONFIG, "YOUR_MEDIATION_CONFIG");
localExtras.put(BidMachineUtils.COPPA, "true");
localExtras.put(BidMachineUtils.LOGGING_ENABLED, "true");
localExtras.put(BidMachineUtils.TEST_MODE, "true");
localExtras.put(BidMachineUtils.CONSENT_STRING, "YOUR_GDPR_CONSENT_STRING");
localExtras.put(BidMachineUtils.ENDPOINT, "YOUR_ENDPOINT");
localExtras.put(BidMachineUtils.BANNER_WIDTH, "320");
localExtras.put(BidMachineUtils.USER_ID, "YOUR_USER_ID");
localExtras.put(BidMachineUtils.GENDER, "F");
localExtras.put(BidMachineUtils.YOB, "2000");
localExtras.put(BidMachineUtils.KEYWORDS, "Keyword_1,Keyword_2,Keyword_3,Keyword_4");
localExtras.put(BidMachineUtils.COUNTRY, "YOUR_COUNTRY");
localExtras.put(BidMachineUtils.CITY, "YOUR_CITY");
localExtras.put(BidMachineUtils.ZIP, "YOUR_ZIP");
localExtras.put(BidMachineUtils.STURL, "https://store_url.com");
localExtras.put(BidMachineUtils.PAID, "true");
localExtras.put(BidMachineUtils.BCAT, "IAB-1,IAB-3,IAB-5");
localExtras.put(BidMachineUtils.BADV, "https://domain_1.com,https://domain_2.org");
localExtras.put(BidMachineUtils.BAPPS, "com.test.application_1,com.test.application_2,com.test.application_3");
localExtras.put(BidMachineUtils.PRICE_FLOORS, jsonArray.toString());

// Create new MoPubView instance and load
MoPubView moPubView = new MoPubView(this);
moPubView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                     ViewGroup.LayoutParams.MATCH_PARENT));
moPubView.setLocalExtras(localExtras);
moPubView.setAutorefreshEnabled(false);
moPubView.setAdUnitId(BANNER_KEY);
moPubView.setBannerAdListener(new BannerViewListener());
moPubView.loadAd();
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L152)

## Interstitial implementation
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
// Prepare priceFloors for BidMachine
JSONArray jsonArray = new JSONArray();
try {
    jsonArray.put(new JSONObject().put("id1", 300.006));
    jsonArray.put(new JSONObject().put("id2", 1000));
    jsonArray.put(302.006);
    jsonArray.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

// Prepare localExtras for MoPubInterstitial
Map<String, Object> localExtras = new HashMap<>();
localExtras.put(BidMachineUtils.SELLER_ID, "YOUR_SELLER_ID");
localExtras.put(BidMachineUtils.MEDIATION_CONFIG, "YOUR_MEDIATION_CONFIG");
localExtras.put(BidMachineUtils.COPPA, "true");
localExtras.put(BidMachineUtils.LOGGING_ENABLED, "true");
localExtras.put(BidMachineUtils.TEST_MODE, "true");
localExtras.put(BidMachineUtils.CONSENT_STRING, "YOUR_GDPR_CONSENT_STRING");
localExtras.put(BidMachineUtils.ENDPOINT, "YOUR_ENDPOINT");
localExtras.put(BidMachineUtils.BANNER_WIDTH, "320");
localExtras.put(BidMachineUtils.USER_ID, "YOUR_USER_ID");
localExtras.put(BidMachineUtils.GENDER, "F");
localExtras.put(BidMachineUtils.YOB, "2000");
localExtras.put(BidMachineUtils.KEYWORDS, "Keyword_1,Keyword_2,Keyword_3,Keyword_4");
localExtras.put(BidMachineUtils.COUNTRY, "YOUR_COUNTRY");
localExtras.put(BidMachineUtils.CITY, "YOUR_CITY");
localExtras.put(BidMachineUtils.ZIP, "YOUR_ZIP");
localExtras.put(BidMachineUtils.STURL, "https://store_url.com");
localExtras.put(BidMachineUtils.PAID, "true");
localExtras.put(BidMachineUtils.BCAT, "IAB-1,IAB-3,IAB-5");
localExtras.put(BidMachineUtils.BADV, "https://domain_1.com,https://domain_2.org");
localExtras.put(BidMachineUtils.BAPPS, "com.test.application_1,com.test.application_2,com.test.application_3");
localExtras.put(BidMachineUtils.PRICE_FLOORS, jsonArray.toString());

// Create new MoPubInterstitial instance and load
MoPubInterstitial moPubInterstitial = new MoPubInterstitial(this, INTERSTITIAL_KEY);
moPubInterstitial.setLocalExtras(localExtras);
moPubInterstitial.setInterstitialAdListener(new InterstitialListener());
moPubInterstitial.load();
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L206)

## RewardedVideo implementation
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
// Prepare priceFloors for BidMachine
JSONArray jsonArray = new JSONArray();
try {
    jsonArray.put(new JSONObject().put("id1", 300.006));
    jsonArray.put(new JSONObject().put("id2", 1000));
    jsonArray.put(302.006);
    jsonArray.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

// Prepare localExtras for MoPubRewardedVideos
Map<String, Object> localExtras = new HashMap<>();
localExtras.put(BidMachineUtils.SELLER_ID, "YOUR_SELLER_ID");
localExtras.put(BidMachineUtils.MEDIATION_CONFIG, "YOUR_MEDIATION_CONFIG");
localExtras.put(BidMachineUtils.COPPA, "true");
localExtras.put(BidMachineUtils.LOGGING_ENABLED, "true");
localExtras.put(BidMachineUtils.TEST_MODE, "true");
localExtras.put(BidMachineUtils.CONSENT_STRING, "YOUR_GDPR_CONSENT_STRING");
localExtras.put(BidMachineUtils.ENDPOINT, "YOUR_ENDPOINT");
localExtras.put(BidMachineUtils.BANNER_WIDTH, "320");
localExtras.put(BidMachineUtils.USER_ID, "YOUR_USER_ID");
localExtras.put(BidMachineUtils.GENDER, "F");
localExtras.put(BidMachineUtils.YOB, "2000");
localExtras.put(BidMachineUtils.KEYWORDS, "Keyword_1,Keyword_2,Keyword_3,Keyword_4");
localExtras.put(BidMachineUtils.COUNTRY, "YOUR_COUNTRY");
localExtras.put(BidMachineUtils.CITY, "YOUR_CITY");
localExtras.put(BidMachineUtils.ZIP, "YOUR_ZIP");
localExtras.put(BidMachineUtils.STURL, "https://store_url.com");
localExtras.put(BidMachineUtils.PAID, "true");
localExtras.put(BidMachineUtils.BCAT, "IAB-1,IAB-3,IAB-5");
localExtras.put(BidMachineUtils.BADV, "https://domain_1.com,https://domain_2.org");
localExtras.put(BidMachineUtils.BAPPS, "com.test.application_1,com.test.application_2,com.test.application_3");
localExtras.put(BidMachineUtils.PRICE_FLOORS, jsonArray.toString());

// Create BidMachineMediationSettings instance with local extras
MediationSettings mediationSettings = new BidMachineMediationSettings()
        .withLocalExtras(localExtras);

// Load MoPubRewardedVideos
MoPubRewardedVideos.setRewardedVideoListener(new RewardedVideoListener());
MoPubRewardedVideos.loadRewardedVideo(REWARDED_KEY, mediationSettings);
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L256)

## Native implementation
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
// Prepare priceFloors for BidMachine
JSONArray jsonArray = new JSONArray();
try {
    jsonArray.put(new JSONObject().put("id1", 300.006));
    jsonArray.put(new JSONObject().put("id2", 1000));
    jsonArray.put(302.006);
    jsonArray.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

// Prepare localExtras for MoPubNative
Map<String, Object> localExtras = new HashMap<>();
localExtras.put(BidMachineUtils.SELLER_ID, "YOUR_SELLER_ID");
localExtras.put(BidMachineUtils.MEDIATION_CONFIG, "YOUR_MEDIATION_CONFIG");
localExtras.put(BidMachineUtils.COPPA, "true");
localExtras.put(BidMachineUtils.LOGGING_ENABLED, "true");
localExtras.put(BidMachineUtils.TEST_MODE, "true");
localExtras.put(BidMachineUtils.CONSENT_STRING, "YOUR_GDPR_CONSENT_STRING");
localExtras.put(BidMachineUtils.ENDPOINT, "YOUR_ENDPOINT");
localExtras.put(BidMachineUtils.USER_ID, "YOUR_USER_ID");
localExtras.put(BidMachineUtils.GENDER, "F");
localExtras.put(BidMachineUtils.YOB, "2000");
localExtras.put(BidMachineUtils.KEYWORDS, "Keyword_1,Keyword_2,Keyword_3,Keyword_4");
localExtras.put(BidMachineUtils.COUNTRY, "YOUR_COUNTRY");
localExtras.put(BidMachineUtils.CITY, "YOUR_CITY");
localExtras.put(BidMachineUtils.ZIP, "YOUR_ZIP");
localExtras.put(BidMachineUtils.STURL, "https://store_url.com");
localExtras.put(BidMachineUtils.PAID, "true");
localExtras.put(BidMachineUtils.BCAT, "IAB-1,IAB-3,IAB-5");
localExtras.put(BidMachineUtils.BADV, "https://domain_1.com,https://domain_2.org");
localExtras.put(BidMachineUtils.BAPPS, "com.test.application_1,com.test.application_2,com.test.application_3");
localExtras.put(BidMachineUtils.PRICE_FLOORS, jsonArray.toString());

// Create a new instance of BidMachineViewBinder with layout which contains NativeAdContentLayout and its ID
BidMachineViewBinder viewBinder = new BidMachineViewBinder(R.layout.native_ad,
                                                           R.id.native_ad_container);

// Create new MoPubNative instance and load
MoPubNative moPubNative = new MoPubNative(this, NATIVE_KEY, new NativeListener());
moPubNative.registerAdRenderer(new BidMachineNativeRendered(viewBinder));
moPubNative.setLocalExtras(localExtras);
moPubNative.makeRequest();
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L300)