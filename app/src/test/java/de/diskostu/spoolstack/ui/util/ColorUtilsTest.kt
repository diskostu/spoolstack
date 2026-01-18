package de.diskostu.spoolstack.ui.util

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorUtilsTest {

    @Test
    fun `getColorNameForHex returns correct name for English`() {
        assertEquals("red", ColorUtils.getColorNameForHex("#FF0000", "en"))
        assertEquals("blue", ColorUtils.getColorNameForHex("#0000FF", "en"))
    }

    @Test
    fun `getColorNameForHex returns correct name for German`() {
        assertEquals("rot", ColorUtils.getColorNameForHex("#FF0000", "de"))
        assertEquals("blau", ColorUtils.getColorNameForHex("#0000FF", "de"))
    }

    @Test
    fun `colorToHex formats correctly`() {
        assertEquals("#FF0000", ColorUtils.colorToHex(Color.Red))
        assertEquals("#00FF00", ColorUtils.colorToHex(Color.Green))
        assertEquals("#0000FF", ColorUtils.colorToHex(Color.Blue))
        assertEquals("#000000", ColorUtils.colorToHex(Color.Black))
        assertEquals("#FFFFFF", ColorUtils.colorToHex(Color.White))
    }

    @Test
    fun `hexToColor parses correctly`() {
        assertEquals(Color.Red, ColorUtils.hexToColor("#FF0000"))
        assertEquals(Color.Green, ColorUtils.hexToColor("#00FF00"))
        assertEquals(Color.Blue, ColorUtils.hexToColor("#0000FF"))
        assertNull(ColorUtils.hexToColor(""))
        assertNull(ColorUtils.hexToColor("invalid"))
    }

    @Test
    fun `isColorLight correctly identifies luminance`() {
        assertTrue(ColorUtils.isColorLight(Color.White))
        assertTrue(ColorUtils.isColorLight(Color.Yellow))
        assertFalse(ColorUtils.isColorLight(Color.Black))
        assertFalse(ColorUtils.isColorLight(Color.Blue))
    }

    @Test
    fun `getClosestColors returns closest matches`() {
        val redMatches = ColorUtils.getClosestColors("#FF0000", "en")
        assertEquals("red", redMatches[0].first)
        
        val blueishMatches = ColorUtils.getClosestColors("#0000FE", "de")
        assertEquals("blau", blueishMatches[0].first)

        assertEquals(5, redMatches.size)
    }
}
