package seoul.its.info.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;

import seoul.its.info.services.users.login.detail.UserDetailsServiceImpl;
import seoul.its.info.services.users.login.handler.LoginFailureHandler;
import seoul.its.info.services.users.login.handler.LoginSuccessHandler;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.core.GrantedAuthorityDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final XssFilter xssFilter;
        private final UserDetailsServiceImpl userDetailsServiceImpl;
        private final LoginSuccessHandler loginSuccessHandler;
        private final LoginFailureHandler loginFailureHandler;

        public SecurityConfig(XssFilter xssFilter,
                        UserDetailsServiceImpl userDetailsServiceImpl,
                        LoginSuccessHandler loginSuccessHandler,
                        LoginFailureHandler loginFailureHandler) {
                this.xssFilter = xssFilter;
                this.userDetailsServiceImpl = userDetailsServiceImpl;
                this.loginSuccessHandler = loginSuccessHandler;
                this.loginFailureHandler = loginFailureHandler;
        }

        @Bean
        public PasswordEncoder springPasswordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public RoleHierarchy roleHierarchy() {
                // Define role hierarchy: ROLE_200 includes ROLE_100, ROLE_100 includes ROLE_0
                String hierarchy = "ROLE_200 > ROLE_100 \n ROLE_100 > ROLE_0";
                return RoleHierarchyImpl.fromHierarchy(hierarchy);
        }

        @Bean
        public GrantedAuthorityDefaults grantedAuthorityDefaults() {
            return new GrantedAuthorityDefaults("");
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .addFilterBefore(xssFilter, CsrfFilter.class)
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/admin/**").hasRole("100")
                                                .requestMatchers("/favicon.ico").permitAll()
                                                .anyRequest().permitAll())
                                .formLogin(formLogin -> formLogin
                                                .loginPage("/user/login")
                                                .loginProcessingUrl("/user/login")
                                                .usernameParameter("login_id")
                                                .passwordParameter("password")
                                                .successHandler(loginSuccessHandler)
                                                .failureHandler(loginFailureHandler))
                                .logout(logout -> logout
                                                .logoutUrl("/users/logout")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .logoutSuccessUrl("/"))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                                                .maximumSessions(1)
                                                .expiredUrl("/user/login?expired=true"))
                                .userDetailsService(userDetailsServiceImpl);

                return http.build();
        }

        
}