import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.PassedPoint
import ru.emkn.kotlin.sms.SportRank
import ru.emkn.kotlin.sms.Time
import ru.emkn.kotlin.sms.protocols.creating.interactiveResultRead
import java.io.ByteArrayInputStream
import kotlin.test.*

class TestInteractiveResultRead {

	private val standardIn = System.`in`
	private val part1 = Participant(
		number = 1,
		firstName = "a",
		lastName = "a",
		year = 1,
		rank = SportRank.III,
		startTime = Time(12, 0),
		organization = "a",
		group = "M1"
	)
	private val part2 = Participant(
		number = 2,
		firstName = "b",
		lastName = "b",
		year = 1,
		rank = SportRank.III,
		startTime = Time(12, 0),
		organization = "a",
		group = "M1"
	)
	private val part3 = Participant(
		number = 3,
		firstName = "c",
		lastName = "c",
		year = 1,
		rank = SportRank.III,
		startTime = Time(12, 0),
		organization = "a",
		group = "M1"
	)

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
		assertEquals(res[0].passedPoints, listOf(PassedPoint(2134, Time(12, 0, 1))))
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
			it.passedPoints == listOf(
				PassedPoint(1, Time(12, 2)),
				PassedPoint(2, Time(12, 5))
			)
		})
	}

	private fun setInput(content: String) {
		System.setIn(ByteArrayInputStream(content.toByteArray()))
	}

}