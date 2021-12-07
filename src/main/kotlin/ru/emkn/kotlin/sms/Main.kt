package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import mu.KotlinLogging
import ru.emkn.kotlin.sms.protocols.creating.*
import java.io.File
import kotlin.random.Random

val logger = KotlinLogging.logger {}

enum class Flags {
	START, RESULT, ORGANIZATIONS_RESULT
}

fun parseArgs(args: Array<String>): Set<Flags> {
	val flags = args.map {
		when {
			it[0] == '-' && it.substring(1..it.lastIndex).all { ch -> ch.isLetter() } -> {
				it.substring(1..it.lastIndex).toSet()
			}
			else -> {
				logger.warn { "Incorrect flag format: $it" }
				setOf()
			}
		}
	}.flatten().toSet()
	return flags.mapNotNull {
		when (it) {
			'r' -> Flags.RESULT
			's' -> Flags.START
			'o' -> Flags.ORGANIZATIONS_RESULT
			else -> {
				logger.warn { "Unknown flag $it. This flag will be ignored" }
				null
			}
		}
	}.toSet()
}

fun main(args: Array<String>) {
//	creating result and writing it to file splits.csv
//	createSplitResult(File("start-protocols").listFiles()!!.map { it.absoluteFile.toString() })
	parseArgs(args).forEach { flag ->
		try {
			when (flag) {
				Flags.START -> start()
				Flags.RESULT -> result()
				Flags.ORGANIZATIONS_RESULT -> organizationsResult()
			}
		} catch (e: IllegalArgumentException) {
			println(e.message)
		}
	}
/*
sample-data/applications
start-protocols
sample-data/splits.csv
result/result.csv
*/
}

private fun organizationsResult() {
	logger.info { "Creating result for organizations is started" }
	println("Enter name of file with result for groups")
	val fileName = readLine() ?: throw IllegalArgumentException()
	createOrganizationsResultProtocol(parseResultFile(fileName))
}

private fun result() {
	logger.info { "Creating result for groups is started" }
	println("Enter names directory with start protocols")
	val dirName = readLine() ?: throw IllegalArgumentException()
	val protocolNames = File(dirName).listFiles()?.map { it.absoluteFile.toString() }
		?: throw IllegalArgumentException()

	println("(interactive/file):")
	val way = readLine()?.lowercase() ?: throw IllegalArgumentException()
	when (way) {
		"file" -> {
			println("Enter name of file with result")
			val fileName = readLine() ?: throw IllegalArgumentException()
			createResultProtocol(readResultFromFile(fileName, getParticipantsList(protocolNames)))
		}
		else -> {
			createResultProtocol(interactiveResultRead(getParticipantsList(protocolNames)))
		}
	}
}

private fun start() {
	logger.info { "Creating started protocols is started" }
	println("Enter name of directory with applications:")
	val dirName = readLine() ?: throw IllegalArgumentException()
	val fileNames = File(dirName).listFiles()?.map { it.absoluteFile.toString() }
		?: throw IllegalArgumentException()
	writeStartProtocol(fileNames)
}

private fun createSplitResult(fileNames: List<String>) {
	val result = mutableListOf<List<String>>()
	fileNames.forEach { fileName ->
		val content = csvReader().readAll(File(fileName))
		val name = content[0][0]
		val points = CoursesFromFileReader.distanceForGroup(name)
		content.subList(1, content.size).forEach { row ->
			var currentTime = parseTime(row[5])
			val resultRow = mutableListOf(row[0]) + points.map {
				currentTime = currentTime.plusSeconds(Random.nextLong(500))
				listOf(it.toString(), currentTime.toString())
			}.flatten()
			result.add(resultRow)
		}
	}
	val fileName = "sample-data/splits.csv"
	logger.info { "Changing $fileName content" }
	csvWriter().writeAll(result, fileName)
}
