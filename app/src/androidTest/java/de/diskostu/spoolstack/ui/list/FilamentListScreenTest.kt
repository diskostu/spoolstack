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

        // 4. Archive the last item
        composeTestRule.onNodeWithText(lastItemText)
            .assertIsDisplayed()

        // Find the "more options" button in the same card. This is tricky in a list.
        // For simplicity in this test, we might just try to find the button if it's unique or rely on hierarchy
        // But since we have many items, finding the *specific* menu button is hard without specific test tags per item.
        // Let's at least verifying we can see the item.
        // To properly test the menu interaction, we should probably add a test tag to the menu button that includes the ID.
        // But I will skip complex menu interaction test for now to keep it simple as requested,
        // just focusing on the scrolling and existence.

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
    fun testArchiveFunctionality() {
        // 1. Create a filament
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.add_filament))
            .performClick()

        composeTestRule.onNodeWithTag("vendor_input").performTextInput("TestVendor")
        composeTestRule.onNodeWithTag("color_input").performTextInput("TestColor")
        composeTestRule.onNodeWithTag("save_button").performClick()

        // 2. Go to list
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.view_filaments))
            .performClick()

        // 3. Open menu and click archive
        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.menu_more_options))
            .performClick()

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.archive))
            .performClick()

        // 4. Open menu again and check if it says unarchive
        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.menu_more_options))
            .performClick()

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.unarchive))
            .assertIsDisplayed()

        // 5. Click unarchive
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.unarchive))
            .performClick()

        // 6. Check if it says archive again
        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.menu_more_options))
            .performClick()

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.archive))
            .assertIsDisplayed()
    }
}
