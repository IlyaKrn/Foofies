package com.ilyakrn.foodies.domain.usecases

import android.util.Log
import com.ilyakrn.foodies.domain.models.core.Product
import com.ilyakrn.foodies.domain.models.core.SelectedProduct
import com.ilyakrn.foodies.domain.models.core.Tag
import com.ilyakrn.foodies.domain.models.extended.ProductExtended
import com.ilyakrn.foodies.domain.repositories.BasketRepository
import com.ilyakrn.foodies.domain.repositories.ProductRepository
import com.ilyakrn.foodies.domain.repositories.TagRepository

//добавление продукта в корзину
class AddProductToBasketUseCase(private val basketRepository: BasketRepository, private val productId: Long) {

    fun invoke() {
        try {
            var isAdded: Boolean = false
            basketRepository.getSelectedProductList().forEach {
                if (it.productId == productId) {
                    val c: Int = it.count;
                    basketRepository.editSelectedProduct(SelectedProduct(productId, c + 1))
                    isAdded = true
                }
            }
            if (!isAdded)
                basketRepository.addSelectedProduct(SelectedProduct(productId, 1))
        } catch (_:ConcurrentModificationException){ }
    }

}