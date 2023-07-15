package com.opdetiicos.securityController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurity{

	
	  
	  @Autowired
	  UserDetailsService service;

		@Bean
	    protected BCryptPasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

	    @Bean
	    protected DaoAuthenticationProvider authenticationProvider() {
	        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	        authProvider.setUserDetailsService(service);
	        authProvider.setPasswordEncoder(passwordEncoder());
	        authProvider.setHideUserNotFoundExceptions(false);
	        return authProvider;
	    }
	   
	    @Bean
	    protected SecurityFilterChain FilteChain(HttpSecurity http) throws Exception {
	    	
	    	 http
	    	.cors().and().csrf().disable()
	    	.authorizeRequests()
	    	.antMatchers("/resources/**").permitAll()
	    	.antMatchers("/opdetiicos/innerprocess/**").permitAll()
	    	.antMatchers("/home").authenticated()
	    	.antMatchers("/log").authenticated()
	    	.antMatchers("/opdtutorial").authenticated()
	    	.antMatchers("/tokentutorial").authenticated()
	    	.antMatchers("/faq").authenticated()
	    	.antMatchers("/whatapptutorial").authenticated()
	    	.anyRequest().permitAll()
	        .and()
	        .rememberMe()
	        .userDetailsService(this.service)
	        .rememberMeCookieName("remember-me-cookie")
	        .tokenValiditySeconds(900)
	        .useSecureCookie(true)
	        .and()
	        .formLogin().loginPage("/login").usernameParameter("gmail").passwordParameter("password")
	        .defaultSuccessUrl("/home")
	        .permitAll()
	    	.and()
	        .logout().logoutSuccessUrl("/login").permitAll()
	        .invalidateHttpSession(true)
	        .deleteCookies("remember-me-cookie")
	        .deleteCookies("JSESSIONID")
	         ;
	     return http.build();
	    }
	
}
