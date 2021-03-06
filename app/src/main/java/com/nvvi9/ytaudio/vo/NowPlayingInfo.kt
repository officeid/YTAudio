package com.nvvi9.ytaudio.vo

import android.net.Uri


data class NowPlayingInfo(
    val id: String?,
    val title: String?,
    val author: String?,
    val thumbnailUri: Uri,
    val durationMillis: Long,
    var audioButtonRes: Int? = null
)