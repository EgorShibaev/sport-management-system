package ru.emkn.kotlin.sms

class Participant(
	val firstName: String,
	val SecondName: String,
	val year: Int,
	val rank: SportRank,
	val group: String,
	val organization: String
) {

	val number = numberForParticipant
	val time: Int? = null

	companion object {
		var numberForParticipant = 0
			get() {
				field++
				return field
			}
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Participant

		if (firstName != other.firstName) return false
		if (SecondName != other.SecondName) return false
		if (year != other.year) return false

		return true
	}

	override fun hashCode(): Int {
		var result = firstName.hashCode()
		result = 31 * result + SecondName.hashCode()
		result = 31 * result + year
		return result
	}

	override fun toString(): String {
		return "Participant(firstName='$firstName', SecondName='$SecondName', year=$year, rank=$rank, group='$group', organization='$organization')"
	}

}