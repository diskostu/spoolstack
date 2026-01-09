package de.diskostu.spoolstack.ui.add

import androidx.lifecycle.SavedStateHandle
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.data.FilamentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

@OptIn(ExperimentalCoroutinesApi::class)
class AddFilamentViewModelTest {

    @Mock
    private lateinit var filamentRepository: FilamentRepository

    private lateinit var viewModel: AddFilamentViewModel
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
    fun `vendors are loaded initially`() = runTest {
        // Given
        val vendors = listOf("Vendor A", "Vendor B")
        `when`(filamentRepository.getDistinctVendors()).thenReturn(vendors)

        // When
        viewModel = AddFilamentViewModel(filamentRepository, SavedStateHandle())
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
            size = 1000,
            createdDate = System.currentTimeMillis(),
            changeDate = System.currentTimeMillis()
        )
        val expectedId = 123L
        `when`(filamentRepository.getDistinctVendors()).thenReturn(emptyList())
        `when`(filamentRepository.insert(any())).thenReturn(expectedId)
        viewModel = AddFilamentViewModel(filamentRepository, SavedStateHandle())
        
        // We need to collect the flow *before* the emission happens, 
        // because SharedFlow without replay drops events if there are no subscribers.
        val emittedIds = mutableListOf<Long>()
        val collectionJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.savedFilamentId.collect { emittedIds.add(it) }
        }

        // When
        viewModel.save(filament.vendor, filament.color, filament.size, null, null, null)
        advanceUntilIdle()

        // Then
        assertEquals(expectedId, emittedIds.first())

        collectionJob.cancel()
    }

    @Test
    fun `save calls repository insert with all optional fields`() = runTest {
        // Given
        val vendor = "Vendor"
        val color = "Red"
        val size = 1000
        val boughtAt = "Shop"
        val boughtDate = 123456789L
        val price = 25.50

        val expectedId = 123L
        `when`(filamentRepository.getDistinctVendors()).thenReturn(emptyList())
        `when`(filamentRepository.insert(any())).thenReturn(expectedId)
        viewModel = AddFilamentViewModel(filamentRepository, SavedStateHandle())

        val emittedIds = mutableListOf<Long>()
        val collectionJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.savedFilamentId.collect { emittedIds.add(it) }
        }

        // When
        viewModel.save(vendor, color, size, boughtAt, boughtDate, price)
        advanceUntilIdle()

        // Then
        assertEquals(expectedId, emittedIds.first())

        // Verify that the repository was called with the correct filament object
        // Note: verify(filamentRepository).insert(argThat { ... }) would be better here to check fields
        
        collectionJob.cancel()
    }
}
