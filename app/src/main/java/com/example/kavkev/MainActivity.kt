package com.example.kavkev

import android.os.Bundle
import android.content.ContentValues
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat

data class Product(val name: String, val unit: String, val price: Double, val category: String)

private val products = listOf(
    Product("Сэндвич большой", "шт", 85.0, "Сэндвичи и фастфуд"),
    Product("Сэндвич маленький", "шт", 70.0, "Сэндвичи и фастфуд"),
    Product("Chicken гриль", "шт", 100.0, "Сэндвичи и фастфуд"),
    Product("Хот-дог", "шт", 100.0, "Сэндвичи и фастфуд"),
    Product("Сэндвич Искендер", "шт", 100.0, "Сэндвичи и фастфуд"),
    Product("Сэндвич с курицей", "шт", 100.0, "Сэндвичи и фастфуд"),
    Product("Сэндвич Чеддер", "шт", 100.0, "Сэндвичи и фастфуд"),
    Product("Сэндвич с котлетой", "шт", 100.0, "Сэндвичи и фастфуд"),
    Product("Сэндвич Цезарь", "шт", 100.0, "Сэндвичи и фастфуд"),
    Product("Кав бургер", "шт", 100.0, "Сэндвичи и фастфуд"),
    Product("Кев бургер", "шт", 100.0, "Сэндвичи и фастфуд"),
    Product("Сэндвич кебаб", "шт", 100.0, "Сэндвичи и фастфуд"),
    Product("Леш чикен бургер", "шт", 100.0, "Сэндвичи и фастфуд"),
    Product("Шеф бургер", "шт", 100.0, "Сэндвичи и фастфуд"),
    Product("Шаверма (курица)", "шт", 100.0, "Сэндвичи и фастфуд"),
    Product("Шаверма (котлета)", "шт", 100.0, "Сэндвичи и фастфуд"),
    Product("Шаверма (мясо)", "шт", 100.0, "Сэндвичи и фастфуд"),
    Product("Панини (с курицей)", "шт", 100.0, "Сэндвичи и фастфуд"),

    Product("Яйцо фасованное", "уп", 140.0, "Яйца, сметана и масло"),
    Product("Яйцо 1 категория", "шт", 13.0, "Яйца, сметана и масло"),
    Product("Яйцо 2 категория (белое)", "шт", 11.5, "Яйца, сметана и масло"),
    Product("Яйцо гигант", "шт", 13.5, "Яйца, сметана и масло"),
    Product("Яйцо 3 категория (40–50 г)", "шт", 10.0, "Яйца, сметана и масло"),
    Product("Яйцо 2 категория (крем)", "шт", 12.0, "Яйца, сметана и масло"),
    Product("Сметана 15% (450 г)", "шт", 60.0, "Яйца, сметана и масло"),
    Product("Сметана 20% (450 г)", "шт", 65.0, "Яйца, сметана и масло"),
    Product("Сметана 15% (180 г)", "шт", 40.0, "Яйца, сметана и масло"),
    Product("Сметана пакет", "шт", 35.0, "Яйца, сметана и масло"),
    Product("Масло сливочное (банка)", "шт", 150.0, "Яйца, сметана и масло"),
    Product("Масло сливочное", "кг", 690.0, "Яйца, сметана и масло"),

    Product("Чечел в ассортименте", "кг", 850.0, "Сыры"),
    Product("Сыр колбасный", "кг", 550.0, "Сыры"),
    Product("Сыр колбасный вакуум", "кг", 570.0, "Сыры"),
    Product("Сыр диетический", "кг", 730.0, "Сыры"),
    Product("Сыр Fitness", "кг", 760.0, "Сыры"),
    Product("Сыр сулугуни", "кг", 780.0, "Сыры"),
    Product("Голландский сливочный Чуйский", "кг", 840.0, "Сыры"),
    Product("Сыр гр. сл. кр. желтый", "кг", 760.0, "Сыры"),
    Product("Сыр Голландский Особый", "кг", 860.0, "Сыры"),
    Product("Голландский сливочный Премиум", "кг", 870.0, "Сыры"),
    Product("Сыр Ландберг", "кг", 980.0, "Сыры")
)

private fun money(value: Double): String = DecimalFormat("#,##0.##").format(value)


private fun exportPriceList(
    context: android.content.Context,
    quantities: Map<String, String>
): String {
    val purchased = products.mapNotNull { product ->
        val quantity = quantities[product.name]?.toDoubleOrNull() ?: 0.0
        if (quantity > 0.0) Triple(product, quantity, quantity * product.price) else null
    }
    if (purchased.isEmpty()) {
        throw IllegalStateException("Сначала добавьте количество хотя бы одного товара")
    }

    val now = Date()
    val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(now)
    val displayDate = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(now)
    val fileName = "KAV_KEV_Order_$timestamp.pdf"
    val document = PdfDocument()
    val pageWidth = 595
    val pageHeight = 842
    val margin = 36f
    val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 19f
        typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
    }
    val headingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 12f
        typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
    }
    val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 9.5f }
    val smallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 8.5f }
    val totalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 13f
        typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
    }
    val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 0.7f
        color = android.graphics.Color.LTGRAY
    }

    var pageNumber = 0
    var page: PdfDocument.Page? = null
    var canvas: android.graphics.Canvas? = null
    var y = 0f

    fun startPage() {
        pageNumber++
        page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
        canvas = page!!.canvas
        y = 42f
        if (pageNumber == 1) {
            canvas!!.drawText("KAV&KEV — СОСТАВ ЗАКАЗА", margin, y, titlePaint)
            y += 22f
            canvas!!.drawText("Дата и время: $displayDate", margin, y, smallPaint)
            y += 22f
        } else {
            canvas!!.drawText("KAV&KEV — СОСТАВ ЗАКАЗА (продолжение)", margin, y, headingPaint)
            y += 22f
        }
        canvas!!.drawText("Товар", margin, y, headingPaint)
        canvas!!.drawText("Кол-во", 330f, y, headingPaint)
        canvas!!.drawText("Цена", 405f, y, headingPaint)
        canvas!!.drawText("Сумма", 490f, y, headingPaint)
        y += 8f
        canvas!!.drawLine(margin, y, pageWidth - margin, y, linePaint)
        y += 15f
    }

    fun finishPage() {
        page?.let { document.finishPage(it) }
        page = null
        canvas = null
    }

    try {
        startPage()
        val groups = listOf("Сэндвичи и фастфуд", "Яйца, сметана и масло", "Сыры")
        groups.forEach { group ->
            val groupItems = purchased.filter { it.first.category == group }
            if (groupItems.isNotEmpty()) {
                if (y > pageHeight - 75) {
                    finishPage()
                    startPage()
                }
                canvas!!.drawText(group, margin, y, headingPaint)
                y += 18f

                groupItems.forEach { (product, quantity, lineTotal) ->
                    if (y > pageHeight - 55) {
                        finishPage()
                        startPage()
                    }
                    val name = if (product.name.length > 43) product.name.take(40) + "..." else product.name
                    val qtyText = money(quantity) + " " + product.unit
                    canvas!!.drawText(name, margin, y, bodyPaint)
                    canvas!!.drawText(qtyText, 330f, y, bodyPaint)
                    canvas!!.drawText(money(product.price), 405f, y, bodyPaint)
                    canvas!!.drawText(money(lineTotal), 490f, y, bodyPaint)
                    y += 17f
                    canvas!!.drawLine(margin, y - 5f, pageWidth - margin, y - 5f, linePaint)
                }
                y += 10f
            }
        }

        val orderTotal = purchased.sumOf { it.third }
        if (y > pageHeight - 65) {
            finishPage()
            startPage()
        }
        y += 6f
        canvas!!.drawText("Позиций: ${purchased.size}", margin, y, bodyPaint)
        y += 22f
        canvas!!.drawText("ИТОГО: ${money(orderTotal)} сом", margin, y, totalPaint)
        canvas!!.drawText("Сформировано приложением KAV&KEV", margin, pageHeight - 25f, smallPaint)
        finishPage()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/KAVKEV")
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: throw IllegalStateException("Не удалось создать файл")
            context.contentResolver.openOutputStream(uri)?.use { document.writeTo(it) }
                ?: throw IllegalStateException("Не удалось записать файл")
            return "Заказ сохранён в Загрузки/KAVKEV/$fileName"
        } else {
            val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "KAVKEV")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, fileName)
            FileOutputStream(file).use { document.writeTo(it) }
            return "Заказ сохранён: ${file.absolutePath}"
        }
    } finally {
        document.close()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { CalculatorScreen() } }
    }
}

@Composable
fun CalculatorScreen() {
    val quantities = remember { mutableStateMapOf<String, String>() }
    var search by remember { mutableStateOf("") }
    val categories = listOf("Сэндвичи и фастфуд", "Яйца, сметана и масло", "Сыры")
    val expanded = remember {
        mutableStateMapOf(
            "Сэндвичи и фастфуд" to true,
            "Яйца, сметана и масло" to true,
            "Сыры" to true
        )
    }
    val total = products.sumOf { p -> (quantities[p.name]?.toDoubleOrNull() ?: 0.0) * p.price }
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.primary, shadowElevation = 3.dp) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
                    Text("KAV&KEV", color = MaterialTheme.colorScheme.onPrimary, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                    Text("Калькулятор заказа", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .85f))
                }
            }
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("ИТОГО", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("${money(total)} сом", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = { quantities.clear() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Очистить заказ")
                    }
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = search, onValueChange = { search = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                label = { Text("Поиск товара") }, singleLine = true
            )
            Button(
                onClick = {
                    try {
                        val message = exportPriceList(context = context, quantities = quantities.toMap())
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Не удалось скачать заказ: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
            ) {
                Text("Скачать заказ PDF")
            }
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories, key = { it }) { group ->
                    val groupProducts = products.filter {
                        it.category == group && it.name.contains(search, ignoreCase = true)
                    }
                    if (groupProducts.isNotEmpty()) {
                        val isExpanded = expanded[group] ?: true
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .clickable { expanded[group] = !isExpanded }
                                    .padding(horizontal = 14.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(group, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                    Text("${groupProducts.size} товаров", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = .75f))
                                }
                                Text(if (isExpanded) "⌄" else "›", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                        if (isExpanded || search.isNotBlank()) {
                            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                                groupProducts.forEach { product ->
                                    val qty = quantities[product.name] ?: ""
                                    val count = qty.toDoubleOrNull() ?: 0.0
                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Column(Modifier.fillMaxWidth().padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Column(Modifier.weight(1f)) {
                                                    Text(product.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                                    Text("${money(product.price)} сом / ${product.unit}", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    FilledTonalButton(
                                                        onClick = {
                                                            val next = (count - 1.0).coerceAtLeast(0.0)
                                                            quantities[product.name] = if (next == 0.0) "" else next.toString().removeSuffix(".0")
                                                        },
                                                        contentPadding = PaddingValues(horizontal = 12.dp),
                                                        modifier = Modifier.height(38.dp)
                                                    ) { Text("−", fontSize = 20.sp) }
                                                    BasicTextField(
                                                        value = qty,
                                                        onValueChange = { value ->
                                                            if (value.isEmpty() || value.matches(Regex("\\d*(\\.\\d*)?"))) quantities[product.name] = value
                                                        },
                                                        modifier = Modifier.width(48.dp).padding(horizontal = 5.dp),
                                                        singleLine = true,
                                                        decorationBox = { inner ->
                                                            Box(contentAlignment = Alignment.Center) {
                                                                if (qty.isEmpty()) Text("0", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                                inner()
                                                            }
                                                        }
                                                    )
                                                    FilledTonalButton(
                                                        onClick = { quantities[product.name] = (count + 1.0).toString().removeSuffix(".0") },
                                                        contentPadding = PaddingValues(horizontal = 12.dp),
                                                        modifier = Modifier.height(38.dp)
                                                    ) { Text("+", fontSize = 20.sp) }
                                                }
                                            }
                                            if (count > 0) {
                                                Spacer(Modifier.height(6.dp))
                                                Text(
                                                    "Сумма: ${money(count * product.price)} сом",
                                                    fontWeight = FontWeight.Medium,
                                                    modifier = Modifier.align(Alignment.End)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
