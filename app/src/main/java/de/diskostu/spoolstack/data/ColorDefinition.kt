package de.diskostu.spoolstack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ColorDefinition(
    @PrimaryKey
    val hex: String
)

@Entity(primaryKeys = ["hex", "language"])
data class ColorName(
    val hex: String,
    val language: String,
    val name: String
)
