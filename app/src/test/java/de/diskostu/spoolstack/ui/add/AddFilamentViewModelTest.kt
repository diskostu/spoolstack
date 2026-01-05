package de.diskostu.spoolstack.ui.add

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
        viewModel = AddFilamentViewModel(filamentRepository)
        advanceUntilIdle()

        // Then
        assertEquals(vendors, viewModel.vendors.value)
    }

    @Test
    fun `save calls repository insert and emits id`() = runTest {
        // Given
        val filament = Filament(vendor = "Vendor", color = "Red", size = "1kg")
        val expectedId = 123L
        `when`(filamentRepository.getDistinctVendors()).thenReturn(emptyList())
        `when`(filamentRepository.insert(any())).thenReturn(expectedId)
        viewModel = AddFilamentViewModel(filamentRepository)
        
        // We need to collect the flow *before* the emission happens, 
        // because SharedFlow without replay drops events if there are no subscribers.
        val emittedIds = mutableListOf<Long>()
        val collectionJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.savedFilamentId.collect { emittedIds.add(it) }
        }

        // When
        viewModel.save(filament.vendor, filament.color, filament.size)
        advanceUntilIdle()

        // Then
        assertEquals(expectedId, emittedIds.first())
        
        collectionJob.cancel()
    }
}
