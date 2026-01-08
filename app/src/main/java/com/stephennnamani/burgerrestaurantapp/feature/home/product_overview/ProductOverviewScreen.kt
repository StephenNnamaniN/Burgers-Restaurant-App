package com.stephennnamani.burgerrestaurantapp.feature.home.product_overview

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stephennnamani.burgerrestaurantapp.core.data.models.ProductCategory
import com.stephennnamani.burgerrestaurantapp.feature.component.CategoryChip
import com.stephennnamani.burgerrestaurantapp.feature.component.InfoCard
import com.stephennnamani.burgerrestaurantapp.feature.component.LoadingCard
import com.stephennnamani.burgerrestaurantapp.feature.component.MainProductCard
import com.stephennnamani.burgerrestaurantapp.feature.component.ProductCard
import com.stephennnamani.burgerrestaurantapp.feature.util.Alpha
import com.stephennnamani.burgerrestaurantapp.feature.util.DisplayResult
import com.stephennnamani.burgerrestaurantapp.ui.theme.FontSize
import com.stephennnamani.burgerrestaurantapp.ui.theme.Resources
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextPrimary
import org.koin.compose.koinInject

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun ProductOverviewScreen(
    onProductClick: (String) -> Unit
){
    val viewModel = koinInject<ProductOverviewViewModel>()

    val heroProduct by viewModel.heroProduct.collectAsState()
    val heroPaused by viewModel.heroPaused.collectAsState()

    val popularProducts by viewModel.popularProducts.collectAsState()
    val discountedProducts by viewModel.discountedProducts.collectAsState()
    val categoryProducts by viewModel.categoryProducts.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    BackHandler(enabled = selectedCategory != null) {
        viewModel.clearCategory()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // MainProductCard
        item {
            AnimatedContent(
                targetState = heroProduct?.id,
                transitionSpec = {
                    fadeIn(tween(500)) togetherWith fadeOut(tween(500))
                }
            ) { _ ->
                heroProduct?.let { product ->
                    MainProductCard(
                        title = product.title,
                        energyValue = "${product.energyValue ?: 0}kcal",
                        price = "£${"%.2f".format(product.price)}",
                        imageUrl = product.productImage,
                        paused = heroPaused,
                        onClick = { onProductClick(product.id) },
                    )
                } ?: LoadingCard(modifier = Modifier.fillMaxSize())
            }
        }

        // Category row
        item { SectionHeader(title = "Our Menu")}
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(ProductCategory.entries) { category ->
                    CategoryChip(
                        title = category.title,
                        iconRes = category.icon,
                        onClick = { viewModel.selectedCategory(category) }
                    )
                }
            }
        }

        if (selectedCategory != null) {
            item { SectionHeader(title = selectedCategory!!.title)}
            item {
                categoryProducts.DisplayResult(
                    onLoading = { LoadingCard(modifier = Modifier.fillMaxSize())},
                    onError = { message ->
                        InfoCard(
                            image = Resources.Icon.Dog,
                            title = "Oops!",
                            subtitle = message,
                        )
                    },
                    onSuccess = { list ->
                        val products = list
                            .distinctBy { it.id }
                            .sortedByDescending { it.createdAt }
                        if (products.isEmpty()) {
                            InfoCard(
                                image = Resources.Icon.Dog,
                                title = "Sorry, Nothing here.",
                                subtitle = "No products found in this category",
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                products.forEach { product ->
                                    ProductCard(
                                        product = product,
                                        onClick = onProductClick
                                    )
                                }
                            }
                        }
                    }
                )
            }
        } else {
            item { SectionHeader(title = "Popular Products")}
            item {
                popularProducts.DisplayResult(
                    onLoading = { LoadingCard(modifier = Modifier.fillMaxSize())},
                    onError = { message ->
                        InfoCard(
                            image = Resources.Icon.Dog,
                            title = "Oops!",
                            subtitle = message,
                        )
                    },
                    onSuccess = { list ->
                        val products = list
                            .distinctBy { it.id }
                            .sortedByDescending { it.createdAt }
                        if (products.isEmpty()) {
                            InfoCard(
                                image = Resources.Icon.Dog,
                                title = "Sorry, Nothing here.",
                                subtitle = "No popular products found.",
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                products.forEach { product ->
                                    ProductCard(
                                        product = product,
                                        onClick = onProductClick
                                    )
                                }
                            }
                        }
                    }
                )
            }
            item { SectionHeader(title = "Discounted Products")}
            item {
                discountedProducts.DisplayResult(
                    onLoading = { LoadingCard(modifier = Modifier.fillMaxSize())},
                    onError = { message ->
                        InfoCard(
                            image = Resources.Icon.Dog,
                            title = "Oops!",
                            subtitle = message,
                        )
                    },
                    onSuccess = { list ->
                        val products = list
                            .distinctBy { it.id }
                            .sortedByDescending { it.createdAt }
                        if (products.isEmpty()) {
                            InfoCard(
                                image = Resources.Icon.Dog,
                                title = "Sorry, Nothing here.",
                                subtitle = "No discounted products found.",
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                products.forEach { product ->
                                    ProductCard(
                                        product = product,
                                        onClick = onProductClick
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String){
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(Alpha.HALF),
        text = title,
        fontSize = FontSize.EXTRA_REGULAR,
        color = TextPrimary,
        textAlign = TextAlign.Center
    )
}