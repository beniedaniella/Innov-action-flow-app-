package com.innovaction.finance.data.security

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

private val Context.securityStore by preferencesDataStore("security_prefs")

/**
 * Gère le PIN de l'application.
 * Le PIN est stocké hashé (SHA-256) — jamais en clair.
 * Aucune valeur de PIN par défaut : l'utilisateur choisit le sien.
 */
@Singleton
class PinManager @Inject constructor(
    private val context: Context,
) {
    companion object {
        val KEY_PIN_HASH      = stringPreferencesKey("pin_hash")
        val KEY_PIN_ENABLED   = booleanPreferencesKey("pin_enabled")
        val KEY_BIOMETRIC_OK  = booleanPreferencesKey("biometric_enabled")
    }

    val isPinEnabled: Flow<Boolean> = context.securityStore.data
        .map { it[KEY_PIN_ENABLED] ?: false }

    val isBiometricEnabled: Flow<Boolean> = context.securityStore.data
        .map { it[KEY_BIOMETRIC_OK] ?: false }

    /** Hash SHA-256 du PIN. */
    private fun hash(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(pin.toByteArray()).joinToString("") { "%02x".format(it) }
    }

    /** Active le PIN avec le code fourni par l'utilisateur. */
    suspend fun activerPin(pin: String) {
        context.securityStore.edit {
            it[KEY_PIN_HASH]    = hash(pin)
            it[KEY_PIN_ENABLED] = true
        }
    }

    /** Désactive le PIN (après vérification). */
    suspend fun desactiverPin() {
        context.securityStore.edit {
            it[KEY_PIN_HASH]    = ""
            it[KEY_PIN_ENABLED] = false
            it[KEY_BIOMETRIC_OK] = false
        }
    }

    /** Active/désactive la biométrie (en plus du PIN). */
    suspend fun setBiometrique(enabled: Boolean) {
        context.securityStore.edit {
            it[KEY_BIOMETRIC_OK] = enabled
        }
    }

    /** Vérifie si le PIN saisi correspond au hash stocké. */
    suspend fun verifierPin(pin: String): Boolean {
        val stored = context.securityStore.data
            .map { it[KEY_PIN_HASH] ?: "" }
            .let { flow ->
                var result = ""
                flow.collect { result = it }
                result
            }
        return stored.isNotEmpty() && stored == hash(pin)
    }
}
