package de.diskostu.spoolstack.ui.settings

import de.diskostu.spoolstack.data.AppTheme
import de.diskostu.spoolstack.data.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @Mock
    private lateinit var settingsRepository: SettingsRepository

    private lateinit var viewModel: SettingsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial theme is loaded from repository`() = runTest {
        // Given
        val expectedTheme = AppTheme.DARK
        `when`(settingsRepository.appTheme).thenReturn(MutableStateFlow(expectedTheme))
        `when`(settingsRepository.defaultFilamentSize).thenReturn(MutableStateFlow(1000))

        // When
        viewModel = SettingsViewModel(settingsRepository)

        // Trigger subscription so stateIn starts collecting
        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.appTheme.collect()
        }

        advanceUntilIdle()

        // Then
        assertEquals(expectedTheme, viewModel.appTheme.value)
        collectJob.cancel()
    }

    @Test
    fun `setAppTheme calls repository`() = runTest {
        // Given
        val newTheme = AppTheme.LIGHT
        `when`(settingsRepository.appTheme).thenReturn(MutableStateFlow(AppTheme.SYSTEM))
        `when`(settingsRepository.defaultFilamentSize).thenReturn(MutableStateFlow(1000))
        viewModel = SettingsViewModel(settingsRepository)

        // When
        viewModel.setAppTheme(newTheme)
        advanceUntilIdle()

        // Then
        verify(settingsRepository).setAppTheme(newTheme)
    }

    @Test
    fun `initial default filament size is loaded from repository`() = runTest {
        // Given
        val expectedSize = 750
        `when`(settingsRepository.appTheme).thenReturn(MutableStateFlow(AppTheme.SYSTEM))
        `when`(settingsRepository.defaultFilamentSize).thenReturn(MutableStateFlow(expectedSize))

        // When
        viewModel = SettingsViewModel(settingsRepository)

        // Trigger subscription so stateIn starts collecting
        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.defaultFilamentSize.collect()
        }

        advanceUntilIdle()

        // Then
        assertEquals(expectedSize, viewModel.defaultFilamentSize.value)
        collectJob.cancel()
    }

    @Test
    fun `setDefaultFilamentSize calls repository`() = runTest {
        // Given
        val newSize = 2000
        `when`(settingsRepository.appTheme).thenReturn(MutableStateFlow(AppTheme.SYSTEM))
        `when`(settingsRepository.defaultFilamentSize).thenReturn(MutableStateFlow(1000))
        viewModel = SettingsViewModel(settingsRepository)

        // When
        viewModel.setDefaultFilamentSize(newSize)
        advanceUntilIdle()

        // Then
        verify(settingsRepository).setDefaultFilamentSize(newSize)
    }
}
