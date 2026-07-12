package com.innovaction.finance.util

import org.junit.Assert.*
import org.junit.Test

class FormatUtilsTest {

    @Test
    fun `montant CDF format correct`() {
        val result = FormatUtils.montantCdf(1_500_000.0)
        assertTrue("Doit contenir 1 500 000", result.contains("1"))
        assertTrue("Doit contenir FC", result.contains("FC"))
    }

    @Test
    fun `montant USD format correct`() {
        val result = FormatUtils.montantUsd(1_240.50)
        assertTrue("Doit contenir le symbole dollar", result.contains("$"))
    }

    @Test
    fun `montant avec signe positif`() {
        val result = FormatUtils.montantCdf(5000.0, avecSigne = true)
        assertTrue("Doit contenir +", result.contains("+"))
    }

    @Test
    fun `pourcentage correct`() {
        assertEquals("50.0%", FormatUtils.pourcentage(0.5f))
        assertEquals("100.0%", FormatUtils.pourcentage(1.0f))
        assertEquals("0.0%", FormatUtils.pourcentage(0.0f))
    }

    @Test
    fun `joursAvant retourne correct pour demain`() {
        val demain = System.currentTimeMillis() + (1000L * 60 * 60 * 24)
        val result = FormatUtils.joursAvant(demain)
        assertEquals("Demain", result)
    }

    @Test
    fun `joursAvant retourne échu pour date passée`() {
        val hier = System.currentTimeMillis() - (1000L * 60 * 60 * 24)
        val result = FormatUtils.joursAvant(hier)
        assertTrue("Doit indiquer échu", result.contains("Échu"))
    }
}
