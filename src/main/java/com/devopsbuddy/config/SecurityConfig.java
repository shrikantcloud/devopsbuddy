package com.devopsbuddy.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.devopsbuddy.backend.service.UserSecurityService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String  DEV_PROFILE     = "dev";

    @Autowired
    private UserSecurityService  userSecurityService;

    @Autowired
    private Environment          env;

    public static final String[] PUBLIC_MATCHERS = { "/webjars/**", "/css/**", "/js/**", "/images/**", "/", "/about/**", "/contact/**",
                                                     "/error/**/*", "/console/**" };
    public static final String   LOGIN_URL       = "/login";
    public static final String   PAYLOAD_URL     = "/payload";
    public static final String   ERROR_URL       = "/login?error";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        List<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(DEV_PROFILE)) {
            http.csrf().disable();
            http.headers().frameOptions().disable();
        }

        http.authorizeRequests().antMatchers(PUBLIC_MATCHERS).permitAll().anyRequest().authenticated().and().formLogin()
            .loginPage(LOGIN_URL).defaultSuccessUrl(PAYLOAD_URL).failureUrl(ERROR_URL).permitAll().and().logout().permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
        auth.userDetailsService(userSecurityService);
    }
}
