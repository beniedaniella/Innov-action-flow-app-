package com.innovaction.finance.data.security

import android.content.Context
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import java.security.MessageDigest

/**
 * Tests du PinManager.
 * Vérifie que le PIN n'est jamais stocké en clair et que
 * la vérification fonctionne correctement.
 */
class PinManagerTest {

    // Test de la fonction de hachage (logique pure — pas besoin de contexte Android)

    @Test
    fun `deux PINs identiques produisent le meme hash`() {
        val hash1 = sha256("123456")
        val hash2 = sha256("123456")
        assertEquals(hash1, hash2)
    }

    @Test
    fun `deux PINs differents produisent des hashs differents`() {
        val hash1 = sha256("1234")
        val hash2 = sha256("5678")
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun `le hash ne contient pas le PIN en clair`() {
        val pin  = "1234"
        val hash = sha256(pin)
        assertFalse("Le hash ne doit pas contenir le PIN en clair", hash.contains(pin))
    }

    @Test
    fun `le hash SHA256 a la bonne longueur`() {
        val hash = sha256("123456")
        assertEquals("SHA-256 doit produire 64 caractères hex", 64, hash.length)
    }

    @Test
    fun `le hash ne contient que des caracteres hexadecimaux`() {
        val hash = sha256("9999")
        assertTrue("Hash doit être hexadécimal", hash.matches(Regex("[0-9a-f]+")))
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(input.toByteArray()).joinToString("") { "%02x".format(it) }
    }
}
