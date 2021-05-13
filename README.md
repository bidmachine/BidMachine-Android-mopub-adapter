# BidMachine Android MoPubAdapter

[<img src="https://img.shields.io/badge/SDK%20Version-1.7.2-brightgreen">](https://github.com/bidmachine/BidMachine-Android-SDK)
[<img src="https://img.shields.io/badge/Adapter%20Version-1.7.2.21-green">](https://artifactory.bidmachine.io/bidmachine/io/bidmachine/ads.adapters.mopub/1.7.2.21/)
[<img src="https://img.shields.io/badge/MoPub%20Version-5.17.0-blue">](https://developers.mopub.com/publishers/android/integrate/)

* [Useful links](#useful-links)
* [Integration](#integration)
* [Types of integration](#types-of-integration)
* [Working with price rounding](#working-with-price-rounding)
* [What's new in last version](#whats-new-in-last-version)

## Useful links
* [MoPub documentation](https://developers.mopub.com/publishers/android/integrate/)
* [BidMachine integration documentation](https://wiki.appodeal.com/display/BID/BidMachine+Android+SDK+Documentation)

## Integration
```gradle
repositories {
    // Add BidMachine maven repository
    maven {
        name 'BidMachine Ads maven repository'
        url 'https://artifactory.bidmachine.io/bidmachine'
    }
}

dependencies {
    // Add BidMachine SDK dependency
    implementation 'io.bidmachine:ads:1.7.2'
    // Add BidMachine SDK MoPub Adapter dependency
    implementation 'io.bidmachine:ads.adapters.mopub:1.7.2.21'
    // Add MoPub SDK dependency
    implementation('com.mopub:mopub-sdk:5.17.0@aar') {
        transitive = true
    }
}
```

## Types of integration
* [Classic MoPub implementation](example)
* [HeaderBidding MoPub implementation](example_fetch)

## Working with price rounding
BidMachine supports server-side price rounding.<br>
To setup it correctly - please contact your manager to set up your own rounding rules. Manager will provide you with the list of prices and you can use them to create orders/line items in partner's dashboard.<br>
If you prefer to automate this process - you can use PubMonkey plugin.<br>
Documentation about how to use plugin could be found [here](https://doc.bidmachine.io/eng/ssp-publisher-integration-documentation/bidmachine-custom-adapters/how-to-use-plugin-for-integration-via-mopub-google/creating-line-items-in-mopub-dashboard)

## What's new in last version
Please view the [changelog](CHANGELOG.md) for details.