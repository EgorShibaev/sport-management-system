package ru.emkn.kotlin.sms.gui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.buttonsColor
import ru.emkn.kotlin.sms.protocols.creating.*
import java.io.File

enum class Title(val displayView: String) {
	PARTICIPANTS("Participants"), APPLIES("Applies"), START_PROTOCOLS("Start protocols"),
	COURSES("Courses"), SPLITS("Splits"), RESULT("Result"), ORG_RESULT("Organization results"),
}

val filesLocation = mutableMapOf(
	Title.APPLIES to "sample-data/applications",
	Title.START_PROTOCOLS to "start-protocols",
	Title.SPLITS to "sample-data/splits.csv",
	Title.RESULT to "result/result.csv",
	Title.ORG_RESULT to "result/result.csv",
	Title.COURSES to "sample-data/courses",
	Title.PARTICIPANTS to "sample-data/applications"
)

fun getFileNames(title: Title): List<String> {
	fun listOfFileInDirectory(dir: String) = File(dir).listFiles()!!.map { it.absoluteFile.toString() }

	val location = filesLocation.getValue(title)
	return when (title) {
		Title.COURSES -> listOfFileInDirectory(location)
		Title.APPLIES -> listOfFileInDirectory(location)
		Title.START_PROTOCOLS -> listOfFileInDirectory(location)
		Title.SPLITS -> listOf(location)
		Title.RESULT -> listOf(location)
		Title.ORG_RESULT -> listOf(location)
		Title.PARTICIPANTS -> listOfFileInDirectory(location)
	}
}

fun updatedBuffers() = Title.values().associateWith {
	when (it) {
		Title.COURSES, Title.SPLITS, Title.APPLIES ->
			getFileNames(it).map { name -> CsvBuffer(name, it) }
		Title.PARTICIPANTS -> listOf(ParticipantsBuffer.apply { files = getFileNames(it) })
		Title.RESULT -> ResultBuffer.getBuffers(getFileNames(it).first())
		Title.ORG_RESULT -> OrgResultBuffer.getBuffers(getFileNames(it).first())
		Title.START_PROTOCOLS -> StartProtocolBuffer.getBuffers(getFileNames(it))
	}
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
		Spacer(Modifier.width(5.dp))
		TextField(
			value = location.value,
			label = { Text("Files location") },
			onValueChange = {
				location.value = it
				filesLocation[title] = it
			},
			singleLine = true
		)
		Spacer(Modifier.width(5.dp))
		Button(
			onClick = {
				buffers = updatedBuffers()
				updateBuffersHash()
			},
			colors = ButtonDefaults.buttonColors(backgroundColor = buttonsColor)
		) { Text("Select location") }
	}
}

@Composable
fun tabContent(buffers: List<Buffer>) {
	val indexOfFile = remember { mutableStateOf(0) }
	if (indexOfFile.value !in buffers.indices)
		indexOfFile.value = 0
	Column {
		buttons(buffers)
		TabRow(
			indexOfFile.value, tabs = {
				buffers.forEachIndexed { index, _ ->
					Tab(
						selected = index == indexOfFile.value,
						onClick = { indexOfFile.value = index },
						text = {
							when (buffers[index].title) {
								Title.RESULT -> Text((buffers[index] as ResultBuffer).groupName)
								Title.ORG_RESULT ->
									Text((buffers[index] as OrgResultBuffer).organizationName, fontSize = 10.sp)
								Title.START_PROTOCOLS -> Text((buffers[index] as StartProtocolBuffer).groupName)
								Title.APPLIES ->
									Text(
										(buffers[index] as CsvBuffer).content?.get(0)?.get(0) ?: (index + 1).toString(),
										fontSize = 10.sp
									)
								Title.PARTICIPANTS -> Text("All participants")
								else -> Text((index + 1).toString())
							}
						},
					)
				}
			},
			backgroundColor = buttonsColor,
			modifier = Modifier.border(1.dp, Color.Black)
		)
		table(buffers[indexOfFile.value])
	}
}

@Composable
private fun buttons(buffers: List<Buffer>) {
	Row {
		if (buffers.all { it.isWritable }) {
			Spacer(Modifier.width(5.dp))
			Button(
				onClick = {
					buffers.forEach { (it as CsvBuffer).save() }
					updateBuffersHash()
				},
				content = {
					Text("Save")
				},
				colors = ButtonDefaults.buttonColors(backgroundColor = buttonsColor)
			)
		}
		Spacer(Modifier.width(5.dp))
		Button(
			onClick = {
				buffers.forEach { it.import() }
				updateBuffersHash()
			},
			content = {
				Text("Import")
			},
			colors = ButtonDefaults.buttonColors(backgroundColor = buttonsColor)
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
		label = { Text("Result for groups file") },
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
		},
		colors = ButtonDefaults.buttonColors(backgroundColor = buttonsColor)
	)
}

@Composable
private fun resultButton() {
	val startProtocols = remember { mutableStateOf("start-protocols") }
	TextField(
		value = startProtocols.value,
		label = { Text("Start protocols directory") },
		onValueChange = {
			startProtocols.value = it
		}
	)
	Spacer(Modifier.width(5.dp))
	val splitsFile = remember { mutableStateOf("sample-data/splits.csv") }
	TextField(
		value = splitsFile.value,
		label = { Text("Splits file (set the field blank for interactive input)") },
		onValueChange = {
			splitsFile.value = it
		}
	)
	Spacer(Modifier.width(5.dp))
	Button(
		onClick = {
			val protocolNames = File(startProtocols.value).listFiles()?.map { it.absoluteFile.toString() }
				?: throw IllegalArgumentException()

			val results = if (splitsFile.value != "")
				readResultFromFile(splitsFile.value, getParticipantsList(protocolNames))
			else
				interactiveResultRead(getParticipantsList(protocolNames))

			createResultProtocol(results)
		},
		content = {
			Text("Create start protocol")
		},
		colors = ButtonDefaults.buttonColors(backgroundColor = buttonsColor)
	)
}

@Composable
private fun createStartProtocolsButton() {
	val tossStep = remember { mutableStateOf("1") }
	TextField(
		value = tossStep.value,
		label = { Text("Tess step") },
		onValueChange = {
			tossStep.value = it
		},
		modifier = Modifier.width(100.dp)
	)
	Spacer(Modifier.width(5.dp))

	val appliesDir = remember { mutableStateOf("sample-data/applications") }
	TextField(
		value = appliesDir.value,
		label = { Text("Applications directory") },
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
			writeStartProtocol(applies, tossStep.value.toIntOrNull() ?: 1)
		},
		content = {
			Text("Create start protocol")
		},
		colors = ButtonDefaults.buttonColors(backgroundColor = buttonsColor)
	)
}