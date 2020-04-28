package community.flock.eco.workday.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import community.flock.eco.feature.user.forms.UserAccountPasswordForm
import community.flock.eco.feature.user.services.UserAccountService
import community.flock.eco.feature.user.services.UserSecurityService
import community.flock.eco.feature.user.services.UserService
import community.flock.eco.workday.Application
import community.flock.eco.workday.forms.HoliDayForm
import community.flock.eco.workday.forms.PersonForm
import community.flock.eco.workday.model.Status
import community.flock.eco.workday.services.PersonService
import community.flock.eco.workday.utils.dayFromLocalDate
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [Application::class])
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class HoliDayControllerTest {
    private val baseUrl: String = "/api/holidays"
    private val email: String = "admin@reynholm-industries.co.uk"

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var userAccountService: UserAccountService

    @Autowired
    private lateinit var personService: PersonService

    @Autowired
    private lateinit var userService: UserService

    private lateinit var user: RequestPostProcessor

    @Before
    fun setup() {
        user = UserAccountPasswordForm(
            email = email,
            name = "Administrator",
            authorities = setOf(
                "HolidayAuthority.ADMIN",
                "HolidayAuthority.READ",
                "HolidayAuthority.WRITE"
            ),
            password = "admin")
            .run { userAccountService.createUserAccountPassword(this) }
            .run { UserSecurityService.UserSecurityPassword(this) }
            .run { user(this) }
    }

    @After
    fun teardown() {
        userAccountService.findUserAccountPasswordByUserEmail(email)
            ?.apply { userService.delete(this.user.code) }
    }

    @Test
    fun `should get a holiday via GET-method`() {
        /* DRY-Block */
        val person = PersonForm(
            firstname = "Jen",
            lastname = "Barber",
            email = "jen@reynholm-industries.co.uk",
            position = "Head of IT",
            number = null,
            userCode = null
        ).run { personService.create(this)!! }

        val holidayForm = HoliDayForm(
            description = "Wimbledon",
            status = Status.REQUESTED,
            from = dayFromLocalDate(),
            to = dayFromLocalDate(2),
            days = listOf(8, 8),
            hours = 16,
            personCode = person.code
        )

        val holidayCode = post(holidayForm)
            .andReturn()
            .response
            .contentAsString
            .run { mapper.readTree(this) }
            .run { this.get("code").textValue() }
        /* DRY-Block */

        get(holidayForm, holidayCode)
    }

    @Test
    fun `should create a valid holiday via POST-method`() {
        /* DRY-Block */
        val person = PersonForm(
            firstname = "Jen",
            lastname = "Barber",
            email = "jen@reynholm-industries.co.uk",
            position = "Head of IT",
            number = null,
            userCode = null
        ).run { personService.create(this)!! }
        /* DRY-Block */

        val holidayForm = HoliDayForm(
            description = "Wimbledon",
            status = Status.REQUESTED,
            from = dayFromLocalDate(),
            to = dayFromLocalDate(2),
            days = listOf(8, 8),
            hours = 16,
            personCode = person.code
        )

        post(holidayForm)
    }

    @Test
    fun `should update a existing holiday via PUT-Method`() {
        /* DRY-Block */
        val person = PersonForm(
            firstname = "Jen",
            lastname = "Barber",
            email = "jen@reynholm-industries.co.uk",
            position = "Head of IT",
            number = null,
            userCode = null
        ).run { personService.create(this)!! }

        val holidayForm = HoliDayForm(
            description = "Wimbledon",
            status = Status.REQUESTED,
            from = dayFromLocalDate(),
            to = dayFromLocalDate(2),
            days = listOf(8, 8),
            hours = 16,
            personCode = person.code
        )

        val holidayCode = post(holidayForm)
            .andReturn()
            .response
            .contentAsString
            .run { mapper.readTree(this) }
            .run { this.get("code").textValue() }
        /* DRY-Block */
        holidayForm.copy(status = Status.APPROVED)

        put(holidayForm, holidayCode)
    }

    @Test
    fun `should delete a holiday via DELETE-Method`() {
        /* DRY-Block */
        val person = PersonForm(
            firstname = "Jen",
            lastname = "Barber",
            email = "jen@reynholm-industries.co.uk",
            position = "Head of IT",
            number = null,
            userCode = null
        ).run { personService.create(this)!! }

        val holidayForm = HoliDayForm(
            description = "Wimbledon",
            status = Status.REQUESTED,
            from = dayFromLocalDate(),
            to = dayFromLocalDate(2),
            days = listOf(8, 8),
            hours = 16,
            personCode = person.code
        )

        val holidayCode = post(holidayForm)
            .andReturn()
            .response
            .contentAsString
            .run { mapper.readTree(this) }
            .run { this.get("code").textValue() }
        /* DRY-Block */

        mvc.perform(delete("$baseUrl/$holidayCode")
            .with(user)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isNoContent)

        mvc.perform(get("$baseUrl/$holidayCode")
            .with(user)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    // *-- utility functions --*
//    private fun getAll() = get()
    /**
     *
     */
    private fun get(holiDayForm: HoliDayForm, holidayCode: String? = null) = mvc.perform(
        get("$baseUrl/$holidayCode")
            .with(user)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
        .andExpect(status().isOk)
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("\$.id").exists())
        .andExpect(jsonPath("\$.code").exists())
        .andExpect(jsonPath("\$.code").isString)
        .andExpect(jsonPath("\$.description").value(holiDayForm.description.toString()))
        .andExpect(jsonPath("\$.status").value(holiDayForm.status.toString()))
        .andExpect(jsonPath("\$.hours").value(holiDayForm.hours))
        .andExpect(jsonPath("\$.personCode").value(holiDayForm.personCode.toString()))

    /**
     *
     */
    private fun post(holiDayForm: HoliDayForm) = mvc.perform(
        post(baseUrl)
            .with(user)
            .content(mapper.writeValueAsString(holiDayForm))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
        .andExpect(status().isOk)
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("\$.id").exists())
        .andExpect(jsonPath("\$.code").exists())
        .andExpect(jsonPath("\$.code").isString)
        .andExpect(jsonPath("\$.description").value(holiDayForm.description.toString()))
        .andExpect(jsonPath("\$.status").value(holiDayForm.status.toString()))
        .andExpect(jsonPath("\$.hours").value(holiDayForm.hours))
        .andExpect(jsonPath("\$.personCode").value(holiDayForm.personCode.toString()))

    /**
     *
     */
    private fun put(holiDayForm: HoliDayForm, holidayCode: String) = mvc.perform(
        put("$baseUrl/$holidayCode")
            .with(user)
            .content(mapper.writeValueAsString(holiDayForm))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
        .andExpect(status().isOk)
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("\$.id").exists())
        .andExpect(jsonPath("\$.code").exists())
        .andExpect(jsonPath("\$.code").isString)
        .andExpect(jsonPath("\$.description").value(holiDayForm.description.toString()))
        .andExpect(jsonPath("\$.status").value(holiDayForm.status.toString()))
        .andExpect(jsonPath("\$.hours").value(holiDayForm.hours))
        .andExpect(jsonPath("\$.personCode").value(holiDayForm.personCode.toString()))
}
