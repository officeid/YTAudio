package com.example.ytaudio.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "audio_playlist_table",
    indices = [Index(value = ["youtube_id"], unique = true)]
)
data class AudioInfo(

    @PrimaryKey(autoGenerate = true) val audioId: Long = 0L,
    @ColumnInfo(name = "youtube_id") val youtubeId: String = "",
    @ColumnInfo(name = "audio_url") var audioUrl: String = "",
    @ColumnInfo(name = "photo_url") var photoUrl: String = "",
    @ColumnInfo(name = "audio_title") var audioTitle: String = "",
    @ColumnInfo(name = "video_author") var author: String = "",
    @ColumnInfo(name = "author_id") var authorId: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "keywords") var keywords: String = "",
    @ColumnInfo(name = "view_count") var viewCount: Int = 0,
    @ColumnInfo(name = "average_rating") var averageRating: Double = 0.0,
    @ColumnInfo(name = "audio_format") var audioFormat: String = "",
    @ColumnInfo(name = "codec") var codec: String = "",
    @ColumnInfo(name = "bitrate") var bitrate: Int = 0,
    @ColumnInfo(name = "average_bitrate") var averageBitrate: Int = 0,
    @ColumnInfo(name = "audio_duration_seconds") var audioDurationSeconds: Long = 0L,
    @ColumnInfo(name = "last_update_time_seconds") var lastUpdateTimeSeconds: Long = 0L,
    @ColumnInfo(name = "url_active_time_seconds") var urlActiveTimeSeconds: Long = 0L,
    @ColumnInfo(name = "playback_state") var playbackState: Int = 0
)