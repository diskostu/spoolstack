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
                    onClearPrints = {}
                )
            }
        }

        // Add filament (first "add" button) should always be enabled
        composeTestRule.onAllNodesWithText("add")[0].assertIsEnabled()

        // View filaments (first "view" button) should be disabled if filamentCount is 0
        composeTestRule.onAllNodesWithText("view")[0].assertIsNotEnabled()

        // Record print (second "add" button) depends on filaments
        composeTestRule.onAllNodesWithText("add")[1].assertIsNotEnabled()

        // View prints (second "view" button) should be disabled if printCount is 0
        composeTestRule.onAllNodesWithText("view")[1].assertIsNotEnabled()
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
                    onClearPrints = {}
                )
            }
        }

        // All buttons should be enabled
        composeTestRule.onAllNodesWithText("add")[0].assertIsEnabled()
        composeTestRule.onAllNodesWithText("view")[0].assertIsEnabled()
        composeTestRule.onAllNodesWithText("add")[1].assertIsEnabled()
        composeTestRule.onAllNodesWithText("view")[1].assertIsEnabled()
    }
}
