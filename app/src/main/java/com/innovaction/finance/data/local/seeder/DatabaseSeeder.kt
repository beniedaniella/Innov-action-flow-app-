package com.innovaction.finance.data.local.seeder

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.innovaction.finance.data.local.dao.*
import com.innovaction.finance.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider

/**
 * Insère les données initiales lors de la première création de la base.
 *
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  TOUTES LES VALEURS ICI SONT MODIFIABLES DEPUIS L'APP.      ║
 * ║  Ce seeder est le SEUL endroit où des données initiales      ║
 * ║  sont définies. Elles ne sont pas codées dans la logique     ║
 * ║  métier et peuvent être modifiées depuis les Paramètres.     ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
class DatabaseSeeder(
    private val deviseDao     : Provider<DeviseDao>,
    private val tauxDao       : Provider<TauxChangeDao>,
    private val modeDao       : Provider<ModePaiementDao>,
    private val categorieDao  : Provider<CategorieDao>,
    private val federationDao : Provider<FederationDao>,
    private val projetDao     : Provider<ProjetDao>,
    private val compteDao     : Provider<CompteDao>,
    private val parametreDao  : Provider<ParametreDao>,
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            seedDevises()
            seedTauxChange()
            seedModesPaiement()
            seedCategories()
            seedFederations()
            seedProjets()
            seedComptes()
            seedParametres()
        }
    }

    // ── Devises ─────────────────────────────────────────────────────────────
    private suspend fun seedDevises() {
        deviseDao.get().insertAll(listOf(
            DeviseEntity(code = "CDF", nom = "Franc Congolais",    symbole = "FC", ordre = 0),
            DeviseEntity(code = "USD", nom = "Dollar américain",   symbole = "$", ordre = 1),
        ))
    }

    // ── Taux de change initial ───────────────────────────────────────────────
    private suspend fun seedTauxChange() {
        val cdf = deviseDao.get().getByCode("CDF") ?: return
        val usd = deviseDao.get().getByCode("USD") ?: return
        // 1 USD = 2800 CDF (modifiable depuis Paramètres > Taux de change)
        tauxDao.get().insert(TauxChangeEntity(
            deviseSourceId = usd.id, deviseCibleId = cdf.id, taux = 2800.0
        ))
    }

    // ── Modes de paiement ───────────────────────────────────────────────────
    private suspend fun seedModesPaiement() {
        modeDao.get().insertAll(listOf(
            ModePaiementEntity(nom = "Espèces",     icone = "payments",         ordre = 0),
            ModePaiementEntity(nom = "M-Pesa",      icone = "phone_android",    ordre = 1),
            ModePaiementEntity(nom = "Airtel Money", icone = "smartphone",      ordre = 2),
            ModePaiementEntity(nom = "Banque",      icone = "account_balance",  ordre = 3),
            ModePaiementEntity(nom = "Autres",      icone = "more_horiz",       ordre = 4),
        ))
    }

    // ── Catégories d'opérations ─────────────────────────────────────────────
    private suspend fun seedCategories() {
        categorieDao.get().insertAll(listOf(
            // Entrées
            CategorieEntity(nom = "Cotisation",            typeDefaut = "ENTREE", icone = "group",         ordre = 0),
            CategorieEntity(nom = "Subvention",            typeDefaut = "ENTREE", icone = "handshake",      ordre = 1),
            CategorieEntity(nom = "Don",                   typeDefaut = "ENTREE", icone = "volunteer_activism", ordre = 2),
            CategorieEntity(nom = "Remboursement reçu",   typeDefaut = "ENTREE", icone = "undo",           ordre = 3),
            CategorieEntity(nom = "Autre recette",         typeDefaut = "ENTREE", icone = "add_circle",     ordre = 4),
            // Sorties
            CategorieEntity(nom = "Fournitures bureau",    typeDefaut = "SORTIE", icone = "inventory",      ordre = 5),
            CategorieEntity(nom = "Transport",             typeDefaut = "SORTIE", icone = "directions_car", ordre = 6),
            CategorieEntity(nom = "Impression / Copies",  typeDefaut = "SORTIE", icone = "print",          ordre = 7),
            CategorieEntity(nom = "Frais notaire / Légal",typeDefaut = "SORTIE", icone = "gavel",          ordre = 8),
            CategorieEntity(nom = "Communication",         typeDefaut = "SORTIE", icone = "call",           ordre = 9),
            CategorieEntity(nom = "Restauration",          typeDefaut = "SORTIE", icone = "restaurant",     ordre = 10),
            CategorieEntity(nom = "Avance accordée",       typeDefaut = "SORTIE", icone = "account_balance_wallet", ordre = 11),
            CategorieEntity(nom = "Frais bancaires",       typeDefaut = "SORTIE", icone = "account_balance",ordre = 12),
            CategorieEntity(nom = "Autre dépense",         typeDefaut = "SORTIE", icone = "remove_circle",  ordre = 13),
            // Neutres
            CategorieEntity(nom = "Transfert entre comptes", typeDefaut = "TOUS", icone = "swap_horiz",     ordre = 14),
        ))
    }

    // ── Fédérations ─────────────────────────────────────────────────────────
    private suspend fun seedFederations() {
        federationDao.get().insertAll(listOf(
            FederationEntity(nom = "Fédération de Kinshasa",  ordre = 0),
            FederationEntity(nom = "Fédération du Katanga",   ordre = 1),
            FederationEntity(nom = "Fédération du Kivu",      ordre = 2),
            FederationEntity(nom = "Fédération de l'Équateur", ordre = 3),
            FederationEntity(nom = "Fédération Orientale",    ordre = 4),
        ))
    }

    // ── Projets ─────────────────────────────────────────────────────────────
    private suspend fun seedProjets() {
        projetDao.get().insertAll(listOf(
            ProjetEntity(nom = "Personnalité Juridique", budgetCdf = 1_500_000.0, budgetUsd = 600.0,  ordre = 0),
            ProjetEntity(nom = "Confestival",            budgetCdf = 8_000_000.0, budgetUsd = 3000.0, ordre = 1),
            ProjetEntity(nom = "Fonctionnement",         budgetCdf = 2_500_000.0, budgetUsd = 1000.0, ordre = 2),
            ProjetEntity(nom = "Autres Projets",         budgetCdf = 0.0,         budgetUsd = 0.0,    ordre = 3),
        ))
    }

    // ── Comptes de caisse ───────────────────────────────────────────────────
    private suspend fun seedComptes() {
        val cdf = deviseDao.get().getByCode("CDF") ?: return
        val usd = deviseDao.get().getByCode("USD") ?: return
        compteDao.get().insertAll(listOf(
            CompteEntity(nom = "Caisse CDF",    deviseId = cdf.id, icone = "payments",         couleur = "#1F3864", ordre = 0),
            CompteEntity(nom = "Caisse USD",    deviseId = usd.id, icone = "payments",         couleur = "#D4A017", ordre = 1),
            CompteEntity(nom = "M-Pesa",        deviseId = cdf.id, icone = "phone_android",    couleur = "#E91E63", ordre = 2),
            CompteEntity(nom = "Airtel Money",  deviseId = cdf.id, icone = "smartphone",       couleur = "#FF5722", ordre = 3),
            CompteEntity(nom = "Banque CDF",    deviseId = cdf.id, icone = "account_balance",  couleur = "#2196F3", ordre = 4),
            CompteEntity(nom = "Banque USD",    deviseId = usd.id, icone = "account_balance",  couleur = "#4CAF50", ordre = 5),
        ))
    }

    // ── Paramètres de l'application ─────────────────────────────────────────
    private suspend fun seedParametres() {
        parametreDao.get().upsertAll(listOf(
            ParametreEntity(cle = "nom_association",     valeur = "INNOV'ACTION",
                description = "Nom de l'association",    categorie = "GENERAL"),
            ParametreEntity(cle = "exercice_en_cours",   valeur = "2026",
                description = "Exercice fiscal en cours", categorie = "GENERAL"),
            ParametreEntity(cle = "seuil_alerte_cdf",   valeur = "500000",
                description = "Solde minimum CDF avant alerte (FC)", categorie = "ALERTES"),
            ParametreEntity(cle = "seuil_alerte_usd",   valeur = "150",
                description = "Solde minimum USD avant alerte",      categorie = "ALERTES"),
            ParametreEntity(cle = "rappel_avances_jours", valeur = "7",
                description = "Jours avant échéance pour rappel avance", categorie = "ALERTES"),
            ParametreEntity(cle = "export_nom_fichier",  valeur = "INNOVACTION_finances",
                description = "Préfixe des fichiers exportés",       categorie = "EXPORT"),
            ParametreEntity(cle = "devise_principale_code", valeur = "CDF",
                description = "Devise d'affichage principale",       categorie = "GENERAL"),
        ))
    }
}
