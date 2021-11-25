import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.SportRank
import ru.emkn.kotlin.sms.protocols.creating.interactiveResultRead
import java.io.ByteArrayInputStream
import java.time.LocalTime
import kotlin.test.*

class TestInteractiveResultRead {

	private val standardIn = System.`in`
	private val part1 = Participant(1, "a", "a", 1, SportRank.III, LocalTime.of(12, 0, 0), "a", "M1")
	private val part2 = Participant(2, "b", "b", 1, SportRank.III, LocalTime.of(12, 0, 0), "a", "M1")
	private val part3 = Participant(3, "c", "c", 1, SportRank.III, LocalTime.of(12, 0, 0), "a", "M1")

	@AfterTest
	fun tearDown() {
		System.setIn(standardIn)
	}

	@Test
	fun testOneParticipant() {
		setInput("1\n1\n2134\n12:00:01\nend")
		val res = interactiveResultRead(listOf(part1))
		assert(res.size == 1)
		assertEquals(res[0], part1)
		assertEquals(res[0].passedPoints, listOf(Pair(2134, LocalTime.of(12, 0, 1))))
	}

	@Test
	fun testTimeDecreasing() {
		setInput("1\n2\n1\n12:02\n2\n12:01\nend")
		val result = interactiveResultRead(listOf(part1))
		assertEquals(result, listOf(part1))
		assert(result[0].passedPoints == listOf<Pair<Int, LocalTime>>())
		assert(result[0].resultTime == null)
	}

	@Test
	fun testManyParticipant() {
		setInput(
			"""
			1
			2
			1
			12:02
			2
			12:05
			2
			2
			1
			12:02
			2
			12:05
			3
			2
			1
			12:02
			2
			12:05
			end
		""".trimIndent()
		)
		val res = interactiveResultRead(listOf(part1, part2, part3))
		assertContentEquals(listOf(part1, part2, part3), res)
		assert(res.all {
			it.passedPoints == listOf(Pair(1, LocalTime.of(12, 2)), Pair(2, LocalTime.of(12, 5)))
		})
	}

	private fun setInput(content: String) {
		System.setIn(ByteArrayInputStream(content.toByteArray()))
	}

}