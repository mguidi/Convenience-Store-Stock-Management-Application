package com.convenience.store.suppliers.domain.usecases

import com.convenience.store.suppliers.domain.entities.Supplier
import com.convenience.store.suppliers.domain.repositories.SupplierRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface SupplierGetUseCase {

    operator fun invoke(supplierId: UUID): Flow<Supplier?>

}

class SupplierGetUseCaseImpl(
    private val supplierRepository: SupplierRepository
) : SupplierGetUseCase {
    override fun invoke(supplierId: UUID): Flow<Supplier?> {
        return supplierRepository.getSupplierById(supplierId)
    }
}