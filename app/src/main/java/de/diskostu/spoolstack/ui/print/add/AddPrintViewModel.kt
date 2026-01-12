package de.diskostu.spoolstack.ui.print.add

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
class AddPrintViewModel @Inject constructor(
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
            filamentRepository.getActiveFilaments().collect {
                _filaments.value = it
            }
        }
    }

    suspend fun getColorName(hex: String): String {
        return filamentRepository.getColorName(hex)
    }

    fun savePrint(
        name: String,
        filament: Filament,
        amountUsed: Double,
        url: String?,
        comment: String?
    ) {
        viewModelScope.launch {
            // Save the print record
            filamentRepository.insertPrint(
                Print(
                    name = name,
                    filamentId = filament.id,
                    amountUsed = amountUsed,
                    url = url,
                    comment = comment
                )
            )

            // Update filament weight
            val currentWeight = filament.currentWeight
            val newWeight = (currentWeight - amountUsed).roundToInt()

            val updatedFilament = filament.copy(
                currentWeight = newWeight,
                changeDate = System.currentTimeMillis()
            )
            filamentRepository.update(updatedFilament)

            _printSaved.emit(Unit)
        }
    }
}
