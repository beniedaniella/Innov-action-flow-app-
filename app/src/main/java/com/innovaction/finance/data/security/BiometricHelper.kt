package com.innovaction.finance.data.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import javax.inject.Singleton

/** Résultat de l'authentification biométrique. */
sealed class BiometricResult {
    object Success       : BiometricResult()
    object Cancelled     : BiometricResult()
    data class Error(val message: String) : BiometricResult()
    object NotAvailable  : BiometricResult()
}

/**
 * Gère l'authentification biométrique (empreinte / face ID).
 * Utilise androidx.biometric pour la compatibilité Android 6+.
 */
@Singleton
class BiometricHelper @Inject constructor() {

    /** Vérifie si la biométrie est disponible sur l'appareil. */
    fun isDisponible(context: Context): Boolean {
        val manager = BiometricManager.from(context)
        return manager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL) ==
               BiometricManager.BIOMETRIC_SUCCESS
    }

    /**
     * Affiche le prompt biométrique.
     * [activity] doit être un FragmentActivity (MainActivity).
     */
    fun authentifier(
        activity   : FragmentActivity,
        titre      : String = "Déverrouillez INNOV'ACTION Finance",
        sous_titre : String = "Utilisez votre empreinte ou votre visage",
        onResultat : (BiometricResult) -> Unit,
    ) {
        if (!isDisponible(activity)) {
            onResultat(BiometricResult.NotAvailable)
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onResultat(BiometricResult.Success)
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                    errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    onResultat(BiometricResult.Cancelled)
                } else {
                    onResultat(BiometricResult.Error(errString.toString()))
                }
            }
            override fun onAuthenticationFailed() {
                // Tentative échouée — BiometricPrompt gère l'UI automatiquement
            }
        }

        val prompt = BiometricPrompt(activity, executor, callback)

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(titre)
            .setSubtitle(sous_titre)
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        prompt.authenticate(info)
    }
}
