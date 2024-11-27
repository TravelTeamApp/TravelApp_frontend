package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySelector() {
    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf("") }

    val cities = listOf(
        "Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Aksaray", "Amasya", "Ankara", "Antalya", "Ardahan", "Artvin",
        "Aydın", "Balıkesir", "Bartın", "Batman", "Bayburt", "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur",
        "Bursa", "Çanakkale", "Çankırı", "Çorum", "Denizli", "Diyarbakır", "Düzce", "Edirne", "Elazığ", "Erzincan",
        "Erzurum", "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane", "Hakkari", "Hatay", "Iğdır", "Isparta", "İstanbul",
        "İzmir", "Kahramanmaraş", "Karabük", "Karaman", "Kars", "Kastamonu", "Kayseri", "Kırıkkale", "Kırklareli", "Kırşehir",
        "Kilis", "Kocaeli", "Konya", "Kütahya", "Malatya", "Manisa", "Mardin", "Mersin", "Muğla", "Muş",
        "Nevşehir", "Niğde", "Ordu", "Osmaniye", "Rize", "Sakarya", "Samsun", "Siirt", "Sinop", "Sivas",
        "Şanlıurfa", "Şırnak", "Tekirdağ", "Tokat", "Trabzon", "Tunceli", "Uşak", "Van", "Yalova", "Yozgat", "Zonguldak"
    )

    // Şehir arama işlemi - yalnızca ilk harfe göre filtreleme
    val filteredCities = cities.filter {
        it.startsWith(searchQuery, ignoreCase = true)
    }

    // Pull.jpg görselini arka plan olarak ekle
    val backgroundImage: Painter = painterResource(id = R.drawable.pull)

    Box(
        modifier = Modifier.fillMaxSize() // Box tam ekran olacak şekilde ayarlandı
    ) {
        // Arka plan resmi
        Image(
            painter = backgroundImage,
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize() // Arka plan resmini ekranın tamamına yay
        )

        // İçerik: Başlık ve Arama TextField
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Scroll eklenmiş
                .padding(70.dp) // İçeriği hizalamak için padding uygulandı
        ) {
            // Başlık
            Text(
                text = "Hangi şehri gezmek istiyorsunuz?",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                modifier = Modifier.padding(bottom = 12.dp)   )


            // Arama TextField
            OutlinedTextField(
                value = if (selectedCity.isEmpty()) searchQuery else selectedCity,
                onValueChange = { query ->
                    searchQuery = query
                    expanded = query.isNotEmpty() // Arama yapılırken dropdown menüyü aç
                },
                label = { Text("Şehir seçin veya arayın") },
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
            )

            // Dropdown menü
            if (expanded) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }, // Menü kapandığında `expanded`'ı güncelle
                    modifier = Modifier
                        .padding(5.dp)
                        .heightIn(max = 400.dp)
                        .widthIn(min = 250.dp) // Menü yüksekliğini sınırlayarak scroll ekleyelim
                ) {
                    // Filtrelenmiş şehirleri listele
                    if (filteredCities.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Sonuç bulunamadı") },
                            onClick = { /* Boş */ }
                        )
                    } else {
                        filteredCities.forEach { city ->
                            DropdownMenuItem(
                                text = { Text(city) },
                                onClick = {
                                    selectedCity = city
                                    searchQuery = "" // Arama sorgusunu temizle
                                    expanded = false // Dropdown menüyü kapat
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
