package de.diskostu.spoolstack.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.data.FilamentRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFilamentViewModel @Inject constructor(
    private val filamentRepository: FilamentRepository
) : ViewModel() {

    fun save(vendor: String, color: String, size: String) {
        viewModelScope.launch {
            filamentRepository.insert(Filament(vendor = vendor, color = color, size = size))
        }
    }
}