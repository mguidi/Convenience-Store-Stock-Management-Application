package com.convenience.store.suppliers.data.repositories

import com.convenience.store.suppliers.domain.entities.Supplier
import com.convenience.store.suppliers.domain.repositories.SupplierRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject

class SupplierRepositoryMock @Inject constructor() : SupplierRepository {

    private val _suppliers = listOf(
        Supplier(UUID.randomUUID(), "Global Foods Inc."),
        Supplier(UUID.randomUUID(), "Local Dairy Farm"),
        Supplier(UUID.randomUUID(), "Soda Distributing Co.")
    )

    override fun getSuppliers(): Flow<List<Supplier>> {
        return flowOf(_suppliers)
    }

    override fun getSupplierById(id: UUID): Flow<Supplier?> {
        return flowOf(_suppliers.find { it.id == id })
    }
}