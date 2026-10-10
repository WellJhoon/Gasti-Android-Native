package com.jhonmedina.gasty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jhonmedina.gasty.ui.theme.GastyTheme

// =========================================================================
// 🎨 PALETA EXACTA DE GASTY DINÁMICA (Dark / Light Theme)
// Se sincronizan en vivo con GastyTheme.colors según el modo seleccionado
// =========================================================================
val GastyBgDark: Color @Composable get() = GastyTheme.colors.bg
val GastyCardDark: Color @Composable get() = GastyTheme.colors.card
val GastyBorderDark: Color @Composable get() = GastyTheme.colors.border
val GastyTextDark: Color @Composable get() = GastyTheme.colors.text
val GastyMutedDark: Color @Composable get() = GastyTheme.colors.textMuted

// Acentos de Marca (Idénticos a tailwind.config.ts)
val GastyOlive = Color(0xFF5E824A)        // olive.DEFAULT
val GastyCoral = Color(0xFFD95D5D)        // pastelCoral.DEFAULT
val GastyAmber = Color(0xFFE09F3E)        // pastelAmber.DEFAULT
val GastyBlue = Color(0xFF4E89AE)         // pastelBlue.DEFAULT
val GastyPurple = Color(0xFF8E7DBE)       // pastelPurple.DEFAULT

// Badges suaves dinámicos (se adaptan automáticamente a oscuro o claro)
val GastyOliveSoftDark: Color @Composable get() = GastyTheme.colors.oliveSoft
val GastyCoralSoftDark: Color @Composable get() = GastyTheme.colors.coralSoft
val GastyAmberSoftDark: Color @Composable get() = GastyTheme.colors.amberSoft
val GastyBlueSoftDark: Color @Composable get() = GastyTheme.colors.blueSoft
val GastyPurpleSoftDark: Color @Composable get() = GastyTheme.colors.purpleSoft

// =========================================================================
// 🧭 NAVEGACIÓN
// =========================================================================
enum class NavTab(val title: String, val icon: ImageVector) {
    DASHBOARD("Inicio", Icons.Default.Home),
    TRANSACTIONS("Movimientos", Icons.AutoMirrored.Filled.List),
    FINANCE("Finanzas", Icons.Default.AccountBalance),
    SETTINGS("Ajustes", Icons.Default.Settings)
}

// =========================================================================
// 📦 MODELOS DE DATOS
// =========================================================================
data class CategoryUiModel(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val spentThisMonth: String,
    val accentColor: Color,
    val softBgColor: Color
)

data class TransactionUiModel(
    val id: String,
    val title: String,
    val categoryName: String,
    val categoryEmoji: String,
    val dateLabel: String,
    val amountFormatted: String,
    val amountNumber: Double,
    val isExpense: Boolean,
    val accentColor: Color,
    val softBgColor: Color
)

data class SavingsGoalUiModel(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val currentAmount: Double,
    val targetAmount: Double,
    val accentColor: Color
)

data class DebtUiModel(
    val id: String,
    val title: String,
    val entityName: String,
    val totalRemaining: Double,
    val nextDueDate: String,
    val monthlyPayment: Double
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GastyApp()
        }
    }
}

// =========================================================================
// 📱 CONTENEDOR PRINCIPAL
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastyApp() {
    // 🌓 Estado global de tema (Oscuro o Claro)
    var isDarkTheme by remember { mutableStateOf(true) }
    var currentTab by remember { mutableStateOf(NavTab.DASHBOARD) }

    // Estados para el Modal Bottom Sheet
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    var selectedTxTypeIsExpense by remember { mutableStateOf(true) }
    var selectedCategoryName by remember { mutableStateOf("Comida & Super") }
    var selectedCategoryEmoji by remember { mutableStateOf("🍔") }

    // 🎨 Wrapper de Tema Dinámico Gasty (Equivalente al ThemeProvider de React)
    GastyTheme(darkTheme = isDarkTheme) {
        Scaffold(
            containerColor = GastyTheme.colors.bg,
            bottomBar = {
                GastyBottomNavigationBar(
                    selectedTab = currentTab,
                    onTabSelected = { newTab -> currentTab = newTab }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    NavTab.DASHBOARD -> GastyDashboardContent(
                        onNewExpenseClick = {
                            selectedTxTypeIsExpense = true
                            isBottomSheetOpen = true
                        },
                        onNewIncomeClick = {
                            selectedTxTypeIsExpense = false
                            isBottomSheetOpen = true
                        },
                        onCategoryClick = { category ->
                            selectedTxTypeIsExpense = true
                            selectedCategoryName = category.name
                            selectedCategoryEmoji = category.iconEmoji
                            isBottomSheetOpen = true
                        }
                    )
                    NavTab.TRANSACTIONS -> TransactionsScreen()
                    NavTab.FINANCE -> FinanceHubScreen()
                    NavTab.SETTINGS -> SettingsScreen(
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { isDarkTheme = it }
                    )
                }
            }

            if (isBottomSheetOpen) {
                TransactionBottomSheet(
                    initialIsExpense = selectedTxTypeIsExpense,
                    selectedCategoryName = selectedCategoryName,
                    selectedCategoryEmoji = selectedCategoryEmoji,
                    onDismiss = { isBottomSheetOpen = false },
                    onSave = { _, _, _ ->
                        isBottomSheetOpen = false
                    }
                )
            }
        }
    }
}

// =========================================================================
// 🌟 PANTALLA DE AJUSTES & PERFILES (SettingsScreen con Switch de Tema)
// =========================================================================
@Composable
fun SettingsScreen(
    isDarkTheme: Boolean = true,
    onToggleTheme: (Boolean) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Cabecera
        Column {
            Text(
                text = "CONFIGURACIÓN",
                color = GastyMutedDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "Ajustes & Perfiles",
                color = GastyTextDark,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        // 2. Tarjeta de Perfil Activo
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GastyCardDark),
            border = BorderStroke(2.dp, GastyBorderDark)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Avatar circular grande
                Surface(
                    shape = CircleShape,
                    color = GastyOliveSoftDark,
                    border = BorderStroke(2.dp, GastyOlive),
                    modifier = Modifier.size(60.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "J", color = GastyOlive, fontSize = 26.sp, fontWeight = FontWeight.Black)
                    }
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Jhon Medina",
                        color = GastyTextDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = GastyOliveSoftDark,
                        border = BorderStroke(1.dp, GastyOlive.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "Perfil Principal",
                            color = GastyOlive,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // 3. Opciones de Cuenta y Finanzas
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "PREFERENCIAS",
                color = GastyMutedDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = GastyCardDark),
                border = BorderStroke(2.dp, GastyBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    // 🌓 Fila con Switch interactivo de Tema
                    SettingsSwitchRowItem(
                        icon = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                        title = "Tema de la Aplicación",
                        value = if (isDarkTheme) "Modo Oscuro (Gasty Night)" else "Modo Claro (Gasty Soft)",
                        checked = isDarkTheme,
                        onCheckedChange = onToggleTheme
                    )
                    HorizontalDivider(color = GastyBorderDark, thickness = 1.dp)
                    SettingsRowItem(
                        icon = Icons.Default.Person,
                        title = "Cambiar de Persona",
                        value = "2 personas activas",
                        onClick = { }
                    )
                    HorizontalDivider(color = GastyBorderDark, thickness = 1.dp)
                    SettingsRowItem(
                        icon = Icons.Default.Lock,
                        title = "PIN de Seguridad",
                        value = "Configurado (4 dígitos)",
                        onClick = { }
                    )
                    HorizontalDivider(color = GastyBorderDark, thickness = 1.dp)
                    SettingsRowItem(
                        icon = Icons.Default.Storage,
                        title = "Servidor API",
                        value = "localhost:4000 (Express)",
                        onClick = { }
                    )
                }
            }
        }

        // 4. Tarjeta del Sistema y Versión
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = GastyCardDark),
            border = BorderStroke(2.dp, GastyBorderDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gasty Control Manager",
                        color = GastyTextDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = GastyOliveSoftDark
                    ) {
                        Text(
                            text = "v1.0.0 Nativo",
                            color = GastyOlive,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "Desarrollado en Kotlin con Jetpack Compose y Material 3",
                    color = GastyMutedDark,
                    fontSize = 12.sp
                )
            }
        }

        // 5. Botón de Cerrar Sesión
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GastyCoralSoftDark),
            border = BorderStroke(1.5.dp, GastyCoral)
        ) {
            Text(
                text = "Cerrar Sesión",
                color = GastyCoral,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Subcomponente individual de Fila de Ajuste con Switch interactivo
@Composable
fun SettingsSwitchRowItem(
    icon: ImageVector,
    title: String,
    value: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GastyOliveSoftDark,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = title, tint = GastyOlive, modifier = Modifier.size(20.dp))
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    color = GastyTextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = value,
                    color = GastyMutedDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = GastyOlive,
                uncheckedThumbColor = GastyMutedDark,
                uncheckedTrackColor = GastyBorderDark
            )
        )
    }
}

// Subcomponente individual de Fila de Ajuste
@Composable
fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GastyBgDark,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = title, tint = GastyOlive, modifier = Modifier.size(20.dp))
                }
            }
            Text(
                text = title,
                color = GastyTextDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = value,
            color = GastyMutedDark,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// =========================================================================
// PANTALLA DE FINANZAS (SHOT 8)
// =========================================================================
@Composable
fun FinanceHubScreen() {
    var selectedSubTab by remember { mutableIntStateOf(0) }

    val goals = remember {
        listOf(
            SavingsGoalUiModel("1", "Fondo de Emergencia", "🛡️", 1800.0, 3000.0, GastyOlive),
            SavingsGoalUiModel("2", "Vacaciones Diciembre", "✈️", 750.0, 1500.0, GastyBlue),
            SavingsGoalUiModel("3", "Mantenimiento Vehículo", "🔧", 320.0, 500.0, GastyAmber)
        )
    }

    val debts = remember {
        listOf(
            DebtUiModel("1", "Tarjeta Visa Platinum", "Banco BHD", 680.0, "15 Oct", 85.0),
            DebtUiModel("2", "Préstamo Personal", "Banco Popular", 1450.0, "20 Oct", 120.0)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column {
            Text(text = "GESTIÓN PATRIMONIAL", color = GastyMutedDark, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Text(text = "Metas & Deudas", color = GastyTextDark, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        }

        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = GastyCardDark,
            contentColor = GastyOlive,
            divider = { },
            indicator = { },
            modifier = Modifier.clip(RoundedCornerShape(16.dp)).border(2.dp, GastyBorderDark, RoundedCornerShape(16.dp))
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = { Text("🎯 Metas de Ahorro", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                selectedContentColor = Color.White,
                unselectedContentColor = GastyMutedDark,
                modifier = Modifier.background(if (selectedSubTab == 0) GastyOlive else Color.Transparent).padding(vertical = 12.dp)
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = { Text("💳 Deudas & Préstamos", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                selectedContentColor = Color.White,
                unselectedContentColor = GastyMutedDark,
                modifier = Modifier.background(if (selectedSubTab == 1) GastyCoral else Color.Transparent).padding(vertical = 12.dp)
            )
        }

        if (selectedSubTab == 0) {
            GoalsSection(goals)
        } else {
            DebtsSection(debts)
        }
    }
}

@Composable
fun GoalsSection(goals: List<SavingsGoalUiModel>) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Tus Metas Activas", color = GastyTextDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = "+ Nueva Meta", color = GastyOlive, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { })
        }
        goals.forEach { goal -> GoalCardItem(goal = goal) }
    }
}

@Composable
fun GoalCardItem(goal: SavingsGoalUiModel) {
    val progress = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = GastyCardDark), border = BorderStroke(2.dp, GastyBorderDark)) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = goal.accentColor.copy(alpha = 0.15f), border = BorderStroke(1.dp, goal.accentColor.copy(alpha = 0.4f)), modifier = Modifier.size(42.dp)) {
                        Box(contentAlignment = Alignment.Center) { Text(text = goal.iconEmoji, fontSize = 20.sp) }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = goal.title, color = GastyTextDark, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Meta: $${"%,.0f".format(goal.targetAmount)}", color = GastyMutedDark, fontSize = 12.sp)
                    }
                }
                Surface(shape = RoundedCornerShape(50), color = goal.accentColor.copy(alpha = 0.15f), border = BorderStroke(1.dp, goal.accentColor.copy(alpha = 0.4f))) {
                    Text(text = "$percentage%", color = goal.accentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                }
            }
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(50)), color = goal.accentColor, trackColor = GastyBgDark)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Ahorrado: $${"%,.0f".format(goal.currentAmount)}", color = GastyOlive, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = "Faltan $${"%,.0f".format(goal.targetAmount - goal.currentAmount)}", color = GastyMutedDark, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun DebtsSection(debts: List<DebtUiModel>) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Compromisos & Deudas", color = GastyTextDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = "+ Nueva Deuda", color = GastyCoral, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { })
        }
        debts.forEach { debt -> DebtCardItem(debt = debt) }
    }
}

@Composable
fun DebtCardItem(debt: DebtUiModel) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = GastyCardDark), border = BorderStroke(2.dp, GastyBorderDark)) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = debt.title, color = GastyTextDark, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(text = debt.entityName, color = GastyMutedDark, fontSize = 12.sp)
                }
                Surface(shape = RoundedCornerShape(50), color = GastyCoralSoftDark, border = BorderStroke(1.dp, GastyCoral.copy(alpha = 0.4f))) {
                    Text(text = "Vence ${debt.nextDueDate}", color = GastyCoral, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                }
            }
            HorizontalDivider(color = GastyBorderDark.copy(alpha = 0.5f), thickness = 1.dp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = "Restante por pagar", color = GastyMutedDark, fontSize = 11.sp)
                    Text(text = "$${"%,.2f".format(debt.totalRemaining)}", color = GastyCoral, fontSize = 18.sp, fontWeight = FontWeight.Black)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Cuota mensual", color = GastyMutedDark, fontSize = 11.sp)
                    Text(text = "$${"%,.2f".format(debt.monthlyPayment)}", color = GastyTextDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// =========================================================================
// PANTALLA DE MOVIMIENTOS (SHOT 7)
// =========================================================================
@Composable
fun TransactionsScreen() {
    val allTransactions = listOf(
        TransactionUiModel("tx-1", "Supermercado Olé", "Comida & Super", "🍔", "Hoy, 14:30", "−$64.20", 64.20, true, GastyAmber, GastyAmberSoftDark),
        TransactionUiModel("tx-2", "Depósito Nómina Quincena", "Ingreso", "💰", "Ayer", "+$1,550.00", 1550.00, false, GastyOlive, GastyOliveSoftDark),
        TransactionUiModel("tx-3", "Gasolina Texaco", "Transporte", "🚗", "7 Oct", "−$35.00", 35.00, true, GastyBlue, GastyBlueSoftDark),
        TransactionUiModel("tx-4", "Suscripción Netflix & Spotify", "Servicios & Casa", "💡", "5 Oct", "−$19.99", 19.99, true, GastyPurple, GastyPurpleSoftDark),
        TransactionUiModel("tx-5", "Cine & Palomitas", "Salidas & Ocio", "🍿", "3 Oct", "−$28.50", 28.50, true, GastyCoral, GastyCoralSoftDark),
        TransactionUiModel("tx-6", "Bono Desempeño Trimestral", "Ingreso Extra", "✨", "1 Oct", "+$300.00", 300.00, false, GastyOlive, GastyOliveSoftDark)
    )

    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("TODOS") }

    val filteredTransactions = allTransactions.filter { tx ->
        val matchesSearch = tx.title.contains(searchQuery, ignoreCase = true) ||
                tx.categoryName.contains(searchQuery, ignoreCase = true)
        val matchesType = when (filterType) {
            "GASTOS" -> tx.isExpense
            "INGRESOS" -> !tx.isExpense
            else -> true
        }
        matchesSearch && matchesType
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column {
            Text(text = "HISTORIAL", color = GastyMutedDark, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Text(text = "Movimientos", color = GastyTextDark, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        }

        OutlinedTextField(
            value = searchQuery, onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar por nombre o categoría...", color = GastyMutedDark, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = GastyMutedDark) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = GastyMutedDark) }
                }
            },
            singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = GastyTextDark, unfocusedTextColor = GastyTextDark,
                focusedBorderColor = GastyOlive, unfocusedBorderColor = GastyBorderDark,
                focusedContainerColor = GastyCardDark, unfocusedContainerColor = GastyCardDark
            )
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterPillChip(title = "Todos", isSelected = filterType == "TODOS", onClick = { filterType = "TODOS" }, modifier = Modifier.weight(1f))
            FilterPillChip(title = "Solo Gastos", isSelected = filterType == "GASTOS", onClick = { filterType = "GASTOS" }, activeColor = GastyCoral, modifier = Modifier.weight(1f))
            FilterPillChip(title = "Solo Ingresos", isSelected = filterType == "INGRESOS", onClick = { filterType = "INGRESOS" }, activeColor = GastyOlive, modifier = Modifier.weight(1f))
        }

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = GastyCardDark), border = BorderStroke(2.dp, GastyBorderDark)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${filteredTransactions.size} movimientos encontrados", color = GastyMutedDark, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                val totalAmount = filteredTransactions.sumOf { if (it.isExpense) -it.amountNumber else it.amountNumber }
                Text(
                    text = "${if (totalAmount >= 0) "+" else "−"}$${"%,.2f".format(kotlin.math.abs(totalAmount))}",
                    color = if (totalAmount >= 0) GastyOlive else GastyCoral,
                    fontSize = 15.sp, fontWeight = FontWeight.Black
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = GastyCardDark), border = BorderStroke(2.dp, GastyBorderDark)) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                if (filteredTransactions.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 36.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "🔍", fontSize = 32.sp)
                            Text(text = "No se encontraron movimientos", color = GastyTextDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Prueba buscando con otro término", color = GastyMutedDark, fontSize = 12.sp)
                        }
                    }
                } else {
                    filteredTransactions.forEachIndexed { index, tx ->
                        TransactionRowItem(transaction = tx)
                        if (index < filteredTransactions.size - 1) {
                            HorizontalDivider(color = GastyBorderDark.copy(alpha = 0.6f), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun FilterPillChip(title: String, isSelected: Boolean, onClick: () -> Unit, activeColor: Color = GastyOlive, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick, modifier = modifier.height(38.dp), shape = RoundedCornerShape(50),
        color = if (isSelected) activeColor else GastyCardDark,
        border = BorderStroke(1.5.dp, if (isSelected) activeColor else GastyBorderDark)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = title, color = if (isSelected) Color.White else GastyMutedDark, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
        }
    }
}

// =========================================================================
// MODAL BOTTOM SHEET (SHOT 6)
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionBottomSheet(
    initialIsExpense: Boolean = true,
    selectedCategoryName: String = "Comida & Super",
    selectedCategoryEmoji: String = "🍔",
    onDismiss: () -> Unit,
    onSave: (amount: String, description: String, isExpense: Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isExpense by remember { mutableStateOf(initialIsExpense) }
    var amountText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    val activeColor = if (isExpense) GastyCoral else GastyOlive

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = GastyCardDark,
        contentColor = GastyTextDark,
        dragHandle = {
            Surface(modifier = Modifier.padding(vertical = 12.dp), shape = RoundedCornerShape(50), color = GastyBorderDark) {
                Box(modifier = Modifier.size(width = 44.dp, height = 4.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = if (isExpense) "Registrar Gasto" else "Registrar Ingreso", color = GastyTextDark, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = GastyMutedDark) }
            }

            Row(
                modifier = Modifier.fillMaxWidth().height(46.dp).clip(RoundedCornerShape(16.dp)).background(GastyBgDark).border(1.5.dp, GastyBorderDark, RoundedCornerShape(16.dp)).padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(12.dp)).background(if (isExpense) GastyCoral else Color.Transparent).clickable { isExpense = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text("− Gasto", color = if (isExpense) Color.White else GastyMutedDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(12.dp)).background(if (!isExpense) GastyOlive else Color.Transparent).clickable { isExpense = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text("+ Ingreso", color = if (!isExpense) Color.White else GastyMutedDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("MONTO", color = GastyMutedDark, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                OutlinedTextField(
                    value = amountText, onValueChange = { amountText = it },
                    prefix = { Text("$ ", color = activeColor, fontSize = 26.sp, fontWeight = FontWeight.Bold) },
                    placeholder = { Text("0.00", color = GastyMutedDark, fontSize = 26.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = GastyTextDark, unfocusedTextColor = GastyTextDark,
                        focusedBorderColor = activeColor, unfocusedBorderColor = GastyBorderDark,
                        focusedContainerColor = GastyBgDark, unfocusedContainerColor = GastyBgDark
                    )
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Categoría:", color = GastyMutedDark, fontSize = 13.sp)
                Surface(shape = RoundedCornerShape(50), color = GastyBgDark, border = BorderStroke(1.dp, GastyBorderDark)) {
                    Text("$selectedCategoryEmoji  $selectedCategoryName", color = GastyTextDark, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("DESCRIPCIÓN / NOTA", color = GastyMutedDark, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                OutlinedTextField(
                    value = descriptionText, onValueChange = { descriptionText = it },
                    placeholder = { Text("Ej. Almuerzo o compras", color = GastyMutedDark) },
                    singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = GastyTextDark, unfocusedTextColor = GastyTextDark,
                        focusedBorderColor = activeColor, unfocusedBorderColor = GastyBorderDark,
                        focusedContainerColor = GastyBgDark, unfocusedContainerColor = GastyBgDark
                    )
                )
            }

            Button(
                onClick = { onSave(amountText, descriptionText, isExpense) }, enabled = amountText.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = activeColor, disabledContainerColor = GastyBorderDark)
            ) {
                Text(if (isExpense) "Guardar Gasto" else "Guardar Ingreso", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// -------------------------------------------------------------------------
// BARRA DE NAVEGACIÓN INFERIOR
// -------------------------------------------------------------------------
@Composable
fun GastyBottomNavigationBar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit
) {
    NavigationBar(containerColor = GastyCardDark, tonalElevation = 8.dp) {
        NavTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = { Icon(imageVector = tab.icon, contentDescription = tab.title, modifier = Modifier.size(24.dp)) },
                label = { Text(text = tab.title, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GastyOlive, selectedTextColor = GastyOlive, indicatorColor = GastyOliveSoftDark,
                    unselectedIconColor = GastyMutedDark, unselectedTextColor = GastyMutedDark
                )
            )
        }
    }
}

// -------------------------------------------------------------------------
// DASHBOARD (SHOTS 1 AL 4)
// -------------------------------------------------------------------------
@Composable
fun GastyDashboardContent(
    onNewExpenseClick: () -> Unit,
    onNewIncomeClick: () -> Unit,
    onCategoryClick: (CategoryUiModel) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        HeaderSection()
        BalanceCard(currentBalance = "$4,250.00", daysUntilNextIncome = 12, dailyBudget = "$85.00/día")
        QuickActionButtons(onExpenseClick = onNewExpenseClick, onIncomeClick = onNewIncomeClick)
        MetricsSummaryRow(variableExpenses = "$1,120.50", monthlyIncome = "$3,400.00")
        QuickCategoriesSection(onCategoryClick = onCategoryClick)
        RecentTransactionsSection()
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun HeaderSection() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = "GASTY CONTROL", color = GastyOlive, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Text(text = "Hola, Jhon 👋", color = GastyTextDark, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        }
        Surface(shape = CircleShape, color = GastyOliveSoftDark, border = BorderStroke(2.dp, GastyOlive), modifier = Modifier.size(46.dp)) {
            Box(contentAlignment = Alignment.Center) { Text(text = "J", color = GastyOlive, fontSize = 20.sp, fontWeight = FontWeight.Black) }
        }
    }
}

@Composable
fun BalanceCard(currentBalance: String, daysUntilNextIncome: Int, dailyBudget: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = GastyCardDark), border = BorderStroke(2.dp, GastyBorderDark)) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "DINERO ACTUAL EN CUENTA", color = GastyMutedDark, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Surface(shape = RoundedCornerShape(50), color = GastyOliveSoftDark, border = BorderStroke(1.dp, GastyOlive.copy(alpha = 0.3f))) {
                    Text(text = "En cuenta", color = GastyOlive, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }
            Text(text = currentBalance, color = GastyOlive, fontSize = 44.sp, fontWeight = FontWeight.Black, letterSpacing = (-1).sp)
            Text(text = "Próximo cobro en $daysUntilNextIncome días • Sugerido: $dailyBudget", color = GastyMutedDark, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun QuickActionButtons(onExpenseClick: () -> Unit, onIncomeClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(onClick = onExpenseClick, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = GastyCoral)) {
            Text(text = "− Nuevo Gasto", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Button(onClick = onIncomeClick, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = GastyOlive)) {
            Text(text = "+ Nuevo Ingreso", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MetricsSummaryRow(variableExpenses: String, monthlyIncome: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        MetricCard("GASTOS DEL MES", variableExpenses, GastyCoral, "En control", GastyCoral, GastyCoralSoftDark, Modifier.weight(1f))
        MetricCard("INGRESOS DEL MES", monthlyIncome, GastyOlive, "Estimado", GastyOlive, GastyOliveSoftDark, Modifier.weight(1f))
    }
}

@Composable
fun MetricCard(title: String, amount: String, amountColor: Color, badgeText: String, badgeColor: Color, badgeBg: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = GastyCardDark), border = BorderStroke(2.dp, GastyBorderDark)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = title, color = GastyMutedDark, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Text(text = amount, color = amountColor, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Surface(shape = RoundedCornerShape(50), color = badgeBg, border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.3f))) {
                Text(text = badgeText, color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
            }
        }
    }
}

@Composable
fun QuickCategoriesSection(onCategoryClick: (CategoryUiModel) -> Unit) {
    val categories = listOf(
        CategoryUiModel("1", "Comida & Super", "🍔", "$184.20", GastyAmber, GastyAmberSoftDark),
        CategoryUiModel("2", "Transporte", "🚗", "$45.00", GastyBlue, GastyBlueSoftDark),
        CategoryUiModel("3", "Servicios & Casa", "💡", "$120.00", GastyPurple, GastyPurpleSoftDark),
        CategoryUiModel("4", "Salidas & Ocio", "🍿", "$71.30", GastyCoral, GastyCoralSoftDark)
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(text = "Registrar Gasto Rápido", color = GastyTextDark, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "Toca para añadir un gasto instantáneamente", color = GastyMutedDark, fontSize = 12.sp)
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CategoryCardItem(category = categories[0], onClick = { onCategoryClick(categories[0]) }, modifier = Modifier.weight(1f))
            CategoryCardItem(category = categories[1], onClick = { onCategoryClick(categories[1]) }, modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CategoryCardItem(category = categories[2], onClick = { onCategoryClick(categories[2]) }, modifier = Modifier.weight(1f))
            CategoryCardItem(category = categories[3], onClick = { onCategoryClick(categories[3]) }, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun CategoryCardItem(category: CategoryUiModel, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.clickable { onClick() }, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = GastyCardDark), border = BorderStroke(2.dp, GastyBorderDark)) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(shape = CircleShape, color = category.softBgColor, border = BorderStroke(1.dp, category.accentColor.copy(alpha = 0.4f)), modifier = Modifier.size(38.dp)) {
                Box(contentAlignment = Alignment.Center) { Text(text = category.iconEmoji, fontSize = 18.sp) }
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = category.name, color = GastyTextDark, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(text = category.spentThisMonth, color = GastyMutedDark, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun RecentTransactionsSection() {
    val transactions = listOf(
        TransactionUiModel("tx-1", "Supermercado Olé", "Comida & Super", "🍔", "Hoy, 14:30", "−$64.20", 64.20, true, GastyAmber, GastyAmberSoftDark),
        TransactionUiModel("tx-2", "Depósito Nómina Quincena", "Ingreso", "💰", "Ayer", "+$1,550.00", 1550.00, false, GastyOlive, GastyOliveSoftDark),
        TransactionUiModel("tx-3", "Gasolina Texaco", "Transporte", "🚗", "7 Oct", "−$35.00", 35.00, true, GastyBlue, GastyBlueSoftDark),
        TransactionUiModel("tx-4", "Suscripción Netflix & Spotify", "Servicios & Casa", "💡", "5 Oct", "−$19.99", 19.99, true, GastyPurple, GastyPurpleSoftDark)
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Últimos Movimientos", color = GastyTextDark, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = "Ver todos", color = GastyOlive, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { })
        }
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = GastyCardDark), border = BorderStroke(2.dp, GastyBorderDark)) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                transactions.forEachIndexed { index, tx ->
                    TransactionRowItem(transaction = tx)
                    if (index < transactions.size - 1) {
                        HorizontalDivider(color = GastyBorderDark.copy(alpha = 0.6f), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionRowItem(transaction: TransactionUiModel) {
    Row(modifier = Modifier.fillMaxWidth().clickable { }, horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = transaction.softBgColor, border = BorderStroke(1.dp, transaction.accentColor.copy(alpha = 0.4f)), modifier = Modifier.size(42.dp)) {
                Box(contentAlignment = Alignment.Center) { Text(text = transaction.categoryEmoji, fontSize = 20.sp) }
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = transaction.title, color = GastyTextDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = "${transaction.dateLabel} • ${transaction.categoryName}", color = GastyMutedDark, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
        Text(text = transaction.amountFormatted, color = if (transaction.isExpense) GastyCoral else GastyOlive, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF101714)
@Composable
fun GastyAppPreview() {
    GastyApp()
}
