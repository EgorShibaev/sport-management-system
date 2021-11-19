package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File

fun getGroups(fileNames: List<String>) = parseApplies(fileNames.map { File(it).readText() })

fun parseApplies(applicationsContent: List<String>) =
	applicationsContent.fold(listOf<Participant>()) { acc, content ->
		val rows = csvReader().readAll(content)
		require(rows.all { it.size == 5 } && rows.size > 2) { "incorrect format" }
		val organization = rows[0][0]
		val participant = rows.subList(2, rows.lastIndex + 1).map { args ->
			Participant(
				args[1],
				args[2],
				args[3].toInt(),
				SportRank.values().firstOrNull { args[4] == it.russianEquivalent }
					?: throw IllegalArgumentException("incorrect rank"),
				args[0],
				organization
			)
		}
		participant + acc
	}.groupBy { it.group }


fun main(args: Array<String>) {
}
