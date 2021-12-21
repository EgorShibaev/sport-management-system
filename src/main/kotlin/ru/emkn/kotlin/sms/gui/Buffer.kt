package ru.emkn.kotlin.sms.gui

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.logger
import ru.emkn.kotlin.sms.protocols.creating.getGroups
import ru.emkn.kotlin.sms.protocols.creating.getParticipantsList
import ru.emkn.kotlin.sms.protocols.creating.parseResultFile
import java.io.File

data class Row(val index: Int, val content: List<String>)

data class FilterState(val content: String, val state: Boolean)

abstract class Buffer(val title: Title, val isWritable: Boolean) {
	abstract fun content(): List<List<String>>

	fun filteredContent(): List<Row> {
		val filters = filters
		return content().mapIndexed { index, row -> Row(index, row) }.filter { row ->
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

	var filters: MutableList<FilterState> = mutableListOf()

	abstract fun import()

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Buffer

		if (filters != other.filters) return false
		if (content() != other.content()) return false

		return true
	}

	override fun hashCode(): Int {
		return filters.hashCode() * 31 + content().hashCode()
	}


}

class CsvBuffer(private val fileName: String, title: Title) : Buffer(title, true) {
	var content: MutableList<MutableList<String>> = mutableListOf()

	override fun content(): List<List<String>> {
		return content.map { row -> row.toList() }.toList()
	}

	var checkBoxes: MutableList<Boolean> = mutableListOf()

	init {
		import()
	}

	override fun import() {
		content = csvReader().readAll(File(fileName)).map { it.toMutableList() }.toMutableList()
		val size = content.first().size
		filters = MutableList(size) { FilterState("", false) }
		checkBoxes = MutableList(content.size) { false }
	}

	fun save() {
		csvWriter().writeAll(content, fileName)
	}

	fun amend(row: Int, column: Int, newValue: String) {
		content[row][column] = newValue
	}

	fun deleteLines() {
		val indices = checkBoxes.withIndex().filter { it.value }.map { it.index }
		checkBoxes.removeAll { it }
		content = content.withIndex().filter { it.index !in indices }.map { it.value }.toMutableList()
	}

	fun addEmptyLines() {
		val emptyLine = MutableList(filters.size) { "" }
		if (content.isEmpty()) {
			content = mutableListOf(emptyLine)
			checkBoxes = mutableListOf(false)
		}
		val indices = checkBoxes.withIndex().filter { it.value }.map { it.index }
		indices.forEachIndexed { indexForAdd, indexOfIndex ->
			content.add(indexForAdd + indexOfIndex, emptyLine)
			checkBoxes.add(indexForAdd + indexOfIndex, false)
		}
	}

	override fun hashCode(): Int {
		var result = fileName.hashCode()
		result = 31 * result + title.hashCode()
		result = 31 * result + content.hashCode()
		result = 31 * result + filters.hashCode()
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

abstract class ReadOnlyBuffer(title: Title) : Buffer(title, false) {

	lateinit var content: List<Participant>

	abstract val headers: List<String>

}

object ParticipantsBuffer : ReadOnlyBuffer(Title.PARTICIPANTS) {

	var files: List<String> = emptyList()

	override val headers = listOf("Number", "First name", "Last name", "Year", "Rank", "Group", "Organization")

	override fun content(): List<List<String>> {
		return content.map {
			listOf(
				it.number.toString(),
				it.firstName,
				it.lastName,
				it.year.toString(),
				it.rank.russianEquivalent,
				it.group,
				it.organization
			)
		}
	}

	override fun import() {
		content = getGroups(files).map { it.participants }.flatten()
		filters = MutableList(headers.size) { FilterState("", false) }
	}
}

class ResultBuffer(val groupName: String, private var fileName: String) : ReadOnlyBuffer(Title.RESULT) {
	override val headers = listOf("Number", "First name", "Last name", "Year", "Rank", "Organization", "Time", "Place")

	override fun import() {
		content = parseResultFile(fileName).filter { it.group == groupName }
		filters = MutableList(headers.size) { FilterState("", false) }
	}


	override fun content(): List<List<String>> {
		return content.map {
			listOf(
				it.number.toString(),
				it.firstName,
				it.lastName,
				it.year.toString(),
				it.rank.russianEquivalent,
				it.organization,
				it.resultTime?.toString() ?: "снят",
				it.place.toString()
			)
		}
	}

	companion object {
		fun getBuffers(fileName: String) =
			parseResultFile(fileName).map { it.group }.toSet().map { ResultBuffer(it, fileName) }
	}

	init {
		import()
	}
}

class OrgResultBuffer(val organizationName: String, private var fileName: String) : ReadOnlyBuffer(Title.ORG_RESULT) {
	override fun import() {
		content = parseResultFile(fileName).filter { it.organization == organizationName }
		filters = MutableList(headers.size) { FilterState("", false) }
	}

	override val headers = listOf("Number", "First name", "Last name", "Year", "Rank", "Group", "Time", "Score")

	override fun content(): List<List<String>> {
		return content.map {
			listOf(
				it.number.toString(),
				it.firstName,
				it.lastName,
				it.year.toString(),
				it.rank.russianEquivalent,
				it.group,
				it.resultTime?.toString() ?: "снят",
				it.score.toString()
			)
		}
	}

	companion object {
		fun getBuffers(fileName: String) =
			parseResultFile(fileName).map { it.organization }.toSet().map { OrgResultBuffer(it, fileName) }
	}

	init {
		import()
	}
}

class StartProtocolBuffer(var groupName: String, private var fileName: String) : ReadOnlyBuffer(Title.START_PROTOCOLS) {

	override fun import() {
		content = getParticipantsList(listOf(fileName))
		groupName = content.first().group
		filters = MutableList(headers.size) { FilterState("", false) }
	}

	override val headers = listOf("Number", "First name", "Last name", "Year", "Rank", "Start Time", "Organization")

	override fun content(): List<List<String>> {
		return content.map {
			listOf(
				it.number.toString(),
				it.firstName,
				it.lastName,
				it.year.toString(),
				it.rank.russianEquivalent,
				it.startTime.toString(),
				it.organization
			)
		}
	}

	companion object {
		fun getBuffers(fileNames: List<String>) =
			fileNames.map {
				val participants = getParticipantsList(listOf(it))
				StartProtocolBuffer(participants.first().group, it)
			}
	}

	init {
		import()
	}
}