package de.diskostu.spoolstack.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.data.FilamentRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFilamentViewModel @Inject constructor(
    private val filamentRepository: FilamentRepository
) : ViewModel() {

    private val _savedFilamentId = MutableSharedFlow<Long>()
    val savedFilamentId = _savedFilamentId.asSharedFlow()

    private val _vendors = MutableStateFlow<List<String>>(emptyList())
    val vendors: StateFlow<List<String>> = _vendors.asStateFlow()

    init {
        loadVendors()
    }

    private fun loadVendors() {
        viewModelScope.launch {
            _vendors.value = filamentRepository.getDistinctVendors()
        }
    }

    fun save(vendor: String, color: String, size: String) {
        viewModelScope.launch {
            val newId = filamentRepository.insert(Filament(vendor = vendor, color = color, size = size))
            _savedFilamentId.emit(newId)
        }
    }
}