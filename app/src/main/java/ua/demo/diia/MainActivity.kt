package ua.demo.diia

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

private val Ink = Color(0xFF101828)
private val Blue = Color(0xFF0066F5)
private val Pale = Color(0xFFF3F6FA)
private val Muted = Color(0xFF667085)

data class DemoPerson(val surname:String="", val name:String="", val patronymic:String="", val birth:String="", val docNumber:String="DEMO-000001", val record:String="TEST-000001")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DemoApp(applicationContext) }
    }
}

@Composable
fun DemoApp(context: Context) {
    val prefs = remember { context.getSharedPreferences("demo_profile", Context.MODE_PRIVATE) }
    var person by remember { mutableStateOf(DemoPerson(
        prefs.getString("surname","") ?: "", prefs.getString("name","") ?: "",
        prefs.getString("patronymic","") ?: "", prefs.getString("birth","") ?: "",
        prefs.getString("docNumber","DEMO-000001") ?: "DEMO-000001",
        prefs.getString("record","TEST-000001") ?: "TEST-000001")) }
    var tab by remember { mutableIntStateOf(0) }
    var page by remember { mutableIntStateOf(0) }
    MaterialTheme(colorScheme = lightColorScheme(primary=Blue, background=Pale, surface=Color.White)) {
        Scaffold(containerColor=Pale, bottomBar={
            NavigationBar(containerColor=Color.White) {
                NavigationBarItem(tab==0,{tab=0;page=0},{Icon(Icons.Default.AccountBox,null)},label={Text("Документи")})
                NavigationBarItem(tab==1,{tab=1;page=0},{Icon(Icons.Default.GridView,null)},label={Text("Послуги")})
                NavigationBarItem(tab==2,{tab=2;page=0},{Icon(Icons.Default.NotificationsNone,null)},label={Text("Сповіщення")})
                NavigationBarItem(tab==3,{tab=3;page=0},{Icon(Icons.Default.Menu,null)},label={Text("Меню")})
            }
        }) { pad ->
            when {
                page==1 -> Editor(person, {page=0}) { p ->
                    person=p
                    prefs.edit().putString("surname",p.surname).putString("name",p.name).putString("patronymic",p.patronymic)
                        .putString("birth",p.birth).putString("docNumber",p.docNumber).putString("record",p.record).apply()
                    page=0
                }
                page==2 -> Passport(person, {page=0}, Modifier.padding(pad))
                tab==0 -> Home(person, {page=2}, Modifier.padding(pad))
                tab==3 -> Menu({page=1}, Modifier.padding(pad))
                else -> Simple(if(tab==1) "Послуги" else "Сповіщення", Modifier.padding(pad))
            }
        }
    }
}

@Composable
fun Notice() {
    Surface(color=Color(0xFFFFF1CC), shape=RoundedCornerShape(13.dp), modifier=Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment=Alignment.CenterVertically) {
            Icon(Icons.Default.Info,null,tint=Color(0xFF805500))
            Spacer(Modifier.width(9.dp))
            Text("ДЕМОЗАСТОСУНОК. Дані вигадані. Не є офіційними документами.", color=Color(0xFF684900), fontSize=12.sp, fontWeight=FontWeight.Medium)
        }
    }
}

@Composable
fun Home(p:DemoPerson, open:()->Unit, modifier:Modifier=Modifier) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment=Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Документи",fontSize=30.sp,fontWeight=FontWeight.Bold,color=Ink)
                Text("Ваші цифрові документи",fontSize=14.sp,color=Muted)
            }
            Box(Modifier.size(44.dp).clip(CircleShape).background(Color.White),contentAlignment=Alignment.Center) {
                Icon(Icons.Default.Person,null,tint=Blue,modifier=Modifier.size(27.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Notice()
        Spacer(Modifier.height(20.dp))
        Text("Мої документи",fontSize=20.sp,fontWeight=FontWeight.Bold,color=Ink)
        Spacer(Modifier.height(12.dp))
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Brush.linearGradient(listOf(Color(0xFF123C72),Color(0xFF0877D5)))).clickable { open() }.padding(18.dp)) {
            Row(verticalAlignment=Alignment.CenterVertically) {
                Text("ДЕМОПАСПОРТ",color=Color.White,fontWeight=FontWeight.Bold,fontSize=12.sp,letterSpacing=1.sp)
                Spacer(Modifier.weight(1f))
                Text("DEMO",color=Color.White,fontWeight=FontWeight.Bold,modifier=Modifier.border(1.dp,Color.White,RoundedCornerShape(5.dp)).padding(5.dp))
            }
            Spacer(Modifier.height(24.dp))
            Text((p.surname+" "+p.name).trim().ifBlank { "Вкажіть демодані" },color=Color.White,fontSize=20.sp,fontWeight=FontWeight.Bold)
            Text(if(p.birth.isBlank()) "Дата народження не вказана" else "Дата народження: "+p.birth,color=Color.White.copy(.85f),fontSize=13.sp)
            Spacer(Modifier.height(16.dp))
            Text(p.docNumber+"  •  НЕ Є ДОКУМЕНТОМ",color=Color.White,fontSize=10.sp,fontWeight=FontWeight.Bold)
        }
        Spacer(Modifier.height(13.dp))
        Surface(color=Color.White,shape=RoundedCornerShape(18.dp),modifier=Modifier.fillMaxWidth()) {
            Row(Modifier.padding(17.dp),verticalAlignment=Alignment.CenterVertically) {
                Icon(Icons.Default.DirectionsCar,null,tint=Blue)
                Spacer(Modifier.width(12.dp))
                Column { Text("Посвідчення водія",fontWeight=FontWeight.SemiBold,color=Ink); Text("Демо-документ",fontSize=12.sp,color=Muted) }
                Spacer(Modifier.weight(1f)); Icon(Icons.Default.ChevronRight,null,tint=Muted)
            }
        }
        Spacer(Modifier.height(18.dp))
        Text("Налаштуйте демодокумент у розділі «Меню».",color=Muted,fontSize=13.sp)
    }
}

@Composable
fun Menu(edit:()->Unit, modifier:Modifier=Modifier) {
    Column(modifier.fillMaxSize().padding(18.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("Меню",fontSize=30.sp,fontWeight=FontWeight.Bold,color=Ink)
        Text("Налаштування демозастосунку",color=Muted)
        Spacer(Modifier.height(20.dp))
        Surface(color=Color.White,shape=RoundedCornerShape(18.dp),modifier=Modifier.fillMaxWidth()) {
            Column {
                Row(Modifier.fillMaxWidth().clickable { edit() }.padding(17.dp),verticalAlignment=Alignment.CenterVertically) {
                    Icon(Icons.Default.Badge,null,tint=Blue); Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) { Text("Дані демопаспорта",fontWeight=FontWeight.SemiBold,color=Ink); Text("Ввести або змінити тестові дані",fontSize=12.sp,color=Muted) }
                    Icon(Icons.Default.ChevronRight,null,tint=Muted)
                }
                HorizontalDivider(color=Pale)
                Row(Modifier.padding(17.dp),verticalAlignment=Alignment.CenterVertically) {
                    Icon(Icons.Default.Info,null,tint=Blue); Spacer(Modifier.width(13.dp))
                    Text("Про демоверсію • Неофіційний макет",color=Ink,fontSize=13.sp)
                }
            }
        }
    }
}

@Composable
fun Editor(p:DemoPerson, back:()->Unit, save:(DemoPerson)->Unit) {
    var surname by remember { mutableStateOf(p.surname) }
    var name by remember { mutableStateOf(p.name) }
    var patronymic by remember { mutableStateOf(p.patronymic) }
    var birth by remember { mutableStateOf(p.birth) }
    var number by remember { mutableStateOf(p.docNumber) }
    var record by remember { mutableStateOf(p.record) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically) { IconButton(onClick=back){Icon(Icons.Default.ArrowBack,"Назад")}; Text("Дані демопаспорта",fontSize=23.sp,fontWeight=FontWeight.Bold,color=Ink) }
        Notice()
        Spacer(Modifier.height(16.dp))
        Text("Тестові персональні дані",fontSize=18.sp,fontWeight=FontWeight.Bold,color=Ink)
        Text("Зберігаються лише на цьому телефоні.",fontSize=13.sp,color=Muted)
        Field("Прізвище",surname){surname=it}
        Field("Ім’я",name){name=it}
        Field("По батькові",patronymic){patronymic=it}
        Field("Дата народження (ДД.ММ.РРРР)",birth){birth=it}
        Field("Номер демодокумента",number){number=it}
        Field("Тестовий номер запису",record){record=it}
        Button(onClick={save(DemoPerson(surname.trim(),name.trim(),patronymic.trim(),birth.trim(),number.trim().ifBlank{"DEMO-000001"},record.trim().ifBlank{"TEST-000001"}))},modifier=Modifier.fillMaxWidth().height(54.dp),shape=RoundedCornerShape(14.dp)) { Text("Зберегти демодані") }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun Field(label:String,value:String,onChange:(String)->Unit) {
    Column(Modifier.padding(top=11.dp)) {
        Text(label,color=Ink,fontSize=13.sp,fontWeight=FontWeight.Medium)
        OutlinedTextField(value,onChange,modifier=Modifier.fillMaxWidth(),singleLine=true,shape=RoundedCornerShape(12.dp))
    }
}

@Composable
fun Passport(p:DemoPerson,back:()->Unit,modifier:Modifier=Modifier) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically) { IconButton(onClick=back){Icon(Icons.Default.ArrowBack,"Назад")}; Text("Демопаспорт",fontSize=24.sp,fontWeight=FontWeight.Bold,color=Ink) }
        Notice()
        Spacer(Modifier.height(16.dp))
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Brush.linearGradient(listOf(Color(0xFF123C72),Color(0xFF0877D5)))).padding(20.dp)) {
            Text("ДЕМОПАСПОРТ • НЕ Є ДОКУМЕНТОМ",color=Color.White,fontWeight=FontWeight.Bold,fontSize=11.sp)
            Spacer(Modifier.height(22.dp))
            Text("ТЕСТОВІ ПЕРСОНАЛЬНІ ДАНІ",color=Color.White.copy(.8f),fontSize=10.sp)
            Text(listOf(p.surname,p.name,p.patronymic).filter{it.isNotBlank()}.joinToString(" ").ifBlank{"ІМ’Я НЕ ВКАЗАНО"},color=Color.White,fontSize=20.sp,fontWeight=FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Detail("Дата народження",p.birth)
            Detail("Номер демодокумента",p.docNumber)
            Detail("Тестовий номер запису",p.record)
            Spacer(Modifier.height(12.dp))
            Text("ВИГАДАНИЙ МАКЕТ. НЕ ПІДТВЕРДЖУЄ ОСОБУ.",color=Color.White,fontSize=10.sp,fontWeight=FontWeight.Bold)
        }
    }
}
@Composable fun Detail(label:String,value:String) { Column(Modifier.padding(vertical=6.dp)){Text(label,color=Color.White.copy(.7f),fontSize=11.sp);Text(value.ifBlank{"Не вказано"},color=Color.White,fontSize=15.sp,fontWeight=FontWeight.SemiBold)} }

@Composable
fun Simple(title:String,modifier:Modifier=Modifier) {
    Column(modifier.fillMaxSize().padding(22.dp)) { Spacer(Modifier.height(18.dp)); Text(title,fontSize=30.sp,fontWeight=FontWeight.Bold,color=Ink); Spacer(Modifier.height(14.dp)); Notice() }
}
