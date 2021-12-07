package ru.emkn.kotlin.sms.protocols.creating

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import ru.emkn.kotlin.sms.*
import java.io.File
import java.time.LocalTime

fun parseTime(s: String): LocalTime = when {
	"""\d\d:\d\d:\d\d""".toRegex().matches(s) -> {
		val hours = s.substring(0..1).toInt()
		val minute = s.substring(3..4).toInt()
		val second = s.substring(6..7).toInt()
		LocalTime.of(hours, minute, second)
	}
	"""\d\d:\d\d""".toRegex().matches(s) -> {
		val hours = s.substring(0..1).toInt()
		val minute = s.substring(3..4).toInt()
		LocalTime.of(hours, minute)
	}
	else -> throw IllegalArgumentException("Incorrect time format")
}

// fileNames - names of files with start protocols
fun getParticipantsList(fileNames: List<String>) =
	fileNames.map { fileName ->
		val content = csvReader().readAll(File(fileName))
		require(content.size > 1)
		val group = content[0][0]
		content.subList(1, content.lastIndex + 1).map { args ->
			Participant(
				args[0].toInt(),
				args[1],
				args[2],
				args[3].toInt(),
				SportRank.values().first { it.russianEquivalent == args[4] },
				parseTime(args[5]),
				args[6],
				group
			)
		}
	}.flatten()

/**
 * These functions get result of function getParticipantsList
 * and set results for participants.
 */
fun interactiveResultRead(participants: List<Participant>): List<Participant> =
	processResult(InteractiveRead, participants)

fun readResultFromFile(fileName: String, participants: List<Participant>): List<Participant> =
	processResult(ReadFromFile(fileName), participants)

fun processResult(readable: InfoReadable, participants: List<Participant>): List<Participant> {
	readable.getContent().forEach { (number, passedPoints) ->
		val participant = participants.firstOrNull { it.number == number }
			?: throw IllegalArgumentException("Participant with this number does not exists or has been processed")
		if (passedPoints.map { it.result }.sorted() == passedPoints.map { it.result } &&
			passedPoints.first().result > participant.startTime) {
			participant.passedPoints = passedPoints
			participant.resultTime = timeDistance(passedPoints.last().result, participant.startTime)
		}
	}
	return participants
}

/**
 * This function get the result of function
 * readResultFromFile or interactiveResultRead
 */
fun createResultProtocol(participants: List<Participant>) {
	val groups = participants.groupBy { it.group }.map { Group(it.value.toMutableList(), true) }
	val dirName = "result"
	val fileName = "result.csv"
	File(dirName).mkdir()
	File("$dirName/$fileName").createNewFile()
	logger.info { "file $dirName/$fileName is created" }
	val fieldCount = 10
	val heading =
		listOf("№ п/п", "Номер", "Фамилия", "Имя", "Г.р.", "Разр.", "Команда", "Результат", "Место", "Отставание")
	csvWriter().open("$dirName/$fileName") {
		writeRow(listOf("Протокол результатов") + List(fieldCount - 1) { "" })
		groups.forEach { group ->
			writeRow(listOf(group.name) + List(fieldCount - 1) { "" })
			writeRow(heading)
			group.resultTable().forEach {
				writeRow(it)
			}
		}
	}
	logger.info { "Result protocol is created" }
}


fun timeDistance(time1: LocalTime, time2: LocalTime): LocalTime =
	time1.minusHours(time2.hour.toLong()).minusMinutes(time2.minute.toLong()).minusSeconds(time2.second.toLong())