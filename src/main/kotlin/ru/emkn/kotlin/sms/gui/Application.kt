package ru.emkn.kotlin.sms.gui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.protocols.creating.*
import java.io.File

enum class Title(val displayView: String) {
	APPLIES("Applies"), START_PROTOCOLS("Start protocols"), SPLITS("Splits"),
	RESULT("Result"), ORG_RESULT("Organization results")
}

val filesLocation = mutableMapOf(
	Title.APPLIES to "sample-data/applications",
	Title.START_PROTOCOLS to "start-protocols",
	Title.SPLITS to "sample-data/splits.csv",
	Title.RESULT to "result/result.csv",
	Title.ORG_RESULT to "result/organizationsResult.csv"
)

fun getFileNames(title: Title): List<String> {
	val location = filesLocation.getValue(title)
	return when (title) {
		Title.APPLIES -> File(location).listFiles()!!.map { it.absoluteFile.toString() }
		Title.START_PROTOCOLS -> File(location).listFiles()!!.map { it.absoluteFile.toString() }
		Title.SPLITS -> listOf(location)
		Title.RESULT -> listOf(location)
		Title.ORG_RESULT -> listOf(location)
	}
}

fun updatedBuffers() = Title.values().associateWith {
	getFileNames(it).map { name -> CsvBuffer(name, it) }
}

var buffers = updatedBuffers()

val buffersHash = mutableStateOf(buffers.hashCode())

fun updateBuffersHash() {
	buffersHash.value = buffers.hashCode()
}

@Composable
fun fileSetter(title: Title) {
	val location = remember { mutableStateOf("") }
	location.value = filesLocation.getValue(title)

	Row {
		TextField(
			value = location.value,
			onValueChange = {
				location.value = it
				filesLocation[title] = it
			},
			singleLine = true
		)
		Button(
			onClick = {
				buffers = updatedBuffers()
				updateBuffersHash()
			}
		) { Text("Select file") }
	}
}

@Composable
fun tabContent(buffers: List<CsvBuffer>) {
	val indexOfFile = remember { mutableStateOf(0) }
	if (indexOfFile.value !in buffers.indices)
		indexOfFile.value = 0
	Column {
		buttons(buffers)
		TabRow(indexOfFile.value, tabs = {
			buffers.forEachIndexed { index, _ ->
				Tab(
					selected = index == indexOfFile.value,
					onClick = { indexOfFile.value = index },
					text = { Text((index + 1).toString()) },
				)
			}
		})
		table(buffers[indexOfFile.value])
	}
}

@Composable
private fun buttons(buffers: List<CsvBuffer>) {
	Row {
		Spacer(Modifier.width(5.dp))
		Button(
			onClick = {
				buffers.forEach { it.save() }
				updateBuffersHash()
			},
			content = {
				Text("Save")
			}
		)
		Spacer(Modifier.width(5.dp))
		Button(
			onClick = {
				buffers.forEach { it.import() }
				updateBuffersHash()
			},
			content = {
				Text("Import")
			}
		)
		Spacer(Modifier.width(5.dp))
		when (buffers.first().title) {
			Title.START_PROTOCOLS -> createStartProtocolsButton()
			Title.RESULT -> resultButton()
			Title.ORG_RESULT -> orgResultButton()
			else -> {}
		}
	}
}

@Composable
private fun orgResultButton() {
	val resultFile = remember { mutableStateOf("result/result.csv") }
	TextField(
		value = resultFile.value,
		onValueChange = {
			resultFile.value = it
		}
	)
	Spacer(Modifier.width(5.dp))
	Button(
		onClick = {
			createOrganizationsResultProtocol(parseResultFile(resultFile.value))
		},
		content = {
			Text("Create organization result")
		}
	)
}

@Composable
private fun resultButton() {
	val startProtocols = remember { mutableStateOf("start-protocols") }
	TextField(
		value = startProtocols.value,
		onValueChange = {
			startProtocols.value = it
		}
	)
	Spacer(Modifier.width(5.dp))
	val splitsFile = remember { mutableStateOf("sample-data/splits.csv") }
	TextField(
		value = splitsFile.value,
		onValueChange = {
			splitsFile.value = it
		}
	)
	Spacer(Modifier.width(5.dp))
	Button(
		onClick = {
			val protocolNames = File(startProtocols.value).listFiles()?.map { it.absoluteFile.toString() }
				?: throw IllegalArgumentException()
			createResultProtocol(
				readResultFromFile(
					splitsFile.value,
					getParticipantsList(protocolNames)
				)
			)
		},
		content = {
			Text("Create start protocol")
		}
	)
}

@Composable
private fun createStartProtocolsButton() {
	val appliesDir = remember { mutableStateOf("sample-data/applications") }
	TextField(
		value = appliesDir.value,
		onValueChange = {
			appliesDir.value = it
		}
	)
	Spacer(Modifier.width(5.dp))
	Button(
		onClick = {
			Participant.numberForParticipant = 0
			val applies = File(appliesDir.value).listFiles()?.map { it.absoluteFile.toString() }
				?: throw IllegalArgumentException()
			writeStartProtocol(applies)
		},
		content = {
			Text("Create start protocol")
		}
	)
}