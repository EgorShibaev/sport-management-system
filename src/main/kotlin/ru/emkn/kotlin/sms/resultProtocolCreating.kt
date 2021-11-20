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

fun interactiveResultRead(participants: List<Participant>): List<Participant> {
	val notProcessed = participants.toMutableList()
	val processed = mutableListOf<Participant>()
	while (true) {
		println("Input number of participant:")
		val number = readLine()?.toIntOrNull() ?: break
		val participant = notProcessed.firstOrNull { it.number == number }
			?: throw IllegalArgumentException("Participant with this number does not exists")
		assert(notProcessed.remove(participant))
		println("Input points count:")
		val count = readLine()?.toInt() ?: throw IllegalArgumentException()
		require(count > 0) { "There should be at least one point" }
		val passedPoints = mutableListOf<Pair<Int, LocalTime>>()
		repeat(count) {
			println("Input point number:")
			val pointNumber = readLine()?.toInt() ?: throw IllegalArgumentException()
			println("Input time of passing:")
			val time = parseTime(readLine() ?: throw IllegalArgumentException())
			passedPoints.add(Pair(pointNumber, time))
		}
		require(passedPoints.map { it.second }.sorted() == passedPoints.map { it.second })
		{ "jumble in time isn't allowed" }
		participant.passedPoints = passedPoints
		processed.add(participant)
	}
	notProcessed.forEach {
		it.passedPoints = emptyList()
		processed.add(it)
	}
	return processed
}

fun readResultFromFile(fileName: String, participants: List<Participant>): List<Participant> {
	val notProcessed = participants.toMutableList()
	val processed = mutableListOf<Participant>()
	csvReader().readAll(File(fileName)).map { row ->
		val number = row[0].toInt()
		val participant = notProcessed.firstOrNull { it.number == number }
			?: throw IllegalArgumentException("Participant with this number does not exists")
		assert(notProcessed.remove(participant))
		require(row.size % 2 == 1) { "incorrect result format" }
		val passedPoints = mutableListOf<Pair<Int, LocalTime>>()
		var isDropped = false
		row.subList(1, row.lastIndex + 1).chunked(2).filter { it[0] != "" }.forEach {
			if (it[0].toIntOrNull() == null)
				isDropped = true
			else {
				val pointNumber = it[0].toInt()
				val time = parseTime(it[1])
				passedPoints.add(Pair(pointNumber, time))
			}
		}
		if (passedPoints.map { it.second }.sorted() != passedPoints.map { it.second }) {
			isDropped = true
		}
		participant.passedPoints = if (isDropped) emptyList() else passedPoints
		processed.add(participant)
	}
	notProcessed.forEach {
		it.passedPoints = emptyList()
		processed.add(it)
	}
	return processed
}

fun createResultProtocol(participants: List<Participant>) {
	val groups = participants.groupBy { it.group }.map { Pair(it.key, getRankedList(it.value)) }
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
			val winnerTime = group.second.first().passedPoints.lastOrNull()?.second
			group.second.forEach {
				val result = if (it.passedPoints.isEmpty())
					"снят"
				else
					timeDistance(it.passedPoints.last().second, it.startTime).toString()
				val diff = if (it.passedPoints.isEmpty() || winnerTime == null)
					"снят"
				else
					"+${timeDistance(it.passedPoints.last().second, winnerTime)}"
				writeRow(
					listOf(
						currentPlace.toString(),
						it.number.toString(),
						it.firstName,
						it.secondName,
						it.year.toString(),
						it.rank.russianEquivalent,
						it.group,
						result,
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
	time1.minusMinutes(time2.minute.toLong()).minusHours(time2.hour.toLong()).minusSeconds(time2.second.toLong())


fun <T : Runnable> getRankedList(list: List<T>): List<T> {
	require(list.windowed(2, 1, false).all { pair ->
		pair[0].passedPoints.map { it.first } == pair[1].passedPoints.map { it.first } ||
				pair[0].passedPoints.isEmpty() || pair[1].passedPoints.isEmpty()
	}) { "Each group should have the same points" }
	return list.filter { it.passedPoints.isNotEmpty() }.sortedBy {
		timeDistance(it.passedPoints.last().second, it.startTime)
	} + list.filter { it.passedPoints.isEmpty() }
}
