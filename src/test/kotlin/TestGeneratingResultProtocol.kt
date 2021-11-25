import ru.emkn.kotlin.sms.Group
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.SportRank
import java.time.LocalTime
import kotlin.test.*

class TestGeneratingResultProtocol {

	private val part1 = Participant(1, "a", "a", 1, SportRank.III, LocalTime.of(12, 0, 0), "a", "M1")
	private val part2 = Participant(2, "b", "b", 1, SportRank.III, LocalTime.of(12, 0, 0), "b", "M1")
	private val part3 = Participant(3, "c", "c", 1, SportRank.III, LocalTime.of(12, 0, 13), "c", "M1")


	@Test
	fun testThreeParticipants() {
		val group = Group(mutableListOf(part1, part2, part3), false)
		assertEquals(
			"""
				1,1,a,a,1,3р,a,снят,1,снят
				2,2,b,b,1,3р,b,снят,2,снят
				3,3,c,c,1,3р,c,снят,3,снят
			""".trimIndent(),
			group.resultTable().joinToString(separator = "\n") { it.joinToString(separator = ",") }
		)
	}

}