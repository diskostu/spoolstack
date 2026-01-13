package de.diskostu.spoolstack.ui.filament.list

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.diskostu.spoolstack.MainActivity
import de.diskostu.spoolstack.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FilamentListScreenTest2 {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setup() {
        // 1. Ensure clean state on MainScreen
        composeTestRule.onNodeWithTag("button_clear_filaments").performClick()

        // Wait until "List" button is disabled (count is 0)
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("button_view_filaments")
                .fetchSemanticsNodes(atLeastOneRootRequired = false)
                .firstOrNull()?.config?.getOrNull(SemanticsProperties.Disabled) != null
        }

        // 2. Add 20 sample filaments
        composeTestRule.onNodeWithTag("button_add_sample_filaments").performClick()

        // 3. Wait until "List" button is enabled (implies filaments added)
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("button_view_filaments")
                .fetchSemanticsNodes(atLeastOneRootRequired = false)
                .firstOrNull()?.config?.getOrNull(SemanticsProperties.Disabled) == null
        }

        // 4. Navigate to the filament list
        composeTestRule.onNodeWithTag("button_view_filaments").performClick()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun initialScreen_showsPopulatedList() {
        // Check if the list container is displayed
        composeTestRule.onNodeWithTag("filament_list").assertIsDisplayed()

        // Wait for cards to appear. 
        // ViewModel has a 500ms delay + LazyGrid might need a frame to compose items.
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("filament_card"), timeoutMillis = 5000)

        // Verify that cards are displayed
        composeTestRule.onAllNodesWithTag("filament_card").onFirst().assertIsDisplayed()
    }

    @Test
    fun search_filtersListCorrectly() {
        // Wait for items to appear
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("filament_card").fetchSemanticsNodes(false)
                .isNotEmpty()
        }

        // Open Search
        composeTestRule.onNodeWithTag("search_button").performClick()

        // Search for "Prusa" (one of the random vendors in sample data)
        composeTestRule.onNodeWithTag("search_textfield").performTextInput("Prusa")

        // Wait for filtering to take effect
        composeTestRule.waitForIdle()

        // All visible cards must contain "Prusa"
        // We use assertAll to ensure the filter works
//        composeTestRule.onAllNodesWithTag("filament_card")
//            .assertAll(hasAnyChild(hasText("Prusa", substring = true)))

        composeTestRule.onAllNodesWithTag("filament_card")
            .assertAll(hasText("Prusa", substring = true))
    }

    @Test
    fun delete_withConfirmation_works() {
        // Wait for items to appear
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("filament_card").fetchSemanticsNodes(false)
                .isNotEmpty()
        }

        // 1. Get the text of the first card to identify it later
        val firstCard = composeTestRule.onAllNodesWithTag("filament_card").onFirst()
        val textList = firstCard.fetchSemanticsNode().config.getOrNull(SemanticsProperties.Text)
        val firstItemText = textList?.firstOrNull()?.text ?: ""

        // 2. Open menu and click delete
        openMenuFor(firstItemText)
        composeTestRule.onNodeWithTag("menu_delete").performClick()

        // 3. Confirm in dialog (samples have weight > 0)
        composeTestRule.onNode(
            hasText(targetContext.getString(R.string.delete)) and hasAnyAncestor(
                isDialog()
            )
        )
            .performClick()

        // 4. Switch to DELETED filter to verify it moved there
        composeTestRule.onNodeWithTag("filter_chip_deleted").performClick()
        composeTestRule.onNodeWithText(firstItemText, substring = true).assertIsDisplayed()

        // 5. Switch back to ACTIVE and verify it is gone
        composeTestRule.onNodeWithTag("filter_chip_active").performClick()
        composeTestRule.onNodeWithText(firstItemText, substring = true).assertDoesNotExist()
    }

    @Test
    fun navigation_backButton_works() {
        composeTestRule.onNodeWithContentDescription(targetContext.getString(R.string.back_button_content_description))
            .performClick()

        // Verify we are back on main screen
        composeTestRule.onNodeWithTag("button_add_sample_filaments").assertIsDisplayed()
    }

    private fun openMenuFor(textSnippet: String) {
        // Find the "More options" button inside the card that contains the text
        composeTestRule.onNode(
            hasTestTag("menu_button") and
                    hasAnyAncestor(
                        hasAnyChild(
                            hasText(
                                textSnippet,
                                substring = true
                            )
                        ) and hasTestTag("filament_card")
                    ), true
        ).performClick()
    }
}
