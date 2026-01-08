package de.diskostu.spoolstack.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FilamentRepository @Inject constructor(
    private val filamentDao: FilamentDao,
    private val printDao: PrintDao
) {

    suspend fun insert(filament: Filament): Long {
        return filamentDao.insert(filament)
    }

    suspend fun update(filament: Filament) {
        filamentDao.update(filament)
    }

    suspend fun getFilamentById(id: Int): Filament? {
        return filamentDao.getFilamentById(id)
    }

    suspend fun getDistinctVendors(): List<String> {
        return filamentDao.getDistinctVendors()
    }

    fun getAllFilaments(): Flow<List<Filament>> {
        return filamentDao.getAllFilaments()
    }

    fun getActiveFilaments(): Flow<List<Filament>> {
        return filamentDao.getActiveFilaments()
    }

    suspend fun insertPrint(print: Print): Long {
        return printDao.insert(print)
    }

    fun getAllPrints(): Flow<List<Print>> {
        return printDao.getAllPrints()
    }
}
