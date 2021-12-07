package ru.emkn.kotlin.sms.protocols.creating

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import ru.emkn.kotlin.sms.Group
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.SportRank
import ru.emkn.kotlin.sms.logger
import java.io.File

fun getGroups(fileNames: List<String>) =
	parseApplies(fileNames.map { File(it).readText() }).groupBy { it.group }
		.map { Group(it.value.toMutableList(), false) }

fun parseApplies(applicationsContent: List<String>): List<Participant> =
	applicationsContent.map { content ->
		val rows = csvReader().readAll(content)
		require(rows.all { it.size == 5 } && rows.size > 2) { "incorrect format" }
		val organization = rows[0][0]
		val participants = rows.subList(2, rows.lastIndex + 1).map { args ->
			Participant(
				firstName = args[1],
				lastName = args[2],
				year = args[3].toInt(),
				rank = SportRank.values().firstOrNull { args[4] == it.russianEquivalent }
					?: throw IllegalArgumentException("incorrect rank"),
				group = args[0],
				organization = organization
			)
		}
		participants
	}.flatten()


fun writeStartProtocol(inputFileNames: List<String>) {
	val groups = getGroups(inputFileNames)
	groups.forEach { it.defineTimeForParticipants() }
	val content = groups.map { it.generateStartProtocol() }
	var currentFileNumber = 1
	content.forEach {
		val outputFileName = "start-protocols/start-protocol$currentFileNumber.csv"
		File("start-protocols").mkdir()
		File(outputFileName).createNewFile()
		csvWriter().writeAll(it, outputFileName)
		logger.info { "start protocol $outputFileName is created" }
		currentFileNumber++
	}
}