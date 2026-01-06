package de.diskostu.spoolstack.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prints",
    foreignKeys = [
        ForeignKey(
            entity = Filament::class,
            parentColumns = ["id"],
            childColumns = ["filamentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["filamentId"], name = "index_prints_filamentId")
    ]
)
data class Print(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val filamentId: Int,
    val amountUsed: Double,
    val url: String? = null,
    val comment: String? = null,
    val printDate: Long = System.currentTimeMillis()
)
