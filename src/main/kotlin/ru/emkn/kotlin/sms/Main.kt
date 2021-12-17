package ru.emkn.kotlin.sms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import mu.KotlinLogging
import ru.emkn.kotlin.sms.gui.*
import java.io.File
import kotlin.random.Random

val logger = KotlinLogging.logger {}

val buttonsColor = Color(0XFF1e847f)

fun main() = application {
	val selectedTabIndex = remember { mutableStateOf(0) }

	Window(
		state = WindowState(size = DpSize(1000.dp, 500.dp)),
		onCloseRequest = ::exitApplication
	) {
		Column(modifier = Modifier.background(Color(0Xffecc19c))) {
			TabRow(
				selectedTabIndex.value, tabs = {
					Title.values().map { it.displayView }.forEachIndexed { index, title ->
						Tab(
							selected = index == selectedTabIndex.value,
							onClick = { selectedTabIndex.value = index },
							text = { Text(title) }
						)
					}
				},
				backgroundColor = buttonsColor,
				modifier = Modifier.border(1.dp, Color.Black)
			)
			val title = Title.values()[selectedTabIndex.value]
			fileSetter(title)
			tabContent(buffers.getValue(title).also { buffersHash.value })
			// also is necessary because this function should be redrawn when buffer is changed
		}
	}
}

private fun createSplitResult(fileNames: List<String>) {
	val result = mutableListOf<List<String>>()
	fileNames.forEach { fileName ->
		val content = csvReader().readAll(File(fileName))
		val name = content[0][0]
		val points = CoursesFromFileReader.distanceForGroup(name)
		content.subList(1, content.size).forEach { row ->
			var currentTime = Time(row[5])
			val resultRow = mutableListOf(row[0]) + points.points.map {
				currentTime += Random.nextInt(500)
				listOf(it.toString(), currentTime.toString())
			}.flatten()
			result.add(resultRow)
		}
	}
	val fileName = "sample-data/splits.csv"
	logger.info { "Changing $fileName content" }
	csvWriter().writeAll(result, fileName)
}