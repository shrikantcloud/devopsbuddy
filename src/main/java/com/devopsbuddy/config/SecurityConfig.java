package com.devopsbuddy.config;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.devopsbuddy.backend.service.UserSecurityService;
import com.devopsbuddy.controllers.ForgotMyPasswordController;
import com.devopsbuddy.controllers.SignupController;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String DEV_PROFILE = "dev";

    /* The encryption Salt */
    private static final String SALT        = "rthr2/;.;424rhr;.dgfbrwml";

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12, new SecureRandom(SALT.getBytes()));
    }

    @Autowired
    private UserSecurityService  userSecurityService;

    @Autowired
    private Environment          env;

    public static final String[] PUBLIC_MATCHERS = { "/webjars/**", "/css/**", "/js/**", "/images/**", "/", "/about/**", "/contact/**",
                                                     "/error/**/*", "/console/**", ForgotMyPasswordController.FORGOT_PASSWORD_URL_MAPPING,
                                                     ForgotMyPasswordController.CHANGE_PASSWORD_PATH,
                                                     SignupController.SIGNUP_URL_MAPPING
    };
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
        auth.userDetailsService(userSecurityService).passwordEncoder(passwordEncoder());

    }
}
