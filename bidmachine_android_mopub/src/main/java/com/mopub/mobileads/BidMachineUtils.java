package com.mopub.mobileads;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mopub.common.MoPub;
import com.mopub.common.logging.MoPubLog;
import com.mopub.common.privacy.PersonalInfoManager;
import com.mopub.nativeads.NativeErrorCode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import io.bidmachine.AdRequest;
import io.bidmachine.AdsType;
import io.bidmachine.BidMachine;
import io.bidmachine.BidMachineFetcher;
import io.bidmachine.PriceFloorParams;
import io.bidmachine.Publisher;
import io.bidmachine.TargetingParams;
import io.bidmachine.utils.BMError;
import io.bidmachine.utils.Gender;

public class BidMachineUtils {

    public static final String SELLER_ID = "seller_id";
    public static final String MEDIATION_CONFIG = "mediation_config";
    public static final String COPPA = "coppa";
    public static final String LOGGING_ENABLED = "logging_enabled";
    public static final String TEST_MODE = "test_mode";
    public static final String SUBJECT_TO_GDPR = "subject_to_gdpr";
    public static final String HAS_CONSENT = "has_consent";
    public static final String CONSENT_STRING = "consent_string";
    public static final String ENDPOINT = "endpoint";
    public static final String AD_CONTENT_TYPE = "ad_content_type";
    public static final String MEDIA_ASSET_TYPES = "media_asset_types";
    public static final String USER_ID = "user_id";
    public static final String GENDER = "gender";
    public static final String YOB = "yob";
    public static final String KEYWORDS = "keywords";
    public static final String COUNTRY = "country";
    public static final String CITY = "city";
    public static final String ZIP = "zip";
    public static final String STURL = "sturl";
    public static final String STORE_CAT = "store_cat";
    public static final String STORE_SUB_CAT = "store_subcat";
    public static final String FMW_NAME = "fmw_name";
    public static final String PAID = "paid";
    public static final String BCAT = "bcat";
    public static final String BADV = "badv";
    public static final String BAPPS = "bapps";
    public static final String PRICE_FLOORS = "price_floors";
    public static final String BANNER_WIDTH = "banner_width";
    public static final String PUBLISHER_ID = "pubid";
    public static final String PUBLISHER_NAME = "pubname";
    public static final String PUBLISHER_DOMAIN = "pubdomain";
    public static final String PUBLISHER_CATEGORIES = "pubcat";

    private static Map<String, String> configuration;

    static void storeConfiguration(@NonNull Map<String, String> configuration) {
        BidMachineUtils.configuration = configuration;
    }

    /**
     * @param extras - map where are seller_id, coppa, logging_enabled, test_mode, consent_string,
     *               mediation_config, endpoint
     * @return was initialize or not
     */
    public static <T> boolean prepareBidMachine(@NonNull Context context,
                                                @NonNull Map<String, T> extras,
                                                boolean isInitializingRequired) {
        Boolean loggingEnabled = parseBoolean(extras.get(LOGGING_ENABLED));
        if (loggingEnabled != null) {
            BidMachine.setLoggingEnabled(loggingEnabled);
        }
        Boolean testMode = parseBoolean(extras.get(TEST_MODE));
        if (testMode != null) {
            BidMachine.setTestMode(testMode);
        }
        Boolean coppa = parseBoolean(extras.get(COPPA));
        if (coppa != null) {
            BidMachine.setCoppa(coppa);
        }
        String endpoint = parseString(extras.get(ENDPOINT));
        if (!TextUtils.isEmpty(endpoint)) {
            assert endpoint != null;
            BidMachine.setEndpoint(endpoint);
        }
        BidMachine.setPublisher(findPublisher(extras));
        BidMachineUtils.updateGDPR(parseString(extras.get(CONSENT_STRING)));
        String jsonData = parseString(extras.get(MEDIATION_CONFIG));
        if (jsonData != null) {
            BidMachine.registerNetworks(context, jsonData);
            return initialize(context, extras);
        } else if (isInitializingRequired) {
            return initialize(context, extras);
        }
        return true;
    }

    private static <T> boolean initialize(@NonNull Context context,
                                          @NonNull Map<String, T> extras) {
        if (!BidMachine.isInitialized()) {
            String sellerId = parseString(extras.get(SELLER_ID));
            if (!TextUtils.isEmpty(sellerId)) {
                assert sellerId != null;
                BidMachine.initialize(context, sellerId);
                return true;
            } else {
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM,
                             BidMachineUtils.class.getSimpleName(),
                             "seller_id not found anywhere (serverExtras, localExtras, configuration). BidMachine not initialized");
                return false;
            }
        }
        return true;
    }

    /**
     * Transform BidMachine error to MoPub error
     *
     * @param bmError - BidMachine error object
     * @return MoPub error object
     */
    @NonNull
    static MoPubErrorCode transformToMoPubErrorCode(@NonNull BMError bmError) {
        if (bmError == BMError.NoContent
                || bmError == BMError.NotLoaded
                || bmError == BMError.Server
                || bmError == BMError.Connection) {
            return MoPubErrorCode.NO_FILL;
        } else if (bmError == BMError.TimeoutError) {
            return MoPubErrorCode.NETWORK_TIMEOUT;
        } else if (bmError == BMError.IncorrectAdUnit) {
            return MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR;
        } else if (bmError == BMError.Internal) {
            return MoPubErrorCode.INTERNAL_ERROR;
        } else if (bmError == BMError.AlreadyShown
                || bmError == BMError.Destroyed
                || bmError == BMError.NotInitialized
                || bmError == BMError.Expired) {
            return MoPubErrorCode.NETWORK_INVALID_STATE;
        } else {
            return MoPubErrorCode.UNSPECIFIED;
        }
    }

    /**
     * Transform BidMachine error to MoPubNative error
     *
     * @param bmError - BidMachine error object
     * @return MoPubNative error object
     */
    @NonNull
    public static NativeErrorCode transformToMoPubNativeErrorCode(@NonNull BMError bmError) {
        if (bmError == BMError.NoContent
                || bmError == BMError.NotLoaded
                || bmError == BMError.Server
                || bmError == BMError.Connection) {
            return NativeErrorCode.NETWORK_NO_FILL;
        } else if (bmError == BMError.TimeoutError) {
            return NativeErrorCode.NETWORK_TIMEOUT;
        } else if (bmError == BMError.IncorrectAdUnit) {
            return NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR;
        } else if (bmError == BMError.Internal) {
            return NativeErrorCode.NETWORK_INVALID_STATE;
        } else if (bmError == BMError.AlreadyShown
                || bmError == BMError.Destroyed
                || bmError == BMError.NotInitialized
                || bmError == BMError.Expired) {
            return NativeErrorCode.NETWORK_INVALID_STATE;
        } else {
            return NativeErrorCode.UNSPECIFIED;
        }
    }

    /**
     * Prepare fused map from MoPub extras, localExtras and configuration
     *
     * @param extras      - MoPub map
     * @param localExtras - map from local, set with setLocalExtras
     * @return fused map which must be contains serverExtras, localExtras and configuration
     */
    @NonNull
    public static Map<String, Object> getFusedMap(@Nullable Map<String, String> extras,
                                                  @Nullable Map<String, Object> localExtras) {
        Map<String, Object> fusedExtras = new HashMap<>();
        putMap(fusedExtras, configuration);
        putMap(fusedExtras, localExtras);
        putMap(fusedExtras, extras);
        return fusedExtras;
    }

    /**
     * Prepare fused map from MoPub extras and configuration
     *
     * @param extras - MoPub map
     * @return fused map which must be contains extras and configuration
     */
    @NonNull
    public static Map<String, Object> getFusedMap(@Nullable Map<String, String> extras) {
        Map<String, Object> fusedExtras = new HashMap<>();
        putMap(fusedExtras, configuration);
        putMap(fusedExtras, extras);
        return fusedExtras;
    }

    /**
     * +--------------+------------------------------------------------------------------------------------------------------+------------+
     * | Key          | Definition                                                                                           | Value type |
     * +--------------+------------------------------------------------------------------------------------------------------+------------+
     * | user_id      | Vendor-specific ID for the user                                                                      | String     |
     * | gender       | Gender, one of following: "F", "M", "O"                                                              | String     |
     * | yob          | Year of birth as a 4-digit integer (e.g - 1990)                                                      | String     |
     * | keywords     | List of keywords, interests, or intents (separated by comma)                                         | String     |
     * | country      | Country of the user's home base (i.e., not necessarily their current location)                       | String     |
     * | city         | City of the user's home base (i.e., not necessarily their current location)                          | String     |
     * | zip          | Zip of the user's home base (i.e., not necessarily their current location)                           | String     |
     * | sturl        | App store URL for an installed app; for IQG 2.1 compliance                                           | String     |
     * | store_cat    | Sets App store category definitions (e.g - "games")                                                  | String     |
     * | store_subcat | Sets App Store Subcategory definitions. The array is always capped at 3 strings (separated by comma) | String     |
     * | fmw_name     | Sets app framework definitions                                                                       | String     |
     * | paid         | Determines, if it is a free or paid version of the app                                               | String     |
     * | bcat         | Block list of content categories using IDs (separated by comma)                                      | String     |
     * | badv         | Block list of advertisers by their domains (separated by comma)                                      | String     |
     * | bapps        | Block list of apps where ads are disallowed (separated by comma)                                     | String     |
     * +--------------+------------------------------------------------------------------------------------------------------+------------+
     * <p>
     * Map<String, String> extraData = new HashMap<>();
     * extraData.put("user_id", "user123");
     * extraData.put("gender", Gender.Female.getOrtbValue());
     * extraData.put("yob", "2000");
     * extraData.put("keywords", "Keyword_1,Keyword_2,Keyword_3,Keyword_4");
     * extraData.put("country", "Russia");
     * extraData.put("city", "Kirov");
     * extraData.put("zip", "610000");
     * extraData.put("sturl", "https://store_url.com");
     * extraData.put("store_cat", "store category");
     * extraData.put("store_subcat", "store_sub_category_1,store_sub_category_2");
     * extraData.put("fmw_name", "native");
     * extraData.put("paid", "true");
     * extraData.put("bcat", "IAB-1,IAB-3,IAB-5");
     * extraData.put("badv", "https://domain_1.com,https://domain_2.org");
     * extraData.put("bapps", "application_1,application_2,application_3");
     *
     * @param extras - map where are the necessary parameters for targeting
     * @return TargetingParams with targeting from extras
     */
    @NonNull
    public static TargetingParams findTargetingParams(@NonNull Map<String, Object> extras) {
        TargetingParams targetingParams = new TargetingParams();
        String userId = parseString(extras.get(USER_ID));
        if (userId == null) {
            userId = parseString(extras.get("userId"));
        }
        if (userId != null) {
            targetingParams.setUserId(userId);
        }
        Gender gender = parseGender(extras.get(GENDER));
        if (gender != null) {
            targetingParams.setGender(gender);
        }
        int birthdayYear = parseInteger(extras.get(YOB));
        if (birthdayYear > -1) {
            targetingParams.setBirthdayYear(birthdayYear);
        }
        String keywords = parseString(extras.get(KEYWORDS));
        if (keywords != null) {
            targetingParams.setKeywords(splitString(keywords));
        }
        String country = parseString(extras.get(COUNTRY));
        if (country != null) {
            targetingParams.setCountry(country);
        }
        String city = parseString(extras.get(CITY));
        if (city != null) {
            targetingParams.setCity(city);
        }
        String zip = parseString(extras.get(ZIP));
        if (zip != null) {
            targetingParams.setZip(zip);
        }
        String sturl = parseString(extras.get(STURL));
        if (sturl != null) {
            targetingParams.setStoreUrl(sturl);
        }
        String storeCategory = parseString(extras.get(STORE_CAT));
        if (storeCategory != null) {
            targetingParams.setStoreCategory(storeCategory);
        }
        String storeSubCategories = parseString(extras.get(STORE_SUB_CAT));
        if (storeSubCategories != null) {
            targetingParams.setStoreSubCategories(splitString(storeSubCategories));
        }
        String frameworkName = parseString(extras.get(FMW_NAME));
        if (frameworkName != null) {
            targetingParams.setFramework(frameworkName);
        }
        Boolean paid = parseBoolean(extras.get(PAID));
        if (paid != null) {
            targetingParams.setPaid(paid);
        }
        String bcat = parseString(extras.get(BCAT));
        if (bcat != null) {
            for (String value : splitString(bcat)) {
                targetingParams.addBlockedAdvertiserIABCategory(value);
            }
        }
        String badv = parseString(extras.get(BADV));
        if (badv != null) {
            for (String value : splitString(badv)) {
                targetingParams.addBlockedAdvertiserDomain(value);
            }
        }
        String bapps = parseString(extras.get(BAPPS));
        if (bapps != null) {
            for (String value : splitString(bapps)) {
                targetingParams.addBlockedApplication(value);
            }
        }
        return targetingParams;
    }

    /**
     * +--------------+---------------------+---------------------+
     * | Key          | Definition          | Value type          |
     * +--------------+---------------------+---------------------+
     * | price_floors | List of price floor | JSONArray in String |
     * +--------------+---------------------+---------------------+
     * <p>
     * JSONArray jsonArray = new JSONArray();
     * jsonArray.put(new JSONObject().put("id1", 300.006));
     * jsonArray.put(new JSONObject().put("id2", 1000));
     * jsonArray.put(302.006);
     * jsonArray.put(1002);
     * <p>
     * Map<String, String> extraData = new HashMap<>();
     * extraData.put("price_floors", jsonArray.toString());
     *
     * @param extras - map where are the necessary parameters for price floor
     * @return PriceFloorParams with price floors from extras
     */
    @NonNull
    public static PriceFloorParams findPriceFloorParams(@NonNull Map<String, Object> extras) {
        String priceFloors = parseString(extras.get(PRICE_FLOORS));
        if (priceFloors == null) {
            priceFloors = parseString(extras.get("priceFloors"));
        }
        return createPriceFloorParams(priceFloors);
    }

    /**
     * +-----------+---------------------------------------------------------------+------------+
     * | Key       | Definition                                                    | Value type |
     * +-----------+---------------------------------------------------------------+------------+
     * | pubid     | Unique publisher identifier                                   | String     |
     * | pubname   | Displayable name of the publisher                             | String     |
     * | pubdomain | Highest level domain of the publisher (e.g., “publisher.com”) | String     |
     * | pubcat    | List of content categories (separated by comma)               | String     |
     * +-----------+---------------------------------------------------------------+------------+
     * <p>
     * Map<String, String> extraData = new HashMap<>();
     * extraData.put("pubid", "publisher_id");
     * extraData.put("pubname", "publisher_name");
     * extraData.put("pubdomain", "publisher.com");
     * extraData.put("pubcat", "cat_1,cat_2,cat_3");
     *
     * @param extras - map where are the necessary parameters for {@link Publisher}
     * @return {@link Publisher} with parameters from extras
     */
    @NonNull
    private static <T> Publisher findPublisher(@NonNull Map<String, T> extras) {
        Publisher.Builder publisherBuilder = new Publisher.Builder();
        publisherBuilder.setId(parseString(extras.get(PUBLISHER_ID)));
        publisherBuilder.setName(parseString(extras.get(PUBLISHER_NAME)));
        publisherBuilder.setDomain(parseString(extras.get(PUBLISHER_DOMAIN)));
        String publisherCategories = parseString(extras.get(PUBLISHER_CATEGORIES));
        if (publisherCategories != null) {
            for (String value : splitString(publisherCategories)) {
                publisherBuilder.addCategory(value);
            }
        }
        return publisherBuilder.build();
    }

    /**
     * Update GDPR state
     *
     * @param consentString - GDPR consent string
     */
    private static void updateGDPR(String consentString) {
        PersonalInfoManager personalInfoManager = MoPub.getPersonalInformationManager();
        if (personalInfoManager != null) {
            BidMachine.setSubjectToGDPR(personalInfoManager.gdprApplies());
            BidMachine.setConsentConfig(personalInfoManager.canCollectPersonalInformation(),
                                        consentString != null ? consentString : "");
        }
    }

    private static void putMap(@Nullable Map<String, Object> fusedMap,
                               @Nullable Map<String, ?> map) {
        if (fusedMap == null || map == null) {
            return;
        }
        try {
            fusedMap.putAll(map);
        } catch (Exception ignore) {
        }
    }

    @Nullable
    private static Boolean parseBoolean(Object object) {
        if (object instanceof Boolean) {
            return (Boolean) object;
        } else if (object instanceof String) {
            return Boolean.parseBoolean((String) object);
        } else {
            return null;
        }
    }

    @Nullable
    public static String parseString(Object object) {
        if (object instanceof String) {
            return (String) object;
        } else {
            return null;
        }
    }

    static int parseInteger(Object object) {
        if (object instanceof Integer) {
            return (int) object;
        } else if (object instanceof Double) {
            return ((Double) object).intValue();
        } else if (object instanceof String) {
            try {
                return Integer.parseInt((String) object);
            } catch (Exception e) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    @Nullable
    private static Gender parseGender(Object object) {
        String ortbValue = parseString(object);
        if (ortbValue == null) {
            return null;
        }
        if (Gender.Female.getOrtbValue().equals(ortbValue)) {
            return Gender.Female;
        } else if (Gender.Male.getOrtbValue().equals(ortbValue)) {
            return Gender.Male;
        } else {
            return Gender.Omitted;
        }
    }

    @NonNull
    public static String[] splitString(String value) {
        if (TextUtils.isEmpty(value)) {
            return new String[0];
        }
        try {
            return value.split(",");
        } catch (Exception e) {
            return new String[0];
        }
    }

    @NonNull
    private static PriceFloorParams createPriceFloorParams(@Nullable String jsonArrayString) {
        PriceFloorParams priceFloorParams = new PriceFloorParams();
        if (TextUtils.isEmpty(jsonArrayString)) {
            return priceFloorParams;
        }

        try {
            JSONArray jsonArray = new JSONArray(jsonArrayString);
            for (int i = 0; i < jsonArray.length(); i++) {
                Object object = jsonArray.opt(i);
                if (object instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) object;
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String id = iterator.next();
                        double price = parsePrice(jsonObject.opt(id));
                        if (!TextUtils.isEmpty(id) && price > -1) {
                            priceFloorParams.addPriceFloor(id, price);
                        }
                    }
                } else {
                    double price = parsePrice(object);
                    if (price > -1) {
                        priceFloorParams.addPriceFloor(price);
                    }
                }
            }
        } catch (Exception e) {
            return new PriceFloorParams();
        }

        return priceFloorParams;
    }

    private static double parsePrice(Object object) {
        if (object instanceof Double) {
            return (double) object;
        } else if (object instanceof Integer) {
            return ((Integer) object).doubleValue();
        } else if (object instanceof String) {
            return convertToPrice((String) object);
        }
        return -1;
    }

    private static double convertToPrice(String value) {
        if (!TextUtils.isEmpty(value)) {
            try {
                if (value.lastIndexOf('.') > value.lastIndexOf(',')) {
                    return NumberFormat.getInstance(Locale.TAIWAN).parse(value).doubleValue();
                } else {
                    return NumberFormat.getInstance().parse(value).doubleValue();
                }
            } catch (Exception e) {
                return -1;
            }
        }
        return -1;
    }

    @Nullable
    public static <T extends AdRequest> T obtainCachedRequest(@NonNull AdsType adsType,
                                                              @NonNull Map<String, Object> fusedMap) {
        return obtainCachedRequest(adsType, fusedMap.get(BidMachineFetcher.KEY_ID));
    }

    @Nullable
    static <T extends AdRequest> T obtainCachedRequest(@NonNull AdsType adsType,
                                                       @Nullable Object id) {
        return id != null ? BidMachineFetcher.release(adsType, String.valueOf(id)) : null;
    }


    /**
     * Append keywords and extras to {@link MoPubAd} from loaded {@link io.bidmachine.AdRequest}
     *
     * @param moPubAd   - {@link MoPubAd} that will be loaded
     * @param adRequest - loaded {@link io.bidmachine.AdRequest}
     */
    public static void appendRequest(@NonNull MoPubAd moPubAd, @NonNull AdRequest<?, ?> adRequest) {
        Map<String, String> fetchParams = BidMachineFetcher.fetch(adRequest);
        if (fetchParams != null) {
            appendRequest(moPubAd, fetchParams);
        }
    }

    /**
     * Append keywords and extras to {@link MoPubAd} from {@link Map} which were obtained
     * using {@link BidMachineFetcher#fetch(io.bidmachine.AdRequest)}
     *
     * @param moPubAd     - {@link MoPubAd} that will be loaded
     * @param fetchParams - parameters which were obtained using {@link BidMachineFetcher#fetch(io.bidmachine.AdRequest)}
     */
    public static void appendRequest(@NonNull MoPubAd moPubAd,
                                     @NonNull Map<String, String> fetchParams) {
        appendKeyword(moPubAd, fetchParams);
        appendLocalExtras(moPubAd, fetchParams);
    }

    /**
     * Append keywords to {@link MoPubAd} from loaded {@link io.bidmachine.AdRequest}
     *
     * @param moPubAd - {@link MoPubAd} that will be loaded
     * @param params  - parameters to be added to {@link MoPubAd#getKeywords()}
     */
    public static void appendKeyword(@NonNull MoPubAd moPubAd,
                                     @Nullable Map<String, String> params) {
        appendKeyword(moPubAd, toKeywords(params));
    }

    /**
     * Append keywords to {@link MoPubAd} from loaded {@link io.bidmachine.AdRequest}
     *
     * @param moPubAd  - {@link MoPubAd} that will be loaded
     * @param keywords - the string to be added to {@link MoPubAd#getKeywords()}
     */
    public static void appendKeyword(@NonNull MoPubAd moPubAd, @Nullable String keywords) {
        String currentKeywords = moPubAd.getKeywords();
        String newKeywords = fuseKeywords(currentKeywords, keywords);
        moPubAd.setKeywords(newKeywords);
    }

    /**
     * Convert {@link Map} to MoPub keywords
     *
     * @param map - parameters that will be transformed in MoPub format keywords
     * @return MoPub format keywords
     */
    @NonNull
    public static String toKeywords(@Nullable Map<String, String> map) {
        StringBuilder builder = new StringBuilder();
        if (map != null) {
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                if (builder.length() > 0) {
                    builder.append(",");
                }
                builder.append(entry.getKey())
                        .append(":")
                        .append(entry.getValue());
            }
        }
        return builder.toString();
    }

    /**
     * Fuse two keywords string into one
     *
     * @param currentKeywords - first keywords string
     * @param newKeywords     - second keywords string
     * @return fused keywords string
     */
    @Nullable
    public static String fuseKeywords(@Nullable String currentKeywords,
                                      @Nullable String newKeywords) {
        if (!TextUtils.isEmpty(currentKeywords) && !TextUtils.isEmpty(newKeywords)) {
            return String.format("%s,%s", currentKeywords, newKeywords);
        } else if (!TextUtils.isEmpty(currentKeywords)) {
            return currentKeywords;
        } else if (!TextUtils.isEmpty(newKeywords)) {
            return newKeywords;
        } else {
            return null;
        }
    }

    /**
     * Append extras to {@link MoPubAd} from loaded {@link io.bidmachine.AdRequest}
     *
     * @param moPubAd     - {@link MoPubAd} that will be loaded
     * @param localExtras - parameters to be added as local extras
     * @param <T>         - value format
     */
    public static <T> void appendLocalExtras(@NonNull MoPubAd moPubAd,
                                             @NonNull Map<String, T> localExtras) {
        Map<String, Object> moPubLocalExtras = moPubAd.getLocalExtras();
        moPubLocalExtras.putAll(localExtras);
        moPubAd.setLocalExtras(moPubLocalExtras);
    }

}
