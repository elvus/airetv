package com.airetv.app.data.repository

import com.airetv.app.data.model.ChannelList

import airetvgo.Airetvgo
import android.util.Log
import com.google.gson.Gson

object ChannelRepository {

     fun getChannels(): ChannelList {
         val channels = Gson().fromJson(Airetvgo.getChannels(), ChannelList::class.java)
         Log.d("channels:", channels.toString())
         return channels
    }

    fun refreshChannels(): ChannelList {
        val channels = Gson().fromJson(Airetvgo.refreshChannels(), ChannelList::class.java)
        return channels
    }
}
