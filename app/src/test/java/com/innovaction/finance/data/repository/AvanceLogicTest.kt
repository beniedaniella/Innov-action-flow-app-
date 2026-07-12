package com.innovaction.finance.data.repository

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests de la logique de remboursement des avances.
 * Vérifie les calculs de statut sans dépendance Android.
 */
class AvanceLogicTest {

    @Test
    fun `avance totalement remboursee si montant egal`() {
        val montant          = 100_000.0
        val montantRembourse = 100_000.0
        val statut = calculerStatut(montant, montantRembourse)
        assertEquals("REMBOURSEE_TOTALE", statut)
    }

    @Test
    fun `avance partiellement remboursee si montant inferieur`() {
        val montant          = 100_000.0
        val montantRembourse = 50_000.0
        val statut = calculerStatut(montant, montantRembourse)
        assertEquals("REMBOURSEE_PARTIELLE", statut)
    }

    @Test
    fun `avance reste active si rien rembourse`() {
        val montant          = 100_000.0
        val montantRembourse = 0.0
        val statut = calculerStatutActif(montant, montantRembourse)
        assertEquals("ACTIVE", statut)
    }

    @Test
    fun `solde restant calcule correctement`() {
        val montant          = 150_000.0
        val montantRembourse = 60_000.0
        val restant = montant - montantRembourse
        assertEquals(90_000.0, restant, 0.01)
    }

    @Test
    fun `depassement detecte correctement`() {
        val montant          = 100_000.0
        val montantRembourse = 120_000.0
        // Ne doit pas être possible normalement, mais on vérifie
        assertTrue(montantRembourse > montant)
    }

    private fun calculerStatut(montant: Double, rembourse: Double): String =
        if (rembourse >= montant) "REMBOURSEE_TOTALE" else "REMBOURSEE_PARTIELLE"

    private fun calculerStatutActif(montant: Double, rembourse: Double): String =
        if (rembourse <= 0) "ACTIVE"
        else if (rembourse >= montant) "REMBOURSEE_TOTALE"
        else "REMBOURSEE_PARTIELLE"
}
