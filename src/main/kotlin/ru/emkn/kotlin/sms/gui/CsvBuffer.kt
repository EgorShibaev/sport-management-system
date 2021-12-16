package ru.emkn.kotlin.sms.gui

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

data class CsvBuffer(private val fileName: String, val title: Title) {
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

	override fun hashCode(): Int {
		var result = fileName.hashCode()
		result = 31 * result + title.hashCode()
		result = 31 * result + (content?.hashCode() ?: 0)
		result = 31 * result + (filters?.hashCode() ?: 0)
		return result
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as CsvBuffer

		if (fileName != other.fileName) return false
		if (title != other.title) return false
		if (content != other.content) return false
		if (filters != other.filters) return false

		return true
	}
}