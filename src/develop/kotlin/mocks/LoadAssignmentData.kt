package community.flock.eco.workday.mocks

import community.flock.eco.workday.model.Assignment
import community.flock.eco.workday.repository.AssignmentRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
@ConditionalOnProperty(prefix = "flock.eco.workday", name = ["develop"])
class LoadAssignmentData(
    private val loadClientData: LoadClientData,
    private val assignmentRepository: AssignmentRepository,
    private val loadPersonData: LoadPersonData
) {

    final val now: LocalDate = LocalDate.now().withDayOfMonth(1)

    val data: MutableSet<Assignment> = mutableSetOf()

    init {
        create("tommy@sesam.straat", "client_a", "DevOps engineer", 90.0, now.minusMonths(3))
        create("ieniemienie@sesam.straat", "client_a", "Test engineer", 100.0, now.minusMonths(8), now.plusMonths(8))
        create("pino@sesam.straat", "client_b", "Senior software engineer", 135.0, now.minusMonths(0), now.plusMonths(12))
        create("bert@sesam.straat", "client_c", "Medior software engineer", 90.0, now.minusMonths(4), now.plusMonths(8))
        create("ernie@sesam.straat", "client_d", "Junior software engineer", 85.0, now.minusMonths(1))
    }

    private final fun create(email: String, client: String, role: String, hourlyRate: Double, from: LocalDate, to: LocalDate? = null) = Assignment(
        from = from,
        to = to,
        person = loadPersonData.findPersonByUserEmail(email),
        client = loadClientData.findClientByCode(client),
        hourlyRate = hourlyRate,
        hoursPerWeek = 36,
        role = role
    )
        .save()

    private fun Assignment.save(): Assignment = assignmentRepository
        .save(this)
        .also { data.add(it) }
}
