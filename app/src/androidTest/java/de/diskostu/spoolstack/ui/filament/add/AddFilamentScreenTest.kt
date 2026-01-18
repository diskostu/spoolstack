package de.diskostu.spoolstack.ui.filament.add

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import de.diskostu.spoolstack.R
import de.diskostu.spoolstack.data.ColorWithName
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme
import org.junit.Rule
import org.junit.Test

import org.mockito.kotlin.*

class AddFilamentScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val targetContext = InstrumentationRegistry.getInstrumentation().targetContext


    @Test
    fun initialState_isCorrect() {
        composeTestRule.setContent {
            SpoolstackTheme {
                AddFilamentContent(
                    existingVendors = emptyList(),
                    frequentColors = emptyList(),
                    recentColors = emptyList(),
                    filamentState = null,
                    defaultFilamentSize = 1000,
                    onNavigateBack = {},
                    onSave = { _, _, _, _, _, _, _, _, _ -> },
                    getColorName = { "Black" }
                )
            }
        }

        // Check if default values are set
        composeTestRule.onNodeWithTag("vendor_input").assertTextContains("")
        composeTestRule.onNodeWithTag("total_weight_input").assertTextContains("1kg")
        composeTestRule.onNodeWithTag("size_input").assertTextContains("1000")
    }


    @Test
    fun frequentColors_isCorrect() {


    }

    @Test
    fun validation_vendorEmpty_showsError() {
        composeTestRule.setContent {
            SpoolstackTheme {
                AddFilamentContent(
                    existingVendors = emptyList(),
                    frequentColors = emptyList(),
                    recentColors = emptyList(),
                    filamentState = null,
                    defaultFilamentSize = 1000,
                    onNavigateBack = {},
                    onSave = { _, _, _, _, _, _, _, _, _ -> },
                    getColorName = { "Black" }
                )
            }
        }

        // first, add a color. Later, we want exactly 1 error text (vendor missing)
        composeTestRule.onNodeWithTag("color_picker_trigger", true).performClick()
        composeTestRule.onNodeWithTag("save_button_color_picker").performClick()

        // Click save without entering vendor
        composeTestRule.onNodeWithTag("save_button").performClick()

        // Check for error text
        composeTestRule
            .onNodeWithText(
                InstrumentationRegistry
                    .getInstrumentation()
                    .targetContext.getString(R.string.error_field_cant_be_empty)
            )
            .assertIsDisplayed()
    }

    @Test
    fun validation_colorNotSelected_showsError() {
        composeTestRule.setContent {
            SpoolstackTheme {
                AddFilamentContent(
                    existingVendors = emptyList(),
                    frequentColors = emptyList(),
                    recentColors = emptyList(),
                    filamentState = null,
                    defaultFilamentSize = 1000,
                    onNavigateBack = {},
                    onSave = { _, _, _, _, _, _, _, _, _ -> },
                    getColorName = { "Black" }
                )
            }
        }

        // Enter vendor but no color
        composeTestRule.onNodeWithTag("vendor_input").performTextInput("Test Vendor")
        composeTestRule.onNodeWithTag("save_button").performClick()

        composeTestRule
            .onNodeWithText(
                targetContext.getString(R.string.error_field_cant_be_empty)
            )
            .assertIsDisplayed()
    }

    @Test
    fun successfulSave_callsOnSave() {
        val onSaveMock =
            mock<(String, String, Int, Int, Int?, String?, Long?, Double?, Boolean) -> Unit>()

        composeTestRule.setContent {
            SpoolstackTheme {
                AddFilamentContent(
                    existingVendors = emptyList(),
                    frequentColors = emptyList(),
                    recentColors = emptyList(),
                    filamentState = null,
                    defaultFilamentSize = 1000,
                    onNavigateBack = {},
                    onSave = onSaveMock,
                    getColorName = { "Green" }
                )
            }
        }

        // Fill data
        composeTestRule.onNodeWithTag("vendor_input").performTextInput("Prusa")

        // Trigger color picker and select a color
        composeTestRule.onNodeWithTag("color_picker_trigger", true).performClick()
        composeTestRule.onNodeWithTag("save_button_color_picker").performClick()

        // save
        composeTestRule.onNodeWithTag("save_button").performClick()

        verify(onSaveMock).invoke(
            eq("Prusa"),
            any(), // colorHex
            eq(1000),
            eq(1000),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            eq(false)
        )
    }

    @Test
    fun vendorDropdown_filtersResults() {
        val vendors = listOf("Prusa", "Polymaker", "Sunlu")
        composeTestRule.setContent {
            SpoolstackTheme {
                AddFilamentContent(
                    existingVendors = vendors,
                    frequentColors = emptyList(),
                    recentColors = emptyList(),
                    filamentState = null,
                    defaultFilamentSize = 1000,
                    onNavigateBack = {},
                    onSave = { _, _, _, _, _, _, _, _, _ -> },
                    getColorName = { "Black" }
                )
            }
        }

        composeTestRule.onNodeWithTag("vendor_input").performTextInput("P")

        composeTestRule.onNodeWithText("Prusa").assertIsDisplayed()
        composeTestRule.onNodeWithText("Polymaker").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sunlu").assertDoesNotExist()
    }

    @Test
    fun frequentColors_selectingChip_updatesColor() {
        val frequentColors = listOf(ColorWithName("#FF0000", "Red"))

        composeTestRule.setContent {
            SpoolstackTheme {
                AddFilamentContent(
                    existingVendors = emptyList(),
                    frequentColors = frequentColors,
                    recentColors = emptyList(),
                    filamentState = null,
                    defaultFilamentSize = 1000,
                    onNavigateBack = {},
                    onSave = { _, _, _, _, _, _, _, _, _ -> },
                    getColorName = { if (it == "#FF0000") "Red" else "Unknown" }
                )
            }
        }

        composeTestRule
            .onAllNodes(
                hasText("Red", substring = false) and hasClickAction()
            )
            .onFirst()
            .performClick()

        // Check if color name is updated in the field
        composeTestRule.onNodeWithTag("color_name_text", true)
            .assertTextContains("Red")
    }

    @Test
    fun editMode_prefillsData() {
        val existingFilament = Filament(
            id = 1,
            vendor = "Sunlu",
            colorHex = "#FF0000",
            currentWeight = 450,
            totalWeight = 1000,
            spoolWeight = 220,
            boughtAt = "Amazon",
            price = 19.99,
            boughtDate = 123456789L
        )

        composeTestRule.setContent {
            SpoolstackTheme {
                AddFilamentContent(
                    existingVendors = emptyList(),
                    frequentColors = emptyList(),
                    recentColors = emptyList(),
                    filamentState = existingFilament,
                    defaultFilamentSize = 1000,
                    onNavigateBack = {},
                    onSave = { _, _, _, _, _, _, _, _, _ -> },
                    getColorName = { "Red" }
                )
            }
        }

        composeTestRule.onNodeWithTag("vendor_input").assertTextContains("Sunlu")
        composeTestRule.onNodeWithTag("size_input").assertTextContains("450")
        composeTestRule.onNodeWithTag("total_weight_input").assertTextContains("1kg")
        composeTestRule.onNodeWithTag("bought_at_input").assertTextContains("Amazon")
        composeTestRule.onNodeWithTag("price_input").assertTextContains("19.99")
    }

    @Test
    fun editMode_weightZero_showsDeleteDialog() {
        val existingFilament = Filament(
            id = 1,
            vendor = "Sunlu",
            colorHex = "#FF0000",
            currentWeight = 450,
            totalWeight = 1000
        )

        composeTestRule.setContent {
            SpoolstackTheme {
                AddFilamentContent(
                    existingVendors = emptyList(),
                    frequentColors = emptyList(),
                    recentColors = emptyList(),
                    filamentState = existingFilament,
                    defaultFilamentSize = 1000,
                    onNavigateBack = {},
                    onSave = { _, _, _, _, _, _, _, _, _ -> },
                    getColorName = { "Red" }
                )
            }
        }

        // Change weight to 0
        composeTestRule.onNodeWithTag("size_input").performTextReplacement("0")
        composeTestRule.onNodeWithTag("save_button").performClick()

        // Check for delete confirmation dialog
        composeTestRule
            .onNodeWithText(
                targetContext.getString(R.string.delete_empty_confirmation_message)
            )
            .assertIsDisplayed()
    }

    @Test
    fun navigation_cancelButton_callsOnNavigateBack() {
        val onNavigateBackMock = mock<() -> Unit>()
        composeTestRule.setContent {
            SpoolstackTheme {
                AddFilamentContent(
                    existingVendors = emptyList(),
                    frequentColors = emptyList(),
                    recentColors = emptyList(),
                    filamentState = null,
                    defaultFilamentSize = 1000,
                    onNavigateBack = onNavigateBackMock,
                    onSave = { _, _, _, _, _, _, _, _, _ -> },
                    getColorName = { "Black" }
                )
            }
        }

        composeTestRule.onNodeWithTag("cancel_button").performClick()
        verify(onNavigateBackMock).invoke()
    }

    @Test
    fun totalWeight_changing_updatesSizeInput() {
        composeTestRule.setContent {
            SpoolstackTheme {
                AddFilamentContent(
                    existingVendors = emptyList(),
                    frequentColors = emptyList(),
                    recentColors = emptyList(),
                    filamentState = null,
                    defaultFilamentSize = 1000,
                    onNavigateBack = {},
                    onSave = { _, _, _, _, _, _, _, _, _ -> },
                    getColorName = { "Black" }
                )
            }
        }

        // Open dropdown
        composeTestRule.onNodeWithTag("total_weight_input").performClick()
        // Select 500g
        composeTestRule.onNodeWithText("500g").performClick()

        // Check if size_input is updated to 500
        composeTestRule.onNodeWithTag("size_input").assertTextContains("500")
    }
}
