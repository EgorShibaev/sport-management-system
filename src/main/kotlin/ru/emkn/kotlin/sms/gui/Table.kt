package ru.emkn.kotlin.sms.gui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.emkn.kotlin.sms.buttonsColor

@Composable
fun table(buffer: Buffer) {
	val state = rememberScrollState(0)
	Box(
		modifier = Modifier.fillMaxSize().horizontalScroll(state).padding(end = 12.dp, bottom = 12.dp)
	) {
		Column {
			filters(buffer)
			LazyColumn(Modifier.padding(5.dp)) {
				buffer.filteredContent().forEach { (rowIndex, row) ->
					item {
						if (buffer.isWritable) {
							writableTable(row, buffer, rowIndex)
						} else {
							buffer as ReadOnlyBuffer
							readableOnlyBufferRow(row, buffer)
						}
					}
				}
			}
		}
	}
}

@Composable
private fun writableTable(row: List<String>, buffer: Buffer, rowIndex: Int) {
	Row {
		row.forEachIndexed { columnIndex, field ->
			val text = remember { mutableStateOf(field) }
			text.value = (buffer as CsvBuffer).content[rowIndex][columnIndex]
			TextField(
				onValueChange = {
					buffer.amend(rowIndex, columnIndex, it)
					text.value = it
				},
				value = text.value,
				singleLine = true,
				modifier = Modifier.width(120.dp),
			)
			Spacer(Modifier.width(5.dp))
		}
		val checkBoxState = remember { mutableStateOf(false) }
		checkBoxState.value =
			(buffer as CsvBuffer).checkBoxes[rowIndex]
		Checkbox(
			checked = checkBoxState.value,
			onCheckedChange = {
				checkBoxState.value = !checkBoxState.value
				buffer.checkBoxes[rowIndex] = checkBoxState.value
			},
			colors = CheckboxDefaults.colors(checkedColor = buttonsColor)
		)
	}
}

@Composable
private fun readableOnlyBufferRow(
	row: List<String>,
	buffer: ReadOnlyBuffer,
) {
	Row {
		row.forEachIndexed { index, string ->
			TextField(
				value = string,
				label = { Text(buffer.headers[index]) },
				onValueChange = {},
				modifier = Modifier.width(120.dp),
				readOnly = true,
				singleLine = true,
			)
			Spacer(Modifier.width(5.dp))
		}
	}
}

@Composable
private fun filters(buffer: Buffer) {
	Row {
		buffer.filters.forEachIndexed { index, (content, state) ->
			val text = remember { mutableStateOf(content) }
			text.value = content
			TextField(
				onValueChange = {
					text.value = it
					buffer.filters[index] = FilterState(it, state)
					updateBuffersHash()
				},
				value = text.value,
				singleLine = true,
				modifier = Modifier.width(85.dp).padding(1.dp),
				textStyle = TextStyle(fontSize = 10.sp),
			)
			val isFilterAvailable = remember { mutableStateOf(state) }
			isFilterAvailable.value = state
			Button(
				onClick = {
					isFilterAvailable.value = !isFilterAvailable.value
					buffer.filters[index] = FilterState(content, isFilterAvailable.value)
					updateBuffersHash()
				},
				content = { Text("Filter", fontSize = 7.sp) },
				modifier = Modifier.width(35.dp).padding(1.dp),
				contentPadding = PaddingValues(0.dp),
				colors = ButtonDefaults.buttonColors(
					backgroundColor = if (isFilterAvailable.value) buttonsColor else Color(0Xff0e544f)
				)
			)
			Spacer(Modifier.width(5.dp))
		}
		if (buffer.isWritable) {
			buffer as CsvBuffer
			Column {
				Button(
					modifier = Modifier.height(25.dp),
					onClick = {
						buffer.deleteLines()
						updateBuffersHash()
					},
					contentPadding = PaddingValues(0.dp),
					colors = ButtonDefaults.buttonColors(buttonsColor)
				) { Text(" Delete ", fontSize = 10.sp) }

				Spacer(Modifier.height(5.dp))

				Button(
					modifier = Modifier.height(25.dp),
					onClick = {
						buffer.addEmptyLines()
						updateBuffersHash()
					},
					contentPadding = PaddingValues(0.dp),
					colors = ButtonDefaults.buttonColors(buttonsColor)
				) { Text("Add line", fontSize = 10.sp) }
			}
		}
	}
}