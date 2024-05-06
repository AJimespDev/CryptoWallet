package com.antonioje.cryptowallet.data.model

data class CryptoData(
    val id: String = "",
    val symbol: String = "",
    val name: String = "",
    val web_slug: String = "",
    val asset_platform_id: String? = null,
    val platforms: Map<String, String> = mapOf(),
    val detail_platforms: Map<String, DetailPlatform> = mapOf(),
    val block_time_in_minutes: Int = 0,
    val hashing_algorithm: String = "",
    val categories: List<String> = listOf(),
    val preview_listing: Boolean = false,
    val public_notice: Any? = null,
    val additional_notices: List<Any> = listOf(),
    val description: Description = Description(),
    val links: Links = Links(),
    val image: Image = Image(),
    val country_origin: String = "",
    val genesis_date: String = "",
    val sentiment_votes_up_percentage: Double = 0.0,
    val sentiment_votes_down_percentage: Double = 0.0,
    val watchlist_portfolio_users: Int = 0,
    val market_cap_rank: Int = 0,
    val status_updates: List<Any> = listOf(),
    val last_updated: String = "",
    val market_data: MarketData = MarketData()
) {
    companion object{
        const val CRYPTO_KEY = "CRYPTODATA"
    }
}

data class MarketData(
    val current_price: Fiat = Fiat(),
    val ath: Fiat = Fiat(),
    val market_cap: Fiat = Fiat(),
    var price_change_percentage_24h: Double = 0.0,
    val market_cap_change_percentage_24h: Double = 0.0
)

data class DetailPlatform(
    val decimal_place: String? = null,
    val contract_address: String = ""
)

data class Description(
    val en: String = ""
)

data class Links(
    val homepage: List<String> = listOf(),
    val whitepaper: String = "",
    val blockchain_site: List<String> = listOf(),
    val official_forum_url: List<String> = listOf(),
    val chat_url: List<String> = listOf(),
    val announcement_url: List<String> = listOf(),
    val twitter_screen_name: String = "",
    val facebook_username: String = "",
    val bitcointalk_thread_identifier: String? = null,
    val telegram_channel_identifier: String = "",
    val subreddit_url: String = "",
    val repos_url: ReposUrl = ReposUrl()
)

data class ReposUrl(
    val github: List<String> = listOf(),
    val bitbucket: List<String> = listOf()
)

data class Image(
    val thumb: String = "",
    val small: String = "",
    val large: String = ""
)



