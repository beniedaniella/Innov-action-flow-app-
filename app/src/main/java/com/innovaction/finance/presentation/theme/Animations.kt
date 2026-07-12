package com.innovaction.finance.presentation.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*

/** Durées standard d'animation. */
object AnimDuration {
    const val FAST   = 150
    const val NORMAL = 280
    const val SLOW   = 450
}

/** Transitions de navigation entre écrans. */
object NavTransitions {
    /** Entrée depuis la droite (navigation forward). */
    val slideInFromRight: EnterTransition
        get() = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec  = tween(AnimDuration.NORMAL, easing = FastOutSlowInEasing),
        ) + fadeIn(tween(AnimDuration.NORMAL))

    /** Sortie vers la gauche (navigation forward). */
    val slideOutToLeft: ExitTransition
        get() = slideOutHorizontally(
            targetOffsetX = { -it / 3 },
            animationSpec = tween(AnimDuration.NORMAL, easing = FastOutSlowInEasing),
        ) + fadeOut(tween(AnimDuration.FAST))

    /** Entrée depuis la gauche (retour arrière). */
    val slideInFromLeft: EnterTransition
        get() = slideInHorizontally(
            initialOffsetX = { -it / 3 },
            animationSpec  = tween(AnimDuration.NORMAL),
        ) + fadeIn(tween(AnimDuration.NORMAL))

    /** Sortie vers la droite (retour arrière). */
    val slideOutToRight: ExitTransition
        get() = slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(AnimDuration.NORMAL, easing = FastOutSlowInEasing),
        ) + fadeOut(tween(AnimDuration.FAST))

    /** Transition fade simple (Bottom Nav). */
    val fadeIn: EnterTransition
        get() = fadeIn(tween(AnimDuration.NORMAL))

    val fadeOut: ExitTransition
        get() = fadeOut(tween(AnimDuration.FAST))
}

/** Spéc d'animation pour les ProgressIndicators (smooth). */
val smoothProgressSpec: AnimationSpec<Float> = tween(AnimDuration.SLOW, easing = FastOutSlowInEasing)
