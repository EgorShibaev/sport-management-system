import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.SportRank
import ru.emkn.kotlin.sms.interactiveResultRead
import java.io.ByteArrayInputStream
import java.time.LocalTime
import kotlin.test.*

class TestInteractiveResultRead {

	private val standardIn = System.`in`
	private val part1 = Participant(1, "a", "a", 1, SportRank.III, LocalTime.of(12, 0, 0), "a", "M1")

	@AfterTest
	fun tearDown() {
		System.setIn(standardIn)
	}

	@Test
	fun testOneParticipant() {
		setInput("1\n1\n2134\n12:00:01\n")
		val res = interactiveResultRead(listOf(part1))
		assert(res.size == 1)
		assertEquals(res[0], part1)
		assertEquals(res[0].passingPoints, listOf(Pair(2134, LocalTime.of(12, 0, 1))))
	}

	@Test
	fun testTimeDecreasing() {
		setInput("1\n2\n1\n12:02\n2\n12:01")
		assertFailsWith<IllegalArgumentException> { interactiveResultRead(listOf(part1)) }
	}

	private fun setInput(content: String) {
		System.setIn(ByteArrayInputStream(content.toByteArray()))
	}

}