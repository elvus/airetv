package com.airetv.app.data.model

import com.google.gson.annotations.SerializedName

data class Channel(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    @SerializedName("stream_url") val streamUrl: String = "",
    @SerializedName("logo_url") val logoUrl: String = "",
    @SerializedName("background_url") val backgroundUrl: String = "",
    val category: String = ""
)