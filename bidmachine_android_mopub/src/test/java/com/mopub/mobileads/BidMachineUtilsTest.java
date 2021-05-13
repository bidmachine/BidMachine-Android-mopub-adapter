package com.mopub.mobileads;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.explorestack.protobuf.adcom.Placement;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import io.bidmachine.AdRequest;
import io.bidmachine.AdsType;
import io.bidmachine.CreativeFormat;
import io.bidmachine.TargetingParams;
import io.bidmachine.banner.BannerRequest;
import io.bidmachine.models.AuctionResult;
import io.bidmachine.models.DataRestrictions;
import io.bidmachine.unified.UnifiedAdRequestParams;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 28)
public class BidMachineUtilsTest {

    private Activity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(Activity.class).create().get();
    }

    @Test
    public void appendRequest_moPubViewWithAdRequest() {
        MoPubView moPubView = new MoPubView(activity);
        Map<String, String> customParams = new HashMap<String, String>() {{
            put("test_key_1", "test_value_1");
            put("test_key_2", "test_value_2");
        }};
        AuctionResult auctionResult = createAuctionResult("test_id",
                                                          10.10,
                                                          "test_network_key",
                                                          CreativeFormat.Banner,
                                                          customParams);
        AdRequest<?, ?> adRequest = spy(createAdRequest(AdsType.Banner));
        doReturn(auctionResult).when(adRequest).getAuctionResult();

        BidMachineUtils.appendRequest(moPubView, adRequest);
        String moPubKeywords = moPubView.getKeywords();
        assertNotNull(moPubKeywords);
        String[] keywordArray = moPubKeywords.split(",");
        assertEquals(6, keywordArray.length);
        contains(keywordArray, "bm_id:test_id");
        contains(keywordArray, "bm_pf:10.10");
        contains(keywordArray, "bm_network_key:test_network_key");
        contains(keywordArray, "bm_ad_type:display");
        contains(keywordArray, "test_key_1:test_value_1");
        contains(keywordArray, "test_key_2:test_value_2");

        Map<String, Object> localExtras = moPubView.getLocalExtras();
        assertEquals(6, localExtras.size());
        assertEquals("test_id", localExtras.get("bm_id"));
        assertEquals("10.10", localExtras.get("bm_pf"));
        assertEquals("test_network_key", localExtras.get("bm_network_key"));
        assertEquals("display", localExtras.get("bm_ad_type"));
        assertEquals("test_value_1", localExtras.get("test_key_1"));
        assertEquals("test_value_2", localExtras.get("test_key_2"));
    }

    @Test
    public void appendRequest_moPubInterstitialWithAdRequest() {
        MoPubInterstitial moPubInterstitial = new MoPubInterstitial(activity, "test_ad_unit_id");
        Map<String, String> customParams = new HashMap<String, String>() {{
            put("test_key_1", "test_value_1");
            put("test_key_2", "test_value_2");
        }};
        AuctionResult auctionResult = createAuctionResult("test_id",
                                                          10.10,
                                                          "test_network_key",
                                                          CreativeFormat.Banner,
                                                          customParams);
        AdRequest<?, ?> adRequest = spy(createAdRequest(AdsType.Banner));
        doReturn(auctionResult).when(adRequest).getAuctionResult();

        BidMachineUtils.appendRequest(moPubInterstitial, adRequest);
        String moPubKeywords = moPubInterstitial.getKeywords();
        assertNotNull(moPubKeywords);
        String[] keywordArray = moPubKeywords.split(",");
        assertEquals(6, keywordArray.length);
        contains(keywordArray, "bm_id:test_id");
        contains(keywordArray, "bm_pf:10.10");
        contains(keywordArray, "bm_network_key:test_network_key");
        contains(keywordArray, "bm_ad_type:display");
        contains(keywordArray, "test_key_1:test_value_1");
        contains(keywordArray, "test_key_2:test_value_2");

        Map<String, Object> localExtras = moPubInterstitial.getLocalExtras();
        assertEquals(6, localExtras.size());
        assertEquals("test_id", localExtras.get("bm_id"));
        assertEquals("10.10", localExtras.get("bm_pf"));
        assertEquals("test_network_key", localExtras.get("bm_network_key"));
        assertEquals("display", localExtras.get("bm_ad_type"));
        assertEquals("test_value_1", localExtras.get("test_key_1"));
        assertEquals("test_value_2", localExtras.get("test_key_2"));
    }

    @Test
    public void appendRequest_moPubViewWithMap() {
        MoPubView moPubView = new MoPubView(activity);
        Map<String, String> fetchParams = new HashMap<>();
        fetchParams.put("bm_id", "test_id");
        fetchParams.put("bm_pf", "10.10");
        fetchParams.put("bm_network_key", "test_network_key");
        fetchParams.put("bm_ad_type", "display");
        fetchParams.put("test_key_1", "test_value_1");
        fetchParams.put("test_key_2", "test_value_2");

        BidMachineUtils.appendRequest(moPubView, fetchParams);
        String moPubKeywords = moPubView.getKeywords();
        assertNotNull(moPubKeywords);
        String[] keywordArray = moPubKeywords.split(",");
        assertEquals(6, keywordArray.length);
        contains(keywordArray, "bm_id:test_id");
        contains(keywordArray, "bm_pf:10.10");
        contains(keywordArray, "bm_network_key:test_network_key");
        contains(keywordArray, "bm_ad_type:display");
        contains(keywordArray, "test_key_1:test_value_1");
        contains(keywordArray, "test_key_2:test_value_2");

        Map<String, Object> localExtras = moPubView.getLocalExtras();
        assertEquals(6, localExtras.size());
        assertEquals("test_id", localExtras.get("bm_id"));
        assertEquals("10.10", localExtras.get("bm_pf"));
        assertEquals("test_network_key", localExtras.get("bm_network_key"));
        assertEquals("display", localExtras.get("bm_ad_type"));
        assertEquals("test_value_1", localExtras.get("test_key_1"));
        assertEquals("test_value_2", localExtras.get("test_key_2"));
    }

    @Test
    public void appendRequest_moPubInterstitialWithMap() {
        MoPubInterstitial moPubInterstitial = new MoPubInterstitial(activity, "test_ad_unit_id");
        Map<String, String> fetchParams = new HashMap<>();
        fetchParams.put("bm_id", "test_id");
        fetchParams.put("bm_pf", "10.10");
        fetchParams.put("bm_network_key", "test_network_key");
        fetchParams.put("bm_ad_type", "display");
        fetchParams.put("test_key_1", "test_value_1");
        fetchParams.put("test_key_2", "test_value_2");

        BidMachineUtils.appendRequest(moPubInterstitial, fetchParams);
        String moPubKeywords = moPubInterstitial.getKeywords();
        assertNotNull(moPubKeywords);
        String[] keywordArray = moPubKeywords.split(",");
        assertEquals(6, keywordArray.length);
        contains(keywordArray, "bm_id:test_id");
        contains(keywordArray, "bm_pf:10.10");
        contains(keywordArray, "bm_network_key:test_network_key");
        contains(keywordArray, "bm_ad_type:display");
        contains(keywordArray, "test_key_1:test_value_1");
        contains(keywordArray, "test_key_2:test_value_2");

        Map<String, Object> localExtras = moPubInterstitial.getLocalExtras();
        assertEquals(6, localExtras.size());
        assertEquals("test_id", localExtras.get("bm_id"));
        assertEquals("10.10", localExtras.get("bm_pf"));
        assertEquals("test_network_key", localExtras.get("bm_network_key"));
        assertEquals("display", localExtras.get("bm_ad_type"));
        assertEquals("test_value_1", localExtras.get("test_key_1"));
        assertEquals("test_value_2", localExtras.get("test_key_2"));
    }

    @Test
    public void appendKeyword_moPubViewWithString() {
        MoPubView moPubView = new MoPubView(activity);

        BidMachineUtils.appendKeyword(moPubView, (String) null);
        assertNull(moPubView.getKeywords());

        BidMachineUtils.appendKeyword(moPubView, "");
        assertNull(moPubView.getKeywords());

        BidMachineUtils.appendKeyword(moPubView, "test_keywords_1");
        assertEquals("test_keywords_1", moPubView.getKeywords());

        BidMachineUtils.appendKeyword(moPubView, "test_keywords_2");
        assertEquals("test_keywords_1,test_keywords_2", moPubView.getKeywords());
    }

    @Test
    public void appendKeyword_moPubInterstitialWithString() {
        MoPubInterstitial moPubInterstitial = new MoPubInterstitial(activity, "test_ad_unit_id");

        BidMachineUtils.appendKeyword(moPubInterstitial, (String) null);
        assertNull(moPubInterstitial.getKeywords());

        BidMachineUtils.appendKeyword(moPubInterstitial, "");
        assertNull(moPubInterstitial.getKeywords());

        BidMachineUtils.appendKeyword(moPubInterstitial, "test_keywords_1");
        assertEquals("test_keywords_1", moPubInterstitial.getKeywords());

        BidMachineUtils.appendKeyword(moPubInterstitial, "test_keywords_2");
        assertEquals("test_keywords_1,test_keywords_2", moPubInterstitial.getKeywords());
    }

    @Test
    public void appendKeyword_moPubViewWithMap() {
        MoPubView moPubView = new MoPubView(activity);

        BidMachineUtils.appendKeyword(moPubView, (Map<String, String>) null);
        assertNull(moPubView.getKeywords());

        Map<String, String> params = new HashMap<>();
        BidMachineUtils.appendKeyword(moPubView, params);
        assertNull(moPubView.getKeywords());

        params.put("test_key_1", "test_value_1");
        BidMachineUtils.appendKeyword(moPubView, params);
        assertEquals("test_key_1:test_value_1", moPubView.getKeywords());

        params.clear();
        params.put("test_key_2", "test_value_2");
        BidMachineUtils.appendKeyword(moPubView, params);
        String[] keywords = moPubView.getKeywords().split(",");
        contains(keywords, "test_key_1:test_value_1");
        contains(keywords, "test_key_2:test_value_2");
    }

    @Test
    public void appendKeyword_moPubInterstitialWithMap() {
        MoPubInterstitial moPubInterstitial = new MoPubInterstitial(activity, "test_ad_unit_id");

        BidMachineUtils.appendKeyword(moPubInterstitial, (Map<String, String>) null);
        assertNull(moPubInterstitial.getKeywords());

        Map<String, String> params = new HashMap<>();
        BidMachineUtils.appendKeyword(moPubInterstitial, params);
        assertNull(moPubInterstitial.getKeywords());

        params.put("test_key_1", "test_value_1");
        BidMachineUtils.appendKeyword(moPubInterstitial, params);
        assertEquals("test_key_1:test_value_1", moPubInterstitial.getKeywords());

        params.clear();
        params.put("test_key_2", "test_value_2");
        BidMachineUtils.appendKeyword(moPubInterstitial, params);
        String[] keywords = moPubInterstitial.getKeywords().split(",");
        contains(keywords, "test_key_1:test_value_1");
        contains(keywords, "test_key_2:test_value_2");
    }

    @Test
    public void toKeywords() {
        assertEquals("", BidMachineUtils.toKeywords(null));

        Map<String, String> params = new TreeMap<>();
        assertEquals("", BidMachineUtils.toKeywords(params));

        params.put("test_key_1", "test_value_1");
        params.put("test_key_2", "test_value_2");
        params.put("test_key_3", "test_value_3");
        assertEquals("test_key_1:test_value_1,test_key_2:test_value_2,test_key_3:test_value_3",
                     BidMachineUtils.toKeywords(params));
    }

    @Test
    public void fuseKeywords() {
        String result = BidMachineUtils.fuseKeywords(null, null);
        assertNull(result);

        result = BidMachineUtils.fuseKeywords("", "");
        assertNull(result);

        result = BidMachineUtils.fuseKeywords("current_keywords", null);
        assertEquals("current_keywords", result);

        result = BidMachineUtils.fuseKeywords(null, "new_keywords");
        assertEquals("new_keywords", result);

        result = BidMachineUtils.fuseKeywords("current_keywords", "new_keywords");
        assertEquals("current_keywords,new_keywords", result);
    }

    @Test
    public void appendLocalExtras_moPubView() {
        MoPubView moPubView = new MoPubView(activity);
        Map<String, Object> localExtras = new HashMap<>();

        BidMachineUtils.appendLocalExtras(moPubView, localExtras);
        Map<String, Object> result = moPubView.getLocalExtras();
        assertNotNull(result);
        assertEquals(0, result.size());

        localExtras.put("test_key_1", "test_value_1");
        BidMachineUtils.appendLocalExtras(moPubView, localExtras);
        result = moPubView.getLocalExtras();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test_value_1", result.get("test_key_1"));

        localExtras.put("test_key_2", true);
        localExtras.put("test_key_3", 123);
        BidMachineUtils.appendLocalExtras(moPubView, localExtras);
        result = moPubView.getLocalExtras();
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("test_value_1", result.get("test_key_1"));
        assertEquals(true, result.get("test_key_2"));
        assertEquals(123, result.get("test_key_3"));
    }

    @Test
    public void appendLocalExtras_moPubInterstitial() {
        MoPubInterstitial moPubInterstitial = new MoPubInterstitial(activity, "test_ad_unit_id");
        Map<String, Object> localExtras = new HashMap<>();

        BidMachineUtils.appendLocalExtras(moPubInterstitial, localExtras);
        Map<String, Object> result = moPubInterstitial.getLocalExtras();
        assertNotNull(result);
        assertEquals(0, result.size());

        localExtras.put("test_key_1", "test_value_1");
        BidMachineUtils.appendLocalExtras(moPubInterstitial, localExtras);
        result = moPubInterstitial.getLocalExtras();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test_value_1", result.get("test_key_1"));

        localExtras.put("test_key_2", true);
        localExtras.put("test_key_3", 123);
        BidMachineUtils.appendLocalExtras(moPubInterstitial, localExtras);
        result = moPubInterstitial.getLocalExtras();
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("test_value_1", result.get("test_key_1"));
        assertEquals(true, result.get("test_key_2"));
        assertEquals(123, result.get("test_key_3"));
    }


    @SuppressWarnings("SameParameterValue")
    private AdRequest<?, ?> createAdRequest(@NonNull AdsType adsType) {
        return new AdRequest<BannerRequest, UnifiedAdRequestParams>(adsType) {
            @Override
            protected boolean isPlacementObjectValid(@NonNull Placement placement) throws Throwable {
                return false;
            }

            @NonNull
            @Override
            protected UnifiedAdRequestParams createUnifiedAdRequestParams(@NonNull TargetingParams targetingParams,
                                                                          @NonNull DataRestrictions dataRestrictions) {
                return null;
            }
        };
    }

    @SuppressWarnings("SameParameterValue")
    private AuctionResult createAuctionResult(@NonNull String id,
                                              double price,
                                              @NonNull String networkKey,
                                              @Nullable CreativeFormat creativeFormat,
                                              @NonNull Map<String, String> customParams) {
        return new AuctionResult() {
            @NonNull
            @Override
            public String getId() {
                return id;
            }

            @Nullable
            @Override
            public String getDemandSource() {
                return null;
            }

            @Override
            public double getPrice() {
                return price;
            }

            @Nullable
            @Override
            public String getDeal() {
                return null;
            }

            @Override
            public String getSeat() {
                return null;
            }

            @NonNull
            @Override
            public String getCreativeId() {
                return null;
            }

            @Nullable
            @Override
            public String getCid() {
                return null;
            }

            @Nullable
            @Override
            public String[] getAdDomains() {
                return new String[0];
            }

            @NonNull
            @Override
            public String getNetworkKey() {
                return networkKey;
            }

            @NonNull
            @Override
            public Map<String, String> getNetworkParams() {
                return null;
            }

            @Nullable
            @Override
            public CreativeFormat getCreativeFormat() {
                return creativeFormat;
            }

            @NonNull
            @Override
            public Map<String, String> getCustomParams() {
                return customParams;
            }
        };
    }

    private void contains(@NonNull String[] array, @NonNull String expected) {
        for (String value : array) {
            if (value.equals(expected)) {
                return;
            }
        }
        fail("The array does not contain the expected string: " + expected);
    }

}