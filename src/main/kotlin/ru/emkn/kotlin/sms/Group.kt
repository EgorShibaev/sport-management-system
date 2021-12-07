package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.protocols.creating.timeDistance
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
			distance = CoursesFromFileReader.distanceForGroup(name)

			// check that each participant passed the necessary points
			require(participants.all { participant ->
				participant.passedPoints.map { it.pointId } == distance || participant.resultTime == null
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
			it.resultTime ?: LocalTime.MAX
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
				it.lastName,
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
		val step = 1
		participants.shuffled().forEach {
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
				participant.lastName,
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