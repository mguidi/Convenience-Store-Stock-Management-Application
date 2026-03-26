package com.convenience.store.suppliers.domain.repositories

import com.convenience.store.suppliers.domain.entities.Supplier
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface SupplierRepository {

    fun getSuppliers(): Flow<List<Supplier>>

    fun getSupplierById(id: UUID): Flow<Supplier?>

}