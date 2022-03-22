package com.tutorial.demo.security.config;

import com.tutorial.demo.appuser.AppUserService;
import com.tutorial.demo.security.jwt.JwtAuthenticationEntryPoint;
import com.tutorial.demo.security.jwt.JwtRequestFilter;
import com.tutorial.demo.security.oauth2.OAuth2AppUser;
import com.tutorial.demo.security.oauth2.OAuth2AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
@Order(1)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AppUserService appUserService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;
    private final OAuth2AppUserService oAuth2AppUserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // disable authentication paths here
                .authorizeRequests()
                .antMatchers("/api/v*/auth/**").permitAll()
                .antMatchers("/login**").permitAll()

                // all authenticated paths must go through this
                .anyRequest()
                .authenticated()
                .and()

                // login form
//                .formLogin()
//                .and()
//                .oauth2Login()

                // add exception handling here
//                .and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .csrf().disable()

                .oauth2Login()
//                .loginPage("/login-me")
                .userInfoEndpoint()
                .userService(oAuth2AppUserService)
                .and()
                .successHandler((request, response, authentication) -> {
                    DefaultOidcUser oAuth2AppUser = (DefaultOidcUser) authentication.getPrincipal();
                    appUserService.processOAuthPostLogin(oAuth2AppUser);
                    // redirect to a thymeleaf page that shows all users
                    response.sendRedirect("/success");
                })
        ;

        // add jwt filter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * This class sets which UserDetailService and PasswordEncoder to use <br>
     * Here I'm overriding it with my AppUserService and a BCryptPasswordEncoder bean I added to the configuration <br>
     * The two Beans above are created using a method below called daoAuthenticationProvider <br>
     *
     * @see DaoAuthenticationProvider
     * @see AppUserService
     * @see BCryptPasswordEncoder
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(daoAuthenticationProvider());
//        you could also remove the daoAuthenticationProvider bean and just use the code below to set the beans instead
        auth.userDetailsService(appUserService).passwordEncoder(bCryptPasswordEncoder);
    }

    /**
     * This bean is used to authenticate user emails and passwords in the controller. <br>
     * Compilation error without declaring this bean. <br>
     *
     * @return returns the default authentication manager
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * specifies what password encoder to use <br>
     * and what service to use to fetch users from the database
     */
//    @Bean
//    public DaoAuthenticationProvider daoAuthenticationProvider() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setPasswordEncoder(bCryptPasswordEncoder);
//        provider.setUserDetailsService(appUserService);
//        return provider;
//    }
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/resources/**");
    }
}
