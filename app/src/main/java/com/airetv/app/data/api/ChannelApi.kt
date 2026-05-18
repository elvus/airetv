package com.airetv.app.data.api

import com.google.gson.annotations.SerializedName
import com.airetv.app.data.model.ChannelList
import retrofit2.http.GET

interface ChannelApi {

    @GET("api/channels")
    suspend fun getChannels(): ChannelList

    @GET("api/channel/refresh")
    suspend fun refreshChannels(): ChannelList
}
