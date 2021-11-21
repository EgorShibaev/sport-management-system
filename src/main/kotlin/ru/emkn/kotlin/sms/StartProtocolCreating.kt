package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

fun getGroups(fileNames: List<String>) = parseApplies(fileNames.map { File(it).readText() })

fun parseApplies(applicationsContent: List<String>) =
	applicationsContent.map { content ->
		val rows = csvReader().readAll(content)
		require(rows.all { it.size == 5 } && rows.size > 2) { "incorrect format" }
		val organization = rows[0][0]
		val participants = rows.subList(2, rows.lastIndex + 1).map { args ->
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
		participants
	}.flatten().groupBy { it.group }

fun defineTimeForGroup(group: List<Participant>): List<Participant> {
	var offset = 0L
	val step = 5
	return group.map {
		it.startTime = it.startTime.plusMinutes(offset)
		offset += step
		it
	}
}

fun generateStartProtocol(groups: List<List<Participant>>): List<List<List<String>>> {
	fun generateLineForParticipant(participant: Participant) =
		listOf(
			participant.number.toString(),
			participant.firstName,
			participant.secondName,
			participant.year.toString(),
			participant.rank.russianEquivalent,
			participant.startTime.toString(),
			participant.organization,
		)

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
		val outputFileName = "start-protocols/start-protocol$currentFileNumber.csv"
		File("start-protocols").mkdir()
		File(outputFileName).createNewFile()
		csvWriter().writeAll(it, outputFileName)
		currentFileNumber++
	}
}