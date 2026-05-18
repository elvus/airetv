package com.airetv.app.data.model

import com.google.gson.annotations.SerializedName

data class ChannelList(
    @SerializedName("channels")
    val channels: List<Channel>
)
