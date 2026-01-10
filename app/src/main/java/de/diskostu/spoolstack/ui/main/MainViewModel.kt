package de.diskostu.spoolstack.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.diskostu.spoolstack.data.FilamentDao
import de.diskostu.spoolstack.data.PrintDao
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val filamentDao: FilamentDao,
    private val printDao: PrintDao
) : ViewModel() {

    fun getFilamentCount(callback: (Int) -> Unit) {
        viewModelScope.launch {
            val count = filamentDao.getCount()
            callback(count)
        }
    }

    fun getPrintCount(callback: (Int) -> Unit) {
        viewModelScope.launch {
            val count = printDao.getCount()
            callback(count)
        }
    }

    fun clearAllFilaments(onCompletion: () -> Unit) {
        viewModelScope.launch {
            filamentDao.deleteAll()
            onCompletion()
        }
    }
}
