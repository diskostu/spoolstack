package de.diskostu.spoolstack.ui.filament.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.data.FilamentRepository
import de.diskostu.spoolstack.data.SettingsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFilamentViewModel @Inject constructor(
    private val filamentRepository: FilamentRepository,
    private val settingsRepository: SettingsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val filamentId: Int = savedStateHandle["filamentId"] ?: -1

    private val _savedFilamentId = MutableSharedFlow<Long>()
    val savedFilamentId = _savedFilamentId.asSharedFlow()

    private val _vendors = MutableStateFlow<List<String>>(emptyList())
    val vendors: StateFlow<List<String>> = _vendors.asStateFlow()

    private val _filamentState = MutableStateFlow<Filament?>(null)
    val filamentState: StateFlow<Filament?> = _filamentState.asStateFlow()

    val defaultFilamentSize: StateFlow<Int> = settingsRepository.defaultFilamentSize
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1000)

    init {
        loadVendors()
        if (filamentId != -1) {
            loadFilament(id = filamentId)
        }
    }

    private fun loadVendors() {
        viewModelScope.launch {
            _vendors.value = filamentRepository.getDistinctVendors()
        }
    }

    private fun loadFilament(id: Int) {
        viewModelScope.launch {
            _filamentState.value = filamentRepository.getFilamentById(id)
        }
    }

    fun save(
        vendor: String,
        color: String,
        currentWeight: Int,
        totalWeight: Int,
        spoolWeight: Int?,
        boughtAt: String?,
        boughtDate: Long?,
        price: Double?,
        deleted: Boolean = false
    ) {
        viewModelScope.launch {
            if (filamentId != -1) {
                // Update existing
                val currentFilament = _filamentState.value
                if (currentFilament != null) {
                    val updatedFilament = currentFilament.copy(
                        vendor = vendor,
                        color = color,
                        currentWeight = currentWeight,
                        totalWeight = totalWeight,
                        spoolWeight = spoolWeight,
                        boughtAt = boughtAt,
                        boughtDate = boughtDate,
                        price = price,
                        deleted = deleted,
                        changeDate = System.currentTimeMillis()
                    )
                    filamentRepository.update(updatedFilament)
                    _savedFilamentId.emit(updatedFilament.id.toLong())
                }
            } else {
                // Create new
                val newId = filamentRepository.insert(
                    Filament(
                        vendor = vendor,
                        color = color,
                        currentWeight = currentWeight,
                        totalWeight = totalWeight,
                        spoolWeight = spoolWeight,
                        boughtAt = boughtAt,
                        boughtDate = boughtDate,
                        price = price,
                        deleted = deleted,
                        createdDate = System.currentTimeMillis(),
                        changeDate = System.currentTimeMillis()
                    )
                )
                _savedFilamentId.emit(newId)
            }
        }
    }
}
