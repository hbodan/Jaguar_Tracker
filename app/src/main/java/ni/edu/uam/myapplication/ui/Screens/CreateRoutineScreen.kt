package ni.edu.uam.myapplication.ui.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ni.edu.uam.myapplication.models.DiaEntrenamiento
import ni.edu.uam.myapplication.models.Ejercicio
import ni.edu.uam.myapplication.models.Rutina
import kotlin.collections.minus
import kotlin.collections.plus

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