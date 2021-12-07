package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File

interface CoursesReader {
	fun distanceForGroup(groupName: String): List<Int>
}

object CoursesFromFileReader: CoursesReader {

	private const val coursesFileName = "sample-data/courses.csv"

	private val courses = csvReader().readAll(File(coursesFileName))

	override fun distanceForGroup(groupName: String): List<Int> {
		val groupRow = courses.subList(1, courses.size).find { it[0] == groupName }
		return groupRow?.subList(1, groupRow.size)?.map { it.toInt() }
			?: throw IllegalArgumentException("Wrong format in line $groupRow in file $coursesFileName")
	}
}