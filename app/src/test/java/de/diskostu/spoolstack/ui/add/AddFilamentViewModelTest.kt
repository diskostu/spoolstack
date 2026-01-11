package de.diskostu.spoolstack.ui.add

import androidx.lifecycle.SavedStateHandle
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.data.FilamentRepository
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
    fun `vendors are loaded initially`() = runTest {
        // Given
        val vendors = listOf("Vendor A", "Vendor B")
        `when`(filamentRepository.getDistinctVendors()).thenReturn(vendors)

        // When
        viewModel = AddFilamentViewModel(filamentRepository, settingsRepository, SavedStateHandle())
        advanceUntilIdle()

        // Then
        assertEquals(vendors, viewModel.vendors.value)
    }

    @Test
    fun `save calls repository insert and emits id`() = runTest {
        // Given
        val filament = Filament(
            vendor = "Vendor",
            color = "Red",
            currentWeight = 1000,
            createdDate = System.currentTimeMillis(),
            changeDate = System.currentTimeMillis()
        )
        val expectedId = 123L
        `when`(filamentRepository.getDistinctVendors()).thenReturn(emptyList())
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
        `when`(filamentRepository.getFilamentById(1)).thenReturn(existingFilament)

        viewModel = AddFilamentViewModel(filamentRepository, settingsRepository, savedStateHandle)
        advanceUntilIdle()

        val emittedIds = mutableListOf<Long>()
        val collectionJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.savedFilamentId.collect { emittedIds.add(it) }
        }

        // When
        viewModel.save("Vendor", "Red", 0, 1000, null, null, null, null, deleted = true)
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
        val currentWeight = 1000
        val totalWeight = 1000
        val boughtAt = "Shop"
        val boughtDate = 123456789L
        val price = 25.50

        val expectedId = 123L
        `when`(filamentRepository.getDistinctVendors()).thenReturn(emptyList())
        `when`(filamentRepository.insert(any())).thenReturn(expectedId)
        viewModel = AddFilamentViewModel(filamentRepository, settingsRepository, SavedStateHandle())

        val emittedIds = mutableListOf<Long>()
        val collectionJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.savedFilamentId.collect { emittedIds.add(it) }
        }

        // When
        viewModel.save(vendor, color, currentWeight, totalWeight, null, boughtAt, boughtDate, price)
        advanceUntilIdle()

        // Then
        assertEquals(expectedId, emittedIds.first())
        
        collectionJob.cancel()
    }
}
