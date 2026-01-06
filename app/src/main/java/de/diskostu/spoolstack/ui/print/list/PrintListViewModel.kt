package de.diskostu.spoolstack.ui.print.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.data.FilamentRepository
import de.diskostu.spoolstack.data.Print
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrintListViewModel @Inject constructor(
    private val filamentRepository: FilamentRepository
) : ViewModel() {

    private val _prints = MutableStateFlow<List<Print>>(emptyList())
    val prints: StateFlow<List<Print>> = _prints.asStateFlow()

    private val _filaments = MutableStateFlow<Map<Int, Filament>>(emptyMap())
    val filaments: StateFlow<Map<Int, Filament>> = _filaments.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Load all filaments to map IDs to details
            filamentRepository.getAllFilaments().collect { filamentsList ->
                _filaments.value = filamentsList.associateBy { it.id }
            }
        }

        viewModelScope.launch {
            filamentRepository.getAllPrints().collect {
                _prints.value = it
            }
        }
    }
}
