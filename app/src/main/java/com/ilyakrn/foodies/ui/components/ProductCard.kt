package com.ilyakrn.foodies.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.ilyakrn.foodies.R
import com.ilyakrn.foodies.domain.models.extended.SelectedProductExtended
import com.ilyakrn.foodies.ui.getPriceFromInt

@Composable
fun ProductCard(product: SelectedProductExtended, onAdd: () -> Unit = {}, onRemove: () -> Unit = {}, onClick: () -> Unit = {}) {

    Box(modifier = Modifier
        .padding(4.dp, 8.dp)
    ){

        Column(modifier = Modifier
            .width(170.dp)
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small)
            .clickable { onClick() }
        ){
            Image(modifier = Modifier
                .fillMaxWidth(),
                painter = painterResource(id = R.drawable.img),
                contentDescription = "image"
            )
            Column(modifier = Modifier
                .padding(16.dp)
            ){
                Text(
                    text = product.product.name,
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${product.product.measure} ${product.product.measureUnit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.height(8.dp))
                if(product.count == 0) {
                    Box(modifier = Modifier
                        .shadow(4.dp)
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.shapes.small
                        )
                        .padding(8.dp)
                        .clickable {
                            onAdd()
                        }
                    ) {
                        Row(modifier = Modifier.align(Alignment.Center)) {
                            Text(text = getPriceFromInt(product.product.priceCurrent))
                            if (product.product.priceOld !=-1) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text =  getPriceFromInt(product.product.priceOld),
                                    style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }
                else {
                   ProductCountChangerCard(
                       onAdd = {
                           onAdd()
                       },
                       onRemove = {
                           onRemove()
                       },
                       count = product.count
                   )
                }
            }
        }
    }


}