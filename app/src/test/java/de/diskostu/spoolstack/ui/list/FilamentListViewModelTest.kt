package de.diskostu.spoolstack.ui.list

import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.data.FilamentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
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

@OptIn(ExperimentalCoroutinesApi::class)
class FilamentListViewModelTest {

    @Mock
    private lateinit var filamentRepository: FilamentRepository

    private lateinit var viewModel: FilamentListViewModel
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
    fun `filaments are loaded from repository`() = runTest {
        // Given
        val filamentList = listOf(
            Filament(
                1,
                "Vendor A",
                "Red",
                "1kg",
                createdDate = System.currentTimeMillis(),
                changeDate = System.currentTimeMillis()
            ),
            Filament(
                2,
                "Vendor B",
                "Blue",
                "500g",
                createdDate = System.currentTimeMillis(),
                changeDate = System.currentTimeMillis()
            )
        )
        `when`(filamentRepository.getAllFilaments()).thenReturn(flowOf(filamentList))

        // When
        viewModel = FilamentListViewModel(filamentRepository)
        
        // Background collection to keep the subscription active
        val job = launch {
            viewModel.filaments.collect {}
        }
        
        // Advance time to allow the initial emission to happen
        testScheduler.advanceUntilIdle()

        // Then
        assertEquals(filamentList, viewModel.filaments.value)
        
        job.cancel()
    }
    
    @Test
    fun `filaments initial value is empty list`() = runTest {
        // Given
        `when`(filamentRepository.getAllFilaments()).thenReturn(flowOf(emptyList()))

        // When
        viewModel = FilamentListViewModel(filamentRepository)

        // Then
        assertEquals(emptyList<Filament>(), viewModel.filaments.value)
    }
}
