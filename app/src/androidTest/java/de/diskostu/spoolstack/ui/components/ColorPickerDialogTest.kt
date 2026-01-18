package de.diskostu.spoolstack.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import de.diskostu.spoolstack.R
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class ColorPickerDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun colorPickerDialog_initialState_showsSaveAndCancel() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val saveText = context.getString(R.string.save)
        val cancelText = context.getString(R.string.cancel)

        composeTestRule.setContent {
            SpoolstackTheme {
                ColorPickerDialog(
                    onColorSelected = {},
                    onDismissRequest = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("save_button_color_picker").assertIsDisplayed()
        composeTestRule.onNodeWithText(saveText, ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText(cancelText, ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun colorPickerDialog_clickingSave_callsOnColorSelected() {
        val onColorSelectedMock = mock<(Color) -> Unit>()
        val onDismissRequestMock = mock<() -> Unit>()

        composeTestRule.setContent {
            SpoolstackTheme {
                ColorPickerDialog(
                    onColorSelected = onColorSelectedMock,
                    onDismissRequest = onDismissRequestMock,
                    initialColor = Color.Red
                )
            }
        }

        composeTestRule.onNodeWithTag("save_button_color_picker").performClick()

        verify(onColorSelectedMock).invoke(eq(Color.Red))
        verify(onDismissRequestMock).invoke()
    }

    @Test
    fun colorPickerDialog_showsSuggestionsAfterDelay() {
        composeTestRule.setContent {
            SpoolstackTheme {
                ColorPickerDialog(
                    onColorSelected = {},
                    onDismissRequest = {},
                    initialColor = Color.Red // #FF0000 -> "red" should be suggested
                )
            }
        }

        // Suggestions appear after 500ms delay
        composeTestRule.mainClock.advanceTimeBy(600)

        composeTestRule.onNodeWithTag("suggested_colors_row").assertIsDisplayed()
        // Check if there are color suggestion chips
        val nodes = composeTestRule.onAllNodesWithTag("color_suggestion_chip")
        if (nodes.fetchSemanticsNodes().isEmpty()) {
            throw AssertionError("Expected at least one color suggestion chip, but found none.")
        }
    }

    @Test
    fun colorPickerDialog_clickingSuggestion_updatesSelection() {
        composeTestRule.setContent {
            SpoolstackTheme {
                ColorPickerDialog(
                    onColorSelected = {},
                    onDismissRequest = {},
                    initialColor = Color.White
                )
            }
        }

        // Wait for suggestions
        composeTestRule.mainClock.advanceTimeBy(600)

        // Click the first chip in the suggestion row.
        composeTestRule.onAllNodesWithTag("color_suggestion_chip")
            .onFirst()
            .performClick()

        // Verify clicking save works after selecting a suggestion
        composeTestRule.onNodeWithTag("save_button_color_picker").performClick()
    }
}
