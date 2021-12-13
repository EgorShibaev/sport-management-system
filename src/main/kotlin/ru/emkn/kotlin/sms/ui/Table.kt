package ru.emkn.kotlin.sms.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun table(buffer: CsvBuffer) {
	val state = rememberScrollState(0)
	Box(
		modifier = Modifier
			.fillMaxSize()
			.horizontalScroll(state)
			.padding(end = 12.dp, bottom = 12.dp)
	) {
		buffer.content?.let {
			LazyColumn {
				it.forEachIndexed { rowIndex, row ->
					item {
						Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
							row.forEachIndexed { columnIndex, field ->
								val text = remember { mutableStateOf(field) }
								text.value = field
								TextField(
									value = text.value,
									onValueChange = {
										buffer.amend(rowIndex, columnIndex, it)
										text.value = it
									},
									modifier = Modifier.border(.1.dp, Color.Blue).width(120.dp),
									singleLine = true
								)
							}
						}
						Spacer(Modifier.height(3.dp))
					}
				}
			}
		}
	}
}