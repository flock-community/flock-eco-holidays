package community.flock.eco.workday.mocks

import community.flock.eco.workday.forms.HoliDayForm
import community.flock.eco.workday.services.HoliDayService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
@ConditionalOnProperty(prefix = "flock.eco.workday", name = ["develop"])
class LoadHolidayData(
    loadPersonData: LoadPersonData,
    private val holiDayService: HoliDayService
) {

    final val now: LocalDate = LocalDate.now().withDayOfYear(1).withDayOfMonth(1)

    init {
        loadPersonData.data.forEach {
            val random = (0..200).shuffled().first().toLong()
            HoliDayForm(
                description = "Test holiday ${it.firstname}",
                from = now.plusDays(random),
                to = now.plusDays(random + 5),
                days = listOf(8.0, 8.0, 8.0, 8.0, 8.0, 8.0),
                hours = 48.0,
                personId = it.uuid
            ).create()
        }
    }

    fun HoliDayForm.create() {
        holiDayService.create(this)
    }
}
