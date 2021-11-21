package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
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

fun interactiveResultRead(participants: List<Participant>): List<Participant> =
	processResult(InteractiveRead, participants)

fun readResultFromFile(fileName: String, participants: List<Participant>): List<Participant> =
	processResult(ReadFromFile(fileName), participants)

fun processResult(readable: InfoReadable, participants: List<Participant>): List<Participant> {
	val notProcessed = participants.toMutableList()
	val processed = mutableListOf<Participant>()
	readable.getContent().forEach { (number, passedPoints) ->
		val participant = notProcessed.firstOrNull { it.number == number }
			?: throw IllegalArgumentException("Participant with this number does not exists or has been processed")
		assert(notProcessed.remove(participant))
		if (passedPoints.map { it.second }.sorted() != passedPoints.map { it.second })
			participant.passedPoints = emptyList()
		else
			participant.passedPoints = passedPoints
		processed.add(participant)
	}
	notProcessed.forEach {
		it.passedPoints = emptyList()
		processed.add(it)
	}
	return processed
}

fun groupPointsCheck(participants: List<Participant>) {
	val name = participants[0].group
	val expectedPoints = csvReader().readAll(File("sample-data/courses.csv")).find { it[0] == name }
		?: throw IllegalArgumentException("courses.csv isn't complete")
	assert(participants.all { participant ->
		participant.passedPoints.map { it.first } == expectedPoints
	})
}

fun createResultProtocol(participants: List<Participant>) {
	val groups = participants.groupBy { it.group }.map { Pair(it.key, getRankedList(it.value)) }
	groups.forEach { groupPointsCheck(it.second) }
	val dirName = "result"
	val fileName = "result.csv"
	File(dirName).mkdir()
	File("$dirName/$fileName").createNewFile()
	val fieldCount = 10
	val heading =
		listOf("№ п/п", "Номер", "Фамилия", "Имя", "Г.р.", "Разр.", "Команда", "Результат", "Место", "Отставание")
	csvWriter().open("$dirName/$fileName") {
		writeRow(listOf("Протокол результатов") + List(fieldCount - 1) { "" })
		groups.forEach { group ->
			writeRow(listOf(group.first) + List(fieldCount - 1) { "" })
			writeRow(heading)
			var currentPlace = 1
			val winnerTime = if (group.second.first().passedPoints.isEmpty())
				null
			else
				timeDistance(group.second.first().passedPoints.last().second, group.second.first().startTime)
			group.second.forEach {
				val result = if (it.passedPoints.isEmpty())
					null
				else
					timeDistance(it.passedPoints.last().second, it.startTime)
				val diff = if (it.passedPoints.isEmpty() || winnerTime == null || result == null)
					"снят"
				else
					"+${timeDistance(result, winnerTime)}"
				writeRow(
					listOf(
						currentPlace.toString(),
						it.number.toString(),
						it.firstName,
						it.secondName,
						it.year.toString(),
						it.rank.russianEquivalent,
						it.organization,
						result?.toString() ?: "снят",
						currentPlace.toString(),
						diff
					)
				)
				currentPlace++
			}
		}
	}
}

fun timeDistance(time1: LocalTime, time2: LocalTime): LocalTime =
	time1.minusHours(time2.hour.toLong()).minusMinutes(time2.minute.toLong()).minusSeconds(time2.second.toLong())


fun <T : Runnable> getRankedList(list: List<T>): List<T> =
	list.filter { it.passedPoints.isNotEmpty() }.sortedBy {
		timeDistance(it.passedPoints.last().second, it.startTime)
	} + list.filter { it.passedPoints.isEmpty() }
