package ru.emkn.kotlin.sms

import java.util.*

class Group(
	private val participants: MutableList<Participant>,
	areResultsReady: Boolean
) {
	val name: String

	// points which participants from this group
	// should pass
	private val distance: Distance
	private val winnerTime: Time? // null when each participant was dropped

	init {
		require(participants.isNotEmpty()) { "The group $participants is empty" }
		logger.info { "Checking passed" }

		name = participants[0].group

		if (areResultsReady) {
			distance = CoursesFromFileReader.distanceForGroup(name)

			participants.forEach { it.resultTime = distance.totalTime(it.passedPoints, it.startTime) }
			sortByTime()
			winnerTime = participants.first().resultTime
		} else {
			distance = PlainDistance(emptyList())
			winnerTime = null
		}
	}

	private fun sortByTime() {
		participants.sortBy {
			it.resultTime ?: Time.MAX
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
				else -> "+${result - winnerTime}"
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
		var offset = 0
		val step = 1
		participants.shuffled(Random(1)).forEach {
			it.startTime = it.startTime + (offset * 60)
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