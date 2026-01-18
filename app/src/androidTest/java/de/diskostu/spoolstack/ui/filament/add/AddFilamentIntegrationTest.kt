package de.diskostu.spoolstack.ui.filament.add

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import de.diskostu.spoolstack.MainActivity
import de.diskostu.spoolstack.SpoolstackApplication
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.ui.util.ColorUtils
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Locale

class AddFilamentIntegrationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        // 1. Datenbank leeren über den vorhandenen Debug-Button auf dem MainScreen
        composeTestRule.onNodeWithTag("button_clear_filaments").performClick()
    }

    @Test
    fun frequentColors_isCorrect() {
        // 2. Filamente direkt über das Repository der Application vorbereiten
        val app =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as SpoolstackApplication
        val repository = app.filamentRepository

        runBlocking {
            // 3x #FFFFFF (White)
            repeat(3) {
                repository.insert(
                    Filament(
                        vendor = "V1",
                        colorHex = "#FFFFFF",
                        currentWeight = 1000
                    )
                )
            }
            // 2x #FF0000 (red)
            repeat(2) {
                repository.insert(
                    Filament(
                        vendor = "V2",
                        colorHex = "#FF0000",
                        currentWeight = 1000
                    )
                )
            }
            // 1x #C0C0C0 (silver)
            repository.insert(Filament(vendor = "V3", colorHex = "#C0C0C0", currentWeight = 1000))
        }

        // 3. AddFilamentScreen öffnen (Navigiert vom MainScreen)
        composeTestRule.onNodeWithTag("button_add_filament").performClick()

        // 4. Warten, bis die Frequent-Chips asynchron geladen wurden
        // Wir nutzen den spezifischen Tag für die Frequent-Row, um Verwechslungen mit "Recent" zu vermeiden
        val frequentChipMatcher = hasTestTag("color_suggestion_chip") and
                hasAnyAncestor(hasTestTag("frequent_colors_row"))

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodes(frequentChipMatcher)
                .fetchSemanticsNodes().size == 3
        }

        val chips = composeTestRule.onAllNodes(frequentChipMatcher)

        // Get current language to retrieve localized color names
        val currentLanguage = Locale.getDefault().language

        // 5. Reihenfolge und Inhalt prüfen (Index 0 ist ganz links)
        // Frequent: White (3), Red (2), Silver (1)
        val whiteColorName = ColorUtils.getColorNameForHex("#FFFFFF", currentLanguage) ?: "#FFFFFF"
        chips[0].assert(hasText(whiteColorName, ignoreCase = true))

        val redColorName = ColorUtils.getColorNameForHex("#FF0000", currentLanguage) ?: "#FF0000"
        chips[1].assert(hasText(redColorName, ignoreCase = true))

        val silverColorName = ColorUtils.getColorNameForHex("#C0C0C0", currentLanguage) ?: "#C0C0C0"
        chips[2].assert(hasText(silverColorName, ignoreCase = true))
    }
}
