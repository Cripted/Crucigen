package com.example.crucigen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crucigen.ui.theme.CrucigenTheme
import java.text.SimpleDateFormat
import java.util.*

// ══════════════════════════════════════════════════════════════════════════════
// COLORES
// ══════════════════════════════════════════════════════════════════════════════

val GradientTop   = Color(0xFF29B6F6)
val GradientBottom = Color(0xFF3F51B5)
val BluePrimary   = Color(0xFF5B7FFF)

// ══════════════════════════════════════════════════════════════════════════════
// ACTIVITY
// ══════════════════════════════════════════════════════════════════════════════

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CrucigenTheme {
                CrucigenApp()
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// NAVEGACIÓN Y MODELOS
// ══════════════════════════════════════════════════════════════════════════════

sealed class Screen {
    object Login : Screen()
    object MisCrucigramas : Screen()
    object CrearCrucigrama : Screen()
    data class VisualizarCrucigrama(val crucigrama: Crucigrama) : Screen()
}

data class Pista(
    val id: Int,
    val pista: String,
    val respuesta: String
)

data class Crucigrama(
    val id: Int,
    val nombre: String,
    val fechaCreacion: String,
    val pistas: List<Pista> = emptyList(),
    val reversa: Boolean = false,
    val grid: List<List<Char?>> = emptyList(),
    val respuestas: List<String> = emptyList()
)

// ══════════════════════════════════════════════════════════════════════════════
// APP ROOT
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun CrucigenApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
    var crucigramas by remember {
        mutableStateOf(
            listOf(
                Crucigrama(1, "Crucigrama 1", "20/03/2026"),
                Crucigrama(2, "Crucigrama 2", "19/03/2026"),
                Crucigrama(3, "Crucigrama 3", "18/03/2026"),
            )
        )
    }

    when (val screen = currentScreen) {
        is Screen.Login -> LoginScreen(
            onLoginSuccess = { currentScreen = Screen.MisCrucigramas }
        )
        is Screen.MisCrucigramas -> MisCrucigamasScreen(
            crucigramas = crucigramas,
            onCrearNuevo = { currentScreen = Screen.CrearCrucigrama },
            onAbrirCrucigrama = { c -> currentScreen = Screen.VisualizarCrucigrama(c) }
        )
        is Screen.CrearCrucigrama -> CrearCrucigramaScreen(
            onGenerar = { nuevo ->
                crucigramas = crucigramas + nuevo
                currentScreen = Screen.VisualizarCrucigrama(nuevo)
            },
            onBack = { currentScreen = Screen.MisCrucigramas }
        )
        is Screen.VisualizarCrucigrama -> VisualizarCrucigramaScreen(
            crucigrama = screen.crucigrama,
            onGuardar = { currentScreen = Screen.MisCrucigramas },
            onEliminar = {
                crucigramas = crucigramas.filter { it.id != screen.crucigrama.id }
                currentScreen = Screen.MisCrucigramas
            },
            onRegenerar = { currentScreen = Screen.CrearCrucigrama }
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// PANTALLA 1 – LOGIN
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(GradientTop, GradientBottom))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Logo
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✦", fontSize = 48.sp, color = Color.White)
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Crucigen",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF90CAF9)
                )
            }

            // Card de login
            Card(
                modifier = Modifier.fillMaxWidth(0.85f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Login",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    // Botón Google
                    OutlinedButton(
                        onClick = onLoginSuccess,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("G", fontWeight = FontWeight.Bold, color = Color(0xFF4285F4), fontSize = 18.sp)
                            Text("Continuar con Google", color = Color.DarkGray)
                        }
                    }

                    // Botón Facebook
                    OutlinedButton(
                        onClick = onLoginSuccess,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("f", fontWeight = FontWeight.Bold, color = Color(0xFF1877F2), fontSize = 18.sp)
                            Text("Continuar con Facebook", color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// PANTALLA 2 – MIS CRUCIGRAMAS
// ══════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisCrucigamasScreen(
    crucigramas: List<Crucigrama>,
    onCrearNuevo: () -> Unit,
    onAbrirCrucigrama: (Crucigrama) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val crucigramasFiltrados = if (searchQuery.isBlank()) crucigramas
    else crucigramas.filter { it.nombre.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                OutlinedButton(
                    onClick = onCrearNuevo,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Crear nuevo crucigrama")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Mis crucigramas", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFBDBDBD))
                )
            }

            Spacer(Modifier.height(16.dp))

            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar crucigramas") },
                leadingIcon = {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Gray)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFE3F2FD),
                    focusedContainerColor = Color(0xFFE3F2FD),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Lista con ícono de editar (primeros 3)
                items(crucigramasFiltrados.take(3)) { c ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAbrirCrucigrama(c) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(c.nombre, fontWeight = FontWeight.Medium)
                            Text(c.fechaCreacion, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    HorizontalDivider(color = Color(0xFFE0E0E0))
                }

                // Tarjetas azules
                items(crucigramasFiltrados) { c ->
                    CrucigramaCard(crucigrama = c, onClick = { onAbrirCrucigrama(c) })
                }
            }
        }
    }
}

@Composable
fun CrucigramaCard(crucigrama: Crucigrama, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BluePrimary)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = crucigrama.nombre,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
            )
            CrucigramaPattern(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
                    .size(80.dp)
            )
        }
    }
}

@Composable
fun CrucigramaPattern(modifier: Modifier = Modifier) {
    val pattern = listOf(
        listOf(false, true, false, true),
        listOf(true, false, true, false),
        listOf(false, true, false, true),
        listOf(true, false, true, false),
    )
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        pattern.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                row.forEach { filled ->
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                if (filled) Color.Black.copy(alpha = 0.3f) else Color.Transparent,
                                RoundedCornerShape(2.dp)
                            )
                            .border(1.dp, Color.Black.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// PANTALLA 3 – CREAR CRUCIGRAMA
// ══════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearCrucigramaScreen(
    onGenerar: (Crucigrama) -> Unit,
    onBack: () -> Unit
) {
    var nombreCrucigrama by remember { mutableStateOf("") }
    var pistaInput       by remember { mutableStateOf("") }
    var respuestaInput   by remember { mutableStateOf("") }
    var reversa          by remember { mutableStateOf(false) }
    var pistas           by remember { mutableStateOf(listOf<Pista>()) }
    var isGenerating     by remember { mutableStateOf(false) }
    var showError        by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                Text("Creando nuevo crucigrama", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        if (pistas.isEmpty()) {
                            showError = true
                        } else {
                            isGenerating = true
                            val grid = generarGridSimulado(pistas)
                            val nuevo = Crucigrama(
                                id = System.currentTimeMillis().toInt(),
                                nombre = nombreCrucigrama.ifBlank { "Nuevo Crucigrama" },
                                fechaCreacion = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                                pistas = pistas,
                                reversa = reversa,
                                grid = grid,
                                respuestas = pistas.map { it.respuesta }
                            )
                            isGenerating = false
                            onGenerar(nuevo)
                        }
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    enabled = !isGenerating
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Generar", color = BluePrimary)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column {
                    Text("Nombre del crucigrama", fontSize = 12.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = nombreCrucigrama,
                        onValueChange = { nombreCrucigrama = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ejemplo") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }

            item {
                Column {
                    Text("Pista", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    OutlinedTextField(
                        value = pistaInput,
                        onValueChange = { pistaInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ejemplo") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }

            item {
                Column {
                    Text("Respuesta", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    OutlinedTextField(
                        value = respuestaInput,
                        onValueChange = { respuestaInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ejemplo") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = {
                            if (pistaInput.isNotBlank() && respuestaInput.isNotBlank()) {
                                pistas = pistas + Pista(
                                    id = pistas.size + 1,
                                    pista = pistaInput,
                                    respuesta = respuestaInput
                                )
                                pistaInput = ""
                                respuestaInput = ""
                                showError = false
                            }
                        },
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("+ Añadir")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Reversa", fontWeight = FontWeight.Medium)
                            Text("Habilita palabras en reversa", fontSize = 10.sp, color = Color.Gray)
                        }
                        Spacer(Modifier.width(8.dp))
                        Switch(
                            checked = reversa,
                            onCheckedChange = { reversa = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = BluePrimary
                            )
                        )
                    }
                }
            }

            if (showError) {
                item {
                    Text("Añade al menos una pista antes de generar", color = Color.Red, fontSize = 12.sp)
                }
            }

            item {
                if (pistas.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column {
                            pistas.forEachIndexed { index, p ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            Text("Pista ${index + 1}: ${p.pista}", fontWeight = FontWeight.Medium)
                                            Text("→ ${p.respuesta}", fontSize = 12.sp, color = Color.Gray)
                                        }
                                    }
                                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                                }
                                if (index < pistas.lastIndex) HorizontalDivider()
                            }
                        }
                    }
                } else {
                    // Placeholder vacío
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, BluePrimary, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column {
                            repeat(5) { index ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Pista ${index + 1}", color = Color.Gray)
                                    }
                                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                                }
                                if (index < 4) HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// PANTALLA 4 – VISUALIZAR CRUCIGRAMA
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun VisualizarCrucigramaScreen(
    crucigrama: Crucigrama,
    onGuardar: () -> Unit,
    onEliminar: () -> Unit,
    onRegenerar: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar crucigrama") },
            text = { Text("¿Estás seguro de que deseas eliminar este crucigrama?") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; onEliminar() }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Visualizando crucigrama", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = onRegenerar) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Regenerar", color = BluePrimary)
                }
                TextButton(onClick = onGuardar) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Guardar", color = Color(0xFF2196F3))
                }
                TextButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Eliminar", color = Color.Red)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { CrosswordGrid(crucigrama = crucigrama) }

            item {
                Text(
                    "Respuestas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            item {
                val respuestas = if (crucigrama.pistas.isNotEmpty())
                    crucigrama.pistas.mapIndexed { i, _ -> "Respuesta ${i + 1}" }
                else
                    (1..21).map { "Respuesta $it" }

                val columnas = respuestas.chunked((respuestas.size / 3).coerceAtLeast(1))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    columnas.forEach { col ->
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            col.forEach { r ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(Color.Black, shape = RoundedCornerShape(50))
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(r, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES DEL GRID
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun CrosswordGrid(crucigrama: Crucigrama) {
    val grid = crucigrama.grid
    if (grid.isEmpty()) {
        CrosswordGridPlaceholder()
        return
    }

    val cellSize = 22.dp
    val visibleRows = grid.size.coerceAtMost(15)
    val visibleCols = if (grid.isNotEmpty()) grid[0].size.coerceAtMost(15) else 15

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .background(Color.White)
            .padding(4.dp)
    ) {
        Column {
            for (row in 0 until visibleRows) {
                Row {
                    for (col in 0 until visibleCols) {
                        val cell = grid.getOrNull(row)?.getOrNull(col)
                        CellView(char = cell, number = getCellNumber(row, col), cellSize = cellSize)
                    }
                }
            }
        }
    }
}

@Composable
fun CellView(char: Char?, number: Int?, cellSize: Dp) {
    val isBlocked = char == null
    Box(
        modifier = Modifier
            .size(cellSize)
            .background(if (isBlocked) Color.Black else Color.White)
            .border(0.3.dp, Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        if (!isBlocked) {
            if (number != null) {
                Text(
                    text = number.toString(),
                    fontSize = 5.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(1.dp)
                )
            }
            if (char != null) {
                Text(text = char.toString(), fontSize = 9.sp, color = Color.Black, fontWeight = FontWeight.Medium)
            }
        }
    }
}

fun getCellNumber(row: Int, col: Int): Int? {
    val numbered = listOf(
        Pair(0, 0), Pair(0, 2), Pair(0, 4), Pair(0, 6), Pair(0, 8),
        Pair(1, 0), Pair(2, 0), Pair(3, 2), Pair(4, 0)
    )
    val idx = numbered.indexOf(Pair(row, col))
    return if (idx >= 0) idx + 1 else null
}

@Composable
fun CrosswordGridPlaceholder() {
    val size = 13
    val blocked = setOf(
        Pair(0,3), Pair(0,4), Pair(0,8),
        Pair(1,1), Pair(1,5), Pair(1,9),
        Pair(2,3), Pair(2,7),
        Pair(3,0), Pair(3,5), Pair(3,10),
        Pair(4,2), Pair(4,6),
        Pair(5,4), Pair(5,8), Pair(5,12),
        Pair(6,1), Pair(6,6), Pair(6,9),
        Pair(7,3), Pair(7,7),
        Pair(8,0), Pair(8,5), Pair(8,11),
        Pair(9,2), Pair(9,8),
        Pair(10,4), Pair(10,9),
        Pair(11,1), Pair(11,6), Pair(11,12),
        Pair(12,3), Pair(12,7)
    )
    val cellSize = 24.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .background(Color.White)
            .padding(4.dp)
    ) {
        Column {
            for (row in 0 until size) {
                Row {
                    for (col in 0 until size) {
                        val isBlocked = Pair(row, col) in blocked
                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .background(if (isBlocked) Color.Black else Color.White)
                                .border(0.3.dp, Color.LightGray)
                        )
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// LÓGICA DE GENERACIÓN DEL GRID
// ══════════════════════════════════════════════════════════════════════════════

fun generarGridSimulado(pistas: List<Pista>): List<List<Char?>> {
    val size = 15
    val grid = Array(size) { arrayOfNulls<Char>(size) }

    pistas.take(10).forEachIndexed { index, pista ->
        val palabra = pista.respuesta.uppercase().filter { it.isLetter() }
        if (palabra.isNotEmpty()) {
            val esHorizontal = index % 2 == 0
            val fila = (index * 2) % (size - 1)
            val col  = (index * 3) % (size - palabra.length).coerceAtLeast(1)

            if (esHorizontal && col + palabra.length <= size) {
                palabra.forEachIndexed { i, c -> grid[fila][col + i] = c }
            } else if (!esHorizontal && fila + palabra.length <= size) {
                val c2 = col % size
                palabra.forEachIndexed { i, c -> if (fila + i < size) grid[fila + i][c2] = c }
            }
        }
    }

    return grid.map { row -> row.toList() }
}
