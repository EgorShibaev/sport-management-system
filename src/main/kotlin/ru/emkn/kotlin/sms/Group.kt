package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import ru.emkn.kotlin.sms.protocols.creating.timeDistance
import java.io.File
import java.time.LocalTime

class Group(
	private val participants: MutableList<Participant>,
	areResultsReady: Boolean
) {
	val name: String

	// points which participants from this group
	// should pass
	private val distance: List<Int>
	private val winnerTime: LocalTime? // null when each participant was dropped

	init {
		require(participants.isNotEmpty()) { "The group $participants is empty" }
		logger.info { "Checking passed" }

		name = participants[0].group

		if (areResultsReady) {
			val fileName = "sample-data/courses.csv"
			val courses = csvReader().readAll(File(fileName))
			val currentGroupRow = courses.subList(1, courses.size).find { it[0] == name }
			distance = currentGroupRow?.subList(1, currentGroupRow.size)?.map { it.toInt() }
				?: throw IllegalArgumentException("Wrong format in line $currentGroupRow in file $fileName")

			// check that each participant passed the necessary points
			require(participants.all { participant ->
				participant.passedPoints.map { it.first } == distance || participant.resultTime == null
			}) { "Participant in group $name passed wrong points" }
			logger.info { "Checking passed" }

			sortByTime()
			winnerTime = participants.first().resultTime
		} else {
			distance = emptyList()
			winnerTime = null
		}
	}

	private fun sortByTime() {
		participants.sortBy {
			if (it.passedPoints.isEmpty())
				LocalTime.MAX
			else
				timeDistance(it.passedPoints.last().second, it.startTime)
		}
		logger.info { "participants are sorted in group $name" }
	}

	fun resultTable(): List<List<String>> {
		var currentPlace = 0
		return participants.map {
			currentPlace++
			val result = it.resultTime
			val diff = when {
				winnerTime == null || result == null -> "снят"
				else -> "+${timeDistance(result, winnerTime)}"
			}
			listOf(
				currentPlace.toString(),
				it.number.toString(),
				it.firstName,
				it.secondName,
				it.year.toString(),
				it.rank.russianEquivalent,
				it.organization,
				it.resultTime?.toString() ?: "снят",
				currentPlace.toString(),
				diff
			)
		}
	}

	fun defineTimeForParticipants() {
		var offset = 0L
		val step = 5
		participants.forEach {
			it.startTime = it.startTime.plusMinutes(offset)
			offset += step
		}
		logger.info { "time is defined for each participant in group $name" }
	}

	fun generateStartProtocol(): List<List<String>> {
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
		return listOf(listOf(name) + List(fieldCount - 1) { "" }) +
				participants.map { generateLineForParticipant(it) }
	}
}