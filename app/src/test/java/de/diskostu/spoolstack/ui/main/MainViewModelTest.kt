package de.diskostu.spoolstack.ui.main

import de.diskostu.spoolstack.data.FilamentDao
import de.diskostu.spoolstack.data.PrintDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @Mock
    private lateinit var filamentDao: FilamentDao

    @Mock
    private lateinit var printDao: PrintDao

    private lateinit var viewModel: MainViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        `when`(filamentDao.getCountFlow()).thenReturn(flowOf(0))
        `when`(printDao.getCountFlow()).thenReturn(flowOf(0))

        viewModel = MainViewModel(filamentDao, printDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addSampleFilaments inserts 20 filaments`() = runTest {
        // When
        var completed = false
        viewModel.addSampleFilaments {
            completed = true
        }
        advanceUntilIdle()

        // Then
        verify(filamentDao, times(20)).insert(any())
        assert(completed)
    }

    @Test
    fun `clearAllFilaments calls dao deleteAll`() = runTest {
        // When
        var completed = false
        viewModel.clearAllFilaments {
            completed = true
        }
        advanceUntilIdle()

        // Then
        verify(filamentDao).deleteAll()
        assert(completed)
    }

    @Test
    fun `clearAllPrints calls dao deleteAll`() = runTest {
        // When
        var completed = false
        viewModel.clearAllPrints {
            completed = true
        }
        advanceUntilIdle()

        // Then
        verify(printDao).deleteAll()
        assert(completed)
    }
}
