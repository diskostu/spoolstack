package de.diskostu.spoolstack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Filament(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val vendor: String,
    val colorHex: String,
    val currentWeight: Int,
    val totalWeight: Int = 1000,
    val spoolWeight: Int? = null,
    val boughtAt: String? = null,
    val boughtDate: Long? = null,
    val price: Double? = null,
    val deleted: Boolean = false,
    val createdDate: Long = System.currentTimeMillis(),
    val changeDate: Long = System.currentTimeMillis()
)

data class ColorWithName(
    val colorHex: String,
    val name: String? = null
)
