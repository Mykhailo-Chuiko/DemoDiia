package ua.demo.diia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF071B3A)
private val Blue = Color(0xFF1769E0)
private val Page = Color(0xFFF3F5F8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DemoApp() }
    }
}

@Composable
fun DemoApp() {
    var selected by remember { mutableIntStateOf(0) }
    MaterialTheme(colorScheme = lightColorScheme(primary = Blue, background = Page)) {
        Scaffold(
            containerColor = Page,
            bottomBar = {
                NavigationBar(containerColor = Color.White) {
                    NavigationBarItem(selected == 0, { selected = 0 }, icon = { Icon(Icons.Default.Folder, null) }, label = { Text("Документи") })
                    NavigationBarItem(selected == 1, { selected = 1 }, icon = { Icon(Icons.Default.GridView, null) }, label = { Text("Послуги") })
                    NavigationBarItem(selected == 2, { selected = 2 }, icon = { Icon(Icons.Default.Notifications, null) }, label = { Text("Сповіщення") })
                    NavigationBarItem(selected == 3, { selected = 3 }, icon = { Icon(Icons.Default.Menu, null) }, label = { Text("Меню") })
                }
            }
        ) { padding ->
            when (selected) {
                0 -> DocumentsScreen(Modifier.padding(padding))
                1 -> SimpleScreen("Послуги", "Тут будуть демонстраційні послуги.", Modifier.padding(padding))
                2 -> SimpleScreen("Сповіщення", "Поки що нових повідомлень немає.", Modifier.padding(padding))
                else -> SimpleScreen("Меню", "Профіль, налаштування та інформація про демоверсію.", Modifier.padding(padding))
            }
        }
    }
}

@Composable
fun DocumentsScreen(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Вітаємо!", color = Navy, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("Ваші демодокументи", color = Color(0xFF667085), fontSize = 14.sp)
            }
            Icon(Icons.Default.AccountCircle, "Профіль", tint = Navy, modifier = Modifier.size(32.dp))
        }
        Surface(color = Color(0xFFFFF3D6), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = Color(0xFF8A5A00))
                Spacer(Modifier.width(8.dp))
                Text("ДЕМОВЕРСІЯ • Усі дані вигадані. Не є офіційними документами.", color = Color(0xFF704900), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        Text("Документи", color = Navy, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        DemoDocumentCard("Паспорт громадянина", "Демонстраційна картка", "ТЕСТ • 000000", Icons.Default.Badge, listOf(Color(0xFF123C7A), Color(0xFF2478D4)))
        DemoDocumentCard("Посвідчення водія", "Демонстраційна картка", "ТЕСТ • DEMO-001", Icons.Default.DirectionsCar, listOf(Color(0xFF243B55), Color(0xFF456B8C)))
        OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(14.dp)) {
            Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Додати демодокумент")
        }
    }
}

@Composable
fun DemoDocumentCard(title: String, subtitle: String, number: String, icon: androidx.compose.ui.graphics.vector.ImageVector, colors: List<Color>) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Brush.linearGradient(colors)).padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(10.dp))
            Text(title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text("DEMO", color = Color.White.copy(alpha = .8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(22.dp))
        Text(subtitle, color = Color.White.copy(alpha = .8f), fontSize = 12.sp)
        Text(number, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text("НЕ Є ДОКУМЕНТОМ", color = Color.White.copy(alpha = .8f), fontSize = 10.sp, letterSpacing = 1.sp)
    }
}

@Composable
fun SimpleScreen(title: String, description: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, color = Navy, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(description, color = Color(0xFF667085), fontSize = 15.sp)
    }
}
