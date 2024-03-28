package com.antonioje.cryptowallet.data.model

data class CryptoData(
    val id: String,
    val symbol: String,
    val name: String,
    val web_slug: String,
    val asset_platform_id: String?,
    val platforms: Map<String, String>,
    val detail_platforms: Map<String, DetailPlatform>,
    val block_time_in_minutes: Int,
    val hashing_algorithm: String,
    val categories: List<String>,
    val preview_listing: Boolean,
    val public_notice: Any?,
    val additional_notices: List<Any>,
    val description: Description,
    val links: Links,
    val image: Image,
    val country_origin: String,
    val genesis_date: String,

    val sentiment_votes_up_percentage: Double,
    val sentiment_votes_down_percentage: Double,
    val watchlist_portfolio_users: Int,
    val market_cap_rank: Int,
    val status_updates: List<Any>,
    val last_updated: String,
    val market_data: MarketData

){
    companion object{
        const val CRYPTO_KEY = "CRYPTODATA"
    }
}


data class MarketData(
    val current_price: Fiat,
    val ath: Fiat
)


data class DetailPlatform(
    val decimal_place: String?,
    val contract_address: String
)

data class Description(
    val en: String
)

data class Links(
    val homepage: List<String>,
    val whitepaper: String,
    val blockchain_site: List<String>,
    val official_forum_url: List<String>,
    val chat_url: List<String>,
    val announcement_url: List<String>,
    val twitter_screen_name: String,
    val facebook_username: String,
    val bitcointalk_thread_identifier: String?,
    val telegram_channel_identifier: String,
    val subreddit_url: String,
    val repos_url: ReposUrl
)

data class ReposUrl(
    val github: List<String>,
    val bitbucket: List<String>
)

data class Image(
    val thumb: String,
    val small: String,
    val large: String
)




