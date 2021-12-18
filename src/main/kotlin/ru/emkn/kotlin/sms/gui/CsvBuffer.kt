package ru.emkn.kotlin.sms.gui

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import ru.emkn.kotlin.sms.logger
import java.io.File

data class CsvBuffer(private val fileName: String, val title: Title) {
	var content: MutableList<MutableList<String>>? = null

	data class Row(val index: Int, val content: List<String>)

	fun filteredContent(): List<Row>? {
		val filters = filters ?: return null
		return content?.mapIndexed { index, row -> Row(index, row) }?.filter { row ->
			filters.withIndex().all {
				try {
					val regex = it.value.content.toRegex()
					!it.value.state || regex.matches(row.content[it.index])
				} catch (e: IllegalArgumentException) {
					logger.error { e.message }
					!it.value.state
				}
			}
		}
	}

	data class FilterState(val content: String, val state: Boolean)

	var filters: MutableList<FilterState>? = null

	var checkBoxes: MutableList<Boolean>? = null

	fun import() {
		content = csvReader().readAll(File(fileName)).map { it.toMutableList() }.toMutableList()
		val size = content?.first()?.size ?: throw IllegalStateException()
		filters = MutableList(size) { FilterState("", false) }
		checkBoxes = MutableList(content?.size ?: throw IllegalStateException()) { false }
	}

	fun save() {
		content?.let {
			csvWriter().writeAll(it, fileName)
		}
	}

	fun amend(row: Int, column: Int, newValue: String) {
		content?.get(row)?.set(column, newValue) ?: throw IllegalArgumentException("Wrong index")
	}

	fun deleteLines() {
		val indices = checkBoxes?.withIndex()?.filter { it.value }?.map { it.index } ?: throw IllegalStateException()
		checkBoxes?.removeAll { it }
		content = content?.withIndex()?.filter { it.index !in indices }?.map { it.value }?.toMutableList()
	}

	fun addEmptyLines() {
		val emptyLine = MutableList(filters?.size ?: throw IllegalStateException()) { "" }
		if (content?.isEmpty() ?: throw IllegalStateException()) {
			content = mutableListOf(emptyLine)
			checkBoxes = mutableListOf(false)
		}
		val indices = checkBoxes?.withIndex()?.filter { it.value }?.map { it.index } ?: throw IllegalStateException()
		indices.forEachIndexed { indexForAdd, indexOfIndex ->
			content?.add(indexForAdd + indexOfIndex, emptyLine)
			checkBoxes?.add(indexForAdd + indexOfIndex, false)
		}
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