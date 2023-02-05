package ru.netology.nmedia.ui

import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object FirebaseModul {
    @Provides
    fun provideFirebaseMessaging():FirebaseMessaging = FirebaseMessaging.getInstance()
}