package de.diskostu.spoolstack.data

import javax.inject.Inject

class FilamentRepository @Inject constructor(
    private val filamentDao: FilamentDao
) {

    suspend fun insert(filament: Filament) {
        filamentDao.insert(filament)
    }
}