package com.convenience.store.products.data.datasources.remote

import android.util.Log
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.convenience.store.products.data.models.remote.ProductApiDto
import com.convenience.store.products.data.models.remote.ProductApiError
import com.convenience.store.products.data.models.remote.ProductCreateApiDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class ProductApiServiceMockImpl @Inject constructor() : ProductApiService {

    private val _cateooryIds = listOf(
        "019d2616-21ee-78b4-a43c-fef07b5ff7ab",
        "019d2616-21ee-78f9-a43d-976eddb2f099",
        "019d2616-21ee-7943-a43e-b38a2961adb6",
        "019d2616-21ee-798d-a43f-10488bf646da",
    )

    private val _ids = listOf(
        "019d2616-21ee-78b4-a43c-fef07b5ff7ab",
        "019d2616-21ee-78f9-a43d-976eddb2f099",
        "019d2616-21ee-7943-a43e-b38a2961adb6",
        "019d2616-21ee-798d-a43f-10488bf646da",
        "019d2616-21ee-79d2-a440-87f15f564ee8",
        "019d2616-21ee-7a18-a441-ea4167a000db",
        "019d2616-21ee-7a5e-a442-5f98e1d161bb",
        "019d2616-21ee-7aa3-a443-efe42cec3261",
        "019d2616-21ee-7af1-a444-103c5a3b6c1d",
        "019d2616-21ee-7b37-a445-fa91452fcd07",
        "019d2616-21ee-7b7c-a446-72fb29f9dc65",
        "019d2616-21ee-7bc2-a447-7269df3295f2",
        "019d2616-21ee-7c08-a448-84431279df7d",
        "019d2616-21ee-7c4d-a449-b6799fddb098",
        "019d2616-21ee-7c9b-a44a-b778c2f3e39b",
        "019d2616-21ee-7ce1-a44b-33b5a05ea8ce",
        "019d2616-21ee-7d26-a44c-f201a8557b41",
        "019d2616-21ee-7d6c-a44d-4497a63719af",
        "019d2616-21ee-7db2-a44e-a4872ddb14d6",
        "019d2616-21ee-7dfb-a44f-324dfd083c7a",
        "019d2616-21ee-7e41-a450-8e6674cb2271",
        "019d2616-21ee-7e8b-a451-b90533b12384",
        "019d2616-21ee-7ed0-a452-94c808534ae7",
        "019d2616-21ee-7f16-a453-3b9a950ed736",
        "019d2616-21ee-7f5c-a454-020b109dc03b",
        "019d2616-21ee-7fa5-a455-da57c82148fd",
        "019d2616-21ee-7feb-a456-4ed773f1a7e3",
        "019d2616-21ef-7035-897e-248a40d932fc",
        "019d2616-21ef-7083-897f-e662a558fd13",
        "019d2616-21ef-70c8-8980-59c38a704e54",
        "019d2616-21ef-7116-8981-f93d913dd0a7",
        "019d2616-21ef-7164-8982-bd683ce6d9ae",
        "019d2616-21ef-71b2-8983-980bf4b36673",
        "019d2616-21ef-71fb-8984-b4546151cb46",
        "019d2616-21ef-7249-8985-0e32bd9bad25",
        "019d2616-21ef-7293-8986-5122deeefbd4",
        "019d2616-21ef-72d9-8987-adaf68843e08",
        "019d2616-21ef-7322-8988-24aa4de5ec81",
        "019d2616-21ef-7368-8989-b87ccf673821",
        "019d2616-21ef-73ae-898a-311d876656d1",
        "019d2616-21ef-73f3-898b-9d443048c819",
        "019d2616-21ef-7439-898c-855ed18f07af",
        "019d2616-21ef-748b-898d-fa2acd31cd69",
        "019d2616-21ef-74d0-898e-63244f5b9045",
        "019d2616-21ef-7516-898f-89bc6d9bbbdc",
        "019d2616-21ef-755c-8990-67c8107cbc75",
        "019d2616-21ef-75a1-8991-14000621ad56",
        "019d2616-21ef-75e7-8992-312842d1a8c8",
        "019d2616-21ef-7635-8993-896027d001ce",
        "019d2616-21ef-7687-8994-2b6d6f7515a5",
        "019d2616-21ef-76d0-8995-63e0ab15d5b6",
        "019d2616-21ef-7716-8996-661443a4c9ca",
        "019d2616-21ef-7760-8997-62d55fe749b7",
        "019d2616-21ef-77ae-8998-6ac3ff3f4949",
        "019d2616-21ef-77fb-8999-91c5ef89a3ef",
        "019d2616-21ef-7845-899a-0c33d93f0352",
        "019d2616-21ef-788b-899b-05cc33f8a49a",
        "019d2616-21ef-78d0-899c-2729026a3a9d",
        "019d2616-21ef-791a-899d-c459ab135f14",
        "019d2616-21ef-7964-899e-210f1b53a214",
        "019d2616-21ef-79a9-899f-7a156bab2a7b",
        "019d2616-21ef-79ef-89a0-2edd6c414bdc",
        "019d2616-21ef-7a35-89a1-9fe453f706d6",
        "019d2616-21ef-7a7a-89a2-50ef74ffba3f",
        "019d2616-21ef-7ac0-89a3-62dab771ba46",
        "019d2616-21ef-7b0a-89a4-bc21f90aeccc",
        "019d2616-21ef-7b4f-89a5-ed183c129b84",
        "019d2616-21ef-7b95-89a6-ae4285389055",
        "019d2616-21ef-7bdb-89a7-1502e055fc16",
        "019d2616-21ef-7c20-89a8-19002828c835",
        "019d2616-21ef-7c66-89a9-1e5e1d2e5253",
        "019d2616-21ef-7cb0-89aa-e16f71fd233b",
        "019d2616-21ef-7cf5-89ab-f4558264a4bb",
        "019d2616-21ef-7d3b-89ac-98510c256111",
        "019d2616-21ef-7d81-89ad-efdaf9b044b1",
        "019d2616-21ef-7dc6-89ae-aae314d0bc12",
        "019d2616-21ef-7e0c-89af-05c1ad3fc098",
        "019d2616-21ef-7e56-89b0-05aac0dcdad1",
        "019d2616-21ef-7e9f-89b1-4636a1eb25b9",
        "019d2616-21ef-7ee5-89b2-c5c888b1418f",
        "019d2616-21ee-71eb-a428-283494f0caf0",
        "019d2616-21ee-7302-a429-8133cd4cf2aa",
        "019d2616-21ee-7358-a42a-71c7f41c307b",
        "019d2616-21ee-73a5-a42b-df04c4301556",
        "019d2616-21ee-73ef-a42c-a9b8f34cd70c",
        "019d2616-21ee-7476-a42d-b0bd90365fc4",
        "019d2616-21ee-74bc-a42e-3e2b02d58cd2",
        "019d2616-21ee-7506-a42f-937f202c980e",
        "019d2616-21ee-754b-a430-4062c1a485ab",
        "019d2616-21ee-7591-a431-1a1996c89696",
        "019d2616-21ee-75db-a432-b9257ba12294",
        "019d2616-21ee-762d-a433-cd92a6c8f25c",
        "019d2616-21ee-7676-a434-b9625f5ffa65",
        "019d2616-21ee-76bc-a435-84bbadda5e1e",
        "019d2616-21ee-7706-a436-9e4b8b7b50e7",
        "019d2616-21ee-774b-a437-2c51140bc0f7",
        "019d2616-21ee-7799-a438-c47ee74715c8",
        "019d2616-21ee-77df-a439-369691617754",
        "019d2616-21ee-7828-a43a-9a9a9904fcbf",
        "019d2616-21ee-786e-a43b-36b82eecbbe8",
    ).sorted()

    // Mocking a large list of products for pagination testing
    private val _allProducts = (1..99).map { i ->
        ProductApiDto(
            id = UUID.fromString(_ids[i - 1]),
            name = "Product $i",
            description = "Description for product $i",
            price = BigDecimal(i).setScale(2),
            barcode = "barcode_$i",
            categoryId = UUID.fromString(_cateooryIds[(i - 1) % 4]),
            supplierId = UUID.randomUUID(),
            version = 0
        )
    }.toMutableList()
    private val _mutex = Mutex()

    override suspend fun createProduct(productCreateApiDto: ProductCreateApiDto): Either<ProductApiError, Unit> {
        Log.d(
            "ProductApiService",
            "createProduct: ${Json.encodeToString(productCreateApiDto)}"
        )
        delay(2000)

        _mutex.withLock {
            _allProducts += ProductApiDto(
                id = productCreateApiDto.id,
                name = productCreateApiDto.name,
                description = productCreateApiDto.description,
                price = productCreateApiDto.price,
                barcode = productCreateApiDto.barcode,
                categoryId = productCreateApiDto.categoryId,
                supplierId = productCreateApiDto.supplierId,
                version = 0,
            )
        }

        return Either.Right(Unit)
    }

    override suspend fun getProductById(id: UUID): Either<ProductApiError, ProductApiDto> {
        Log.d(
            "ProductApiService",
            "getProductById: id = $id"
        )
        delay(2000)
        val product = _allProducts.find { it.id == id }
        return product?.right() ?: ProductApiError.NotFound.left()
    }

    override suspend fun getProducts(
        page: Int,
        pageSize: Int
    ): Either<ProductApiError, List<ProductApiDto>> {
        Log.d(
            "ProductApiService",
            "getProducts: page = $page, pageSize = $pageSize"
        )
        delay(1000)

        val start = (page - 1) * pageSize
        val end = (start + pageSize).coerceAtMost(_allProducts.size)

        return if (start < _allProducts.size) {
            _allProducts.subList(start, end).right()
        } else {
            emptyList<ProductApiDto>().right()
        }
    }

    override suspend fun getProductsByCategoryId(
        categoryId: UUID,
        page: Int,
        pageSize: Int
    ): Either<ProductApiError, List<ProductApiDto>> {
        Log.d(
            "ProductApiService",
            "getProductsByCategoryId: categoryId = $categoryId, page = $page, pageSize = $pageSize"
        )
        delay(1000)

        val allProductsByCategoryId = _allProducts.filter { it.categoryId == categoryId }

        val start = (page - 1) * pageSize
        val end = (start + pageSize).coerceAtMost(allProductsByCategoryId.size)

        return if (start < allProductsByCategoryId.size) {
            allProductsByCategoryId.subList(start, end).right()
        } else {
            emptyList<ProductApiDto>().right()
        }
    }
}
