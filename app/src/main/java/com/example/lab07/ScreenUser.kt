package com.example.lab07

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DeleteForever

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenUser(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val db: UserDatabase = crearDatabase(context)

    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val dataUser = remember { mutableStateOf("") }

    val dao = db.userDao()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios") },
                actions = {
                    // Botón para agregar un usuario
                    IconButton(onClick = {
                        val user = User(0, firstName.value, lastName.value)
                        coroutineScope.launch {
                            agregarUsuario(user, dao)
                        }
                        firstName.value = ""
                        lastName.value = ""
                    }) {
                        Icon(Icons.Filled.AddCircle, contentDescription = "Agregar Usuario")
                    }
                    // Botón para listar los usuarios
                    IconButton(onClick = {
                        coroutineScope.launch {
                            val data = getUsers(dao)
                            dataUser.value = data
                        }
                    }) {

                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Listar Usuarios")


                    }
                    // Botón para eliminar el último usuario añadido
                    IconButton(onClick = {
                        coroutineScope.launch {
                            eliminarUltimoUsuario(dao)
                        }
                    }) {
                        Icon(Icons.Filled.DeleteForever, contentDescription = "Eliminar último Usuario")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Spacer(Modifier.height(50.dp))
                TextField(
                    value = firstName.value,
                    onValueChange = { firstName.value = it },
                    label = { Text("First Name") },
                    singleLine = true
                )
                TextField(
                    value = lastName.value,
                    onValueChange = { lastName.value = it },
                    label = { Text("Last Name") },
                    singleLine = true
                )
                Text(text = dataUser.value, fontSize = 20.sp)
            }
        }
    )
}

suspend fun eliminarUltimoUsuario(dao: UserDao) {
    try {
        dao.deleteLastUser()
    } catch (e: Exception) {
        Log.e("User", "Error al eliminar último usuario: ${e.message}")
    }
}

@Composable
fun crearDatabase(context: Context): UserDatabase {
    return Room.databaseBuilder(
        context,
        UserDatabase::class.java,
        "user_db"
    ).build()
}

suspend fun getUsers(dao: UserDao): String {
    var rpta = ""
    val users = dao.getAll()
    users.forEach { user ->
        val fila = "${user.firstName} - ${user.lastName}\n"
        rpta += fila
    }
    return rpta
}

suspend fun agregarUsuario(user: User, dao: UserDao) {
    try {
        dao.insert(user)
    } catch (e: Exception) {
        Log.e("User", "Error: insert: ${e.message}")
    }
}
