package de.diskostu.spoolstack.ui.list

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
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

            // For the last one, ensure it's unique by using a specific value not in the list
            val randomVendor: String
            val randomColor: String

            if (it == 19) {
                randomVendor = "UniqueVendor"
                randomColor = "UniqueColor"
                lastVendor = randomVendor
                lastColor = randomColor
            } else {
                randomVendor = vendors.random()
                randomColor = colors.random()
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

        // 5. Delete all filaments and ensure list is empty
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

    @Test
    fun testArchiveConfirmationDialog() {
        // 1. Create a filament with size > 0
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.add_filament))
            .performClick()

        composeTestRule.onNodeWithTag("vendor_input").performTextInput("ConfirmationVendor")
        composeTestRule.onNodeWithTag("color_input").performTextInput("ConfirmationColor")
        // Size is 1000 by default in the UI usually, but let's assume it is > 0
        composeTestRule.onNodeWithTag("save_button").performClick()

        // 2. Go to list
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.view_filaments))
            .performClick()

        // 3. Open menu and click archive
        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.menu_more_options))
            .performClick()

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.archive))
            .performClick()

        // 4. Check if confirmation dialog is displayed
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.archive_confirmation_title))
            .assertIsDisplayed()

        // 5. Click cancel
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.cancel))
            .performClick()

        // 6. Dialog should be gone
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.archive_confirmation_title))
            .assertDoesNotExist()

        // 7. Verify it's still "Archive" (not archived yet)
        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.menu_more_options))
            .performClick()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.archive))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.archive))
            .performClick()

        // 8. Click confirm (Archive)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.archive))
            .performClick()

        // 9. Verify it's now archived (menu should show "Unarchive")
        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.menu_more_options))
            .performClick()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.unarchive))
            .assertIsDisplayed()
    }

    @Test
    fun testUnarchiveDirectly() {
        // 1. Create a filament
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.add_filament))
            .performClick()

        composeTestRule.onNodeWithTag("vendor_input").performTextInput("UnarchiveVendor")
        composeTestRule.onNodeWithTag("color_input").performTextInput("UnarchiveColor")
        composeTestRule.onNodeWithTag("save_button").performClick()

        // 2. Go to list
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.view_filaments))
            .performClick()

        // 3. Archive it (confirm dialog)
        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.menu_more_options))
            .performClick()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.archive))
            .performClick()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.archive))
            .performClick()

        // 4. Now Unarchive it - should NOT show dialog
        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.menu_more_options))
            .performClick()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.unarchive))
            .performClick()

        // 5. Verify it's unarchived (menu shows Archive)
        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.menu_more_options))
            .performClick()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.archive))
            .assertIsDisplayed()

        // 6. Ensure dialog didn't show up during unarchiving
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.archive_confirmation_title))
            .assertDoesNotExist()
    }
}
