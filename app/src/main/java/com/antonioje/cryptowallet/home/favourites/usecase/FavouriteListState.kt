package com.antonioje.cryptowallet.home.favourites.usecase

sealed class FavouriteListState{
    data object NoData:FavouriteListState()
    data object Success:FavouriteListState()
}
