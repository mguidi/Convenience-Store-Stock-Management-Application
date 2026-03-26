package com.convenience.store.suppliers.data.repositories

import com.convenience.store.suppliers.domain.entities.Supplier
import com.convenience.store.suppliers.domain.repositories.SupplierRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject

class SupplierRepositoryMock @Inject constructor() : SupplierRepository {

    private val _suppliers = listOf(
        Supplier(UUID.fromString("019d2616-21ee-78b4-a43c-fef07b5ff7ab"), "Global Foods Inc."),
        Supplier(UUID.fromString("019d2616-21ee-78f9-a43d-976eddb2f099"), "Local Dairy Farm"),
        Supplier(UUID.fromString("019d2616-21ee-7943-a43e-b38a2961adb6"), "Soda Distributing Co.")
    )

    override fun getSuppliers(): Flow<List<Supplier>> {
        return flowOf(_suppliers)
    }

    override fun getSupplierById(id: UUID): Flow<Supplier?> {
        return flowOf(_suppliers.find { it.id == id })
    }
}