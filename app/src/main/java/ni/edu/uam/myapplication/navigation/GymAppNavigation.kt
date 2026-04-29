package ni.edu.uam.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ni.edu.uam.myapplication.models.Rutina
import ni.edu.uam.myapplication.models.Screen
import ni.edu.uam.myapplication.models.User
import ni.edu.uam.myapplication.ui.Screens.ConfigurationScreen
import ni.edu.uam.myapplication.ui.Screens.CreateRoutineScreen
import ni.edu.uam.myapplication.ui.Screens.DashboardScreen
import ni.edu.uam.myapplication.ui.Screens.LoginScreen

@Composable
fun GymAppNavigation() {
    val navController = rememberNavController()
    // Estado compartido simulado (en una app real se usaría un ViewModel)
    var currentUser by remember { mutableStateOf(User(cif = "")) }
    val rutinas = remember { mutableStateListOf<Rutina>() }

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToConfig = { cif ->
                    currentUser = currentUser.copy(cif = cif)
                    navController.navigate(Screen.Configuration.route)
                }
            )
        }
        composable(Screen.Configuration.route) {
            ConfigurationScreen(
                onNavigateToDashboard = { weight, sex ->
                    currentUser = currentUser.copy(weight = weight, sex = sex)
                    navController.navigate(Screen.Dashboard.route) {
                        // Limpiar el stack de navegación para que no vuelva al login
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                user = currentUser,
                rutinas = rutinas,
                onNavigateToCreateRoutine = {
                    navController.navigate(Screen.CreateRoutine.route)
                }
            )
        }
        composable(Screen.CreateRoutine.route) {
            CreateRoutineScreen(
                onCancel = { navController.popBackStack() },
                onSave = { nuevaRutina ->
                    rutinas.add(nuevaRutina)
                    navController.popBackStack()
                }
            )
        }
    }
}

