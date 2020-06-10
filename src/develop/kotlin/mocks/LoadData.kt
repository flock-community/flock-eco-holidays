package community.flock.eco.workday.mocks

import community.flock.eco.workday.repository.DayRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("develop")
class LoadData(
    val dayRepository: DayRepository
)
