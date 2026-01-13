package de.diskostu.spoolstack.ui.main

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun buttons_areDisabled_whenCountsAreZero() {
        composeTestRule.setContent {
            SpoolstackTheme {
                MainScreenContent(
                    navController = rememberNavController(),
                    filamentCount = 0,
                    printCount = 0,
                    onClearFilaments = {},
                    onClearPrints = {},
                    onAddSampleFilaments = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("button_add_filament").assertIsEnabled()
        composeTestRule.onNodeWithTag("button_view_filaments").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("button_add_print").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("button_view_prints").assertIsNotEnabled()
    }

    @Test
    fun buttons_areEnabled_whenCountsAreGreaterThanZero() {
        composeTestRule.setContent {
            SpoolstackTheme {
                MainScreenContent(
                    navController = rememberNavController(),
                    filamentCount = 5,
                    printCount = 10,
                    onClearFilaments = {},
                    onClearPrints = {},
                    onAddSampleFilaments = {}
                )
            }
        }

        // All buttons should be enabled
        listOf(
            "button_add_filament",
            "button_view_filaments",
            "button_add_print",
            "button_view_prints"
        ).forEach { tag ->
            composeTestRule.onNodeWithTag(tag).assertIsEnabled()
        }
    }
}
