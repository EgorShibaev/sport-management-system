package ru.emkn.kotlin.sms

import java.time.LocalTime

data class Time(private val hour: Int, private val minute: Int, private val second: Int = 0): Comparable<Time> {
	private val time = LocalTime.of(hour, minute, second)

	val allSecond = hour * 60 * 60 + minute * 60 + second

	operator fun minus(other: Time): Time {
		val resTime =
			time.minusHours(other.hour.toLong()).minusMinutes(other.minute.toLong()).minusSeconds(other.second.toLong())
		return Time(resTime.hour, resTime.minute, resTime.second)
	}

	operator fun plus(other: Time): Time {
		val resTime =
			time.plusHours(other.hour.toLong()).plusMinutes(other.minute.toLong()).plusSeconds(other.second.toLong())
		return Time(resTime.hour, resTime.minute, resTime.second)
	}

	operator fun plus(other: Int): Time {
		return Time(time.plusSeconds(other.toLong()))
	}

	operator fun times(other: Int): Time {
		val second = allSecond * other
		return Time(second / (60 * 60), (second % (60 * 60)) / 60, second % 60)
	}

	override operator fun compareTo(other: Time) = time.compareTo(other.time)

	constructor(time: LocalTime): this(time.hour, time.minute, time.second)

	companion object {
		fun parseTime(s: String): LocalTime = when {
			"""(\d\d):(\d\d):(\d\d)""".toRegex().matches(s) -> {
				val hours = s.substring(0..1).toInt()
				val minute = s.substring(3..4).toInt()
				val second = s.substring(6..7).toInt()
				LocalTime.of(hours, minute, second)
			}
			"""(\d\d):(\d\d)""".toRegex().matches(s) -> {
				val hours = s.substring(0..1).toInt()
				val minute = s.substring(3..4).toInt()
				LocalTime.of(hours, minute)
			}
			else -> throw IllegalArgumentException("Incorrect time format")
		}

		val MAX = Time(LocalTime.MAX)
	}

	constructor(line: String) : this(parseTime(line))

	override fun toString() = time.toString()
}