package de.diskostu.spoolstack.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FilamentDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var filamentDao: FilamentDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        filamentDao = database.filamentDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun getDistinctVendors_shouldSortCaseInsensitive() = runBlocking {
        // Arrange
        val filaments = listOf(
            Filament(vendor = "b_vendor", colorHex = "#FFFFFF", currentWeight = 1000),
            Filament(vendor = "A_vendor", colorHex = "#FFFFF0", currentWeight = 1000),
            Filament(vendor = "c_vendor", colorHex = "#FFFFFA", currentWeight = 1000)
        )
        filaments.forEach { filamentDao.insert(it) }

        // Act
        val vendors = filamentDao.getDistinctVendors()

        // Assert
        assertEquals(listOf("A_vendor", "b_vendor", "c_vendor"), vendors)
    }

    @Test
    fun getDistinctVendors_shouldReturnDistinctValues() = runBlocking {
        // Arrange
        val filaments = listOf(
            Filament(vendor = "VendorA", colorHex = "#FFFFFF", currentWeight = 1000),
            Filament(vendor = "VendorA", colorHex = "#FFFFF0", currentWeight = 1000),
            Filament(vendor = "VendorB", colorHex = "#FFFFFA", currentWeight = 1000)
        )
        filaments.forEach { filamentDao.insert(it) }

        // Act
        val vendors = filamentDao.getDistinctVendors()

        // Assert
        assertEquals(2, vendors.size)
        assertEquals(listOf("VendorA", "VendorB"), vendors)
    }
}
