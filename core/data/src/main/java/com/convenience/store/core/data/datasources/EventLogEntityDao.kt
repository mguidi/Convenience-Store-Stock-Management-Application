package com.convenience.store.core.data.datasources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.convenience.store.core.data.models.EventLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the event_log table.
 * This interface defines the methods for interacting with the stored event logs.
 */
@Dao
interface EventLogEntityDao {

    /**
     * Inserts a new event log entry into the database.
     *
     * @param event The event entity to be saved.
     */
    @Insert
    suspend fun insertEvent(event: EventLogEntity)

    /**
     * Retrieves a reactive stream of events that have occurred after a given ID.
     * Results are sorted by ID in ascending order to facilitate sequential processing.
     *
     * @param lastProcessedId The ID of the last event that was successfully handled.
     * @return A Flow emitting lists of pending EventLogEntity entries.
     */
    @Query("SELECT * FROM event_log WHERE id > :lastProcessedId ORDER BY id ASC")
    fun getEventsAfter(lastProcessedId: Long): Flow<List<EventLogEntity>>

    /**
     * Deletes events from the log that have already been processed up to a specified ID.
     * Used for database maintenance to remove obsolete records.
     *
     * @param minProcessedId The threshold ID; all events with an ID less than or equal to this will be removed.
     */
    @Query("DELETE FROM event_log WHERE id <= :minProcessedId")
    suspend fun deleteProcessedEvents(minProcessedId: Long)
}