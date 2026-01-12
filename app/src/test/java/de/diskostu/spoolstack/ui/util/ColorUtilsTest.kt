package de.diskostu.spoolstack.ui.util

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ColorUtilsTest {

    @Test
    fun `inferColorFromText matches exact English color`() {
        val color = ColorUtils.inferColorFromText("red")
        assertNotNull(color)
        assertEquals(Color.Red, color)
    }

    @Test
    fun `inferColorFromText matches exact German color`() {
        val color = ColorUtils.inferColorFromText("rot")
        assertNotNull(color)
        assertEquals(Color.Red, color)
    }

    @Test
    fun `inferColorFromText matches fuzzy English color`() {
        // "reeed" should match "red"
        val color = ColorUtils.inferColorFromText("reeed")
        assertNotNull(color)
        assertEquals(Color.Red, color)
    }

    @Test
    fun `inferColorFromText matches fuzzy German color`() {
        // "rooot" should match "rot"
        val color = ColorUtils.inferColorFromText("rooot")
        assertNotNull(color)
        assertEquals(Color.Red, color)
    }

    @Test
    fun `inferColorFromText returns null for unknown color`() {
        val color = ColorUtils.inferColorFromText("xyz123")
        assertNull(color)
    }

    @Test
    fun `inferColorFromText handles hex codes`() {
        val color = ColorUtils.inferColorFromText("#FF0000")
        assertNotNull(color)
        assertEquals(Color.Red, color)
    }

    @Test
    fun `inferColorFromText handles mixed case and whitespace`() {
        val color = ColorUtils.inferColorFromText("  Blau  ")
        assertNotNull(color)
        assertEquals(Color.Blue, color)
    }

    @Test
    fun `inferColorFromText returns result if only one match exists`() {
        val color = ColorUtils.inferColorFromText("crims")
        assertNotNull(color)
        assertEquals(Color(0xFFDC143C), color)
    }

    @Test
    fun `inferColorFromText returns null if too many matches exist`() {
        // typing "bl" might match black, blue, blau, etc.
        val color = ColorUtils.inferColorFromText("bl")
        assertNull(color)
    }

    @Test
    fun `inferColorFromText returns 100 percent match even if many other matches exist`() {
        val color = ColorUtils.inferColorFromText("blue")
        assertNotNull(color)
        assertEquals(Color.Blue, color)
    }

    @Test
    fun `inferColorFromText handles light and dark variations`() {
        val lightBlue = ColorUtils.inferColorFromText("light blue")
        assertNotNull(lightBlue)
        assertEquals(Color(0xFFADD8E6), lightBlue)

        val hellBlau = ColorUtils.inferColorFromText("hellblau")
        assertNotNull(hellBlau)
        assertEquals(Color(0xFFADD8E6), hellBlau)
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
}
