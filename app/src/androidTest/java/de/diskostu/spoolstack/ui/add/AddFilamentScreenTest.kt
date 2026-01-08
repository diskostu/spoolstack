package de.diskostu.spoolstack.ui.add

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.diskostu.spoolstack.MainActivity
import de.diskostu.spoolstack.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddFilamentScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testValidation_priceWithoutBoughtAt_showsError() {
        // 1. Navigate to Add Filament Screen
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.add_filament))
            .performClick()

        // 2. Fill mandatory fields
        composeTestRule.onNodeWithTag("vendor_input").performTextInput("TestVendor")
        composeTestRule.onNodeWithTag("color_input").performTextInput("TestColor")

        // 3. Fill Price but leave BoughtAt empty
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.price_label))
            .performTextInput("25.00")

        // 4. Click Save
        composeTestRule.onNodeWithTag("save_button").performClick()

        // 5. Verify error message on "Bought at" field
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.error_field_cant_be_empty))
            .assertIsDisplayed()

        // 6. Fill BoughtAt
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.bought_at_label))
            .performTextInput("TestShop")

        // 7. Click Save again
        composeTestRule.onNodeWithTag("save_button").performClick()

        // Wait for navigation
        composeTestRule.waitForIdle()

        // 8. Verify we are back on the main screen
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.view_filaments))
            .assertIsDisplayed()
    }

    @Test
    fun testValidation_boughtAtWithoutPrice_isAllowed() {
        // 1. Navigate to Add Filament Screen
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.add_filament))
            .performClick()

        // 2. Fill mandatory fields
        composeTestRule.onNodeWithTag("vendor_input").performTextInput("TestVendor")
        composeTestRule.onNodeWithTag("color_input").performTextInput("TestColor")

        // 3. Fill BoughtAt, leave Price empty
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.bought_at_label))
            .performTextInput("TestShop")

        // 4. Click Save
        composeTestRule.onNodeWithTag("save_button").performClick()

        // Wait for navigation
        composeTestRule.waitForIdle()

        // 5. Verify success (back to main screen)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.view_filaments))
            .assertIsDisplayed()
    }
}
