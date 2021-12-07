import ru.emkn.kotlin.sms.Group
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.SportRank
import java.time.LocalTime
import kotlin.test.*

class TestGeneratingResultProtocol {

	private val part1 = Participant(
		number = 1,
		firstName = "a",
		lastName = "a",
		year = 1,
		rank = SportRank.III,
		startTime = LocalTime.of(12, 0, 0),
		organization = "a",
		group = "M1"
	)
	private val part2 = Participant(
		number = 2,
		firstName = "b",
		lastName = "b",
		year = 1,
		rank = SportRank.III,
		startTime = LocalTime.of(12, 0, 0),
		organization = "b",
		group = "M1"
	)
	private val part3 = Participant(
		number = 3,
		firstName = "c",
		lastName = "c",
		year = 1,
		rank = SportRank.III,
		startTime = LocalTime.of(12, 0, 0),
		organization = "c",
		group = "M1"
	)

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