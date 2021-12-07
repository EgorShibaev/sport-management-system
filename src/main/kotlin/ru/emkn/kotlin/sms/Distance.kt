package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.protocols.creating.timeDistance
import java.time.LocalTime

abstract class Distance(val points: List<Int>) {
	abstract fun totalTime(result: List<PassedPoint>, startTime: LocalTime): LocalTime?
}

data class PassedPoint(val pointId: Int, val result: LocalTime)

class PlainDistance(points: List<Int>) : Distance(points) {

	override fun totalTime(result: List<PassedPoint>, startTime: LocalTime): LocalTime? {
		when {
			result.map { it.pointId } != points -> logger.warn { "Participant didn't passed necessary points" }
			result.map { it.result }.sorted() != result.map { it.result } ->
				logger.warn { "Jumping in time is not allowed" }
			result.first().result <= startTime -> logger.warn { "False start" }
			else -> return timeDistance(result.last().result, startTime)
		}
		return null
	}
}

class OptionallyDistance(points: List<Int>, private val necessaryPointsCount: Int) : Distance(points) {

	override fun totalTime(result: List<PassedPoint>, startTime: LocalTime): LocalTime? {
		when {
			result.any { it.pointId !in points } -> logger.warn { "Wrong point" }
			result.size < necessaryPointsCount -> logger.warn { "Not enough points" }
			else -> return timeDistance(result.last().result, startTime)
		}
		return null
	}
}