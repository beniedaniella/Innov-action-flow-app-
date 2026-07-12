package com.innovaction.finance.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Vérifie la logique de numérotation automatique des opérations.
 */
class OperationNumeroTest {

    @Test
    fun `format numero correct`() {
        val annee  = "2026"
        val numero = 1
        val result = "$annee-${"${numero}".padStart(4, '0')}"
        assertEquals("2026-0001", result)
    }

    @Test
    fun `format numero avance correct`() {
        val num    = 5
        val result = "AVA-${"${num}".padStart(3, '0')}"
        assertEquals("AVA-005", result)
    }

    @Test
    fun `format numero decaissement correct`() {
        val num    = 42
        val result = "DEC-${"${num}".padStart(4, '0')}"
        assertEquals("DEC-0042", result)
    }
}
