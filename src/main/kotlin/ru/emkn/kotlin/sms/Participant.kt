package ru.emkn.kotlin.sms

import java.time.LocalTime

class Participant(
	val firstName: String,
	val secondName: String,
	val year: Int,
	val rank: SportRank,
	val group: String,
	val organization: String
): Runnable {

	var number = numberForParticipant
	override var startTime: LocalTime = LocalTime.of(12, 0, 0)
	override var passedPoints: List<Pair<Int, LocalTime>> = emptyList()

	companion object {
		var numberForParticipant = 0
			get() {
				field++
				return field
			}
	}

	constructor(
		inputNumber: Int,
		firstName: String,
		secondName: String,
		year: Int,
		rank: SportRank,
		inputStartTime: LocalTime,
		organization: String,
		group: String
	) : this(firstName, secondName, year, rank, group, organization) {
		startTime = inputStartTime
		number = inputNumber
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Participant

		if (firstName != other.firstName) return false
		if (secondName != other.secondName) return false
		if (year != other.year) return false

		return true
	}

	override fun hashCode(): Int {
		var result = firstName.hashCode()
		result = 31 * result + secondName.hashCode()
		result = 31 * result + year
		return result
	}

	override fun toString(): String {
		return "Participant(firstName='$firstName', SecondName='$secondName', year=$year, rank=$rank, group='$group', organization='$organization')"
	}

}