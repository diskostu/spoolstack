package de.diskostu.spoolstack.data

import kotlinx.coroutines.flow.Flow
import java.util.Locale
import javax.inject.Inject

class FilamentRepository @Inject constructor(
    private val filamentDao: FilamentDao,
    private val printDao: PrintDao,
    private val colorDao: ColorDao
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

    suspend fun getFrequentColors(limit: Int): List<ColorWithName> {
        val colors = filamentDao.getFrequentColors(limit)
        return colors.map { ColorWithName(it, getColorName(it)) }
    }

    suspend fun getRecentColors(limit: Int): List<ColorWithName> {
        val colors = filamentDao.getRecentColors(limit)
        return colors.map { ColorWithName(it, getColorName(it)) }
    }

    suspend fun getColorName(hex: String): String {
        val language = Locale.getDefault().language
        return colorDao.getColorName(hex, language)
            ?: colorDao.getColorName(hex, "en")
            ?: hex
    }

    suspend fun initializeColors(colors: Map<String, Map<String, String>>) {
        val definitions = colors.keys.map { ColorDefinition(it) }
        val names = colors.flatMap { (hex, languageMap) ->
            languageMap.map { (lang, name) -> ColorName(hex, lang, name) }
        }
        colorDao.insertColorDefinitions(definitions)
        colorDao.insertColorNames(names)
    }
}
