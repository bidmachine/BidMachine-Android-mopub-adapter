# BidMachine Android MoPubAdapter
BidMachine Android adapter for MoPub mediation

[BidMachine integration documentation](https://wiki.appodeal.com/display/BID/BidMachine+Android+SDK+Documentation)

## Integration:
[<img src="https://img.shields.io/badge/SDK%20Version-1.4.0-brightgreen">](https://github.com/bidmachine/BidMachine-Android-SDK)
[<img src="https://img.shields.io/badge/Adapter%20Version-1.4.0.4-brightgreen">](https://artifactory.bidmachine.io/bidmachine/io/bidmachine/ads.adapters.mopub/1.4.0.4/)
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
    implementation 'io.bidmachine:ads:1.4.0'
    //Add BidMachine SDK Mopub Adapter dependency
    implementation 'io.bidmachine:ads.adapters.mopub:1.4.0.4'
    //Add Mopub SDK dependency
    implementation('com.mopub:mopub-sdk:5.8.0@aar') {
        transitive = true
    }
    ...
}
```

## Examples:

#### Initialize: [Sample](example/src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L104)
#### Load Banner: [Sample](example/src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L154)
#### Load Interstitial: [Sample](example/src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L209)
#### Load Rewarded Video: [Sample](example/src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L254)
#### Load Native: [Sample](example/src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L277)

## Configuration:
On the <a href="https://app.mopub.com">MoPub web interface</a>, create a network with the "Custom SDK Network" type. Place the fully qualified class name of your custom event (for example, com.mopub.mobileads.BidMachineBanner) in the "Custom Event Class" column.

| Ad Type        | Custom Event Class                          |
|:-------------- |:------------------------------------------- |
| Banner         | [com.mopub.mobileads.BidMachineBanner](bidmachine_android_mopub/src/main/java/com/mopub/mobileads/BidMachineBanner.java) |
| Interstitial   | [com.mopub.mobileads.BidMachineInterstitial](bidmachine_android_mopub/src/main/java/com/mopub/mobileads/BidMachineInterstitial.java) |
| Rewarded Video | [com.mopub.mobileads.BidMachineRewardedVideo](bidmachine_android_mopub/src/main/java/com/mopub/mobileads/BidMachineRewardedVideo.java) |
| Native         | [com.mopub.nativeads.BidMachineNative](bidmachine_android_mopub/src/main/java/com/mopub/nativeads/BidMachineNative.java) |

List of parameters for local and server configuration:

| Key              | Definition | Value type |
|:---------------- |:---------- |:---------- |
| seller_id        | Your unique seller id. To get your Seller Id or for more info please visit https://bidmachine.io/ | String |
| mediation_config | Your mediation config | JSONArray in String |
| coppa            | Flag indicating if COPPA regulations can be applied. The Children's Online Privacy Protection Act (COPPA) was established by the U.S. Federal Trade Commission. | String |
| logging_enabled  | Enable logs if required | String |
| test_mode        | Enable test mode | String |
| consent_string   | GDPR consent string if applicable, complying with the comply with the IAB standard <a href="https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/Consent%20string%20and%20vendor%20list%20formats%20v1.1%20Final.md">Consent String Format</a> in the <a href="https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework">Transparency and Consent Framework</a> technical specifications | String |
| endpoint         | Your custom endpoint | String |
| ad_content_type  | Content type for interstitial ad, one of following: "All", "Static", "Video"   | String              |
| user_id          | Vendor-specific ID for the user                                                | String              |
| gender           | Gender, one of following: "F", "M", "O"                                        | String              |
| yob              | Year of birth as a 4-digit integer (e.g - 1990)                                | String              |
| keywords         | List of keywords, interests, or intents (separated by comma)                   | String              |
| country          | Country of the user's home base (i.e., not necessarily their current location) | String              |
| city             | City of the user's home base (i.e., not necessarily their current location)    | String              |
| zip              | Zip of the user's home base (i.e., not necessarily their current location)     | String              |
| sturl            | App store URL for an installed app; for IQG 2.1 compliance                     | String              |
| paid             | Determines, if it is a free or paid version of the app                         | String              |
| bcat             | Block list of content categories using IDs (separated by comma)                | String              |
| badv             | Block list of advertisers by their domains (separated by comma)                | String              |
| bapps            | Block list of apps where ads are disallowed (separated by comma)               | String              |
| price_floors     | List of price floor                                                            | JSONArray in String |

Local SDK configuration sample:
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
```


Server Banner configuration sample:
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

Local Banner configuration sample:
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
Map<String, String> localExtras = new HashMap<>();
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
moPubView = new MoPubView(this);
moPubView.setLayoutParams(new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));
moPubView.setLocalExtras(localExtras);
moPubView.setAutorefreshEnabled(false);
moPubView.setAdUnitId(BANNER_KEY);
moPubView.setBannerAdListener(new BannerViewListener());
moPubView.loadAd();
```

Server Interstitial configuration sample:
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

Local Interstitial configuration sample:
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
Map<String, String> localExtras = new HashMap<>();
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
moPubInterstitial = new MoPubInterstitial(this, INTERSTITIAL_KEY);
moPubInterstitial.setLocalExtras(localExtras);
moPubInterstitial.setInterstitialAdListener(new InterstitialListener());
moPubInterstitial.load();
```

Server RewardedVideo configuration sample:
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

Local Native configuration sample:
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
Map<String, String> localExtras = new HashMap<>();
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
moPubNative = new MoPubNative(this, NATIVE_KEY, new NativeListener());
moPubNative.registerAdRenderer(new BidMachineNativeRendered(viewBinder));
moPubNative.setLocalExtras(localExtras);
moPubNative.makeRequest();
```

## BidMachine Header Bidding for MoPub

Please read this [documentation](https://wiki.appodeal.com/display/BID/BidMachine+MoPub+Android+Header+bidding) for more details.

## What's new in this version

Please view the [changelog](CHANGELOG.md) for details.
