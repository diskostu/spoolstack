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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilamentListViewModel @Inject constructor(
    filamentRepository: FilamentRepository
) : ViewModel() {

    private val _filaments = MutableStateFlow<List<Filament>>(emptyList())
    val filaments: StateFlow<List<Filament>> = _filaments.asStateFlow()

    private var pendingList: List<Filament>? = null
    private var isUiVisible = false
    private var animationJob: Job? = null

    init {
        viewModelScope.launch {
            filamentRepository.getAllFilaments().collect { newList ->
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
}
