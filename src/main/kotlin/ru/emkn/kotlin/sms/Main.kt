package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File
import kotlin.random.Random

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
				// logging
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
				// logging
				null
			}
		}
	}.toSet()
}

fun main(args: Array<String>) {
	parseArgs(args).forEach { flag ->
		when (flag) {
			Flags.START -> {
				println("Enter name of directory with applications:")
				val dirName = readLine() ?: throw IllegalArgumentException()
				val fileNames = File(dirName).listFiles()?.map { it.absoluteFile.toString() }
					?: throw IllegalArgumentException()
				writeStartProtocol(fileNames)
			}
			Flags.RESULT -> {
				println("Enter name of file with result")
				val fileName = readLine() ?: throw IllegalArgumentException()
				println("Enter names directory with start protocols")
				val dirName = readLine() ?: throw IllegalArgumentException()
				val protocolNames = File(dirName).listFiles()?.map { it.absoluteFile.toString() }
					?: throw IllegalArgumentException()
				createResultProtocol(readResultFromFile(fileName, getParticipantsList(protocolNames)))
			}
			Flags.ORGANIZATIONS_RESULT -> {
				println("Enter name of file with result for groups")
				val fileName = readLine() ?: throw IllegalArgumentException()
				createOrganizationsResultProtocol(parseResultFile(fileName))
			}
		}
	}
	// sample-data/applications
	// start-protocols
	// sample-data/splits.csv
	// result/result.csv
}
