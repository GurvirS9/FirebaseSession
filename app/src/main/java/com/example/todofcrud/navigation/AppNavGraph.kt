package com.example.todofcrud.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todofcrud.auth.viewmodel.AuthState
import com.example.todofcrud.auth.viewmodel.AuthViewModel
import com.example.todofcrud.todo.ui.TodoScreen
import com.example.todofcrud.auth.ui.LoginScreen
import com.example.todofcrud.auth.ui.RegisterScreen
// Import TodoViewModel to instantiate it for the screen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todofcrud.todo.viewmodel.TodoViewModel

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val TODO = "todo"
}

@Composable
fun AppNavGraph(
    authViewModel: AuthViewModel,
    navController: NavHostController = rememberNavController()
) {
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                // If user is logged in, navigate to Todo screen.
                // popUpTo(LOGIN) inclusive ensures back button doesn't go back to login.
                navController.navigate(Routes.TODO) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                    launchSingleTop = true
                }
            }
            is AuthState.Unauthenticated -> {
                // If user is logged out, navigate to Login screen.
                // We verify we are not already there to avoid loop.
                if (navController.currentDestination?.route != Routes.LOGIN && 
                    navController.currentDestination?.route != Routes.REGISTER) {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true } // Clear back stack
                        launchSingleTop = true
                    }
                }
            }
            else -> {
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.TODO) {
           val todoViewModel: TodoViewModel = viewModel()
           TodoScreenWrapper(authViewModel, todoViewModel)
        }
    }
}

@Composable
fun TodoScreenWrapper(authViewModel: AuthViewModel, todoViewModel: TodoViewModel) {
     TodoScreen(
         viewModel = todoViewModel,
         onLogout = { authViewModel.logout() }
     )
}
