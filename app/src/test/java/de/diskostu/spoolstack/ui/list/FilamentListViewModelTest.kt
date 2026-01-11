package de.diskostu.spoolstack.ui.list

import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.data.FilamentRepository
import de.diskostu.spoolstack.ui.filament.list.FilamentFilter
import de.diskostu.spoolstack.ui.filament.list.FilamentListViewModel
import de.diskostu.spoolstack.ui.filament.list.FilamentSort
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

    private val filament1 = Filament(
        id = 1,
        vendor = "Vendor A",
        color = "Red",
        currentWeight = 1000,
        deleted = false,
        createdDate = 1000L,
        changeDate = 1000L
    )

    private val filament2 = Filament(
        id = 2,
        vendor = "Vendor B",
        color = "Blue",
        currentWeight = 500,
        deleted = true,
        createdDate = 2000L,
        changeDate = 2000L
    )

    private val filament3 = Filament(
        id = 3,
        vendor = "Vendor A",
        color = "Green",
        currentWeight = 2000,
        deleted = false,
        createdDate = 3000L,
        changeDate = 3000L
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
    fun `filaments are loaded from repository and default filter is ACTIVE`() = runTest {
        // Given
        val filamentList = listOf(filament1, filament2)
        `when`(filamentRepository.getAllFilaments()).thenReturn(flowOf(filamentList))

        // When
        viewModel = FilamentListViewModel(filamentRepository)

        val job = launch { viewModel.filaments.collect {} }
        testScheduler.advanceUntilIdle()

        // Then
        // Default filter is ACTIVE, so only filament1 (non-deleted)
        assertEquals(listOf(filament1), viewModel.filaments.value)
        assertEquals(FilamentFilter.ACTIVE, viewModel.filter.value)
        
        job.cancel()
    }

    @Test
    fun `filter ACTIVE only shows non-deleted filaments`() = runTest {
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
    fun `filter DELETED only shows deleted filaments`() = runTest {
        // Given
        val filamentList = listOf(filament1, filament2)
        `when`(filamentRepository.getAllFilaments()).thenReturn(flowOf(filamentList))
        viewModel = FilamentListViewModel(filamentRepository)

        val job = launch { viewModel.filaments.collect {} }
        testScheduler.advanceUntilIdle()

        // When
        viewModel.setFilter(FilamentFilter.DELETED)
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
        viewModel.setFilter(FilamentFilter.ALL)
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
        viewModel.setFilter(FilamentFilter.ALL)
        viewModel.setSearchQuery("Red")
        testScheduler.advanceUntilIdle()

        // Then
        val expected = listOf(filament1)
        assertEquals(expected, viewModel.filaments.value)

        job.cancel()
    }

    @Test
    fun `sort by NAME sorts by vendor then color`() = runTest {
        // Given
        val filamentList = listOf(filament1, filament2, filament3)
        `when`(filamentRepository.getAllFilaments()).thenReturn(flowOf(filamentList))
        viewModel = FilamentListViewModel(filamentRepository)

        val job = launch { viewModel.filaments.collect {} }
        testScheduler.advanceUntilIdle()

        // When
        viewModel.setFilter(FilamentFilter.ALL)
        viewModel.setSort(FilamentSort.VENDOR)
        testScheduler.advanceUntilIdle()

        // Then
        // filament1: Vendor A, Red
        // filament3: Vendor A, Green
        // filament2: Vendor B, Blue
        // Expected order: Green then Red (G < R)
        val expected = listOf(filament3, filament1, filament2)
        assertEquals(expected, viewModel.filaments.value)

        job.cancel()
    }

    @Test
    fun `sort by LAST_MODIFIED sorts by changeDate descending`() = runTest {
        // Given
        val filamentList = listOf(filament1, filament2, filament3)
        `when`(filamentRepository.getAllFilaments()).thenReturn(flowOf(filamentList))
        viewModel = FilamentListViewModel(filamentRepository)

        val job = launch { viewModel.filaments.collect {} }
        testScheduler.advanceUntilIdle()

        // When
        viewModel.setFilter(FilamentFilter.ALL)
        viewModel.setSort(FilamentSort.LAST_MODIFIED)
        testScheduler.advanceUntilIdle()

        // Then
        // filament3: 3000
        // filament2: 2000
        // filament1: 1000
        val expected = listOf(filament3, filament2, filament1)
        assertEquals(expected, viewModel.filaments.value)

        job.cancel()
    }

    @Test
    fun `sort by REMAINING_AMOUNT sorts by currentWeight descending`() = runTest {
        // Given
        val filamentList = listOf(filament1, filament2, filament3)
        `when`(filamentRepository.getAllFilaments()).thenReturn(flowOf(filamentList))
        viewModel = FilamentListViewModel(filamentRepository)

        val job = launch { viewModel.filaments.collect {} }
        testScheduler.advanceUntilIdle()

        // When
        viewModel.setFilter(FilamentFilter.ALL)
        viewModel.setSort(FilamentSort.REMAINING_AMOUNT)
        testScheduler.advanceUntilIdle()

        // Then
        // filament3: 2000
        // filament1: 1000
        // filament2: 500
        val expected = listOf(filament3, filament1, filament2)
        assertEquals(expected, viewModel.filaments.value)

        job.cancel()
    }
}
