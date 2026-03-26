package com.convenience.store.suppliers.domain.usecases

import com.convenience.store.suppliers.domain.entities.Supplier
import com.convenience.store.suppliers.domain.repositories.SupplierRepository
import kotlinx.coroutines.flow.Flow

interface SuppliersGetUseCase {

    operator fun invoke(): Flow<List<Supplier>>

}

class SuppliersGetUseCaseImpl(
    private val supplierRepository: SupplierRepository
) : SuppliersGetUseCase {
    override fun invoke(): Flow<List<Supplier>> {
        return supplierRepository.getSuppliers()
    }
}