package com.stephennnamani.burgerrestaurantapp.feature.home

import android.content.res.Resources
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.stephennnamani.burgerrestaurantapp.R
import com.stephennnamani.burgerrestaurantapp.feature.home.component.BurgersBottomBar
import com.stephennnamani.burgerrestaurantapp.feature.home.component.CustomDrawer
import com.stephennnamani.burgerrestaurantapp.feature.home.domain.BottomBarDestinations
import com.stephennnamani.burgerrestaurantapp.feature.home.domain.CustomDrawerState
import com.stephennnamani.burgerrestaurantapp.feature.home.domain.isOpened
import com.stephennnamani.burgerrestaurantapp.feature.home.domain.reverse
import com.stephennnamani.burgerrestaurantapp.feature.home.product_overview.ProductOverviewScreen
import com.stephennnamani.burgerrestaurantapp.feature.nav.Screens
import com.stephennnamani.burgerrestaurantapp.ui.theme.BrandBrown
import com.stephennnamani.burgerrestaurantapp.ui.theme.FontSize
import com.stephennnamani.burgerrestaurantapp.ui.theme.IconPrimary
import com.stephennnamani.burgerrestaurantapp.ui.theme.Surface
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextPrimary
import com.stephennnamani.burgerrestaurantapp.ui.theme.oswaldVariableFont
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToAuth: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToAdminPanel: () -> Unit
){
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState()

    val viewModel = koinViewModel<HomeViewModel>()
    val isAdmin by viewModel.isAdmin.collectAsState()
    val context = LocalContext.current

    val selectedDestination by remember {
        derivedStateOf {
            val route = currentRoute.value?.destination?.route.toString()
            when {
                route.contains(BottomBarDestinations.ProductOverviewScreen.screen.toString()) -> BottomBarDestinations.ProductOverviewScreen
                route.contains(BottomBarDestinations.CartScreen.screen.toString()) -> BottomBarDestinations.CartScreen
                route.contains(BottomBarDestinations.NotificationsScreen.screen.toString()) -> BottomBarDestinations.NotificationsScreen
                route.contains(BottomBarDestinations.CategoriesScreen.screen.toString()) -> BottomBarDestinations.CategoriesScreen
                else -> BottomBarDestinations.ProductOverviewScreen
            }
        }
    }

    val screenWidth = remember { getScreenWidth() }
    var drawerState by remember { mutableStateOf(CustomDrawerState.Closed) }

    val offsetValue by remember { derivedStateOf { (screenWidth / 1.8).dp } }

    val animatedOffset by animateDpAsState(
        targetValue = if (drawerState.isOpened()) offsetValue else 0.dp
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (drawerState.isOpened()) 0.9f else 1f
    )
    val animatedBackground by animateColorAsState(
        targetValue = if (drawerState.isOpened()) BrandBrown else Surface
    )
    val animatedRadius by animateDpAsState(
        targetValue = if (drawerState.isOpened()) 20.dp else 0.dp
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBackground)
            .systemBarsPadding()
    ){
        CustomDrawer(
            onProfileClick = navigateToProfile,
            onContactUsClick = {},
            onSignOutClick = {
                viewModel.signOut(
                    onSuccess = navigateToAuth,
                    onError = {
                        Toast.makeText(
                            context,
                            "Sign-out error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            },
            onAdminPanelClick = navigateToAdminPanel,
            isAdmin = isAdmin
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(animatedOffset)
                .scale(animatedScale)
                .clip(RoundedCornerShape(animatedRadius))
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(animatedRadius),
                    ambientColor = Color.Black.copy(0.6f),
                    spotColor = Color.Black.copy(0.6f)
                )
        ){
            Scaffold(
                containerColor = Surface,
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            AnimatedContent(
                                targetState = selectedDestination
                            ) { destination ->
                                Text(
                                    text = destination.title,
                                    fontFamily = oswaldVariableFont(),
                                    fontSize = FontSize.LARGE,
                                    color = TextPrimary
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { drawerState = drawerState.reverse()}
                            ) {
                                AnimatedContent(
                                    targetState = drawerState
                                ) { drawer ->
                                    if (!drawer.isOpened()){
                                        Icon(
                                            painter = painterResource(R.drawable.menu),
                                            contentDescription = "Menu icon",
                                            tint = IconPrimary
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(R.drawable.close),
                                            contentDescription = "Menu icon",
                                            tint = IconPrimary
                                        )
                                    }
                                }

                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Surface,
                            scrolledContainerColor = Surface,
                            navigationIconContentColor = IconPrimary,
                            titleContentColor = TextPrimary,
                            actionIconContentColor = IconPrimary
                        )
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    NavHost(
                        modifier = Modifier.weight(1f),
                        navController = navController,
                        startDestination = Screens.ProductOverviewScreen
                    ){
                        composable<Screens.ProductOverviewScreen> {
                            ProductOverviewScreen (
                                onProductClick = {}
                            )
                        }
                        composable<Screens.Cart> {}
                        composable<Screens.Notifications> {}
                        composable<Screens.Categories> {}
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        BurgersBottomBar(
                            selected = selectedDestination,
                            onSelect = { destinations ->
                                navController.navigate(destinations.screen){
                                    launchSingleTop = true
                                    popUpTo<Screens.ProductOverviewScreen> {
                                        saveState = true
                                        inclusive = false
                                    }
                                    restoreState = true
                                }
                            }
                        )
                    }
                }

            }
        }
    }


}
fun getScreenWidth(): Float {
    return Resources.getSystem().displayMetrics.widthPixels /
            Resources.getSystem().displayMetrics.density
}