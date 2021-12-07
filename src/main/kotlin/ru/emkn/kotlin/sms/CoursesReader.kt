package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File

interface CoursesReader {
	fun distanceForGroup(groupName: String): Distance
}

object CoursesFromFileReader: CoursesReader {

	private const val coursesFileName = "sample-data/courses.csv"

	private val courses = csvReader().readAll(File(coursesFileName))

	override fun distanceForGroup(groupName: String): Distance {
		val groupRow = courses.subList(1, courses.size).find { it[0] == groupName }
		val points = groupRow?.subList(1, groupRow.size)?.map { it.toInt() }
			?: throw IllegalArgumentException("Wrong format in line $groupRow in file $coursesFileName")
		return PlainDistance(points)
	}
}