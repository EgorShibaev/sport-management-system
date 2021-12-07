import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.SportRank
import ru.emkn.kotlin.sms.protocols.creating.parseApplies
import kotlin.test.*

class TestAppliesParsing {

	@Test
	fun testOneGroupOneOrganization() {
		val content = listOf(
			"""
			0-ПСКОВ,,,,
			Группа,Фамилия,Имя,Г.р.,Разр.
			M1,Сосницкая,Анна,2013,
			M1,Сосницкий,Тимофей,2008,КМС
			M1,Абросимов,Василий,1982,3р
		""".trimIndent()
		)
		println(listOf(
			Participant("Сосницкая", "Анна", 2013, SportRank.NONE, "M1", "0-ПСКОВ"),
			Participant("Сосницкий", "Тимофей", 2008, SportRank.CMS, "M1", "0-ПСКОВ"),
			Participant("Абросимов", "Василий", 1982, SportRank.III, "M1", "0-ПСКОВ")
		))
		println(parseApplies(content))
		assertEquals(
			listOf(
				Participant("Сосницкая", "Анна", 2013, SportRank.NONE, "M1", "0-ПСКОВ"),
				Participant("Сосницкий", "Тимофей", 2008, SportRank.CMS, "M1", "0-ПСКОВ"),
				Participant("Абросимов", "Василий", 1982, SportRank.III, "M1", "0-ПСКОВ")
			),
			parseApplies(content)
		)
	}

	@Test
	fun testEmpty() {
		val content = listOf("")
		assertFailsWith<IllegalArgumentException> { parseApplies(content) }
	}

	@Test
	fun testDifferentOrganizationsOneGroup() {
		val content = listOf(
			"""
			0-ПСКОВ,,,,
			Группа,Фамилия,Имя,Г.р.,Разр.
			M1,Сосницкая,Анна,2013,
		""".trimIndent(),
			"""
			1-ПСКОВ,,,,
			Группа,Фамилия,Имя,Г.р.,Разр.
			M1,Сосницкий,Тимофей,2008,КМС
		""".trimIndent(),
			"""
			2-ПСКОВ,,,,
			Группа,Фамилия,Имя,Г.р.,Разр.
			M1,Абросимов,Василий,1982,3р
		""".trimIndent()
		)
		val res = parseApplies(content)
		assertEquals(
			listOf(
				Participant("Сосницкая", "Анна", 2013, SportRank.NONE, "M1", "0-ПСКОВ"),
				Participant("Сосницкий", "Тимофей", 2008, SportRank.CMS, "M1", "1-ПСКОВ"),
				Participant("Абросимов", "Василий", 1982, SportRank.III, "M1", "2-ПСКОВ")
			).toSet(),
			res.toSet()
		)
	}

	@Test
	fun testManyGroups() {
		val content = listOf(
			"""
			0-ПСКОВ,,,,
			Группа,Фамилия,Имя,Г.р.,Разр.
			M1,Сосницкая,Анна,2013,
		""".trimIndent(),
			"""
			1-ПСКОВ,,,,
			Группа,Фамилия,Имя,Г.р.,Разр.
			M2,Сосницкий,Тимофей,2008,КМС
		""".trimIndent(),
			"""
			2-ПСКОВ,,,,
			Группа,Фамилия,Имя,Г.р.,Разр.
			M3,Абросимов,Василий,1982,3р
		""".trimIndent()
		)
		val res = parseApplies(content)
		assertEquals(
			listOf(
				Participant("Сосницкая", "Анна", 2013, SportRank.NONE, "M1", "0-ПСКОВ"),
				Participant("Сосницкий", "Тимофей", 2008, SportRank.CMS, "M2", "1-ПСКОВ"),
				Participant("Абросимов", "Василий", 1982, SportRank.III, "M3", "2-ПСКОВ")
			).toSet(),
			res.toSet()
		)
	}
}