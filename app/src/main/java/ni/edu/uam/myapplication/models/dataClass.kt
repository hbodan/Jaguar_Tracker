package ni.edu.uam.myapplication.models

// Data class para el Usuario
data class User(
    val cif: String,
    val weight: String = "",
    val sex: String = ""
)

// Nuevas Data classes
data class Ejercicio(
    val nombre: String,
    val musculo: String
)

data class DiaEntrenamiento(
    val nombreDia: String,
    val ejerciciosSeleccionados: List<Ejercicio>
)

data class Rutina(
    val nombre: String,
    val dias: Int,
    val planPorDias: List<DiaEntrenamiento>
)

// Sealed class para las rutas de navegación
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Configuration : Screen("configuration")
    object Dashboard : Screen("dashboard")
    object CreateRoutine : Screen("create_routine")
}
