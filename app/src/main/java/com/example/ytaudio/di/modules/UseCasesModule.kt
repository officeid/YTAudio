package com.example.ytaudio.di.modules

import com.example.ytaudio.domain.PlaylistUseCases
import com.example.ytaudio.domain.PlaylistUseCasesImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton


@Module
abstract class UseCasesModule {

    @Binds
    @Singleton
    abstract fun bindPlaylistUseCases(playlistUseCasesImpl: PlaylistUseCasesImpl): PlaylistUseCases
}