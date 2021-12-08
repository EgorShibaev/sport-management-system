package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File

interface CoursesReader {
	fun distanceForGroup(groupName: String): Distance
}

object CoursesFromFileReader: CoursesReader {

	private const val plainCoursesFileName = "sample-data/courses/PlainCourses.csv"
	private const val optionallyCoursesFileName = "sample-data/courses/OptionallyCourses.csv"
	private const val weightedCoursesFileName = "sample-data/courses/WeightedPoints.csv"


	private val plainCourses = csvReader().readAll(File(plainCoursesFileName))
	private val optionallyCourses = csvReader().readAll(File(optionallyCoursesFileName))
	private val weightedCourses = csvReader().readAll(File(weightedCoursesFileName))


	override fun distanceForGroup(groupName: String): Distance {
		val plainRow = plainCourses.subList(1, plainCourses.size).find { it[0] == groupName }
		val optionallyRow = optionallyCourses.subList(1, optionallyCourses.size).find { it[0] == groupName }
		val weightedRow = weightedCourses.subList(1, weightedCourses.size).find { it[0] == groupName }

		require(listOf(plainRow, optionallyRow, weightedRow).count { it != null } <= 1)
		{ "each group should be in one file" }
		plainRow?.let {
			val points = plainRow.subList(1, plainRow.size).map { it.toInt() }
			return PlainDistance(points)
		}
		optionallyRow?.let {
			val count = optionallyRow[1].toInt()
			val point = optionallyRow.subList(2, optionallyRow.size).map { it.toInt() }
			return OptionallyDistance(point, count)
		}
		weightedRow?.let {
			val points = weightedRow.subList(1, weightedRow.size).chunked(2).map { it[0].toInt() }
			val cost = mutableMapOf<Int, Int>()
			weightedRow.subList(1, weightedRow.size).chunked(2).map { cost[it[0].toInt()] = it[1].toInt() }
			return WeightedDistance(points, cost)
		}
		throw IllegalArgumentException("No course for group $groupName")
	}
}