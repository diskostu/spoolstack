package de.diskostu.spoolstack.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.diskostu.spoolstack.InitialColors
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.data.FilamentDao
import de.diskostu.spoolstack.data.PrintDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val filamentDao: FilamentDao,
    private val printDao: PrintDao
) : ViewModel() {

    val filamentCount: StateFlow<Int> = filamentDao.getCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val printCount: StateFlow<Int> = printDao.getCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun clearAllFilaments(onCompletion: () -> Unit) {
        viewModelScope.launch {
            filamentDao.deleteAll()
            onCompletion()
        }
    }

    fun clearAllPrints(onCompletion: () -> Unit) {
        viewModelScope.launch {
            printDao.deleteAll()
            onCompletion()
        }
    }

    fun addSampleFilaments(onCompletion: () -> Unit) {
        viewModelScope.launch {
            val vendors = listOf("Prusa", "Bambu Lab", "Extrudr", "Sunlu", "Esun")
            val sizes = listOf(1000, 750, 500, 250)
            repeat(20) {
                val filament = Filament(
                    vendor = vendors.random(),
                    colorHex = InitialColors.initialColors.keys.random(),
                    currentWeight = sizes.random(),
                    totalWeight = 1000
                )
                filamentDao.insert(filament)
            }
            onCompletion()
        }
    }
}
