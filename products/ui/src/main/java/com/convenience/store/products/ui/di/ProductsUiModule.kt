package com.convenience.store.products.ui.di

import com.convenience.store.core.domain.services.UuidService
import com.convenience.store.products.domain.repositories.CategoryRepository
import com.convenience.store.products.domain.repositories.ProductRepository
import com.convenience.store.products.domain.services.ProductSyncService
import com.convenience.store.products.domain.usecases.CategoriesGetUseCase
import com.convenience.store.products.domain.usecases.CategoriesGetUseCaseImpl
import com.convenience.store.products.domain.usecases.CategoryGetUseCase
import com.convenience.store.products.domain.usecases.CategoryGetUseCaseImpl
import com.convenience.store.products.domain.usecases.ProductAddUseCaseImpl
import com.convenience.store.products.domain.usecases.ProductCreateUseCase
import com.convenience.store.products.domain.usecases.ProductGetUseCase
import com.convenience.store.products.domain.usecases.ProductGetUseCaseImpl
import com.convenience.store.products.domain.usecases.ProductUpdateUseCase
import com.convenience.store.products.domain.usecases.ProductUpdateUseCaseImpl
import com.convenience.store.products.domain.usecases.ProductsGetByCategoryUseCase
import com.convenience.store.products.domain.usecases.ProductsGetByCategoryUseCaseImpl
import com.convenience.store.products.domain.usecases.ProductsGetUseCase
import com.convenience.store.products.domain.usecases.ProductsGetUseCaseImpl
import com.convenience.store.suppliers.domain.repositories.SupplierRepository
import com.convenience.store.suppliers.domain.usecases.SupplierGetUseCase
import com.convenience.store.suppliers.domain.usecases.SupplierGetUseCaseImpl
import com.convenience.store.suppliers.domain.usecases.SuppliersGetUseCase
import com.convenience.store.suppliers.domain.usecases.SuppliersGetUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductsUiModule {
    @Provides
    @Singleton
    fun provideProductAddUseCase(
        uuidService: UuidService,
        productSyncService: ProductSyncService,
        productRepository: ProductRepository,
        categoryRepository: CategoryRepository,
        supplierRepository: SupplierRepository
    ): ProductCreateUseCase {
        return ProductAddUseCaseImpl(
            uuidService,
            productSyncService,
            productRepository,
            categoryRepository,
            supplierRepository
        )
    }

    @Provides
    @Singleton
    fun provideProductUpdateUseCase(
        productSyncService: ProductSyncService,
        productRepository: ProductRepository,
        categoryRepository: CategoryRepository,
        supplierRepository: SupplierRepository
    ): ProductUpdateUseCase {
        return ProductUpdateUseCaseImpl(
            productSyncService,
            productRepository,
            categoryRepository,
            supplierRepository
        )
    }

    @Provides
    @Singleton
    fun provideProductsGetUseCase(
        productRepository: ProductRepository
    ): ProductsGetUseCase {
        return ProductsGetUseCaseImpl(productRepository)
    }

    @Provides
    @Singleton
    fun provideProductGetUseCase(
        productRepository: ProductRepository
    ): ProductGetUseCase {
        return ProductGetUseCaseImpl(productRepository)
    }

    @Provides
    @Singleton
    fun provideProductsGetsByCategoryUseCase(
        productRepository: ProductRepository
    ): ProductsGetByCategoryUseCase {
        return ProductsGetByCategoryUseCaseImpl(productRepository)
    }

    @Provides
    @Singleton
    fun provideCategoriesGetUseCase(
        categoryRepository: CategoryRepository
    ): CategoriesGetUseCase {
        return CategoriesGetUseCaseImpl(categoryRepository)
    }

    @Provides
    @Singleton
    fun provideCategoryGetUseCase(
        categoryRepository: CategoryRepository
    ): CategoryGetUseCase {
        return CategoryGetUseCaseImpl(categoryRepository)
    }

    @Provides
    @Singleton
    fun provideSuppliersGetUseCase(
        supplierRepository: SupplierRepository
    ): SuppliersGetUseCase {
        return SuppliersGetUseCaseImpl(supplierRepository)
    }

    @Provides
    @Singleton
    fun provideSupplierGetUseCase(
        supplierRepository: SupplierRepository
    ): SupplierGetUseCase {
        return SupplierGetUseCaseImpl(supplierRepository)
    }


}
