package com.ilyakrn.foodies.domain.repositories

import com.ilyakrn.foodies.domain.models.core.Category
import com.ilyakrn.foodies.domain.models.core.Product

//репозиторий продуктов
interface ProductRepository {
    fun getProductList(listener: (List<Product>) -> Unit)
}