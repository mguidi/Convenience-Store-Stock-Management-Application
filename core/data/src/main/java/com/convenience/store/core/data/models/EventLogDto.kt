package com.convenience.store.core.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Represents an DTO in the local event log table.
 * This entity is used to track changes or actions that need to be synchronized
 * or processed by other components of the system.
 */
@Entity(tableName = "event_log")
data class EventLogDto(
    /**
     * Unique identifier for the event log entry, need to be sortable
     */
    @PrimaryKey val id: UUID,

    /**
     * The category or type of the event (e.g., "PRODUCT_CREATED", "STOCK_UPDATED").
     */
    val type: String,

    /**
     * A JSON string containing additional data or details related to the event.
     */
    val payload: String,

    /**
     * The exact time when the event occurred, in milliseconds.
     * Defaults to the current system time.
     */
    val timestamp: Long = System.currentTimeMillis()
)