package community.flock.eco.fundraising.config

import community.flock.eco.feature.user.services.UserAccountService
import community.flock.eco.feature.user.services.UserAuthorityService
import community.flock.eco.feature.user.services.UserSecurityService
import community.flock.eco.workday.authorities.HolidayAuthority
import community.flock.eco.workday.filters.GoogleTokenFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var userAuthorityService: UserAuthorityService

    @Autowired
    lateinit var userSecurityService: UserSecurityService

    @Autowired
    lateinit var userAccountService: UserAccountService

    override fun configure(http: HttpSecurity) {

        userAuthorityService.addAuthority(HolidayAuthority::class.java)

        http
                .csrf().disable()
        http
                .authorizeRequests()
                .antMatchers("/_ah/**").permitAll()
                .antMatchers("/login/**").permitAll()
                .anyRequest().authenticated()
        http
                .cors()
        http
                .addFilterBefore(GoogleTokenFilter(userAccountService), UsernamePasswordAuthenticationFilter::class.java)

        userSecurityService.googleLogin(http)
//        userSecurityService.testLogin(http)

    }

}

