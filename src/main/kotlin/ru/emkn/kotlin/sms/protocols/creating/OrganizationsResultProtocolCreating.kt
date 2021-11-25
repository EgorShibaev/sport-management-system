package ru.emkn.kotlin.sms.protocols.creating

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.SportRank
import java.io.File
import java.lang.Integer.max
import java.time.LocalTime


fun LocalTime.allSeconds() = hour * 3600 + minute * 60 + second

fun calculateScore(winnerTime: LocalTime, participantTime: LocalTime) =
	max(0, (100 * (2 - participantTime.allSeconds().toDouble() / winnerTime.allSeconds())).toInt())

fun parseLine(line: List<String>, group: String, winnerTime: LocalTime): Participant {
	// Is instead time there is "снят" - score is 0 (parseTime throw exception if time format in incorrect)
	val score = try {
		calculateScore(winnerTime, parseTime(line[7]))
	} catch (e: IllegalArgumentException) {
		0
	}
	return Participant(
		line[1].toInt(),
		line[2],
		line[3],
		line[4].toInt(),
		SportRank.values().firstOrNull { it.russianEquivalent == line[5] }
			?: throw IllegalArgumentException("Wrong sport rank"),
		line[6],
		group,
		score
	)
}


fun parseResultFile(fileName: String): List<Participant> {
	// result file contain information about all groups
	// for each group there is line with group name, heading line and below
	// lines with participant
	var group: String? = null // name of current group
	var winnerTime: LocalTime? = null // Time of winner in current group
	val content = csvReader().readAll(File(fileName))
	val result = mutableListOf<Participant>()
	content.subList(1, content.size).forEach { row ->
		when {
			row.subList(1, row.size).all { it == "" } -> {
				// lines only with group name
				group = row[0]
				winnerTime = null
			}
			row[0].toIntOrNull() != null -> { // ignoring heading row
				if (winnerTime == null)
					winnerTime = parseTime(row[7])
				val participant = parseLine(
					row,
					group ?: throw IllegalArgumentException("Wrong format"),
					winnerTime
						?: throw IllegalArgumentException("Wrong format") // group and winnerTime must be initialized
				)
				result.add(participant)
			}
		}

	}
	return result
}

/**
 * This function gets result of function parseResultFile and
 * write info in file result/organizationsResult.csv
 */
fun createOrganizationsResultProtocol(participants: List<Participant>) {
	val groups = participants.groupBy { it.organization }
	val dirName = "result"
	val fileName = "organizationsResult.csv"
	val fieldCount = 7
	val heading = listOf(
		"Номер", "Фамилия", "Имя", "Год", "Разряд", "Группа", "Балл"
	)
	File(dirName).mkdir()
	File("$dirName/$fileName").createNewFile()
	csvWriter().open("$dirName/$fileName") {
		writeRow(listOf("результаты по группам") + List(fieldCount - 1) { "" })
		groups.forEach { (name, participants) ->
			writeRow(listOf(name) + List(fieldCount - 1) { "" })
			writeRow(heading)
			participants.sortedBy { it.score }.reversed().forEach {
				writeRow(
					listOf(
						it.number,
						it.firstName,
						it.secondName,
						it.year,
						it.rank.russianEquivalent,
						it.group,
						it.score
					)
				)
			}
		}
	}
}