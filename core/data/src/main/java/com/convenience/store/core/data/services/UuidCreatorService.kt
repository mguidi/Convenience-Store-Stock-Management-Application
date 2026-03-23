package com.convenience.store.core.data.services

import com.convenience.store.core.domain.services.UuidService
import com.github.f4b6a3.uuid.UuidCreator
import java.util.UUID
import javax.inject.Inject

class UuidCreatorService @Inject constructor()
    : UuidService {

    override fun createRandomUuid(): UUID {
        return UuidCreator.getRandomBased()
    }

    override fun createSortableUuid(): UUID {
        return UuidCreator.getTimeOrderedEpoch()
    }
}