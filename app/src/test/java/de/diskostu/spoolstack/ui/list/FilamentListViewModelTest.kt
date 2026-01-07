package de.diskostu.spoolstack.ui.list

import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.data.FilamentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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

    private val filament1 = Filament(
        id = 1,
        vendor = "Vendor A",
        color = "Red",
        size = "1kg",
        archived = false,
        createdDate = System.currentTimeMillis(),
        changeDate = System.currentTimeMillis()
    )

    private val filament2 = Filament(
        id = 2,
        vendor = "Vendor B",
        color = "Blue",
        size = "500g",
        archived = true,
        createdDate = System.currentTimeMillis(),
        changeDate = System.currentTimeMillis()
    )

    private val filament3 = Filament(
        id = 3,
        vendor = "Vendor A",
        color = "Green",
        size = "1kg",
        archived = false,
        createdDate = System.currentTimeMillis(),
        changeDate = System.currentTimeMillis()
    )

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
    fun `filaments are loaded from repository and default filter is ALL`() = runTest {
        // Given
        val filamentList = listOf(filament1, filament2)
        `when`(filamentRepository.getAllFilaments()).thenReturn(flowOf(filamentList))

        // When
        viewModel = FilamentListViewModel(filamentRepository)

        val job = launch { viewModel.filaments.collect {} }
        testScheduler.advanceUntilIdle()

        // Then
        assertEquals(filamentList, viewModel.filaments.value)
        assertEquals(FilamentFilter.ALL, viewModel.filter.value)
        
        job.cancel()
    }

    @Test
    fun `filter ACTIVE only shows non-archived filaments`() = runTest {
        // Given
        val filamentList = listOf(filament1, filament2)
        `when`(filamentRepository.getAllFilaments()).thenReturn(flowOf(filamentList))
        viewModel = FilamentListViewModel(filamentRepository)

        val job = launch { viewModel.filaments.collect {} }
        testScheduler.advanceUntilIdle()

        // When
        viewModel.setFilter(FilamentFilter.ACTIVE)
        testScheduler.advanceUntilIdle()

        // Then
        val expected = listOf(filament1)
        assertEquals(expected, viewModel.filaments.value)

        job.cancel()
    }

    @Test
    fun `filter ARCHIVED only shows archived filaments`() = runTest {
        // Given
        val filamentList = listOf(filament1, filament2)
        `when`(filamentRepository.getAllFilaments()).thenReturn(flowOf(filamentList))
        viewModel = FilamentListViewModel(filamentRepository)

        val job = launch { viewModel.filaments.collect {} }
        testScheduler.advanceUntilIdle()

        // When
        viewModel.setFilter(FilamentFilter.ARCHIVED)
        testScheduler.advanceUntilIdle()

        // Then
        val expected = listOf(filament2)
        assertEquals(expected, viewModel.filaments.value)

        job.cancel()
    }

    @Test
    fun `search filters by vendor`() = runTest {
        // Given
        val filamentList = listOf(filament1, filament2)
        `when`(filamentRepository.getAllFilaments()).thenReturn(flowOf(filamentList))
        viewModel = FilamentListViewModel(filamentRepository)

        val job = launch { viewModel.filaments.collect {} }
        testScheduler.advanceUntilIdle()

        // When
        viewModel.setSearchQuery("Vendor B")
        testScheduler.advanceUntilIdle()

        // Then
        val expected = listOf(filament2)
        assertEquals(expected, viewModel.filaments.value)

        job.cancel()
    }

    @Test
    fun `search filters by color`() = runTest {
        // Given
        val filamentList = listOf(filament1, filament2)
        `when`(filamentRepository.getAllFilaments()).thenReturn(flowOf(filamentList))
        viewModel = FilamentListViewModel(filamentRepository)

        val job = launch { viewModel.filaments.collect {} }
        testScheduler.advanceUntilIdle()

        // When
        viewModel.setSearchQuery("Red")
        testScheduler.advanceUntilIdle()

        // Then
        val expected = listOf(filament1)
        assertEquals(expected, viewModel.filaments.value)

        job.cancel()
    }

    @Test
    fun `showFilters is true when 3 or more filaments exist`() = runTest {
        // Given
        val filamentList = listOf(filament1, filament2, filament3)
        `when`(filamentRepository.getAllFilaments()).thenReturn(flowOf(filamentList))
        viewModel = FilamentListViewModel(filamentRepository)

        val job = launch { viewModel.showFilters.collect {} }
        testScheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.showFilters.value)

        job.cancel()
    }

    @Test
    fun `showFilters is false when less than 3 filaments exist`() = runTest {
        // Given
        val filamentList = listOf(filament1, filament2)
        `when`(filamentRepository.getAllFilaments()).thenReturn(flowOf(filamentList))
        viewModel = FilamentListViewModel(filamentRepository)

        val job = launch { viewModel.showFilters.collect {} }
        testScheduler.advanceUntilIdle()

        // Then
        assertFalse(viewModel.showFilters.value)

        job.cancel()
    }

    @Test
    fun `filter visibility updates dynamically as filaments are added`() = runTest {
        // Given
        val dbFlow = MutableStateFlow<List<Filament>>(emptyList())
        `when`(filamentRepository.getAllFilaments()).thenReturn(dbFlow)

        viewModel = FilamentListViewModel(filamentRepository)

        // Start collecting flows
        val filamentsJob = launch { viewModel.filaments.collect {} }
        val showFiltersJob = launch { viewModel.showFilters.collect {} }
        testScheduler.advanceUntilIdle()

        // 1. Initial state: Empty
        assertEquals(emptyList<Filament>(), viewModel.filaments.value)
        assertFalse("Filters should be hidden for 0 items", viewModel.showFilters.value)

        // 2. Add 2 filaments
        dbFlow.value = listOf(filament1, filament2)
        testScheduler.advanceUntilIdle()

        assertEquals(2, viewModel.filaments.value.size)
        assertFalse("Filters should be hidden for 2 items", viewModel.showFilters.value)

        // 3. Add 3rd filament
        dbFlow.value = listOf(filament1, filament2, filament3)
        testScheduler.advanceUntilIdle()

        assertEquals(3, viewModel.filaments.value.size)
        assertTrue("Filters should be visible for 3 items", viewModel.showFilters.value)

        // Cleanup
        filamentsJob.cancel()
        showFiltersJob.cancel()
    }
}
