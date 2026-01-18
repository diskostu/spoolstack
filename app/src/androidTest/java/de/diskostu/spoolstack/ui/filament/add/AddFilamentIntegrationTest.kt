package de.diskostu.spoolstack.ui.filament.add

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import de.diskostu.spoolstack.MainActivity
import de.diskostu.spoolstack.SpoolstackApplication
import de.diskostu.spoolstack.data.Filament
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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
        // Wir nutzen die Instanz, die Hilt bereits in die App injiziert hat.
        val app =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as SpoolstackApplication
        val repository = app.filamentRepository

        runBlocking {
            // 3x #FFFFFF (In InitialColors als "White"/"Weiß" vorhanden)
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
            // 1x #BBBBBB (silver)
            repository.insert(Filament(vendor = "V3", colorHex = "#C0C0C0", currentWeight = 1000))
        }

        // 3. AddFilamentScreen öffnen (Navigiert vom MainScreen)
        composeTestRule.onNodeWithTag("button_add_filament").performClick()

        // 4. Warten, bis die Chips asynchron geladen wurden
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("color_suggestion_chip")
                .fetchSemanticsNodes().size == 3
        }

        val chips = composeTestRule.onAllNodesWithTag("color_suggestion_chip")

        // 5. Reihenfolge und Inhalt prüfen (Index 0 ist ganz links)
        chips[0].assert(hasText("White") or hasText("Weiß") or hasText("#FFFFFF"))
        chips[1].assertTextContains("#FF0000")
        chips[2].assertTextContains("#C0C0C0")
    }
}