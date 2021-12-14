package ru.emkn.kotlin.sms.gui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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

val fileNames = mapOf(
	Title.APPLIES to File("sample-data/applications").listFiles()!!.map { it.absoluteFile.toString() },
	Title.START_PROTOCOLS to File("start-protocols").listFiles()!!.map { it.absoluteFile.toString() },
	Title.SPLITS to listOf("sample-data/splits.csv"),
	Title.RESULT to listOf("result/result.csv"),
	Title.ORG_RESULT to listOf("result/organizationsResult.csv")
)

val buffers = fileNames.map {
	Pair(it.key, it.value.map { name -> CsvBuffer(name, it.key) })
}.toMap()

@Composable
fun tabContent(buffers: List<CsvBuffer>) {
	val indexOfFile = remember { mutableStateOf(0) }
	if (indexOfFile.value !in buffers.indices)
		indexOfFile.value = 0
	Column {
		buttons(buffers, indexOfFile)
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
private fun buttons(buffers: List<CsvBuffer>, indexOfFile: MutableState<Int>) {
	Row {
		Spacer(Modifier.width(5.dp))
		Button(
			onClick = {
				buffers.forEach { it.save() }
			},
			content = {
				Text("Save")
			}
		)
		Spacer(Modifier.width(5.dp))
		Button(
			onClick = {
				indexOfFile.value++
				indexOfFile.value--
				// table should be repainted when this button is pressed
				// but table is repainted only when MutableState is changed
				buffers.forEach { it.import() }
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