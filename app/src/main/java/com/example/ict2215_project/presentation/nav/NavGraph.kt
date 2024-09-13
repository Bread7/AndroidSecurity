package com.example.ict2215_project.presentation.nav

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ict2215_project.presentation.screen.channelList.CustomChannelListScreen
import com.example.ict2215_project.presentation.screen.loading.LoadingScreen
import com.example.ict2215_project.presentation.screen.login.LoginScreen
import com.example.ict2215_project.presentation.screen.message.CustomMessageScreen
import com.example.ict2215_project.presentation.screen.profile.ProfileScreen
import com.example.ict2215_project.presentation.screen.signUp.SignUpScreen
import com.google.android.gms.maps.model.LatLng

sealed class Screen(val route: String) {
    data object LoadingScreen : Screen(route = "loading_screen")
    data object LoginScreen : Screen(route = "login_screen")
    data object SignUpScreen : Screen(route = "signup_screen")
    data object ChannelListScreen : Screen(route = "channel_list_screen")
    data object MessagesScreen : Screen("messages_screen/{channelId}") {
        fun messagesScreenRoute(channelId: String) = "messages_screen/$channelId"
    }

    data object ProfileScreen : Screen(route = "profile_screen")
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: NavGraphViewModel = hiltViewModel(),
) {
    val loginStatus by viewModel.navGraphState.collectAsState()
    val startDestination = when (loginStatus) {
        is NavGraphState.Loading -> Screen.LoadingScreen.route
        is NavGraphState.Success -> Screen.ChannelListScreen.route
        is NavGraphState.Error -> Screen.LoginScreen.route
    }
    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        composable(Screen.LoadingScreen.route) {
            LoadingScreen()
        }
        composable(Screen.LoginScreen.route) {
            LoginScreen(modifier = Modifier, navigateToConversations = {
                navController.navigate(Screen.ChannelListScreen.route) {
                    // This clears the back stack up to the login screen, including it.
                    popUpTo(Screen.LoginScreen.route) { inclusive = true }
                    // This makes sure you're not launching multiple instances of the destination screen.
                    launchSingleTop = true
                }
            }, navigateToSignUp = { navController.navigate(Screen.SignUpScreen.route) })
        }
        composable(Screen.SignUpScreen.route) {
            SignUpScreen(modifier = Modifier, navigateToLogin = { navController.popBackStack() })
        }
        composable(Screen.ChannelListScreen.route) {
            CustomChannelListScreen(modifier = Modifier,
                onChannelClick = { channel ->
                    navController.navigate(Screen.MessagesScreen.messagesScreenRoute(channel.cid))
                },
                onAvatarClick = { navController.navigate(Screen.ProfileScreen.route) })
        }
        composable(
            route = Screen.MessagesScreen.route,
            arguments = listOf(navArgument("channelId") { type = NavType.StringType })
        ) { backStackEntry ->
            // Assuming you have a MessagesScreen composable that takes channelId as a parameter
            val channelId = backStackEntry.arguments?.getString("channelId")
            channelId?.let {
                CustomMessageScreen(
                    channelId,
                    onBackPressed = { navController.popBackStack() })
            }
        }
        composable(Screen.ProfileScreen.route) {
            ProfileScreen(navigateToChannelList = { navController.popBackStack() },
                navigateToLogin = {
                    navController.navigate(Screen.LoginScreen.route) {
                        // Clear the entire back stack
                        popUpTo(0) { inclusive = true }
                        // Avoid launching multiple instances
                        launchSingleTop = true
                    }
                })
        }
    }
}