import ru.emkn.kotlin.sms.Group
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.SportRank
import ru.emkn.kotlin.sms.protocols.creating.allSeconds
import java.time.LocalTime
import kotlin.test.*

class TestDefiningTimeInGroup {

	private val part1 = Participant(1, "a", "a", 1, SportRank.III, LocalTime.of(12, 0, 0), "a", "M1")
	private val part2 = Participant(2, "b", "b", 1, SportRank.III, LocalTime.of(12, 0, 0), "a", "M1")
	private val part3 = Participant(3, "c", "c", 1, SportRank.III, LocalTime.of(12, 0, 0), "a", "M1")


	@Test
	fun testThreeParticipants() {
		val group = Group(mutableListOf(part1, part2, part3), false)
		group.defineTimeForParticipants()
		assertEquals(part1.startTime, LocalTime.of(12, 0, 0))
		assertEquals(part2.startTime, LocalTime.of(12, 5, 0))
		assertEquals(part3.startTime, LocalTime.of(12, 10, 0))
	}

	@Test
	fun testManyParticipants() {
		val parts = mutableListOf<Participant>()
		repeat(100) {
			parts.add(Participant(it.toString(), it.toString(), 1, SportRank.I, "", ""))
		}
		val group = Group(parts, false)
		group.defineTimeForParticipants()
		parts.withIndex().forEach {
			assertEquals(12 * 3600 + it.index * 300, it.value.startTime.allSeconds())
		}
	}
}