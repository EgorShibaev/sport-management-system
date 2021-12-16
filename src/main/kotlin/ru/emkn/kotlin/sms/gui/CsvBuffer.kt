package ru.emkn.kotlin.sms.gui

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

class CsvBuffer(private val fileName: String, val title: Title) {
	var content: List<MutableList<String>>? = null

	fun filteredContent(): List<MutableList<String>>?  {
		val filters = filters ?: return null
		return content?.filter { line ->
			filters.withIndex().all { !it.value.state || it.value.content.toRegex().matches(line[it.index]) }
		}
	}

	data class FilterState(val content: String, val state: Boolean)

	var filters: MutableList<FilterState>? = null

	fun import() {
		content = csvReader().readAll(File(fileName)).map { it.toMutableList() }
		val size = content?.first()?.size ?: throw IllegalStateException()
		filters = MutableList(size) { FilterState("", false) }
	}

	fun save() {
		content?.let {
			csvWriter().writeAll(it, fileName)
		}
	}

	fun amend(row: Int, column: Int, newValue: String) {
		content?.get(row)?.set(column, newValue) ?: throw IllegalArgumentException("Wrong index")
	}

	operator fun get(index: Int) = content?.get(index) ?: throw IllegalArgumentException("Wrong index")

}