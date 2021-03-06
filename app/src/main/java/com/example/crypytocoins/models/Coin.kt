package com.example.crypytocoins.models

data class Coin(
    val viewType: ViewType,
    val id: Int,
    val rank: Int,
    val uuid: String,
    val slug: String,
    val symbol: String,
    val name: String,
    val description: String,
    var iconType: String,
    var iconUrl: String
)