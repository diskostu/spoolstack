package de.diskostu.spoolstack.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.data.FilamentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class FilterType {
    ALL, ACTIVE, ARCHIVED
}

@HiltViewModel
class FilamentListViewModel @Inject constructor(
    private val filamentRepository: FilamentRepository
) : ViewModel() {

    private val _filaments = MutableStateFlow<List<Filament>>(emptyList())
    val filaments: StateFlow<List<Filament>> = _filaments.asStateFlow()

    private val _filterState = MutableStateFlow(FilterType.ALL)
    val filterState: StateFlow<FilterType> = _filterState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    private val _totalFilaments = MutableStateFlow(0)
    val totalFilaments: StateFlow<Int> = _totalFilaments.asStateFlow()

    private var pendingList: List<Filament>? = null
    private var isUiVisible = false
    private var animationJob: Job? = null

    init {
        viewModelScope.launch {
            val allFilamentsFlow = filamentRepository.getAllFilaments()

            allFilamentsFlow.collect { allFilaments ->
                _totalFilaments.value = allFilaments.size
            }

            combine(
                allFilamentsFlow,
                _filterState,
                _searchQuery,
                _isSearchActive
            ) { allFilaments, filter, query, isSearchActive ->
                if (isSearchActive && query.isNotBlank()) {
                    allFilaments.filter {
                        it.vendor.contains(query, ignoreCase = true) ||
                                it.color.contains(query, ignoreCase = true) ||
                                it.boughtAt?.contains(query, ignoreCase = true) == true
                    }
                } else {
                    when (filter) {
                        FilterType.ACTIVE -> allFilaments.filter { !it.archived }
                        FilterType.ARCHIVED -> allFilaments.filter { it.archived }
                        FilterType.ALL -> allFilaments
                    }
                }
            }.collect { newList ->
                val oldList = _filaments.value
                val oldIds = oldList.map { it.id }.toSet()
                val newIds = newList.map { it.id }.toSet()

                // Check if this is an update (reorder) of existing items
                if (oldList.isNotEmpty() && oldIds == newIds && oldList != newList) {
                    // Create intermediate list: New Data in Old Order
                    val newMap = newList.associateBy { it.id }
                    val intermediateList = oldList.mapNotNull { newMap[it.id] }

                    _filaments.value = intermediateList
                    pendingList = newList

                    // Cancel any previous pending animation
                    animationJob?.cancel()
                    checkPending()
                } else {
                    // Initial load, Add, Delete, or no change
                    _filaments.value = newList
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
                _filaments.value = pending
            }
        }
    }

    fun toggleArchived(filament: Filament) {
        viewModelScope.launch {
            filamentRepository.update(filament.copy(archived = !filament.archived))
        }
    }

    fun setFilter(filterType: FilterType) {
        _filterState.value = filterType
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchActive(isActive: Boolean) {
        _isSearchActive.value = isActive
        if (!isActive) {
            setSearchQuery("")
        }
    }
}
