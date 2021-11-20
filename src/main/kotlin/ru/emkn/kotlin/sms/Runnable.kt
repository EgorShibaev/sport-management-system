package ru.emkn.kotlin.sms

import java.time.LocalTime

interface Runnable {

	val startTime: LocalTime

	val passedPoints: List<Pair<Int, LocalTime>>
}