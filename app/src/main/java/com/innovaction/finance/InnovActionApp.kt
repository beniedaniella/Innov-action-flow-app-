package com.innovaction.finance

import android.app.Application
import com.innovaction.finance.data.notification.AlerteWorker
import com.innovaction.finance.data.notification.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class InnovActionApp : Application() {

    @Inject lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        // Crée les canaux de notification (obligatoire Android 8+)
        notificationHelper.creerCanaux()
        // Planifie les vérifications périodiques d'alertes (toutes les 6h)
        AlerteWorker.planifier(this)
    }
}
