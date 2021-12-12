package ru.emkn.kotlin.sms.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
	Pair(it.key, it.value.map { name -> CsvBuffer(name) })
}.toMap()

@Composable
fun tabContent(buffers: List<CsvBuffer>) {
	val indexOfFile = remember { mutableStateOf(0) }
	Column {
		buttons(buffers)
		TabRow(indexOfFile.value, tabs = {
			buffers.map { it.fileName }.forEachIndexed { index, title ->
				Tab(
					selected = index == indexOfFile.value,
					onClick = { indexOfFile.value = index },
					text = { Text(title) },
				)
			}
		})

		table(buffers[indexOfFile.value])
	}
}

@Composable
private fun buttons(buffers: List<CsvBuffer>) {
	Row {
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
				buffers.forEach { it.export() }
			},
			content = {
				Text("Export")
			}
		)
	}
}