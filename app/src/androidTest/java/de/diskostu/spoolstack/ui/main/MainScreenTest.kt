package de.diskostu.spoolstack.ui.main

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
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

        // Add filament (first "Add" button) should always be enabled
        composeTestRule.onAllNodesWithText("Add")[0].assertIsEnabled()

        // List filaments (first "List" button) should be disabled if filamentCount is 0
        composeTestRule.onAllNodesWithText("List")[0].assertIsNotEnabled()

        // Record print (second "Add" button) depends on filaments
        composeTestRule.onAllNodesWithText("Add")[1].assertIsNotEnabled()

        // List prints (second "List" button) should be disabled if printCount is 0
        composeTestRule.onAllNodesWithText("List")[1].assertIsNotEnabled()
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
        composeTestRule.onAllNodesWithText("Add")[0].assertIsEnabled()
        composeTestRule.onAllNodesWithText("List")[0].assertIsEnabled()
        composeTestRule.onAllNodesWithText("Add")[1].assertIsEnabled()
        composeTestRule.onAllNodesWithText("List")[1].assertIsEnabled()
    }
}
