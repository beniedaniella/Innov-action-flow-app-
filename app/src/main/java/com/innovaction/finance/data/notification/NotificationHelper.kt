package com.innovaction.finance.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.innovaction.finance.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gère la création des canaux et l'envoi des notifications.
 * Tous les libellés et seuils viennent de la base de données
 * via le Worker — aucune valeur codée en dur ici.
 */
@Singleton
class NotificationHelper @Inject constructor(
    private val context: Context,
) {
    companion object {
        const val CHANNEL_AVANCES  = "channel_avances"
        const val CHANNEL_SOLDE    = "channel_solde"
        const val CHANNEL_RAPPEL   = "channel_rappel"

        const val NOTIF_AVANCES_ID = 1001
        const val NOTIF_SOLDE_ID   = 1002
        const val NOTIF_RAPPEL_ID  = 1003
    }

    /** À appeler au démarrage de l'app (dans InnovActionApp). */
    fun creerCanaux() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

        listOf(
            NotificationChannel(
                CHANNEL_AVANCES, "Avances & Décharges",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Alertes pour les avances non remboursées" },

            NotificationChannel(
                CHANNEL_SOLDE, "Solde de caisse",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Alertes lorsque le solde devient faible" },

            NotificationChannel(
                CHANNEL_RAPPEL, "Rappels",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Rappels de sauvegarde et autres" },
        ).forEach { manager.createNotificationChannel(it) }
    }

    fun notifierAvancesEnRetard(nb: Int, montantTotal: Double, devise: String) {
        envoyer(
            id       = NOTIF_AVANCES_ID,
            channel  = CHANNEL_AVANCES,
            titre    = "⚠️ $nb avance${if (nb > 1) "s" else ""} en retard",
            message  = "Total dû : ${"%.0f".format(montantTotal)} $devise — Appuyez pour gérer",
            priority = NotificationCompat.PRIORITY_HIGH,
        )
    }

    fun notifierSoldeBasCdf(solde: Double, seuil: Double) {
        envoyer(
            id      = NOTIF_SOLDE_ID,
            channel = CHANNEL_SOLDE,
            titre   = "🔔 Solde CDF faible",
            message = "Solde : ${"%.0f".format(solde)} FC (seuil : ${"%.0f".format(seuil)} FC)",
        )
    }

    fun notifierSoldeBasUsd(solde: Double, seuil: Double) {
        envoyer(
            id      = NOTIF_SOLDE_ID + 1,
            channel = CHANNEL_SOLDE,
            titre   = "🔔 Solde USD faible",
            message = "Solde : $${"%.2f".format(solde)} (seuil : $${"%.2f".format(seuil)})",
        )
    }

    fun notifierRappelSauvegarde() {
        envoyer(
            id      = NOTIF_RAPPEL_ID,
            channel = CHANNEL_RAPPEL,
            titre   = "💾 Pensez à exporter vos données",
            message = "Aucun export depuis plus de 7 jours. Exportez un rapport pour sauvegarder.",
        )
    }

    private fun envoyer(
        id       : Int,
        channel  : String,
        titre    : String,
        message  : String,
        priority : Int = NotificationCompat.PRIORITY_DEFAULT,
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(context, channel)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titre)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager
        manager.notify(id, notif)
    }
}
