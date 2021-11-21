package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import java.time.LocalTime

/**
 * This interface is used for reading result from file(split.scv) or from console
 */
interface InfoReadable {
	// return pair - number of participant and list of passed points - Number of point and time
	// return null when info ended
	fun getContent(): Map<Int, List<Pair<Int, LocalTime>>>
}

object InteractiveRead : InfoReadable {
	override fun getContent(): Map<Int, List<Pair<Int, LocalTime>>> {
		val result = mutableMapOf<Int, List<Pair<Int, LocalTime>>>()
		while (true) {
			println("Participant number (enter 'end' if you want to end):")
			val input = readLine() ?: throw IllegalArgumentException()
			if (input.lowercase() == "end")
				break
			val number = input.toIntOrNull() ?: throw IllegalArgumentException()
			println("Points count:")
			val count = readLine()?.toIntOrNull() ?: throw IllegalArgumentException()
			val passedPoints = mutableListOf<Pair<Int, LocalTime>>()
			repeat(count) {
				val pointNumber = readLine()?.toIntOrNull() ?: throw IllegalArgumentException()
				val time = parseTime(readLine() ?: throw IllegalArgumentException())
				passedPoints.add(Pair(pointNumber, time))
			}
			result[number] = passedPoints
		}
		return result
	}
}


class ReadFromFile(private val fileName: String) : InfoReadable {
	override fun getContent(): Map<Int, List<Pair<Int, LocalTime>>> =
		csvReader().readAll(File(fileName)).associate { row ->
			val number = row[0].toIntOrNull() ?: throw IllegalArgumentException()
			require(row.size % 2 == 1) { "Wrong input line format" }
			val passedPoints = row.subList(1, row.size).chunked(2).map {
				Pair(
					it[0].toIntOrNull() ?: throw IllegalArgumentException("Point number should be number"),
					parseTime(it[1])
				)
			}
			Pair(number, passedPoints)
		}
}
