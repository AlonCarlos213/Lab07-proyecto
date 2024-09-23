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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.automirrored.filled.List


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val db: MenuDatabase = crearMenuDatabase(context)

    val itemName = remember { mutableStateOf("") }
    val itemPrice = remember { mutableStateOf("") }
    val dataMenu = remember { mutableStateOf("") }

    val dao = db.menuDao()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Menús") },
                actions = {
                    // Botón para agregar un ítem del menú
                    IconButton(onClick = {
                        val menuItem = MenuItem(0, itemName.value, itemPrice.value.toDouble())
                        coroutineScope.launch {
                            agregarMenuItem(menuItem, dao)
                        }
                        itemName.value = ""
                        itemPrice.value = ""
                    }) {
                        Icon(Icons.Filled.AddCircle, contentDescription = "Agregar Menú")
                    }
                    // Botón para listar los ítems del menú
                    IconButton(onClick = {
                        coroutineScope.launch {
                            val data = getMenuItems(dao)
                            dataMenu.value = data
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Listar Menús")

                    }
                    // Botón para eliminar el último ítem añadido
                    IconButton(onClick = {
                        coroutineScope.launch {
                            eliminarUltimoMenuItem(dao)
                        }
                    }) {
                        Icon(Icons.Filled.DeleteForever, contentDescription = "Eliminar último Menú")
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
                    value = itemName.value,
                    onValueChange = { itemName.value = it },
                    label = { Text("Nombre del Menú") },
                    singleLine = true
                )
                TextField(
                    value = itemPrice.value,
                    onValueChange = { itemPrice.value = it },
                    label = { Text("Precio del Menú") },
                    singleLine = true
                )
                Text(text = dataMenu.value, fontSize = 20.sp)
            }
        }
    )
}

suspend fun eliminarUltimoMenuItem(dao: MenuDao) {
    try {
        dao.deleteLastMenuItem()
    } catch (e: Exception) {
        Log.e("Menu", "Error al eliminar último menú: ${e.message}")
    }
}

@Composable
fun crearMenuDatabase(context: Context): MenuDatabase {
    return Room.databaseBuilder(
        context,
        MenuDatabase::class.java,
        "menu_db"
    ).build()
}

suspend fun getMenuItems(dao: MenuDao): String {
    var result = ""
    val menuItems = dao.getAll()
    menuItems.forEach { item ->
        val row = "${item.name} - ${item.price}\n"
        result += row
    }
    return result
}

suspend fun agregarMenuItem(menuItem: MenuItem, dao: MenuDao) {
    try {
        dao.insert(menuItem)
    } catch (e: Exception) {
        Log.e("Menu", "Error: insert: ${e.message}")
    }
}
