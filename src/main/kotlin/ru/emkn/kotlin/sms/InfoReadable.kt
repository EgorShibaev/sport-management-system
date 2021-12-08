package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File

data class ParticipantResult(val id: Int, val result: List<PassedPoint>)

/**
 * This interface is used for reading result from file(split.scv) or from console
 */
interface InfoReadable {
	fun getContent(): List<ParticipantResult>
}

object InteractiveRead : InfoReadable {
	override fun getContent(): List<ParticipantResult> {
		val result = mutableListOf<ParticipantResult>()
		while (true) {
			println("Participant number (enter 'end' if you want to end):")
			val input = readLine() ?: throw IllegalArgumentException()
			if (input.lowercase() == "end")
				break
			val number = input.toIntOrNull() ?: throw IllegalArgumentException()
			println("Points count:")
			val count = readLine()?.toIntOrNull() ?: throw IllegalArgumentException()
			val passedPoints = mutableListOf<PassedPoint>()
			repeat(count) {
				val pointNumber = readLine()?.toIntOrNull() ?: throw IllegalArgumentException()
				val time = Time(readLine() ?: throw IllegalArgumentException())
				passedPoints.add(PassedPoint(pointNumber, time))
			}
			result.add(ParticipantResult(number, passedPoints))
		}
		return result
	}
}


class ReadFromFile(private val fileName: String) : InfoReadable {
	override fun getContent(): List<ParticipantResult> =
		csvReader().readAll(File(fileName)).map { row ->
			val number = row[0].toIntOrNull() ?: throw IllegalArgumentException()
			require(row.size % 2 == 1) { "Wrong input line format" }
			val passedPoints = row.subList(1, row.size).chunked(2).map {
				PassedPoint(
					it[0].toIntOrNull() ?: throw IllegalArgumentException("Point number should be number"),
					Time(it[1])
				)
			}
			ParticipantResult(number, passedPoints)
		}
}
