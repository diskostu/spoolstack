package de.diskostu.spoolstack.ui.add

import androidx.lifecycle.SavedStateHandle
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.data.FilamentRepository
import de.diskostu.spoolstack.data.FrequentColor
import de.diskostu.spoolstack.data.SettingsRepository
import de.diskostu.spoolstack.ui.filament.add.AddFilamentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor

@OptIn(ExperimentalCoroutinesApi::class)
class AddFilamentViewModelTest {

    @Mock
    private lateinit var filamentRepository: FilamentRepository

    @Mock
    private lateinit var settingsRepository: SettingsRepository

    private lateinit var viewModel: AddFilamentViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        `when`(settingsRepository.defaultFilamentSize).thenReturn(MutableStateFlow(1000))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `vendors, frequent and recent colors are loaded initially`() = runTest {
        // Given
        val vendors = listOf("Vendor A", "Vendor B")
        val frequentColors = listOf(FrequentColor("Red", "#FF0000"))
        // We now allow duplicates between frequent and recent as per requirements
        val recentColors = listOf(FrequentColor("Blue", "#0000FF"), FrequentColor("Red", "#FF0000"))
        
        `when`(filamentRepository.getDistinctVendors()).thenReturn(vendors)
        `when`(filamentRepository.getFrequentColors(any())).thenReturn(frequentColors)
        `when`(filamentRepository.getRecentColors(any())).thenReturn(recentColors)

        // When
        viewModel = AddFilamentViewModel(filamentRepository, settingsRepository, SavedStateHandle())
        advanceUntilIdle()

        // Then
        assertEquals(vendors, viewModel.vendors.value)
        assertEquals(frequentColors, viewModel.frequentColors.value)
        // recentColors should contain both Blue and Red, even if Red is in frequentColors
        assertEquals(recentColors, viewModel.recentColors.value)
    }

    @Test
    fun `save calls repository insert and emits id`() = runTest {
        // Given
        val filament = Filament(
            vendor = "Vendor",
            color = "Red",
            colorHex = "#FF0000",
            currentWeight = 1000,
            createdDate = System.currentTimeMillis(),
            changeDate = System.currentTimeMillis()
        )
        val expectedId = 123L
        `when`(filamentRepository.getDistinctVendors()).thenReturn(emptyList())
        `when`(filamentRepository.getFrequentColors(any())).thenReturn(emptyList())
        `when`(filamentRepository.getRecentColors(any())).thenReturn(emptyList())
        `when`(filamentRepository.insert(any())).thenReturn(expectedId)
        viewModel = AddFilamentViewModel(filamentRepository, settingsRepository, SavedStateHandle())
        
        // We need to collect the flow *before* the emission happens, 
        // because SharedFlow without replay drops events if there are no subscribers.
        val emittedIds = mutableListOf<Long>()
        val collectionJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.savedFilamentId.collect { emittedIds.add(it) }
        }

        // When
        viewModel.save(
            filament.vendor,
            filament.color,
            filament.colorHex,
            filament.currentWeight,
            1000,
            null,
            null,
            null,
            null
        )
        advanceUntilIdle()

        // Then
        assertEquals(expectedId, emittedIds.first())

        collectionJob.cancel()
    }

    @Test
    fun `save calls repository update when editing and deleted is true`() = runTest {
        // Given
        val existingFilament = Filament(
            id = 1,
            vendor = "Vendor",
            color = "Red",
            currentWeight = 1000
        )
        val savedStateHandle = SavedStateHandle(mapOf("filamentId" to 1))
        `when`(filamentRepository.getDistinctVendors()).thenReturn(emptyList())
        `when`(filamentRepository.getFrequentColors(any())).thenReturn(emptyList())
        `when`(filamentRepository.getRecentColors(any())).thenReturn(emptyList())
        `when`(filamentRepository.getFilamentById(1)).thenReturn(existingFilament)

        viewModel = AddFilamentViewModel(filamentRepository, settingsRepository, savedStateHandle)
        advanceUntilIdle()

        val emittedIds = mutableListOf<Long>()
        val collectionJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.savedFilamentId.collect { emittedIds.add(it) }
        }

        // When
        viewModel.save("Vendor", "Red", "#FF0000", 0, 1000, null, null, null, null, deleted = true)
        advanceUntilIdle()

        // Then
        val captor = argumentCaptor<Filament>()
        verify(filamentRepository).update(captor.capture())
        val updatedFilament = captor.firstValue
        assertTrue(updatedFilament.deleted)
        assertEquals(0, updatedFilament.currentWeight)
        assertEquals(1L, emittedIds.first())

        collectionJob.cancel()
    }

    @Test
    fun `save calls repository insert with all optional fields`() = runTest {
        // Given
        val vendor = "Vendor"
        val color = "Red"
        val colorHex = "#FF0000"
        val currentWeight = 1000
        val totalWeight = 1000
        val boughtAt = "Shop"
        val boughtDate = 123456789L
        val price = 25.50

        val expectedId = 123L
        `when`(filamentRepository.getDistinctVendors()).thenReturn(emptyList())
        `when`(filamentRepository.getFrequentColors(any())).thenReturn(emptyList())
        `when`(filamentRepository.getRecentColors(any())).thenReturn(emptyList())
        `when`(filamentRepository.insert(any())).thenReturn(expectedId)
        viewModel = AddFilamentViewModel(filamentRepository, settingsRepository, SavedStateHandle())

        val emittedIds = mutableListOf<Long>()
        val collectionJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.savedFilamentId.collect { emittedIds.add(it) }
        }

        // When
        viewModel.save(
            vendor,
            color,
            colorHex,
            currentWeight,
            totalWeight,
            null,
            boughtAt,
            boughtDate,
            price
        )
        advanceUntilIdle()

        // Then
        assertEquals(expectedId, emittedIds.first())
        
        collectionJob.cancel()
    }
}
