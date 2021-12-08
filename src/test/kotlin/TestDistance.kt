import ru.emkn.kotlin.sms.*
import kotlin.test.*

class TestDistance {

	@Test
	fun testPlainDistance() {
		val distance = PlainDistance(listOf(1, 2, 3, 4, 5, 7, 10))
		val passedPoints = listOf(
			PassedPoint(1, Time(0, 1)),
			PassedPoint(2, Time(0, 2)),
			PassedPoint(3, Time(0, 4)),
			PassedPoint(4, Time(0, 10)),
			PassedPoint(5, Time(0, 15)),
			PassedPoint(7, Time(0, 20)),
			PassedPoint(10, Time(0, 23)),
		)
		assertEquals(Time(0, 23), distance.totalTime(passedPoints, Time(0, 0)))
	}

	@Test
	fun testFalseStart() {
		val distance = PlainDistance(listOf(1, 2, 3, 4, 5, 7, 10))
		val passedPoints = listOf(
			PassedPoint(1, Time(0, 0)),
			PassedPoint(2, Time(0, 2)),
			PassedPoint(3, Time(0, 4)),
			PassedPoint(4, Time(0, 10)),
			PassedPoint(5, Time(0, 15)),
			PassedPoint(7, Time(0, 20)),
			PassedPoint(10, Time(0, 23)),
		)
		assertEquals(null, distance.totalTime(passedPoints, Time(0, 0)))
	}

	@Test
	fun testTimeJumping() {
		val distance = PlainDistance(listOf(1, 2, 3, 4, 5, 7, 10))
		val passedPoints = listOf(
			PassedPoint(1, Time(0, 1)),
			PassedPoint(2, Time(0, 2)),
			PassedPoint(3, Time(0, 4)),
			PassedPoint(4, Time(0, 10)),
			PassedPoint(5, Time(0, 9)), // here
			PassedPoint(7, Time(0, 20)),
			PassedPoint(10, Time(0, 23)),
		)
		assertEquals(null, distance.totalTime(passedPoints, Time(0, 0)))
	}

	@Test
	fun testOptionallyDistance() {
		val distance = OptionallyDistance(listOf(1, 2, 4, 5), 3)
		val passedPoint = listOf(
			PassedPoint(2, Time(0, 1)),
			PassedPoint(4, Time(0, 2)),
			PassedPoint(1, Time(0, 3)), // it's ok
		)
		assertEquals(Time(0, 3), distance.totalTime(passedPoint, Time(0, 0)))
	}

	@Test
	fun testOptionallyDistanceNotEnoughPoints() {
		val distance = OptionallyDistance(listOf(1, 2, 4, 5), 3)
		val passedPoint = listOf(
			PassedPoint(2, Time(0, 1)),
			PassedPoint(4, Time(0, 2)),
		)
		assertEquals(null, distance.totalTime(passedPoint, Time(0, 0)))
	}

	@Test
	fun testWeightedDistance() {
		val distance = WeightedDistance(listOf(1, 2, 4), mapOf(1 to 5, 2 to 1, 4 to 10))
		val passedPoint = listOf(
			PassedPoint(1, Time(0, 1)),
			PassedPoint(4, Time(0, 3)),
		)
		assertEquals(Time(0, 25), distance.totalTime(passedPoint, Time(0, 0)))
	}
}