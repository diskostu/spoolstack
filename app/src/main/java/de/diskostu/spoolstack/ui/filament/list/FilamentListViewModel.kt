package de.diskostu.spoolstack.ui.filament.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.data.FilamentRepository
import de.diskostu.spoolstack.data.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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

data class FilamentUiModel(
    val filament: Filament,
    val colorName: String
)

@HiltViewModel
class FilamentListViewModel @Inject constructor(
    private val filamentRepository: FilamentRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _sourceFilaments = MutableStateFlow<List<FilamentUiModel>>(emptyList())

    private val _filter = MutableStateFlow(FilamentFilter.ACTIVE)
    val filter = _filter.asStateFlow()

    private val _sort = MutableStateFlow(FilamentSort.LAST_MODIFIED)
    val sort = _sort.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.DESCENDING)
    val sortOrder = _sortOrder.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val filaments: StateFlow<List<FilamentUiModel>> = combine(
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
            FilamentFilter.ACTIVE -> result.filter { !it.filament.deleted }
            FilamentFilter.DELETED -> result.filter { it.filament.deleted }
        }
        // Apply Search
        if (query.isNotEmpty()) {
            val q = query.lowercase()
            result = result.filter {
                it.filament.vendor.lowercase().contains(q) ||
                        it.colorName.lowercase().contains(q) ||
                        (it.filament.boughtAt?.lowercase()?.contains(q) == true)
            }
        }
        // Apply Sort
        result = when (sort) {
            FilamentSort.VENDOR -> {
                val comparator = compareBy<FilamentUiModel> { it.filament.vendor.lowercase() }
                    .thenBy { it.colorName.lowercase() }
                if (sortOrder == SortOrder.ASCENDING) result.sortedWith(comparator)
                else result.sortedWith(comparator.reversed())
            }

            FilamentSort.COLOR -> {
                val comparator = compareBy<FilamentUiModel> { it.colorName.lowercase() }
                    .thenBy { it.filament.vendor.lowercase() }
                if (sortOrder == SortOrder.ASCENDING) result.sortedWith(comparator)
                else result.sortedWith(comparator.reversed())
            }

            FilamentSort.LAST_MODIFIED -> {
                if (sortOrder == SortOrder.ASCENDING) result.sortedBy { it.filament.changeDate }
                else result.sortedByDescending { it.filament.changeDate }
            }

            FilamentSort.REMAINING_AMOUNT -> {
                if (sortOrder == SortOrder.ASCENDING) result.sortedBy { it.filament.currentWeight }
                else result.sortedByDescending { it.filament.currentWeight }
            }
        }

        result
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )


    private var pendingList: List<FilamentUiModel>? = null
    private var isUiVisible = false
    private var animationJob: Job? = null

    init {
        loadSettings()
        viewModelScope.launch {
            filamentRepository.getAllFilaments().collect { newList ->
                val uiModels = newList.map {
                    FilamentUiModel(it, filamentRepository.getColorName(it.colorHex))
                }
                
                val oldList = _sourceFilaments.value
                val oldIds = oldList.map { it.filament.id }.toSet()
                val newIds = uiModels.map { it.filament.id }.toSet()

                if (oldList.isNotEmpty() && oldIds == newIds && oldList != uiModels) {
                    val newMap = uiModels.associateBy { it.filament.id }
                    val intermediateList = oldList.mapNotNull { newMap[it.filament.id] }

                    _sourceFilaments.value = intermediateList
                    pendingList = uiModels

                    animationJob?.cancel()
                    checkPending()
                } else {
                    _sourceFilaments.value = uiModels
                    pendingList = null
                }
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val savedSort = settingsRepository.filamentSort.first()
            val savedOrder = settingsRepository.filamentSortOrder.first()

            if (savedSort != null) {
                try {
                    _sort.value = FilamentSort.valueOf(savedSort)
                } catch (e: Exception) {
                }
            }
            if (savedOrder != null) {
                try {
                    _sortOrder.value = SortOrder.valueOf(savedOrder)
                } catch (e: Exception) {
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
            pendingList = null

            animationJob = viewModelScope.launch {
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
        val newOrder = if (_sort.value == newSort) {
            if (_sortOrder.value == SortOrder.ASCENDING) {
                SortOrder.DESCENDING
            } else {
                SortOrder.ASCENDING
            }
        } else {
            SortOrder.ASCENDING
        }

        _sort.value = newSort
        _sortOrder.value = newOrder

        viewModelScope.launch {
            settingsRepository.setFilamentSort(newSort.name)
            settingsRepository.setFilamentSortOrder(newOrder.name)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
