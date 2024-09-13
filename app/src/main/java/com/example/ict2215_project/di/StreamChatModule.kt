package com.example.ict2215_project.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StreamChatModule {
    @Provides
    @Singleton
    fun provideOfflinePluginFactory(@ApplicationContext context: Context): StreamOfflinePluginFactory =
        StreamOfflinePluginFactory(appContext = context)

    @Provides
    @Singleton
    fun provideStatePluginFactory(@ApplicationContext context: Context): StreamStatePluginFactory =
        StreamStatePluginFactory(config = StatePluginConfig(), appContext = context)

    @Provides
    @Singleton
    fun provideChatClient(
        @ApplicationContext context: Context,
        offlinePluginFactory: StreamOfflinePluginFactory,
        statePluginFactory: StreamStatePluginFactory
    ): ChatClient {
        val apiKey = "fe4bycj2hda3"
        return ChatClient.Builder(apiKey, context)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL) // Remember to set to NOTHING in prod
            .build()
    }
}