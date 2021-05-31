# Classic MoPub implementation

* [SDK configuration sample](#sdk-configuration-sample)
* [Banner implementation](#banner-implementation)
* [MREC implementation](#mrec-implementation)
* [Interstitial implementation](#interstitial-implementation)
* [Rewarded implementation](#rewarded-implementation)
* [Native implementation](#native-implementation)

## SDK configuration sample
```java
// Prepare priceFloors for BidMachine
JSONArray priceFloors = new JSONArray();
try {
    priceFloors.put(new JSONObject().put("id1", 300.006));
    priceFloors.put(new JSONObject().put("id2", 1000));
    priceFloors.put(302.006);
    priceFloors.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

// Prepare externalUserIds for BidMachine
JSONArray externalUserIds = new JSONArray();
try {
    JSONObject externalUserId = new JSONObject()
            .put(BidMachineUtils.EXTERNAL_USER_SOURCE_ID, "source_id")
            .put(BidMachineUtils.EXTERNAL_USER_VALUE, "value");
    externalUserIds.put(externalUserId);
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
configuration.put(BidMachineUtils.AD_CONTENT_TYPE, "All");
configuration.put(BidMachineUtils.MEDIA_ASSET_TYPES, "Icon,Image");
configuration.put(BidMachineUtils.USER_ID, "YOUR_USER_ID");
configuration.put(BidMachineUtils.GENDER, "F");
configuration.put(BidMachineUtils.YOB, "2000");
configuration.put(BidMachineUtils.KEYWORDS, "Keyword_1,Keyword_2,Keyword_3,Keyword_4");
configuration.put(BidMachineUtils.COUNTRY, "YOUR_COUNTRY");
configuration.put(BidMachineUtils.CITY, "YOUR_CITY");
configuration.put(BidMachineUtils.ZIP, "YOUR_ZIP");
configuration.put(BidMachineUtils.STURL, "https://store_url.com");
configuration.put(BidMachineUtils.STORE_CAT, "YOUR_STORE_CATEGORY");
configuration.put(BidMachineUtils.STORE_SUB_CAT, "YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2");
configuration.put(BidMachineUtils.FMW_NAME, Framework.UNITY);
configuration.put(BidMachineUtils.PAID, "true");
configuration.put(BidMachineUtils.EXTERNAL_USER_IDS, externalUserIds.toString());
configuration.put(BidMachineUtils.BCAT, "IAB-1,IAB-3,IAB-5");
configuration.put(BidMachineUtils.BADV, "https://domain_1.com,https://domain_2.org");
configuration.put(BidMachineUtils.BAPPS, "com.test.application_1,com.test.application_2,com.test.application_3");
configuration.put(BidMachineUtils.PRICE_FLOORS, priceFloors.toString());
configuration.put(BidMachineUtils.PUBLISHER_ID, "YOUR_PUBLISHER_ID");
configuration.put(BidMachineUtils.PUBLISHER_NAME, "YOUR_PUBLISHER_NAME");
configuration.put(BidMachineUtils.PUBLISHER_DOMAIN, "YOUR_PUBLISHER_DOMAIN");
configuration.put(BidMachineUtils.PUBLISHER_CATEGORIES, "YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2");
configuration.put(BidMachineUtils.PLACEMENT_ID, "YOUR_PLACEMENT_ID");

// Prepare SdkConfiguration for initialize MoPub with BidMachineAdapterConfiguration
SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(AD_UNIT_ID)
        .withLogLevel(MoPubLog.LogLevel.DEBUG)
        .withAdditionalNetwork(BidMachineAdapterConfiguration.class.getName())
        .withMediatedNetworkConfiguration(BidMachineAdapterConfiguration.class.getName(), configuration)
        .build();

// Initialize MoPub SDK
MoPub.initializeSdk(this, sdkConfiguration, new InitializationListener());
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L122)

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
    "store_cat": "YOUR_STORE_CATEGORY",
    "store_subcat": "YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2",
    "fmw_name": "YOUR_FRAMEWORK_NAME",
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
    ],
    "external_user_ids": [{
        "source_id": "source_id_1",
        "value": "value_1"
    }, {
        "source_id": "source_id_2",
        "value": "value_2"
    }],
    "pubid": "YOUR_PUBLISHER_ID",
    "pubname": "YOUR_PUBLISHER_NAME",
    "pubdomain": "YOUR_PUBLISHER_DOMAIN",
    "pubcat": "YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2",
    "placement_id": "YOUR_PLACEMENT_ID"
}
```

Local configuration sample:
```java
// Prepare priceFloors for BidMachine
JSONArray priceFloors = new JSONArray();
try {
    priceFloors.put(new JSONObject().put("id1", 300.006));
    priceFloors.put(new JSONObject().put("id2", 1000));
    priceFloors.put(302.006);
    priceFloors.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

// Prepare externalUserIds for BidMachine
JSONArray externalUserIds = new JSONArray();
try {
    JSONObject externalUserId = new JSONObject()
            .put(BidMachineUtils.EXTERNAL_USER_SOURCE_ID, "source_id")
            .put(BidMachineUtils.EXTERNAL_USER_VALUE, "value");
    externalUserIds.put(externalUserId);
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
localExtras.put(BidMachineUtils.STORE_CAT, "YOUR_STORE_CATEGORY");
localExtras.put(BidMachineUtils.STORE_SUB_CAT, "YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2");
localExtras.put(BidMachineUtils.FMW_NAME, Framework.UNITY);
localExtras.put(BidMachineUtils.PAID, "true");
localExtras.put(BidMachineUtils.EXTERNAL_USER_IDS, externalUserIds.toString());
localExtras.put(BidMachineUtils.BCAT, "IAB-1,IAB-3,IAB-5");
localExtras.put(BidMachineUtils.BADV, "https://domain_1.com,https://domain_2.org");
localExtras.put(BidMachineUtils.BAPPS, "com.test.application_1,com.test.application_2,com.test.application_3");
localExtras.put(BidMachineUtils.PRICE_FLOORS, priceFloors.toString());
localExtras.put(BidMachineUtils.PUBLISHER_ID, "YOUR_PUBLISHER_ID");
localExtras.put(BidMachineUtils.PUBLISHER_NAME, "YOUR_PUBLISHER_NAME");
localExtras.put(BidMachineUtils.PUBLISHER_DOMAIN, "YOUR_PUBLISHER_DOMAIN");
localExtras.put(BidMachineUtils.PUBLISHER_CATEGORIES, "YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2");
localExtras.put(BidMachineUtils.PLACEMENT_ID, "YOUR_PLACEMENT_ID");

// Create new MoPubView instance and load
MoPubView bannerMoPubView = new MoPubView(this);
bannerMoPubView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                           ViewGroup.LayoutParams.MATCH_PARENT));
bannerMoPubView.setLocalExtras(localExtras);
bannerMoPubView.setAutorefreshEnabled(false);
bannerMoPubView.setAdUnitId(BANNER_KEY);
bannerMoPubView.setBannerAdListener(new BannerViewListener());
bannerMoPubView.loadAd(MoPubView.MoPubAdSize.HEIGHT_50);
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L163)

## MREC implementation
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
    "banner_width": "300",
    "user_id": "YOUR_USER_ID",
    "gender": "F",
    "yob": "2000",
    "keywords": "Keyword_1,Keyword_2,Keyword_3,Keyword_4",
    "country": "YOUR_COUNTRY",
    "city": "YOUR_CITY",
    "zip": "YOUR_ZIP",
    "sturl": "https://store_url.com",
    "store_cat": "YOUR_STORE_CATEGORY",
    "store_subcat": "YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2",
    "fmw_name": "YOUR_FRAMEWORK_NAME",
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
    ],
    "external_user_ids": [{
        "source_id": "source_id_1",
        "value": "value_1"
    }, {
        "source_id": "source_id_2",
        "value": "value_2"
    }],
    "pubid": "YOUR_PUBLISHER_ID",
    "pubname": "YOUR_PUBLISHER_NAME",
    "pubdomain": "YOUR_PUBLISHER_DOMAIN",
    "pubcat": "YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2",
    "placement_id": "YOUR_PLACEMENT_ID"
}
```

Local configuration sample:
```java
// Prepare priceFloors for BidMachine
JSONArray priceFloors = new JSONArray();
try {
    priceFloors.put(new JSONObject().put("id1", 300.006));
    priceFloors.put(new JSONObject().put("id2", 1000));
    priceFloors.put(302.006);
    priceFloors.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

// Prepare externalUserIds for BidMachine
JSONArray externalUserIds = new JSONArray();
try {
    JSONObject externalUserId = new JSONObject()
            .put(BidMachineUtils.EXTERNAL_USER_SOURCE_ID, "source_id")
            .put(BidMachineUtils.EXTERNAL_USER_VALUE, "value");
    externalUserIds.put(externalUserId);
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
localExtras.put(BidMachineUtils.BANNER_WIDTH, "300");
localExtras.put(BidMachineUtils.USER_ID, "YOUR_USER_ID");
localExtras.put(BidMachineUtils.GENDER, "F");
localExtras.put(BidMachineUtils.YOB, "2000");
localExtras.put(BidMachineUtils.KEYWORDS, "Keyword_1,Keyword_2,Keyword_3,Keyword_4");
localExtras.put(BidMachineUtils.COUNTRY, "YOUR_COUNTRY");
localExtras.put(BidMachineUtils.CITY, "YOUR_CITY");
localExtras.put(BidMachineUtils.ZIP, "YOUR_ZIP");
localExtras.put(BidMachineUtils.STURL, "https://store_url.com");
localExtras.put(BidMachineUtils.STORE_CAT, "YOUR_STORE_CATEGORY");
localExtras.put(BidMachineUtils.STORE_SUB_CAT, "YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2");
localExtras.put(BidMachineUtils.FMW_NAME, Framework.UNITY);
localExtras.put(BidMachineUtils.PAID, "true");
localExtras.put(BidMachineUtils.EXTERNAL_USER_IDS, externalUserIds.toString());
localExtras.put(BidMachineUtils.BCAT, "IAB-1,IAB-3,IAB-5");
localExtras.put(BidMachineUtils.BADV, "https://domain_1.com,https://domain_2.org");
localExtras.put(BidMachineUtils.BAPPS, "com.test.application_1,com.test.application_2,com.test.application_3");
localExtras.put(BidMachineUtils.PRICE_FLOORS, priceFloors.toString());
localExtras.put(BidMachineUtils.PUBLISHER_ID, "YOUR_PUBLISHER_ID");
localExtras.put(BidMachineUtils.PUBLISHER_NAME, "YOUR_PUBLISHER_NAME");
localExtras.put(BidMachineUtils.PUBLISHER_DOMAIN, "YOUR_PUBLISHER_DOMAIN");
localExtras.put(BidMachineUtils.PUBLISHER_CATEGORIES, "YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2");
localExtras.put(BidMachineUtils.PLACEMENT_ID, "YOUR_PLACEMENT_ID");

// Create new MoPubView instance and load
MoPubView mrecMoPubView = new MoPubView(this);
mrecMoPubView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                         ViewGroup.LayoutParams.MATCH_PARENT));
mrecMoPubView.setLocalExtras(localExtras);
mrecMoPubView.setAutorefreshEnabled(false);
mrecMoPubView.setAdUnitId(MREC_KEY);
mrecMoPubView.setBannerAdListener(new MrecViewListener());
mrecMoPubView.loadAd(MoPubView.MoPubAdSize.HEIGHT_250);
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L217)

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
    "store_cat": "YOUR_STORE_CATEGORY",
    "store_subcat": "YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2",
    "fmw_name": "YOUR_FRAMEWORK_NAME",
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
    ],
    "external_user_ids": [{
        "source_id": "source_id_1",
        "value": "value_1"
    }, {
        "source_id": "source_id_2",
        "value": "value_2"
    }],
    "pubid": "YOUR_PUBLISHER_ID",
    "pubname": "YOUR_PUBLISHER_NAME",
    "pubdomain": "YOUR_PUBLISHER_DOMAIN",
    "pubcat": "YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2",
    "placement_id": "YOUR_PLACEMENT_ID"
}
```

Local configuration sample:
```java
// Prepare priceFloors for BidMachine
JSONArray priceFloors = new JSONArray();
try {
    priceFloors.put(new JSONObject().put("id1", 300.006));
    priceFloors.put(new JSONObject().put("id2", 1000));
    priceFloors.put(302.006);
    priceFloors.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

// Prepare externalUserIds for BidMachine
JSONArray externalUserIds = new JSONArray();
try {
    JSONObject externalUserId = new JSONObject()
            .put(BidMachineUtils.EXTERNAL_USER_SOURCE_ID, "source_id")
            .put(BidMachineUtils.EXTERNAL_USER_VALUE, "value");
    externalUserIds.put(externalUserId);
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
localExtras.put(BidMachineUtils.AD_CONTENT_TYPE, "All");
localExtras.put(BidMachineUtils.USER_ID, "YOUR_USER_ID");
localExtras.put(BidMachineUtils.GENDER, "F");
localExtras.put(BidMachineUtils.YOB, "2000");
localExtras.put(BidMachineUtils.KEYWORDS, "Keyword_1,Keyword_2,Keyword_3,Keyword_4");
localExtras.put(BidMachineUtils.COUNTRY, "YOUR_COUNTRY");
localExtras.put(BidMachineUtils.CITY, "YOUR_CITY");
localExtras.put(BidMachineUtils.ZIP, "YOUR_ZIP");
localExtras.put(BidMachineUtils.STURL, "https://store_url.com");
localExtras.put(BidMachineUtils.STORE_CAT, "YOUR_STORE_CATEGORY");
localExtras.put(BidMachineUtils.STORE_SUB_CAT, "YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2");
localExtras.put(BidMachineUtils.FMW_NAME, Framework.UNITY);
localExtras.put(BidMachineUtils.PAID, "true");
localExtras.put(BidMachineUtils.EXTERNAL_USER_IDS, externalUserIds.toString());
localExtras.put(BidMachineUtils.BCAT, "IAB-1,IAB-3,IAB-5");
localExtras.put(BidMachineUtils.BADV, "https://domain_1.com,https://domain_2.org");
localExtras.put(BidMachineUtils.BAPPS, "com.test.application_1,com.test.application_2,com.test.application_3");
localExtras.put(BidMachineUtils.PRICE_FLOORS, priceFloors.toString());
localExtras.put(BidMachineUtils.PUBLISHER_ID, "YOUR_PUBLISHER_ID");
localExtras.put(BidMachineUtils.PUBLISHER_NAME, "YOUR_PUBLISHER_NAME");
localExtras.put(BidMachineUtils.PUBLISHER_DOMAIN, "YOUR_PUBLISHER_DOMAIN");
localExtras.put(BidMachineUtils.PUBLISHER_CATEGORIES, "YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2");
localExtras.put(BidMachineUtils.PLACEMENT_ID, "YOUR_PLACEMENT_ID");

// Create new MoPubInterstitial instance and load
MoPubInterstitial moPubInterstitial = new MoPubInterstitial(this, INTERSTITIAL_KEY);
moPubInterstitial.setLocalExtras(localExtras);
moPubInterstitial.setInterstitialAdListener(new InterstitialListener());
moPubInterstitial.load();
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L271)

## Rewarded implementation
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
    "store_cat": "YOUR_STORE_CATEGORY",
    "store_subcat": "YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2",
    "fmw_name": "YOUR_FRAMEWORK_NAME",
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
    ],
    "external_user_ids": [{
        "source_id": "source_id_1",
        "value": "value_1"
    }, {
        "source_id": "source_id_2",
        "value": "value_2"
    }],
    "pubid": "YOUR_PUBLISHER_ID",
    "pubname": "YOUR_PUBLISHER_NAME",
    "pubdomain": "YOUR_PUBLISHER_DOMAIN",
    "pubcat": "YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2",
    "placement_id": "YOUR_PLACEMENT_ID"
}
```
Local configuration sample:
```java
// Prepare priceFloors for BidMachine
JSONArray priceFloors = new JSONArray();
try {
    priceFloors.put(new JSONObject().put("id1", 300.006));
    priceFloors.put(new JSONObject().put("id2", 1000));
    priceFloors.put(302.006);
    priceFloors.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

// Prepare externalUserIds for BidMachine
JSONArray externalUserIds = new JSONArray();
try {
    JSONObject externalUserId = new JSONObject()
            .put(BidMachineUtils.EXTERNAL_USER_SOURCE_ID, "source_id")
            .put(BidMachineUtils.EXTERNAL_USER_VALUE, "value");
    externalUserIds.put(externalUserId);
} catch (Exception e) {
    e.printStackTrace();
}

// Prepare localExtras for MoPubRewardedAds
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
localExtras.put(BidMachineUtils.STORE_CAT, "YOUR_STORE_CATEGORY");
localExtras.put(BidMachineUtils.STORE_SUB_CAT, "YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2");
localExtras.put(BidMachineUtils.FMW_NAME, Framework.UNITY);
localExtras.put(BidMachineUtils.PAID, "true");
localExtras.put(BidMachineUtils.EXTERNAL_USER_IDS, externalUserIds.toString());
localExtras.put(BidMachineUtils.BCAT, "IAB-1,IAB-3,IAB-5");
localExtras.put(BidMachineUtils.BADV, "https://domain_1.com,https://domain_2.org");
localExtras.put(BidMachineUtils.BAPPS, "com.test.application_1,com.test.application_2,com.test.application_3");
localExtras.put(BidMachineUtils.PRICE_FLOORS, priceFloors.toString());
localExtras.put(BidMachineUtils.PUBLISHER_ID, "YOUR_PUBLISHER_ID");
localExtras.put(BidMachineUtils.PUBLISHER_NAME, "YOUR_PUBLISHER_NAME");
localExtras.put(BidMachineUtils.PUBLISHER_DOMAIN, "YOUR_PUBLISHER_DOMAIN");
localExtras.put(BidMachineUtils.PUBLISHER_CATEGORIES, "YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2");
localExtras.put(BidMachineUtils.PLACEMENT_ID, "YOUR_PLACEMENT_ID");

// Create BidMachineMediationSettings instance with local extras
MediationSettings mediationSettings = new BidMachineMediationSettings()
        .withLocalExtras(localExtras);

// Load MoPubRewardedAds
MoPubRewardedAds.setRewardedAdListener(new RewardedAdListener());
MoPubRewardedAds.loadRewardedAd(REWARDED_KEY, mediationSettings);
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L321)

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
    "media_asset_types": "Icon,Image",
    "user_id": "YOUR_USER_ID",
    "gender": "F",
    "yob": "2000",
    "keywords": "Keyword_1,Keyword_2,Keyword_3,Keyword_4",
    "country": "YOUR_COUNTRY",
    "city": "YOUR_CITY",
    "zip": "YOUR_ZIP",
    "sturl": "https://store_url.com",
    "store_cat": "YOUR_STORE_CATEGORY",
    "store_subcat": "YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2",
    "fmw_name": "YOUR_FRAMEWORK_NAME",
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
    ],
    "external_user_ids": [{
        "source_id": "source_id_1",
        "value": "value_1"
    }, {
        "source_id": "source_id_2",
        "value": "value_2"
    }],
    "pubid": "YOUR_PUBLISHER_ID",
    "pubname": "YOUR_PUBLISHER_NAME",
    "pubdomain": "YOUR_PUBLISHER_DOMAIN",
    "pubcat": "YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2",
    "placement_id": "YOUR_PLACEMENT_ID"
}
```
Local configuration sample:
```java
// Prepare priceFloors for BidMachine
JSONArray priceFloors = new JSONArray();
try {
    priceFloors.put(new JSONObject().put("id1", 300.006));
    priceFloors.put(new JSONObject().put("id2", 1000));
    priceFloors.put(302.006);
    priceFloors.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

// Prepare externalUserIds for BidMachine
JSONArray externalUserIds = new JSONArray();
try {
    JSONObject externalUserId = new JSONObject()
            .put(BidMachineUtils.EXTERNAL_USER_SOURCE_ID, "source_id")
            .put(BidMachineUtils.EXTERNAL_USER_VALUE, "value");
    externalUserIds.put(externalUserId);
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
localExtras.put(BidMachineUtils.MEDIA_ASSET_TYPES, "Icon,Image");
localExtras.put(BidMachineUtils.USER_ID, "YOUR_USER_ID");
localExtras.put(BidMachineUtils.GENDER, "F");
localExtras.put(BidMachineUtils.YOB, "2000");
localExtras.put(BidMachineUtils.KEYWORDS, "Keyword_1,Keyword_2,Keyword_3,Keyword_4");
localExtras.put(BidMachineUtils.COUNTRY, "YOUR_COUNTRY");
localExtras.put(BidMachineUtils.CITY, "YOUR_CITY");
localExtras.put(BidMachineUtils.ZIP, "YOUR_ZIP");
localExtras.put(BidMachineUtils.STURL, "https://store_url.com");
localExtras.put(BidMachineUtils.STORE_CAT, "YOUR_STORE_CATEGORY");
localExtras.put(BidMachineUtils.STORE_SUB_CAT, "YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2");
localExtras.put(BidMachineUtils.FMW_NAME, Framework.UNITY);
localExtras.put(BidMachineUtils.PAID, "true");
localExtras.put(BidMachineUtils.EXTERNAL_USER_IDS, externalUserIds.toString());
localExtras.put(BidMachineUtils.BCAT, "IAB-1,IAB-3,IAB-5");
localExtras.put(BidMachineUtils.BADV, "https://domain_1.com,https://domain_2.org");
localExtras.put(BidMachineUtils.BAPPS, "com.test.application_1,com.test.application_2,com.test.application_3");
localExtras.put(BidMachineUtils.PRICE_FLOORS, priceFloors.toString());
localExtras.put(BidMachineUtils.PUBLISHER_ID, "YOUR_PUBLISHER_ID");
localExtras.put(BidMachineUtils.PUBLISHER_NAME, "YOUR_PUBLISHER_NAME");
localExtras.put(BidMachineUtils.PUBLISHER_DOMAIN, "YOUR_PUBLISHER_DOMAIN");
localExtras.put(BidMachineUtils.PUBLISHER_CATEGORIES, "YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2");
localExtras.put(BidMachineUtils.PLACEMENT_ID, "YOUR_PLACEMENT_ID");

// Create a new instance of BidMachineViewBinder with layout which contains NativeAdContentLayout and its ID
BidMachineViewBinder viewBinder = new BidMachineViewBinder(R.layout.native_ad,
                                                           R.id.native_ad_container);

// Create new MoPubNative instance and load
MoPubNative moPubNative = new MoPubNative(this, NATIVE_KEY, new NativeListener());
moPubNative.registerAdRenderer(new BidMachineNativeRendered(viewBinder));
moPubNative.setLocalExtras(localExtras);
moPubNative.makeRequest();
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineMoPubActivity.java#L376)