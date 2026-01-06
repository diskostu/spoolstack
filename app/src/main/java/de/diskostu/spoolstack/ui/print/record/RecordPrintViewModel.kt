package de.diskostu.spoolstack.ui.print.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.data.FilamentRepository
import de.diskostu.spoolstack.data.Print
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class RecordPrintViewModel @Inject constructor(
    private val filamentRepository: FilamentRepository
) : ViewModel() {

    private val _filaments = MutableStateFlow<List<Filament>>(emptyList())
    val filaments: StateFlow<List<Filament>> = _filaments.asStateFlow()

    private val _printSaved = MutableSharedFlow<Unit>()
    val printSaved = _printSaved.asSharedFlow()

    init {
        loadFilaments()
    }

    private fun loadFilaments() {
        viewModelScope.launch {
            filamentRepository.getAllFilaments().collect {
                _filaments.value = it
            }
        }
    }

    fun savePrint(
        filament: Filament,
        amountUsed: Double,
        url: String?,
        comment: String?
    ) {
        viewModelScope.launch {
            // Save the print record
            filamentRepository.insertPrint(
                Print(
                    filamentId = filament.id,
                    amountUsed = amountUsed,
                    url = url,
                    comment = comment
                )
            )

            // Update filament size
            val currentSizeStr = filament.size
            val currentSize = parseSize(currentSizeStr)
            val newSize = currentSize - amountUsed
            val newSizeStr = "${newSize.roundToInt()}g"

            val updatedFilament = filament.copy(
                size = newSizeStr,
                changeDate = System.currentTimeMillis()
            )
            filamentRepository.update(updatedFilament)

            _printSaved.emit(Unit)
        }
    }

    private fun parseSize(sizeStr: String): Double {
        // Remove "g" or "kg" and parse
        // Assuming format like "500g" or "1kg"
        // Based on AddFilamentScreen logic:
        // "1kg" -> 1000.0
        // "500g" -> 500.0

        // This simple parsing might need to be more robust if formats change
        val lower = sizeStr.lowercase()
        return if (lower.endsWith("kg")) {
            lower.replace("kg", "").toDoubleOrNull()?.times(1000) ?: 0.0
        } else if (lower.endsWith("g")) {
            lower.replace("g", "").toDoubleOrNull() ?: 0.0
        } else {
            0.0
        }
    }
}
