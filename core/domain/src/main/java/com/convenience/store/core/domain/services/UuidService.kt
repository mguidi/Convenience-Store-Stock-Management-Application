package com.convenience.store.core.domain.services

import java.util.UUID

interface UuidService {

    fun createRandomUuid(): UUID

    fun createSortableUuid(): UUID
}
