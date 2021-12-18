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
fun table(buffer: CsvBuffer) {
	val state = rememberScrollState(0)
	Box(
		modifier = Modifier.fillMaxSize().horizontalScroll(state).padding(end = 12.dp, bottom = 12.dp)
	) {
		buffer.filteredContent()?.let {
			Column {
				filters(buffer)
				LazyColumn(Modifier.padding(5.dp)) {
					it.forEach { (rowIndex, row) ->
						item {
							Row {
								row.forEachIndexed { columnIndex, field ->
									val text = remember { mutableStateOf(field) }
									text.value = field
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
								val a = remember { mutableStateOf(false) }
								a.value = buffer.checkBoxes?.get(rowIndex) ?: throw IllegalStateException()
								Checkbox(
									checked = a.value,
									onCheckedChange = {
										a.value = !a.value
										buffer.checkBoxes?.set(rowIndex, a.value)
									},
									colors = CheckboxDefaults.colors(checkedColor = buttonsColor)
								)
							}
						}
					}
				}
			}
		}
	}
}

@Composable
private fun filters(buffer: CsvBuffer) {
	Row {
		buffer.filters?.forEachIndexed { index, (content, state) ->
			val text = remember { mutableStateOf(content) }
			text.value = content
			TextField(
				onValueChange = {
					text.value = it
					buffer.filters?.set(index, CsvBuffer.FilterState(it, state))
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
					buffer.filters?.set(index, CsvBuffer.FilterState(content, isFilterAvailable.value))
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