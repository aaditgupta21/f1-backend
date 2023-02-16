package com.nighthawk.spring_portfolio.security;

import com.nighthawk.spring_portfolio.mvc.jwt.*;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
* To enable HTTP Security in Spring, extend the WebSecurityConfigurerAdapter.
*/
@Configuration
@EnableWebSecurity // Beans to enable basic Web security
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // Provide a default configuration using configure(HttpSecurity http)
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // httpSecurity.csrf().disable();
        httpSecurity
                // We don't need CSRF for this example
                .csrf().disable()
                // don't authenticate this particular request
                .authorizeRequests().antMatchers("/authenticate").permitAll()
                .antMatchers("/api/user/newUser").permitAll()
                .antMatchers("/api/user/updateRole").permitAll()
                // .antMatchers("/api/team/newTeam").hasAnyAuthority("Admin")
                .antMatchers("/api/team/newTeam").permitAll()
                .antMatchers("/api/team/").permitAll()
                .antMatchers("/api/user/").hasAnyAuthority("Admin")
                .antMatchers("/api/item/").permitAll()
                .antMatchers("/api/item/newItem").permitAll()
                .antMatchers("/api/user/bets").permitAll()
                .antMatchers("/api/team/setDriverLog").permitAll()
                .antMatchers("/api/team/drivelogs").permitAll()
                .antMatchers("/api/team/drivelog/delete/*").permitAll()
                .antMatchers("/api/race/races/*").permitAll()
                .antMatchers("/api/bets/").permitAll()
                .antMatchers("/api/user/makeBet").permitAll()
                .antMatchers("/api/race/raceResults").permitAll()
                .antMatchers("/api/race/customRace").permitAll()
                .antMatchers("/api/race/declareWinner").permitAll()
                .antMatchers("/api/user/makeBet").permitAll()
                .antMatchers("/api/calendar/newCalendar").permitAll()
                .antMatchers("/api/calendar/").permitAll()
                .antMatchers("/").permitAll()

                // all other requests need to be authenticated
                .anyRequest().authenticated().and().cors().and()
                .headers()
                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Credentials", "true"))
                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-ExposedHeaders", "*", "Authorization"))
                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Headers", "Content-Type",
                        "Authorization", "x-csrf-token"))
                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-MaxAge", "600"))
                .addHeaderWriter(
                        new StaticHeadersWriter("Access-Control-Allow-Methods", "POST", "GET", "OPTIONS", "HEAD"))
                .and()
                // make sure we use stateless session; session won't be used to
                // store user's state.
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Add a filter to validate the tokens with every request
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}