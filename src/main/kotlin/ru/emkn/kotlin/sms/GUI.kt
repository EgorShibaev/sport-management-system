package ru.emkn.kotlin.sms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.lang.Integer.max

@Composable
fun table(columnNames : MutableList<String>, data : MutableList<MutableList<MutableState<String>>>) {
    val cellWidth = columnNames.mapIndexed { index, it -> 13 * max(it.length, data.maxOf {it[index].value.length})}
    Column(Modifier.fillMaxSize(), Arrangement.spacedBy(0.dp)) {
        Row(Modifier.fillMaxWidth()) {
            for (columnId in 0 until columnNames.size) {
                Surface(
                    border = BorderStroke(1.dp, Color.Black),
                    contentColor = Color.Transparent,
                    modifier = Modifier.width(cellWidth[columnId].dp)
                ) {
                    Text(columnNames[columnId], modifier = Modifier.padding(4.dp))
                }
            }
        }
        for (rowId in 0 until data.size) {
            Row(Modifier.fillMaxWidth()) {
                for (columnId in 0 until data[rowId].size) {
                    Surface(
                        border = BorderStroke(1.dp, Color.Black),
                        contentColor = Color.Transparent,
                        modifier = Modifier.width(cellWidth[columnId].dp)
                    ) {
                        OutlinedTextField(
                            value = data[rowId][columnId].value,
                            onValueChange = { data[rowId][columnId].value = it },
                            maxLines = 1,
                            singleLine = true
                        )
                    }
                }
            }
        }
    }
}

var tables = mutableListOf(
    Pair(
        mutableListOf("Значение 1", "Значение 2"),
        mutableListOf(
            mutableListOf(mutableStateOf("123"), mutableStateOf("xxx")),
            mutableListOf(mutableStateOf("fjasj"), mutableStateOf("zzzz"))
        )
    ),
    Pair(
        mutableListOf("Значение 33", "Значение x"),
        mutableListOf(
            mutableListOf(mutableStateOf("fjasj"), mutableStateOf("zzzz")),
            mutableListOf(mutableStateOf("аыфафы"), mutableStateOf("xxx")),
            mutableListOf(mutableStateOf("fjasj"), mutableStateOf("zzzz"))
        )
    )
)

var tablesNames = mutableListOf("Таб1", "Таб2")

var selectedTable = mutableStateOf(0)

@Composable
fun LazyScrollable() {
    Box(
        modifier = Modifier.fillMaxSize()
            .padding(2.dp)
    ) {
        val state = rememberLazyListState()
        LazyColumn(Modifier.fillMaxSize().padding(end = 1.dp), state) {
            items(1)  {
                Row {
                    for (tableId in 0 until tablesNames.size) {
                        Button(
                            onClick = { selectedTable.value = tableId },
                            modifier = Modifier.width(100.dp).padding(5.dp)
                        ) {
                            Text(tablesNames[tableId])
                        }
                    }
                }
                table(tables[selectedTable.value].first, tables[selectedTable.value].second)
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )
    }
}
