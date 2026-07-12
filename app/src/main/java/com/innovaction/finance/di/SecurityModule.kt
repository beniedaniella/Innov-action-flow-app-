package com.innovaction.finance.di

import com.innovaction.finance.data.security.BiometricHelper
import com.innovaction.finance.data.security.PinManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides @Singleton
    fun providePinManager(@ApplicationContext ctx: Context): PinManager = PinManager(ctx)

    @Provides @Singleton
    fun provideBiometricHelper(): BiometricHelper = BiometricHelper()
}
