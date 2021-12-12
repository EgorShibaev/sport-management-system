package ru.emkn.kotlin.sms

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.apurebase.arkenv.util.argument
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import mu.KotlinLogging
import ru.emkn.kotlin.sms.protocols.creating.*
import ru.emkn.kotlin.sms.ui.Title
import java.io.File
import kotlin.random.Random
import ru.emkn.kotlin.sms.ui.buffers
import ru.emkn.kotlin.sms.ui.tabContent

val logger = KotlinLogging.logger {}

fun main() = application {
	val selectedTabIndex = remember { mutableStateOf(0) }

	Window(
		state = WindowState(size = DpSize(350.dp, 500.dp)),
		onCloseRequest = ::exitApplication
	) {
		Column {
			TabRow(selectedTabIndex.value, tabs = {
				Title.values().map { it.displayView }.forEachIndexed { index, title ->
					Tab(
						selected = index == selectedTabIndex.value,
						onClick = { selectedTabIndex.value = index },
						text = { Text(title) }
					)
				}
			})
			tabContent(buffers.getValue(Title.values()[selectedTabIndex.value]))
		}
	}
}

/*
sample-data/applications
start-protocols
sample-data/splits.csv
result/result.csv
*/


private fun organizationsResult() {
	logger.info { "Creating result for organizations is started" }
	println("Enter name of file with result for groups")
	val fileName = readLine() ?: throw IllegalArgumentException()
	createOrganizationsResultProtocol(parseResultFile(fileName))
}

private fun result() {
	logger.info { "Creating result for groups is started" }
	println("Enter names directory with start protocols")
	val dirName = readLine() ?: throw IllegalArgumentException()
	val protocolNames = File(dirName).listFiles()?.map { it.absoluteFile.toString() }
		?: throw IllegalArgumentException()

	println("(interactive/file):")
	val way = readLine()?.lowercase() ?: throw IllegalArgumentException()
	when (way) {
		"file" -> {
			println("Enter name of file with result")
			val fileName = readLine() ?: throw IllegalArgumentException()
			createResultProtocol(readResultFromFile(fileName, getParticipantsList(protocolNames)))
		}
		else -> {
			createResultProtocol(interactiveResultRead(getParticipantsList(protocolNames)))
		}
	}
}

private fun start() {
	logger.info { "Creating started protocols is started" }
	println("Enter name of directory with applications:")
	val dirName = readLine() ?: throw IllegalArgumentException()
	val fileNames = File(dirName).listFiles()?.map { it.absoluteFile.toString() }
		?: throw IllegalArgumentException()
	writeStartProtocol(fileNames)
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
