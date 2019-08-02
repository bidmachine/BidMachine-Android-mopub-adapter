# BidMachine-Android-mopub-adapter
BidMachine Android adapter for MoPub mediation

## Integration:
```gradle
repositories {
    //Add BidMachine maven repository
    maven {
        url 'https://artifactory.bidmachine.io/bidmachine'
    }
}

dependencies {
    //Add BidMachine SDK dependency
    implementation 'io.bidmachine:ads:1.1.0'
    //Add BidMachine SDK Mopub Adapter dependency
    implementation 'io.bidmachine:ads-mopub:1.0.2'
    //Add Mopub SDK dependency
    implementation('com.mopub:mopub-sdk:5.7.0@aar') {
        transitive = true
    }
    ...
}
```

## Examples:

#### Initialize: [Sample](example/src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L80)
#### Load Banner: [Sample](example/src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L125)
#### Load Interstitial: [Sample](example/src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L177)
#### Load Rewarded Video: [Sample](example/src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L222)


List of parameters for local and server configuration:

| Key         | Definition | Value type |
|:----------- |:---------- |:---------- |
| seller_id       | You unique seller id. To get your Seller Id or for more info please visit https://bidmachine.io/ | String |
| coppa           | Flag indicating if COPPA regulations can be applied. The Children's Online Privacy Protection Act (COPPA) was established by the U.S. Federal Trade Commission. | String |
| logging_enabled | Enable logs if required | String |
| test_mode       | Enable test mode | String |
| consent_string | GDPR consent string if applicable, complying with the comply with the IAB standard <a href="https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/Consent%20string%20and%20vendor%20list%20formats%20v1.1%20Final.md">Consent String Format</a> in the <a href="https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework">Transparency and Consent Framework</a> technical specifications | String |
| ad_content_type | Content type for interstitial ad, one of following: "All", "Static", "Video"   | String              |
| userId      | Vendor-specific ID for the user                                                | String              |
| gender      | Gender, one of following: "F", "M", "O"                                        | String              |
| yob         | Year of birth as a 4-digit integer (e.g - 1990)                                | String              |
| keywords    | List of keywords, interests, or intents (separated by comma)                   | String              |
| country     | Country of the user's home base (i.e., not necessarily their current location) | String              |
| city        | City of the user's home base (i.e., not necessarily their current location)    | String              |
| zip         | Zip of the user's home base (i.e., not necessarily their current location)     | String              |
| sturl       | App store URL for an installed app; for IQG 2.1 compliance                     | String              |
| paid        | Determines, if it is a free or paid version of the app                         | String              |
| bcat        | Block list of content categories using IDs (separated by comma)                | String              |
| badv        | Block list of advertisers by their domains (separated by comma)                | String              |
| bapps       | Block list of apps where ads are disallowed (separated by comma)               | String              |
| priceFloors | List of price floor                                                            | JSONArray in String |

Local SDK configuration sample:
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

//Prepare configuration map for BidMachineAdapterConfiguration
Map<String, String> configuration = new HashMap<>();
configuration.put("seller_id", "YOUR_SELLER_ID");
configuration.put("coppa", "true");
configuration.put("logging_enabled", "true");
configuration.put("test_mode", "true");
configuration.put("consent_string", "YOUR_GDPR_CONSENT_STRING");
configuration.put("banner_width", "320");
configuration.put("userId", "YOUR_USER_ID");
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
configuration.put("priceFloors", jsonArray.toString());

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
    "coppa": "true",
    "logging_enabled": "true",
    "test_mode": "true",
    "consent_string": "YOUR_GDPR_CONSENT_STRING",
    "banner_width": "320",
    "userId": "YOUR_USER_ID",
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
    "priceFloors": [{
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
localExtras.put("coppa", "true");
localExtras.put("logging_enabled", "true");
localExtras.put("test_mode", "true");
localExtras.put("consent_string", "YOUR_GDPR_CONSENT_STRING");
localExtras.put("banner_width", "320");
localExtras.put("userId", "YOUR_USER_ID");
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
localExtras.put("priceFloors", jsonArray.toString());

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
    "coppa": "true",
    "logging_enabled": "true",
    "test_mode": "true",
    "consent_string": "YOUR_GDPR_CONSENT_STRING",
    "ad_content_type": "All",
    "userId": "YOUR_USER_ID",
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
    "priceFloors": [{
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
localExtras.put("coppa", "true");
localExtras.put("logging_enabled", "true");
localExtras.put("test_mode", "true");
localExtras.put("consent_string", "YOUR_GDPR_CONSENT_STRING");
localExtras.put("banner_width", "320");
localExtras.put("userId", "YOUR_USER_ID");
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
localExtras.put("priceFloors", jsonArray.toString());

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
    "coppa": "true",
    "logging_enabled": "true",
    "test_mode": "true",
    "consent_string": "YOUR_GDPR_CONSENT_STRING",
    "userId": "YOUR_USER_ID",
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
    "priceFloors": [{
            "id_1": 300.06
        }, {
            "id_2": 1000
        },
        302.006,
        1002
    ]
}
```