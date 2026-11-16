package ua.demo.diia

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

private val Ink = Color(0xFF101114)
private val Blue = Color(0xFF1769E8)
private val Sky = Color(0xFFD9F0FF)
private val Mint = Color(0xFFE1F5E9)
private val Muted = Color(0xFF747982)
private val Page = Color(0xFFEAF4FA)
private val Card = Color(0xFFF8FCFF)
private val Black = Color(0xFF08090B)

data class DemoPerson(
    val surname: String = "",
    val name: String = "",
    val patronymic: String = "",
    val birth: String = "",
    val docNumber: String = "DEMO-000001",
    val record: String = "TEST-000001"
)

data class ServiceTile(val title: String, val icon: String, val tint: Color)

private val services = listOf(
    ServiceTile("Допомога армії", "♡", Color(0xFFE6F3FF)),
    ServiceTile("Незламність", "ϟ", Color(0xFFE4F5EC)),
    ServiceTile("Військові облігації", "₴", Color(0xFFFFF0D8)),
    ServiceTile("єВідновлення", "⌂", Color(0xFFECE9FF)),
    ServiceTile("Водіям", "↗", Color(0xFFE6F3FF)),
    ServiceTile("Місце проживання", "⌖", Color(0xFFE4F5EC)),
    ServiceTile("Опитування", "☷", Color(0xFFFFF0D8)),
    ServiceTile("Податки", "%", Color(0xFFECE9FF)),
    ServiceTile("Довідки та витяги", "▤", Color(0xFFE6F3FF)),
    ServiceTile("Послуги для ВПО", "⌂", Color(0xFFE4F5EC))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DemoDiiaApp(applicationContext) }
    }
}

@Composable
fun DemoDiiaApp(context: Context) {
    val prefs = remember { context.getSharedPreferences("demo_profile", Context.MODE_PRIVATE) }
    var person by remember {
        mutableStateOf(
            DemoPerson(
                prefs.getString("surname", "") ?: "",
                prefs.getString("name", "") ?: "",
                prefs.getString("patronymic", "") ?: "",
                prefs.getString("birth", "") ?: "",
                prefs.getString("docNumber", "DEMO-000001") ?: "DEMO-000001",
                prefs.getString("record", "TEST-000001") ?: "TEST-000001"
            )
        )
    }
    var tab by remember { mutableIntStateOf(0) }
    var page by remember { mutableStateOf("root") }
    var selectedService by remember { mutableStateOf<ServiceTile?>(null) }

    MaterialTheme(colorScheme = lightColorScheme(primary = Blue, background = Page, surface = Card)) {
        Scaffold(
            containerColor = Page,
            bottomBar = {
                NavigationBar(containerColor = Black, contentColor = Color.White, tonalElevation = 0.dp) {
                    val labels = listOf("Стрічка", "Документи", "Сервіси", "Меню")
                    val icons = listOf(Icons.Default.Home, Icons.Default.Badge, Icons.Default.GridView, Icons.Default.Menu)
                    labels.forEachIndexed { index, label ->
                        NavigationBarItem(
                            selected = tab == index && page == "root",
                            onClick = { tab = index; page = "root"; selectedService = null },
                            icon = { Icon(icons[index], contentDescription = label) },
                            label = { Text(label, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color(0xFF9CA3AF),
                                unselectedTextColor = Color(0xFF9CA3AF),
                                indicatorColor = Color(0xFF34383F)
                            )
                        )
                    }
                }
            }
        ) { padding ->
            when {
                page == "edit" -> ProfileEditor(person, onBack = { page = "root" }, onSave = { updated ->
                    person = updated
                    prefs.edit()
                        .putString("surname", updated.surname)
                        .putString("name", updated.name)
                        .putString("patronymic", updated.patronymic)
                        .putString("birth", updated.birth)
                        .putString("docNumber", updated.docNumber)
                        .putString("record", updated.record)
                        .apply()
                    page = "root"
                }, modifier = Modifier.padding(padding))
                page == "passport" -> DemoPassport(person, onBack = { page = "root" }, modifier = Modifier.padding(padding))
                page == "notifications" -> NotificationsScreen(onBack = { page = "root" }, modifier = Modifier.padding(padding))
                page == "service" && selectedService != null -> ServiceDetail(selectedService!!, onBack = { page = "root" }, modifier = Modifier.padding(padding))
                page == "about" -> AboutScreen(onBack = { page = "root" }, modifier = Modifier.padding(padding))
                tab == 0 -> FeedScreen(person, onDocuments = { tab = 1 }, onNotifications = { page = "notifications" }, modifier = Modifier.padding(padding))
                tab == 1 -> DocumentsScreen(person, onOpen = { page = "passport" }, modifier = Modifier.padding(padding))
                tab == 2 -> ServicesScreen(onSelect = { selectedService = it; page = "service" }, modifier = Modifier.padding(padding))
                else -> MenuScreen(onEdit = { page = "edit" }, onNotifications = { page = "notifications" }, onAbout = { page = "about" }, modifier = Modifier.padding(padding))
            }
        }
    }
}

@Composable
private fun DemoTag(modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = Color(0xFFFFE8A3), shape = RoundedCornerShape(7.dp)) {
        Text("ДЕМО • НЕ ОФІЦІЙНИЙ ЗАСТОСУНОК", modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp), color = Color(0xFF634600), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = .35.sp)
    }
}

@Composable
private fun ScreenTitle(title: String, subtitle: String? = null) {
    Text(title, fontSize = 29.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold, color = Ink)
    if (subtitle != null) Text(subtitle, color = Muted, fontSize = 13.sp)
}

@Composable
private fun FeedScreen(person: DemoPerson, onDocuments: () -> Unit, onNotifications: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFD8F0FF), Color(0xFFE7F5F1), Page))).verticalScroll(rememberScrollState()).padding(horizontal = 17.dp)) {
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Привіт 👋", fontSize = 29.sp, fontWeight = FontWeight.Bold, color = Ink)
                Text("Раді тебе бачити", color = Color(0xFF4F5D69), fontSize = 14.sp)
            }
            Box(Modifier.size(43.dp).clip(CircleShape).background(Color.White.copy(.85f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, null, tint = Ink, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(Modifier.height(14.dp))
        DemoTag()
        Spacer(Modifier.height(15.dp))
        Surface(color = Color.White.copy(.75f), shape = RoundedCornerShape(17.dp), modifier = Modifier.fillMaxWidth().clickable { onNotifications() }) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(38.dp).clip(CircleShape).background(Color(0xFFE8F2FF)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Notifications, null, tint = Blue)
                }
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f)) {
                    Text("Нові повідомлення", fontWeight = FontWeight.Bold, color = Ink, fontSize = 14.sp)
                    Text("Переглянути демоповідомлення", color = Muted, fontSize = 12.sp)
                }
                Icon(Icons.Default.ChevronRight, null, tint = Ink)
            }
        }
        Spacer(Modifier.height(14.dp))
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Brush.linearGradient(listOf(Color(0xFF122C4B), Color(0xFF176BC4), Color(0xFF54B8E8)))).clickable { onDocuments() }.padding(18.dp)) {
            Text("Дія.Картка", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Демонстраційна картка • без платіжних функцій", color = Color.White.copy(.83f), fontSize = 11.sp)
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Відкрити демо-картку", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Icon(Icons.Default.ArrowForward, null, tint = Color.White)
            }
            Text("DEMO", color = Color.White.copy(.55f), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
        }
        Spacer(Modifier.height(15.dp))
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color.White.copy(.8f)).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Незламність", fontWeight = FontWeight.Bold, color = Ink, fontSize = 18.sp, modifier = Modifier.weight(1f))
                Text("ϟ", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Blue)
            }
            Text("Мапа пунктів незламності та укриттів", color = Muted, fontSize = 12.sp)
            Spacer(Modifier.height(10.dp))
            Text("Демоінформація. Перегляд карти недоступний.", color = Ink, fontSize = 12.sp)
        }
        Spacer(Modifier.height(15.dp))
        Text("Швидкі дії", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Ink)
        Spacer(Modifier.height(9.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            QuickAction("Сканувати\nQR-код", "▦", Modifier.weight(1f))
            QuickAction("Військові\nоблігації", "₴", Modifier.weight(1f))
            QuickAction("єВорог", "◉", Modifier.weight(1f))
        }
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun QuickAction(title: String, symbol: String, modifier: Modifier = Modifier) {
    Column(modifier.clip(RoundedCornerShape(15.dp)).background(Color.White.copy(.86f)).padding(vertical = 13.dp, horizontal = 5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(38.dp).clip(CircleShape).background(Black), contentAlignment = Alignment.Center) {
            Text(symbol, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(7.dp))
        Text(title, color = Ink, fontSize = 10.sp, lineHeight = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DocumentsScreen(person: DemoPerson, onOpen: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFD9F0FF), Page))).verticalScroll(rememberScrollState()).padding(17.dp)) {
        Spacer(Modifier.height(17.dp))
        ScreenTitle("Документи", "Ваші демодокументи")
        Spacer(Modifier.height(12.dp))
        DemoTag()
        Spacer(Modifier.height(17.dp))
        DemoDocumentCard(person, onOpen)
        Spacer(Modifier.height(13.dp))
        Surface(color = Color.White.copy(.9f), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().clickable { onOpen() }) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFE5F1FF)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.DirectionsCar, null, tint = Blue)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Посвідчення водія", color = Ink, fontWeight = FontWeight.SemiBold)
                    Text("Макет документа • DEMO", color = Muted, fontSize = 11.sp)
                }
                Icon(Icons.Default.ChevronRight, null, tint = Muted)
            }
        }
        Spacer(Modifier.height(18.dp))
        Text("Документи в цьому застосунку — лише демонстраційні макети.", color = Muted, fontSize = 12.sp)
    }
}

@Composable
private fun DemoDocumentCard(person: DemoPerson, onOpen: () -> Unit) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Brush.linearGradient(listOf(Color(0xFFBFE7F8), Color(0xFFE6F7F1), Color(0xFFB8DDF6)))).clickable { onOpen() }.padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("ДЕМОПАСПОРТ", color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, letterSpacing = .8.sp)
            Spacer(Modifier.weight(1f))
            Text("DEMO", color = Color(0xFF9C1C1C), fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.border(1.dp, Color(0xFF9C1C1C), RoundedCornerShape(5.dp)).padding(horizontal = 7.dp, vertical = 4.dp))
        }
        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(74.dp).clip(RoundedCornerShape(13.dp)).background(Color.White.copy(.75f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, null, tint = Color(0xFF7D9BAE), modifier = Modifier.size(48.dp))
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text(listOf(person.surname, person.name, person.patronymic).filter { it.isNotBlank() }.joinToString(" ").ifBlank { "Ім’я не вказано" }, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(5.dp))
                Text(if (person.birth.isBlank()) "Дата народження —" else "Дата народження: " + person.birth, color = Ink.copy(.75f), fontSize = 11.sp)
                Text("№ " + person.docNumber, color = Ink.copy(.75f), fontSize = 11.sp)
            }
        }
        Spacer(Modifier.height(14.dp))
        Box(Modifier.fillMaxWidth().background(Color.White.copy(.48f), RoundedCornerShape(8.dp)).padding(8.dp), contentAlignment = Alignment.Center) {
            Text("НЕ Є ДОКУМЕНТОМ • НЕ ПІДТВЕРДЖУЄ ОСОБУ", color = Color(0xFF8D2020), fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = .5.sp)
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Натисніть, щоб переглянути макет", color = Ink.copy(.65f), fontSize = 10.sp, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = Ink)
        }
    }
}

@Composable
private fun DemoPassport(person: DemoPerson, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().background(Page).verticalScroll(rememberScrollState()).padding(17.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Назад", tint = Ink) }
            Text("Демопаспорт", fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Ink)
        }
        DemoTag()
        Spacer(Modifier.height(15.dp))
        DemoDocumentCard(person) {}
        Spacer(Modifier.height(14.dp))
        Surface(color = Color.White, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Тестові дані", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Ink)
                Spacer(Modifier.height(10.dp))
                InfoRow("Прізвище", person.surname)
                InfoRow("Ім’я", person.name)
                InfoRow("По батькові", person.patronymic)
                InfoRow("Дата народження", person.birth)
                InfoRow("Номер демодокумента", person.docNumber)
                InfoRow("Тестовий номер запису", person.record)
            }
        }
        Spacer(Modifier.height(14.dp))
        Text("Цей екран є макетом для тестування інтерфейсу. Він не містить справжнього QR-коду, підпису чи державної перевірки.", color = Muted, fontSize = 12.sp)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 7.dp)) {
        Text(label, color = Muted, fontSize = 11.sp)
        Text(value.ifBlank { "Не вказано" }, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
    HorizontalDivider(color = Color(0xFFE9EEF2))
}

@Composable
private fun ServicesScreen(onSelect: (ServiceTile) -> Unit, modifier: Modifier = Modifier) {
    var query by remember { mutableStateOf("") }
    val filtered = services.filter { it.title.contains(query, ignoreCase = true) }
    Column(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFD9F0FF), Page)))) {
        Column(Modifier.padding(horizontal = 17.dp).padding(top = 18.dp, bottom = 10.dp)) {
            ScreenTitle("Сервіси")
            Spacer(Modifier.height(13.dp))
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Пошук сервісів", color = Muted) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Muted) },
                singleLine = true, shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color.Transparent, focusedBorderColor = Blue)
            )
            Spacer(Modifier.height(8.dp))
            DemoTag()
        }
        LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(start = 17.dp, end = 17.dp, bottom = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            items(filtered) { item ->
                Column(Modifier.fillMaxWidth().height(126.dp).clip(RoundedCornerShape(19.dp)).background(Color.White.copy(.88f)).clickable { onSelect(item) }.padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
                    Box(Modifier.size(35.dp).clip(CircleShape).background(Black), contentAlignment = Alignment.Center) {
                        Text(item.icon, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(item.title, color = Ink, fontSize = 14.sp, lineHeight = 17.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun ServiceDetail(item: ServiceTile, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().background(Page).padding(17.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Назад", tint = Ink) }
            Text(item.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Ink)
        }
        Surface(color = Color.White, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                DemoTag()
                Spacer(Modifier.height(13.dp))
                Text(item.title, color = Ink, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Text("Демонстраційний розділ. Реальні державні послуги тут не надаються.", color = Muted, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun MenuScreen(onEdit: () -> Unit, onNotifications: () -> Unit, onAbout: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFD9F0FF), Page))).verticalScroll(rememberScrollState()).padding(17.dp)) {
        Spacer(Modifier.height(17.dp))
        ScreenTitle("Меню", "Налаштування та інформація")
        Spacer(Modifier.height(13.dp))
        DemoTag()
        Spacer(Modifier.height(17.dp))
        Surface(color = Color.White, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
            Column {
                MenuRow(Icons.Default.Person, "Дані демопаспорта", "Редагувати тестові дані", onEdit)
                MenuDivider()
                MenuRow(Icons.Default.Notifications, "Повідомлення", "Переглянути демоповідомлення", onNotifications)
                MenuDivider()
                MenuRow(Icons.Default.Settings, "Налаштування", "Параметри демозастосунку", onAbout)
                MenuDivider()
                MenuRow(Icons.Default.Info, "Про застосунок", "Версія та обмеження демоверсії", onAbout)
                MenuDivider()
                MenuRow(Icons.Default.SupportAgent, "Підтримка", "Демонстраційний розділ", onAbout)
            }
        }
        Spacer(Modifier.height(15.dp))
        Text("DemoDiia • тестовий інтерфейс. Не є офіційним продуктом державних органів.", color = Muted, fontSize = 11.sp)
    }
}

@Composable
private fun MenuRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, action: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { action() }.padding(horizontal = 15.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFEAF3FB)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = Ink, modifier = Modifier.size(21.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(subtitle, color = Muted, fontSize = 11.sp)
        }
        Icon(Icons.Default.ChevronRight, null, tint = Muted)
    }
}

@Composable
private fun MenuDivider() { HorizontalDivider(color = Color(0xFFEDF1F4), modifier = Modifier.padding(start = 67.dp)) }

@Composable
private fun ProfileEditor(person: DemoPerson, onBack: () -> Unit, onSave: (DemoPerson) -> Unit, modifier: Modifier = Modifier) {
    var surname by remember(person) { mutableStateOf(person.surname) }
    var name by remember(person) { mutableStateOf(person.name) }
    var patronymic by remember(person) { mutableStateOf(person.patronymic) }
    var birth by remember(person) { mutableStateOf(person.birth) }
    var number by remember(person) { mutableStateOf(person.docNumber) }
    var record by remember(person) { mutableStateOf(person.record) }
    Column(modifier.fillMaxSize().background(Page).verticalScroll(rememberScrollState()).padding(17.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Назад", tint = Ink) }
            Text("Дані демопаспорта", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Ink)
        }
        DemoTag()
        Spacer(Modifier.height(12.dp))
        Text("Введіть тестові дані", color = Ink, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Text("Дані зберігаються лише на цьому пристрої та використовуються тільки в макеті.", color = Muted, fontSize = 12.sp)
        EditorField("Прізвище", surname) { surname = it }
        EditorField("Ім’я", name) { name = it }
        EditorField("По батькові", patronymic) { patronymic = it }
        EditorField("Дата народження (ДД.ММ.РРРР)", birth) { birth = it }
        EditorField("Номер демодокумента", number) { number = it }
        EditorField("Тестовий номер запису", record) { record = it }
        Spacer(Modifier.height(17.dp))
        Button(onClick = {
            onSave(DemoPerson(surname.trim(), name.trim(), patronymic.trim(), birth.trim(), number.trim().ifBlank { "DEMO-000001" }, record.trim().ifBlank { "TEST-000001" }))
        }, modifier = Modifier.fillMaxWidth().height(53.dp), shape = RoundedCornerShape(15.dp)) {
            Text("Зберегти демодані", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun EditorField(label: String, value: String, onChange: (String) -> Unit) {
    Column(Modifier.padding(top = 11.dp)) {
        Text(label, color = Ink, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(value, onChange, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(13.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, focusedBorderColor = Blue, unfocusedBorderColor = Color(0xFFD5E0E8)))
    }
}

@Composable
private fun NotificationsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().background(Page).padding(17.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Назад", tint = Ink) }
            Text("Повідомлення", fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Ink)
        }
        DemoTag()
        Spacer(Modifier.height(15.dp))
        Surface(color = Color.White, shape = RoundedCornerShape(17.dp), modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Campaign, null, tint = Blue)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Вітаємо в DemoDiia!", color = Ink, fontWeight = FontWeight.Bold)
                    Text("Це тестове повідомлення для демонстрації інтерфейсу.", color = Muted, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun AboutScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().background(Page).padding(17.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Назад", tint = Ink) }
            Text("Про демоверсію", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Ink)
        }
        DemoTag()
        Spacer(Modifier.height(14.dp))
        Surface(color = Color.White, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("DemoDiia", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Ink)
                Text("Навчальний макет інтерфейсу. Не пов’язаний із Міністерством цифрової трансформації України та не надає державних послуг.", color = Muted, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                Text("Версія 0.2 • Усі дані тестові", color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }
    }
}
