package de.diskostu.spoolstack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Filament(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val vendor: String,
    val color: String,
    val size: String,
    val boughtAt: String? = null,
    val boughtDate: Long? = null,
    val price: Double? = null,
    val createdDate: Long = System.currentTimeMillis(),
    val changeDate: Long = System.currentTimeMillis()
)
