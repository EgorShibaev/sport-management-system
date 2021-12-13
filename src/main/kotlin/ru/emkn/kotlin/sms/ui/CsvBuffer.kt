package ru.emkn.kotlin.sms.ui

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import ru.emkn.kotlin.sms.logger
import java.io.File

class CsvBuffer(val fileName: String) {
	var content: List<MutableList<String>>? = null

	fun export() {
		content = csvReader().readAll(File(fileName)).map { it.toMutableList() }
	}

	fun save() {
		content?.let {
			csvWriter().writeAll(it, fileName)
		}
	}

	fun amend(row: Int, column: Int, newValue: String) {
		logger.error { "$fileName $row $column $newValue" }
		content?.get(row)?.set(column, newValue) ?: throw IllegalArgumentException("Wrong index")
		logger.error(content?.get(row)?.get(column))
	}

	operator fun get(index: Int) = content?.get(index) ?: throw IllegalArgumentException("Wrong index")

}