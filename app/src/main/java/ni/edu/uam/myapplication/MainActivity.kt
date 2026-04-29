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
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ni.edu.uam.myapplication.ui.theme.MyApplicationTheme
import ni.edu.uam.myapplication.ui.Screens.LoginScreen
import ni.edu.uam.myapplication.ui.Screens.ConfigurationScreen
import ni.edu.uam.myapplication.models.*
import ni.edu.uam.myapplication.ui.Screens.CreateRoutineScreen
import ni.edu.uam.myapplication.ui.Screens.DashboardScreen
import ni.edu.uam.myapplication.navigation.GymAppNavigation
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

