package com.innovaction.finance.di

import android.content.Context
import androidx.room.Room
import com.innovaction.finance.data.local.AppDatabase
import com.innovaction.finance.data.local.seeder.DatabaseSeeder
import com.innovaction.finance.data.repository.*
import com.innovaction.finance.util.AppConstants
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        // Les Providers évitent les dépendances circulaires dans le Callback
        deviseProvider      : Provider<com.innovaction.finance.data.local.dao.DeviseDao>,
        tauxProvider        : Provider<com.innovaction.finance.data.local.dao.TauxChangeDao>,
        modeProvider        : Provider<com.innovaction.finance.data.local.dao.ModePaiementDao>,
        categorieProvider   : Provider<com.innovaction.finance.data.local.dao.CategorieDao>,
        federationProvider  : Provider<com.innovaction.finance.data.local.dao.FederationDao>,
        projetProvider      : Provider<com.innovaction.finance.data.local.dao.ProjetDao>,
        compteProvider      : Provider<com.innovaction.finance.data.local.dao.CompteDao>,
        parametreProvider   : Provider<com.innovaction.finance.data.local.dao.ParametreDao>,
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        AppConstants.DB_NAME,
    )
    .addCallback(DatabaseSeeder(
        deviseProvider, tauxProvider, modeProvider, categorieProvider,
        federationProvider, projetProvider, compteProvider, parametreProvider,
    ))
    // ⚠️ Ne JAMAIS utiliser .fallbackToDestructiveMigration()
    // Toute migration doit être explicite :
    // .addMigrations(MIGRATION_1_2, MIGRATION_2_3, …)
    .build()

    // ── DAOs ────────────────────────────────────────────────────────────────
    @Provides fun provideDeviseDao(db: AppDatabase)       = db.deviseDao()
    @Provides fun provideTauxDao(db: AppDatabase)         = db.tauxChangeDao()
    @Provides fun provideModeDao(db: AppDatabase)         = db.modePaiementDao()
    @Provides fun provideCategorieDao(db: AppDatabase)    = db.categorieDao()
    @Provides fun provideFederationDao(db: AppDatabase)   = db.federationDao()
    @Provides fun provideProjetDao(db: AppDatabase)       = db.projetDao()
    @Provides fun provideCompteDao(db: AppDatabase)       = db.compteDao()
    @Provides fun provideOperationDao(db: AppDatabase)    = db.operationDao()
    @Provides fun provideAvanceDao(db: AppDatabase)       = db.avanceDao()
    @Provides fun provideParametreDao(db: AppDatabase)    = db.parametreDao()
}

/** Binding des interfaces Repository vers leurs implémentations. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindOperationRepo(impl: OperationRepositoryImpl): OperationRepository
    @Binds @Singleton abstract fun bindAvanceRepo(impl: AvanceRepositoryImpl): AvanceRepository
    @Binds @Singleton abstract fun bindConfigRepo(impl: ConfigRepositoryImpl): ConfigRepository
}
