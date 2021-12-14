package ru.emkn.kotlin.sms.gui

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

class CsvBuffer(private val fileName: String, val title: Title) {
	var content: List<MutableList<String>>? = null

	fun import() {
		content = csvReader().readAll(File(fileName)).map { it.toMutableList() }
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