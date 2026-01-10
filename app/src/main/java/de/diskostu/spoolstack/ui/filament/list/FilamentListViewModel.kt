package de.diskostu.spoolstack.ui.filament.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.data.FilamentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class FilamentFilter {
    ALL, ACTIVE, ARCHIVED
}

enum class FilamentSort {
    NAME, LAST_MODIFIED, REMAINING_AMOUNT
}

@HiltViewModel
class FilamentListViewModel @Inject constructor(
    private val filamentRepository: FilamentRepository
) : ViewModel() {

    // Internal source list (raw data from DB, potentially animated)
    private val _sourceFilaments = MutableStateFlow<List<Filament>>(emptyList())

    private val _filter = MutableStateFlow(FilamentFilter.ALL)
    val filter = _filter.asStateFlow()

    private val _sort = MutableStateFlow(FilamentSort.NAME)
    val sort = _sort.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Exposed list for UI, applying filters, search and sort
    val filaments: StateFlow<List<Filament>> = combine(
        _sourceFilaments,
        _filter,
        _sort,
        _searchQuery
    ) { list, filter, sort, query ->
        var result = list
        // Apply Filter
        result = when (filter) {
            FilamentFilter.ALL -> result
            FilamentFilter.ACTIVE -> result.filter { !it.archived }
            FilamentFilter.ARCHIVED -> result.filter { it.archived }
        }
        // Apply Search
        if (query.isNotEmpty()) {
            val q = query.lowercase()
            result = result.filter {
                it.vendor.lowercase().contains(q) ||
                        it.color.lowercase().contains(q) ||
                        (it.boughtAt?.lowercase()?.contains(q) == true)
            }
        }
        // Apply Sort
        result = when (sort) {
            FilamentSort.NAME -> result.sortedWith(
                compareBy(
                    { it.vendor.lowercase() },
                    { it.color.lowercase() })
            )

            FilamentSort.LAST_MODIFIED -> result.sortedByDescending { it.changeDate }
            FilamentSort.REMAINING_AMOUNT -> result.sortedByDescending { it.size }
        }

        result
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val showFilters: StateFlow<Boolean> = _sourceFilaments
        .map { it.size >= 3 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private var pendingList: List<Filament>? = null
    private var isUiVisible = false
    private var animationJob: Job? = null

    init {
        viewModelScope.launch {
            filamentRepository.getAllFilaments().collect { newList ->
                val oldList = _sourceFilaments.value
                val oldIds = oldList.map { it.id }.toSet()
                val newIds = newList.map { it.id }.toSet()

                // Check if this is an update (reorder) of existing items
                if (oldList.isNotEmpty() && oldIds == newIds && oldList != newList) {
                    // Create intermediate list: New Data in Old Order
                    val newMap = newList.associateBy { it.id }
                    val intermediateList = oldList.mapNotNull { newMap[it.id] }

                    _sourceFilaments.value = intermediateList
                    pendingList = newList

                    // Cancel any previous pending animation
                    animationJob?.cancel()
                    checkPending()
                } else {
                    // Initial load, Add, Delete, or no change
                    _sourceFilaments.value = newList
                    pendingList = null
                }
            }
        }
    }

    fun onUiStart() {
        isUiVisible = true
        checkPending()
    }

    fun onUiStop() {
        isUiVisible = false
    }

    private fun checkPending() {
        val pending = pendingList
        if (isUiVisible && pending != null) {
            // Consume pending list so we don't trigger again
            pendingList = null

            animationJob = viewModelScope.launch {
                // Give time for the user to see the "old" order with updated data
                delay(500)
                _sourceFilaments.value = pending
            }
        }
    }

    fun toggleArchived(filament: Filament) {
        viewModelScope.launch {
            filamentRepository.update(filament.copy(archived = !filament.archived))
        }
    }

    fun setFilter(filter: FilamentFilter) {
        _filter.value = filter
    }

    fun setSort(sort: FilamentSort) {
        _sort.value = sort
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
