package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

fun getGroups(fileNames: List<String>) = parseApplies(fileNames.map { File(it).readText() })

fun parseApplies(applicationsContent: List<String>) =
	applicationsContent.fold(listOf<Participant>()) { acc, content ->
		val rows = csvReader().readAll(content)
		require(rows.all { it.size == 5 } && rows.size > 2) { "incorrect format" }
		val organization = rows[0][0]
		val participant = rows.subList(2, rows.lastIndex + 1).map { args ->
			Participant(
				args[1],
				args[2],
				args[3].toInt(),
				SportRank.values().firstOrNull { args[4] == it.russianEquivalent }
					?: throw IllegalArgumentException("incorrect rank"),
				args[0],
				organization
			)
		}
		participant + acc
	}.groupBy { it.group }

fun defineTimeForGroup(group: List<Participant>): List<Participant> {
	val offset = 1L
	return group.map {
		it.startTime = it.startTime.plusMinutes(offset)
		it
	}
}

fun generateStartProtocol(groups: List<List<Participant>>): List<List<List<String>>> {
	fun generateLineForParticipant(participant: Participant) =
		listOf(
			participant.number.toString(),
			participant.firstName,
			participant.SecondName,
			participant.year.toString(),
			participant.rank.russianEquivalent,
			participant.startTime.toString(),
			participant.organization,
		)

	// make sure that participant in each group have same groups
	assert(groups.all { group -> group.windowed(2, 1, false).all { it[0].group == it[1].group } })
	val fieldCount = 7
	return groups.map { group ->
		val groupName = group.first().group
		listOf(listOf(groupName) + List(fieldCount - 1) { "" }) +
				group.map { generateLineForParticipant(it) }
	}
}

fun writeStartProtocol(inputFileNames: List<String>) {
	val content = generateStartProtocol(getGroups(inputFileNames).map { defineTimeForGroup(it.value) })
	var currentFileNumber = 1
	content.forEach {
		val outputFileName = "start-protocols/start-protocol$currentFileNumber"
		File("start-protocols").mkdir()
		File(outputFileName).createNewFile()
		csvWriter().writeAll(it, outputFileName)
		currentFileNumber++
	}
}