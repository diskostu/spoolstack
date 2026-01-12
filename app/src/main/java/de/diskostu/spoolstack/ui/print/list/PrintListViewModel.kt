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

data class PrintUiModel(
    val print: Print,
    val filament: Filament?,
    val colorName: String
)

@HiltViewModel
class PrintListViewModel @Inject constructor(
    private val filamentRepository: FilamentRepository
) : ViewModel() {

    private val _prints = MutableStateFlow<List<PrintUiModel>>(emptyList())
    val prints: StateFlow<List<PrintUiModel>> = _prints.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            filamentRepository.getAllPrints().collect { printList ->
                val uiModels = printList.map { print ->
                    val filament = filamentRepository.getFilamentById(print.filamentId)
                    val colorName =
                        filament?.let { filamentRepository.getColorName(it.colorHex) } ?: "Unknown"
                    PrintUiModel(print, filament, colorName)
                }
                _prints.value = uiModels
            }
        }
    }
}
