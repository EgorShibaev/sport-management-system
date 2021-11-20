package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

fun main(args: Array<String>) {
//	val inputFileNames = listOf(
//		"sample-data/applications/application1.csv",
//		"sample-data/applications/application2.csv",
//		"sample-data/applications/application3.csv",
//		"sample-data/applications/application4.csv",
//		"sample-data/applications/application5.csv",
//		"sample-data/applications/application6.csv",
//		"sample-data/applications/application7.csv",
//		"sample-data/applications/application8.csv",
//		"sample-data/applications/application9.csv",
//		"sample-data/applications/application10.csv",
//		"sample-data/applications/application11.csv",
//		"sample-data/applications/application12.csv",
//		"sample-data/applications/application13.csv",
//		"sample-data/applications/application14.csv",
//		"sample-data/applications/application15.csv",
//		"sample-data/applications/application16.csv",
//	)
//	writeStartProtocol(inputFileNames)
	val fileNames = listOf(
		"start-protocols/start-protocol1.csv",
		"start-protocols/start-protocol2.csv",
		"start-protocols/start-protocol3.csv",
		"start-protocols/start-protocol4.csv",
		"start-protocols/start-protocol5.csv",
		"start-protocols/start-protocol6.csv",
		"start-protocols/start-protocol7.csv",
		"start-protocols/start-protocol8.csv",
		"start-protocols/start-protocol9.csv",
		"start-protocols/start-protocol10.csv",
		"start-protocols/start-protocol11.csv",
		"start-protocols/start-protocol12.csv",
		"start-protocols/start-protocol13.csv",
		"start-protocols/start-protocol14.csv",
		"start-protocols/start-protocol15.csv",
		"start-protocols/start-protocol16.csv",
		"start-protocols/start-protocol17.csv",
		"start-protocols/start-protocol18.csv",
		"start-protocols/start-protocol19.csv",
	)
	val resName = "sample-data/splits.csv"
	createResultProtocol(readResultFromFile(resName, getParticipantsList(fileNames)))
}
