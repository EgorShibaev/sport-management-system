package ru.emkn.kotlin.sms

data class Participant(
	val firstName: String,
	val lastName: String,
	val year: Int,
	val rank: SportRank,
	val group: String,
	val organization: String,
	var number: Int = numberForParticipant,
	var startTime: Time = Time(12, 0),
	var passedPoints: List<PassedPoint> = emptyList(),
	var resultTime: Time? = null,
	var score: Int? = null,
	var place: Int? = null
) {

	companion object {
		var numberForParticipant = 0
			get() {
				field++
				logger.info { "New participant's number is created: $field" }
				return field
			}
	}

	override fun toString(): String {
		return "Participant(firstName='$firstName', SecondName='$lastName', year=$year, rank=$rank, group='$group', organization='$organization')"
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Participant

		if (firstName != other.firstName) return false
		if (lastName != other.lastName) return false
		if (year != other.year) return false

		return true
	}

	override fun hashCode(): Int {
		var result = firstName.hashCode()
		result = 31 * result + lastName.hashCode()
		result = 31 * result + year
		return result
	}

}