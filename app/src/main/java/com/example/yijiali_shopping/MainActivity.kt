package com.example.yijiali_shopping

import android.os.Bundle
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

data class Product(
    val name: String,
    val price: String,
    val description: String
)

class ShoppingViewModel : androidx.lifecycle.ViewModel() {
    val products = listOf(
        Product("Product A", "$100", "This is a great product A."),
        Product("Product B", "$150", "This is product B with more features."),
        Product("Product C", "$200", "Premium product C.")
    )

    var selectedProduct = mutableStateOf<Product?>(null)
        private set

    fun selectProduct(product: Product) {
        selectedProduct.value = product
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MultiPaneShoppingAppTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(shoppingViewModel: ShoppingViewModel = viewModel()) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(modifier = Modifier.fillMaxSize()) {
            ProductList(
                products = shoppingViewModel.products,
                onProductClick = { product ->
                    shoppingViewModel.selectProduct(product)
                },
                modifier = Modifier.weight(1f)
            )
            ProductDetails(
                product = shoppingViewModel.selectedProduct.value,
                modifier = Modifier.weight(2f)
            )
        }
    } else {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "productList") {
            composable("productList") {
                ProductList(
                    products = shoppingViewModel.products,
                    onProductClick = { product ->
                        shoppingViewModel.selectProduct(product)
                        navController.navigate("productDetails")
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("productDetails") {
                Column(modifier = Modifier.fillMaxSize()) {
                    val selectedProduct = shoppingViewModel.selectedProduct.value
                    ProductDetails(
                        product = selectedProduct,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp)
                    ) {
                        Text("Back")
                    }
                }
            }
        }
    }
}

@Composable
fun ProductList(
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(products) { product ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onProductClick(product) },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun ProductDetails(product: Product?, modifier: Modifier = Modifier) {
    if (product == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Select a product to view details.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Price: ${product.price}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun MultiPaneShoppingAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        typography = Typography(),
        content = content
    )
}
