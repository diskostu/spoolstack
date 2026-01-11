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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class FilamentFilter {
    ALL, ACTIVE, DELETED
}

enum class FilamentSort {
    VENDOR, COLOR, LAST_MODIFIED, REMAINING_AMOUNT
}

enum class SortOrder {
    ASCENDING, DESCENDING
}

@HiltViewModel
class FilamentListViewModel @Inject constructor(
    private val filamentRepository: FilamentRepository
) : ViewModel() {

    // Internal source list (raw data from DB, potentially animated)
    private val _sourceFilaments = MutableStateFlow<List<Filament>>(emptyList())

    private val _filter = MutableStateFlow(FilamentFilter.ACTIVE)
    val filter = _filter.asStateFlow()

    private val _sort = MutableStateFlow(FilamentSort.VENDOR)
    val sort = _sort.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.ASCENDING)
    val sortOrder = _sortOrder.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Exposed list for UI, applying filters, search and sort
    val filaments: StateFlow<List<Filament>> = combine(
        _sourceFilaments,
        _filter,
        _sort,
        _sortOrder,
        _searchQuery
    ) { list, filter, sort, sortOrder, query ->
        var result = list
        // Apply Filter
        result = when (filter) {
            FilamentFilter.ALL -> result
            FilamentFilter.ACTIVE -> result.filter { !it.deleted }
            FilamentFilter.DELETED -> result.filter { it.deleted }
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
            FilamentSort.VENDOR -> {
                val comparator = compareBy<Filament> { it.vendor.lowercase() }
                    .thenBy { it.color.lowercase() }
                if (sortOrder == SortOrder.ASCENDING) result.sortedWith(comparator)
                else result.sortedWith(comparator.reversed())
            }

            FilamentSort.COLOR -> {
                val comparator = compareBy<Filament> { it.color.lowercase() }
                    .thenBy { it.vendor.lowercase() }
                if (sortOrder == SortOrder.ASCENDING) result.sortedWith(comparator)
                else result.sortedWith(comparator.reversed())
            }

            FilamentSort.LAST_MODIFIED -> {
                if (sortOrder == SortOrder.ASCENDING) result.sortedBy { it.changeDate }
                else result.sortedByDescending { it.changeDate }
            }

            FilamentSort.REMAINING_AMOUNT -> {
                if (sortOrder == SortOrder.ASCENDING) result.sortedBy { it.currentWeight }
                else result.sortedByDescending { it.currentWeight }
            }
        }

        result
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
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

    fun toggleDeleted(filament: Filament) {
        viewModelScope.launch {
            filamentRepository.update(filament.copy(deleted = !filament.deleted))
        }
    }

    fun setFilter(filter: FilamentFilter) {
        _filter.value = filter
    }

    fun setSort(newSort: FilamentSort) {
        if (_sort.value == newSort) {
            _sortOrder.value = if (_sortOrder.value == SortOrder.ASCENDING) {
                SortOrder.DESCENDING
            } else {
                SortOrder.ASCENDING
            }
        } else {
            _sort.value = newSort
            _sortOrder.value = SortOrder.ASCENDING
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
