package com.ilyakrn.foodies.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ilyakrn.foodies.R
import com.ilyakrn.foodies.data.repos.BasketRepositoryImpl
import com.ilyakrn.foodies.data.repos.CategoryRepositoryImpl
import com.ilyakrn.foodies.data.repos.ProductRepositoryImpl
import com.ilyakrn.foodies.data.repos.TagRepositoryImpl
import com.ilyakrn.foodies.domain.models.core.Category
import com.ilyakrn.foodies.domain.models.extended.SelectedProductExtended
import com.ilyakrn.foodies.domain.usecases.AddProductToBasketUseCase
import com.ilyakrn.foodies.domain.usecases.GetBasketPriceUseCase
import com.ilyakrn.foodies.domain.usecases.GetCategoryListUseCase
import com.ilyakrn.foodies.domain.usecases.GetProductListByCategoryUseCase
import com.ilyakrn.foodies.domain.usecases.RemoveProductFromBasketUseCase
import com.ilyakrn.foodies.ui.components.BottomButton
import com.ilyakrn.foodies.ui.components.CategoryBar
import com.ilyakrn.foodies.ui.components.CategoryCard
import com.ilyakrn.foodies.ui.components.MainTopBar
import com.ilyakrn.foodies.ui.components.ProductCard
import com.ilyakrn.foodies.ui.getPriceFromInt

@Preview
@Composable
fun CatalogScreen(onShowBasket: () -> Unit = {},onShowProductInfo: (Long) -> Unit = {} ) {

    val categoryRepository = CategoryRepositoryImpl()
    val productRepository = ProductRepositoryImpl()
    val tagRepository = TagRepositoryImpl()
    val basketRepository = BasketRepositoryImpl()


    val categoryList = remember {
        mutableStateOf(ArrayList<Category>())
    }
    val selectedCategory = remember {
        mutableStateOf(-1L)
    }
    val productList = remember {
        mutableStateOf(ArrayList<SelectedProductExtended>())
    }
    val mutableBasketPrice = remember {
        mutableStateOf(0)
    }
    val mutableIsLoading = remember {
        mutableStateOf(true)
    }

    if(selectedCategory.value == -1L) {
        GetCategoryListUseCase(categoryRepository).invoke {
            categoryList.value = it as ArrayList<Category>
            selectedCategory.value = if (it.isNotEmpty()) it[0].id else -1L
            GetProductListByCategoryUseCase(basketRepository, productRepository, tagRepository, it[0].id).invoke {
                productList.value = it as ArrayList<SelectedProductExtended>
                mutableIsLoading.value = false
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ){
        MainTopBar()
        CategoryBar(
            categories = categoryList.value,
            selectedId = selectedCategory.value,
            onSelect = {
                productList.value.clear()
                mutableIsLoading.value = true
                GetProductListByCategoryUseCase(basketRepository, productRepository, tagRepository, it).invoke {
                    productList.value = it as ArrayList<SelectedProductExtended>
                    mutableIsLoading.value = false
                }
                GetBasketPriceUseCase(basketRepository, productRepository).invoke{
                    mutableBasketPrice.value = it
                }
                selectedCategory.value = it
            }
        )
        Box(modifier = Modifier
            .fillMaxSize()
        ){

            if(productList.value.isEmpty()) {
                if(mutableIsLoading.value){
                    CircularProgressIndicator(modifier = Modifier
                        .align(Alignment.Center)
                    )
                }
                else{
                    Text(modifier = Modifier
                        .align(Alignment.Center),
                        text = stringResource(id = R.string.no_products_in_category)
                    )
                }
            }
            else{
                Column {
                    LazyVerticalGrid(modifier = Modifier
                        .padding(12.dp, 12.dp, 12.dp, if(mutableBasketPrice.value != 0) 72.dp else 0.dp),
                        columns = GridCells.Adaptive(((LocalConfiguration.current.screenWidthDp / 2) - 24).dp)
                    ){
                        items(productList.value){
                            ProductCard(
                                product = it,
                                onAdd = {
                                    AddProductToBasketUseCase(basketRepository, it.product.id).invoke()
                                    GetProductListByCategoryUseCase(basketRepository, productRepository, tagRepository, it.product.categoryId).invoke {
                                        productList.value = it as ArrayList<SelectedProductExtended>
                                        mutableIsLoading.value = false
                                    }
                                    GetBasketPriceUseCase(basketRepository, productRepository).invoke{
                                        mutableBasketPrice.value = it
                                    }
                                },
                                onRemove = {
                                    RemoveProductFromBasketUseCase(basketRepository, it.product.id).invoke()
                                    GetProductListByCategoryUseCase(basketRepository, productRepository, tagRepository, it.product.categoryId).invoke {
                                        productList.value = it as ArrayList<SelectedProductExtended>
                                        mutableIsLoading.value = false
                                    }
                                    GetBasketPriceUseCase(basketRepository, productRepository).invoke{
                                        mutableBasketPrice.value = it
                                    }
                                },
                                onClick = {
                                    onShowProductInfo(it.product.id)
                                }
                            )
                        }
                    }
                }
            }
            if(mutableBasketPrice.value != 0){
               Box(modifier = Modifier
                    .align(Alignment.BottomCenter)
                ) {
                    BottomButton(text = getPriceFromInt(mutableBasketPrice.value), onClick = onShowBasket, iconId = R.drawable.basket)
                }
            }
        }
    }
}
