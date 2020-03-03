package community.flock.eco.workday.repository

import community.flock.eco.workday.model.SickDay
import java.util.Optional
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SickdayRepository : CrudRepository<SickDay, Long> {
    fun findByCode(code: String): Optional<SickDay>
    fun deleteByCode(code: String)
    fun findAllByPersonCode(personCode: String): Iterable<SickDay>
    fun findAllByPersonUserCode(userCode: String): Iterable<SickDay>
}
