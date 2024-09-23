package com.example.lab07

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import kotlinx.coroutines.launch

@Composable
fun ScreenUser(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val db: UserDatabase = crearDatabase(context)

    val id = remember { mutableStateOf("") }
    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val dataUser = remember { mutableStateOf("") }

    val dao = db.userDao()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(50.dp))
        TextField(
            value = id.value,
            onValueChange = { newValue: String -> id.value = newValue },
            label = { Text("ID (solo lectura)") },
            readOnly = true,
            singleLine = true
        )
        TextField(
            value = firstName.value,
            onValueChange = { newValue: String -> firstName.value = newValue },
            label = { Text("First Name: ") },
            singleLine = true
        )
        TextField(
            value = lastName.value,
            onValueChange = { newValue: String -> lastName.value = newValue },
            label = { Text("Last Name:") },
            singleLine = true
        )
        Button(
            onClick = {
                val user = User(0, firstName.value, lastName.value)
                coroutineScope.launch {
                    agregarUsuario(user = user, dao = dao)
                }
                firstName.value = ""
                lastName.value = ""
            }
        ) {
            Text("Agregar Usuario", fontSize = 16.sp)
        }
        Button(
            onClick = {
                coroutineScope.launch {
                    val data = getUsers(dao = dao)
                    dataUser.value = data
                }
            }
        ) {
            Text("Listar Usuarios", fontSize = 16.sp)
        }
        Text(
            text = dataUser.value, fontSize = 20.sp
        )
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
