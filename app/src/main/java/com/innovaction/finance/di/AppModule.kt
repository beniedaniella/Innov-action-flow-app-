package com.innovaction.finance.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.innovaction.finance.util.AppConstants
import javax.inject.Singleton

// Extension DataStore — créé une seule fois au niveau Application
private val Context.dataStore: DataStore<Preferences>
    by preferencesDataStore(name = AppConstants.PREFS_NAME)

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Fournit le DataStore des préférences utilisateur (thème sombre, biométrie…).
     * La base de données Room sera fournie dans DatabaseModule (étape 3).
     */
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.dataStore
}
