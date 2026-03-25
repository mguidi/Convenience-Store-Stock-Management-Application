package com.convenience.store.core.data.datasources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.convenience.store.core.data.models.EventLogOffsetDto

/**
 * Data Access Object (DAO) for the event_log_offset table.
 * This interface defines the methods for interacting with the stored event logs offset.
 */
@Dao
interface EventLogOffsetDao {

    /**
     * Inserts a new event log offset entry into the database.
     *
     * @param offset The offset entity to be saved.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(offset: EventLogOffsetDto)


    @Query("SELECT * FROM event_log_offset WHERE consumer = :consumer")
    suspend fun getEventLogOffsetForConsumer(consumer: String): EventLogOffsetDto?

}