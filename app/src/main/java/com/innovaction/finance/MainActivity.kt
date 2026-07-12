package com.innovaction.finance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovaction.finance.data.security.BiometricHelper
import com.innovaction.finance.presentation.navigation.AppNavigation
import com.innovaction.finance.presentation.securite.EtatVerrou
import com.innovaction.finance.presentation.securite.PinScreen
import com.innovaction.finance.presentation.securite.SecuriteViewModel
import com.innovaction.finance.presentation.theme.InnovActionTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val securiteViewModel: SecuriteViewModel by viewModels()

    @Inject lateinit var biometricHelper: BiometricHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            InnovActionTheme {
                Surface(Modifier.fillMaxSize()) {
                    val secState by securiteViewModel.uiState.collectAsStateWithLifecycle()

                    AnimatedContent(
                        targetState = secState.etat,
                        transitionSpec = {
                            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                        },
                        label = "verrou_transition",
                    ) { etat ->
                        when (etat) {
                            EtatVerrou.VERROUILLE -> {
                                PinScreen(
                                    titre          = "Entrez votre PIN",
                                    pinSaisi       = secState.pinSaisi,
                                    erreur         = secState.erreur,
                                    showBiometric  = secState.biometricEnabled && secState.biometricDispo,
                                    onChiffreSaisi = {
                                        securiteViewModel.onPinSaisiChange(secState.pinSaisi + it)
                                    },
                                    onEffacer = {
                                        if (secState.pinSaisi.isNotEmpty())
                                            securiteViewModel.onPinSaisiChange(
                                                secState.pinSaisi.dropLast(1))
                                    },
                                    onValider    = { securiteViewModel.verifierPin(secState.pinSaisi) },
                                    onBiometric  = {
                                        biometricHelper.authentifier(
                                            activity   = this@MainActivity,
                                            onResultat = { result ->
                                                if (result is com.innovaction.finance.data.security.BiometricResult.Success)
                                                    securiteViewModel.verifierPin("__biometric__skip__")
                                            }
                                        )
                                    },
                                )
                            }
                            EtatVerrou.CONFIGURATION_PIN -> {
                                PinScreen(
                                    titre = if (!secState.etapeConfirmation)
                                        "Choisissez un PIN" else "Confirmez votre PIN",
                                    pinSaisi       = secState.pinSaisi,
                                    erreur         = secState.erreur,
                                    showBiometric  = false,
                                    onChiffreSaisi = {
                                        securiteViewModel.onPinSaisiChange(secState.pinSaisi + it)
                                    },
                                    onEffacer = {
                                        if (secState.pinSaisi.isNotEmpty())
                                            securiteViewModel.onPinSaisiChange(
                                                secState.pinSaisi.dropLast(1))
                                    },
                                    onValider = securiteViewModel::validerEtapeSaisie,
                                )
                            }
                            EtatVerrou.DEVERROUILLE -> AppNavigation()
                        }
                    }
                }
            }
        }
    }
}
