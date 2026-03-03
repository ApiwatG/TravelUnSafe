package com.example.hotel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun NavGraph(navController: NavHostController) {
    val viewModel: HotelViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.HotelManage.route
    ) {
        composable(Screen.HotelManage.route) {
            HotelManageScreen(viewModel = viewModel, onSaveSuccess = { navController.popBackStack() })
        }
    }
}
@Composable
fun NavGraph1(navController: NavHostController) {
    val viewModel: HotelViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.HotelList.route) {

        composable(Screen.HotelList.route) {
            HotelListScreen(
                viewModel = viewModel,
                onAddClick = { navController.navigate(Screen.HotelManage.route) },
                onEditClick = { hotel ->
                    navController.navigate(Screen.HotelEdit.createRoute(hotel.hotelId))
                }
            )
        }

        composable(Screen.HotelManage.route) {
            HotelManageScreen(
                viewModel = viewModel,
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.HotelEdit.route,
            arguments = listOf(navArgument("hotelId") { type = NavType.StringType })
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getString("hotelId") ?: ""
            val hotel = viewModel.hotels.collectAsState().value.find { it.hotelId == hotelId }
            hotel?.let {
                HotelEditScreen(
                    viewModel = viewModel,
                    hotel = it,
                    onSaveSuccess = { navController.popBackStack() }
                )
            }
        }
        composable(
            route = Screen.HotelEdit.route,
            arguments = listOf(navArgument("hotelId") { type = NavType.StringType })
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getString("hotelId") ?: ""
            val hotel = viewModel.hotels.collectAsState().value
                .find { it.hotelId == hotelId }
            hotel?.let {
                HotelEditScreen(
                    viewModel = viewModel,
                    hotel = it,
                    onSaveSuccess = { navController.popBackStack() }
                )
            }
        }
    }
}

