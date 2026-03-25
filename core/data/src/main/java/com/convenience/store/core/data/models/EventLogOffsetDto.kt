package com.convenience.store.core.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Represents an DTO in the local event log offset table.
 * This entity is used to store consumer offsets for events consumption.
 *
 * @property consumer The consumer name.
 * @property lastProcessedId The ID of the last event that was successfully handled.
 */
@Entity(tableName = "event_log_offset")
data class EventLogOffsetDto(
    @PrimaryKey val consumer: String,

    val lastProcessedId: UUID,

    /**
     * The exact time when the event occurred, in milliseconds.
     * Defaults to the current system time.
     */
    val timestamp: Long = System.currentTimeMillis()
)