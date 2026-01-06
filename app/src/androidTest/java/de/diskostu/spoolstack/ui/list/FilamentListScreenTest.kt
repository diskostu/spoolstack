package de.diskostu.spoolstack.ui.list

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.diskostu.spoolstack.MainActivity
import de.diskostu.spoolstack.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FilamentListScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        // Clear all filaments before starting
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.debug_clear_filaments))
            .performClick()
    }

    @Test
    fun createAndScrollFilaments() {
        // 1. Ensure list is empty
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.view_filaments))
            .performClick()

        // Wait for list to load
        composeTestRule.onNodeWithTag("filament_list").assertIsDisplayed()

        // Ensure no items are present
        composeTestRule.onAllNodesWithTag("filament_card").assertCountEquals(0)

        // Go back to main screen
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }

        // 2. Create 20 random filaments
        val vendors = listOf("Prusament", "Sunlu", "Esun", "Overture", "Polymaker")
        val colors = listOf("Black", "White", "Red", "Blue", "Green", "Yellow", "Orange", "Purple")

        var lastVendor = ""
        var lastColor = ""

        repeat(20) {
            composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.add_filament))
                .performClick()

            val randomVendor = vendors.random()
            val randomColor = colors.random()

            // Keep track of the last one to verify scrolling
            if (it == 19) {
                lastVendor = randomVendor
                lastColor = randomColor
            }

            composeTestRule.onNodeWithTag("vendor_input")
                .performTextInput(randomVendor)

            composeTestRule.onNodeWithTag("color_input")
                .performTextInput(randomColor)

            composeTestRule.onNodeWithTag("save_button")
                .performClick()
        }

        // 3. Ensure list scrolls and all elements are visible (by scrolling to the last one)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.view_filaments))
            .performClick()

        composeTestRule.onNodeWithTag("filament_list").assertIsDisplayed()

        // Scroll to the last item we added
        val lastItemText = "$lastVendor | $lastColor"
        composeTestRule.onNodeWithTag("filament_list")
            .performScrollToNode(hasText(lastItemText))

        composeTestRule.onNodeWithText(lastItemText).assertIsDisplayed()

        // 4. Delete all filaments and ensure list is empty
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.debug_clear_filaments))
            .performClick()

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.view_filaments))
            .performClick()

        composeTestRule.onNodeWithTag("filament_list").assertIsDisplayed()
        composeTestRule.onAllNodesWithTag("filament_card").assertCountEquals(0)
    }
}
