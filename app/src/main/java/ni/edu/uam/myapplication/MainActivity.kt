package ni.edu.uam.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ni.edu.uam.myapplication.ui.theme.MyApplicationTheme

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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    GymAppNavigation()
                }
            }
        }
    }
}

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

@Composable
fun LoginScreen(onNavigateToConfig: (String) -> Unit) {
    var cif by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Bienvenido al Gimnasio Universitario", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = cif,
            onValueChange = { cif = it },
            label = { Text("CIF del estudiante") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                if (cif.isNotBlank()) onNavigateToConfig(cif)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }
    }
}

@Composable
fun ConfigurationScreen(onNavigateToDashboard: (String, String) -> Unit) {
    var weight by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Configuración Inicial", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Peso (kg)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = sex,
            onValueChange = { sex = it },
            label = { Text("Sexo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onNavigateToDashboard(weight, sex) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar y Continuar")
        }
    }
}

@Composable
fun DashboardScreen(user: User, rutinas: List<Rutina>, onNavigateToCreateRoutine: () -> Unit) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateRoutine) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Rutina")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Hola, Estudiante: ${user.cif}", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(text = "Tus Rutinas", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (rutinas.isEmpty()) {
                Text(text = "No tienes rutinas asignadas. ¡Crea una nueva!", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(rutinas) { rutina ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = rutina.nombre, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Días: ${rutina.dias}", style = MaterialTheme.typography.bodyMedium)
                                val totalEjercicios = rutina.planPorDias.sumOf { it.ejerciciosSeleccionados.size }
                                Text(text = "Ejercicios planificados: $totalEjercicios", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateRoutineScreen(onCancel: () -> Unit, onSave: (Rutina) -> Unit) {
    var routineName by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("") }
    val numDays = days.toIntOrNull() ?: 0

    val ejerciciosDisponibles = listOf(
        Ejercicio("Press de Banca", "Pecho"),
        Ejercicio("Sentadilla", "Piernas"),
        Ejercicio("Peso Muerto", "Espalda/Piernas"),
        Ejercicio("Dominadas", "Espalda"),
        Ejercicio("Curl de Bíceps", "Bíceps")
    )
    
    // Estado complejo: Mapa para rastrear dinámicamente qué ejercicios se seleccionan por cada día
    var seleccionesPorDia by remember { mutableStateOf(mapOf<Int, Set<Ejercicio>>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Crear Nueva Rutina", 
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = routineName,
            onValueChange = { routineName = it },
            label = { Text("Nombre de la rutina") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = days,
            onValueChange = { days = it },
            label = { Text("Cantidad de días") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        if (numDays > 0) {
            Text(text = "Configura tus días:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(numDays) { index ->
                    val dia = index + 1
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Día $dia", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            ejerciciosDisponibles.forEach { ejercicio ->
                                val seleccionadosActuales = seleccionesPorDia[dia] ?: emptySet()
                                val isSelected = seleccionadosActuales.contains(ejercicio)
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .toggleable(
                                            value = isSelected,
                                            onValueChange = { selected ->
                                                val nuevosSeleccionados = if (selected) {
                                                    seleccionadosActuales + ejercicio
                                                } else {
                                                    seleccionadosActuales - ejercicio
                                                }
                                                // Crear un nuevo mapa para disparar la recomposición
                                                seleccionesPorDia = seleccionesPorDia + (dia to nuevosSeleccionados)
                                            }
                                        )
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = null // Manejado por toggleable
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(text = ejercicio.nombre, style = MaterialTheme.typography.bodyLarge)
                                        Text(text = ejercicio.musculo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Espaciador si aún no se han digitado días
            Spacer(modifier = Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { 
                    if (routineName.isNotBlank() && numDays > 0) {
                        // Construir el plan por días a partir del mapa de selecciones
                        val planPorDias = (1..numDays).map { dia ->
                            DiaEntrenamiento(
                                nombreDia = "Día $dia",
                                ejerciciosSeleccionados = (seleccionesPorDia[dia] ?: emptySet()).toList()
                            )
                        }
                        onSave(Rutina(routineName, numDays, planPorDias))
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
        }
    }
}