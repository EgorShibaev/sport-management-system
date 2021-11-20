package ru.emkn.kotlin.sms

fun main(args: Array<String>) {
//	val inputFileNames = listOf(
//		"sample-data/applications/application2.csv",
//	)
//	writeStartProtocol(inputFileNames)
	val fileNames = listOf(
		"start-protocols/start-protocol1",
//		"start-protocols/start-protocol2",
//		"start-protocols/start-protocol3",
//		"start-protocols/start-protocol4"
	)
	createResultProtocol(interactiveResultRead(getParticipantsList(fileNames)))
}
