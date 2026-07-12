package com.innovaction.finance.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.innovaction.finance.data.local.dao.*
import com.innovaction.finance.data.local.entity.*
import com.innovaction.finance.util.AppConstants

/**
 * Base de données Room — version 1.
 *
 * Pour toute modification de schéma, créer une Migration dans di/DatabaseModule.kt
 * et incrémenter DB_VERSION dans AppConstants.
 * Ne jamais utiliser fallbackToDestructiveMigration().
 */
@Database(
    entities = [
        DeviseEntity::class,
        TauxChangeEntity::class,
        ModePaiementEntity::class,
        CategorieEntity::class,
        FederationEntity::class,
        ProjetEntity::class,
        CompteEntity::class,
        OperationEntity::class,
        AvanceEntity::class,
        ParametreEntity::class,
    ],
    version      = AppConstants.DB_VERSION,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviseDao()      : DeviseDao
    abstract fun tauxChangeDao()  : TauxChangeDao
    abstract fun modePaiementDao(): ModePaiementDao
    abstract fun categorieDao()   : CategorieDao
    abstract fun federationDao()  : FederationDao
    abstract fun projetDao()      : ProjetDao
    abstract fun compteDao()      : CompteDao
    abstract fun operationDao()   : OperationDao
    abstract fun avanceDao()      : AvanceDao
    abstract fun parametreDao()   : ParametreDao
}
