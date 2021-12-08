package ru.emkn.kotlin.sms

abstract class Distance(val points: List<Int>) {
	abstract fun totalTime(result: List<PassedPoint>, startTime: Time): Time?
}

data class PassedPoint(val pointId: Int, val result: Time)

class PlainDistance(points: List<Int>) : Distance(points) {

	override fun totalTime(result: List<PassedPoint>, startTime: Time): Time? {
		when {
			result.map { it.pointId } != points -> logger.warn { "Participant didn't passed necessary points" }
			result.map { it.result }.sorted() != result.map { it.result } ->
				logger.warn { "Jumping in time is not allowed" }
			result.first().result <= startTime -> logger.warn { "False start" }
			else -> return result.last().result - startTime
		}
		return null
	}
}

class OptionallyDistance(points: List<Int>, private val necessaryPointsCount: Int) : Distance(points) {

	override fun totalTime(result: List<PassedPoint>, startTime: Time): Time? {
		when {
			result.any { it.pointId !in points } -> logger.warn { "Wrong point" }
			result.size < necessaryPointsCount -> logger.warn { "Not enough points" }
			result.map { it.result }.sorted() != result.map { it.result } ->
				logger.warn { "Jumping in time is not allowed" }
			result.first().result <= startTime -> logger.warn { "False start" }
			else -> return result.last().result - startTime
		}
		return null
	}
}

class WeightedDistance(points: List<Int>, private val cost: Map<Int, Int>): Distance(points) {

	init {
		require(cost.all { it.key in points })
	}

	override fun totalTime(result: List<PassedPoint>, startTime: Time): Time? {
		when {
			result.any { it.pointId !in points } -> logger.warn { "Wrong point" }
			result.map { it.result }.sorted() != result.map { it.result } ->
				logger.warn { "Jumping in time is not allowed" }
			result.first().result <= startTime -> logger.warn { "False start" }
			else -> {
				var res = Time(0, 0)
				var lastPointTime = startTime
				result.forEach {
					res += (it.result - lastPointTime) * cost.getValue(it.pointId)
					lastPointTime = it.result
				}
				return res
			}
		}
		return null
	}
}