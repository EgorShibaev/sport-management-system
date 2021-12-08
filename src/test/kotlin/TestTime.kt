import ru.emkn.kotlin.sms.Time
import kotlin.test.*

class TestTime {

	@Test
	fun fromString() {
		val lines = listOf("00:00:00", "01:00:00", "23:00:01", "12:01:00", "10:01:00")
		val res = lines.map { Time(it) }
		assertEquals(
			listOf(Time(0, 0), Time(1, 0), Time(23, 0, 1), Time(12, 1), Time(10, 1)),
			res
		)
	}

	@Test
	fun testArithmetics() {
		val time = Time(1, 13, 14)
		assertEquals(Time(1, 13, 15), time + 1)
		assertEquals(Time(1, 14), time + 46)
		assertEquals(Time(1, 13), time + (-14))
		val time2 = Time(0, 1, 1)
		assertEquals(Time(1, 14, 15), time + time2)
		assertEquals(Time(1, 12, 13), time - time2)
		assertEquals(Time(2, 26, 28), time * 2)
	}

	@Test
	fun testSorting() {
		val times = listOf(

			Time(12, 1, 0),
			Time(1, 0, 1),
			Time(0, 0, 59),
			Time(1, 0),
			Time(23, 1, 1)
		)
		assertEquals(
			listOf(
				Time(0, 0, 59),
				Time(1, 0, 0),
				Time(1, 0, 1),
				Time(12, 1, 0),
				Time(23, 1, 1)
			), times.sorted()
		)
	}
}