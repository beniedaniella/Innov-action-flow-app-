package com.innovaction.finance.presentation.securite

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovaction.finance.data.security.BiometricHelper
import com.innovaction.finance.data.security.PinManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuriteViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pinManager    : PinManager,
    private val biometricHelper: BiometricHelper,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SecuriteUiState())
    val uiState: StateFlow<SecuriteUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                pinManager.isPinEnabled,
                pinManager.isBiometricEnabled,
            ) { pinOn, bioOn ->
                _uiState.update { it.copy(
                    isLoading        = false,
                    pinEnabled       = pinOn,
                    biometricEnabled = bioOn,
                    biometricDispo   = biometricHelper.isDisponible(context),
                    etat             = if (pinOn) EtatVerrou.VERROUILLE
                                       else EtatVerrou.DEVERROUILLE,
                )}
            }.collect()
        }
    }

    // ── Vérification PIN au démarrage ──────────────────────────────────────
    fun verifierPin(pin: String) {
        viewModelScope.launch {
            if (pinManager.verifierPin(pin)) {
                _uiState.update { it.copy(
                    etat    = EtatVerrou.DEVERROUILLE,
                    pinSaisi = "",
                    erreur  = null,
                )}
            } else {
                _uiState.update { it.copy(
                    pinSaisi = "",
                    erreur   = "PIN incorrect. Réessayez.",
                )}
            }
        }
    }

    // ── Configuration PIN ──────────────────────────────────────────────────
    fun commencerConfigPin() {
        _uiState.update { it.copy(
            etat              = EtatVerrou.CONFIGURATION_PIN,
            pinSaisi          = "",
            pinConfirmation   = "",
            etapeConfirmation = false,
            erreur            = null,
        )}
    }

    fun onPinSaisiChange(v: String) {
        if (v.length > 6) return  // PIN max 6 chiffres
        _uiState.update { it.copy(pinSaisi = v, erreur = null) }
    }

    fun validerEtapeSaisie() {
        val s = _uiState.value
        if (s.pinSaisi.length < 4) {
            _uiState.update { it.copy(erreur = "Le PIN doit contenir au moins 4 chiffres") }
            return
        }
        if (!s.etapeConfirmation) {
            // Première saisie → passer à la confirmation
            _uiState.update { it.copy(
                etapeConfirmation = true,
                pinConfirmation   = s.pinSaisi,
                pinSaisi          = "",
            )}
        } else {
            // Deuxième saisie → vérifier correspondance
            if (s.pinSaisi == s.pinConfirmation) {
                sauvegarderPin(s.pinSaisi)
            } else {
                _uiState.update { it.copy(
                    erreur            = "Les deux PIN ne correspondent pas. Recommencez.",
                    pinSaisi          = "",
                    pinConfirmation   = "",
                    etapeConfirmation = false,
                )}
            }
        }
    }

    private fun sauvegarderPin(pin: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            pinManager.activerPin(pin)
            _uiState.update { it.copy(
                isSaving          = false,
                etat              = EtatVerrou.DEVERROUILLE,
                pinEnabled        = true,
                pinSaisi          = "",
                pinConfirmation   = "",
                etapeConfirmation = false,
                succesMessage     = "PIN activé avec succès",
            )}
        }
    }

    fun desactiverPin() {
        viewModelScope.launch {
            pinManager.desactiverPin()
            _uiState.update { it.copy(
                pinEnabled       = false,
                biometricEnabled = false,
                etat             = EtatVerrou.DEVERROUILLE,
                succesMessage    = "PIN désactivé",
            )}
        }
    }

    fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch {
            pinManager.setBiometrique(enabled)
            _uiState.update { it.copy(
                biometricEnabled = enabled,
                succesMessage    = if (enabled) "Biométrie activée" else "Biométrie désactivée",
            )}
        }
    }

    fun effacerMessages() { _uiState.update { it.copy(erreur = null, succesMessage = null) } }
}
