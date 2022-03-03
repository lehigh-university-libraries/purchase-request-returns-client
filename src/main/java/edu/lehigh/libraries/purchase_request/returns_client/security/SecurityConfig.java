package edu.lehigh.libraries.purchase_request.returns_client.security;

import edu.lehigh.libraries.purchase_request.returns_client.config.PropertiesConfig;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

	private PropertiesConfig properties;

	SecurityConfig(PropertiesConfig properties) {
		this.properties = properties;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		final boolean disableSecurity = properties.isDisableSecurity();
		if (disableSecurity) {
			log.warn("Security is DISABLED");
			httpSecurity
					.httpBasic().disable();
		} else {
			httpSecurity
					.csrf().disable()
					.authorizeRequests().anyRequest().authenticated().and()
					.formLogin().permitAll()
					.and()
					.logout().invalidateHttpSession(true).clearAuthentication(true).permitAll();
		}
		return httpSecurity.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsService() {
			@Autowired
			private UserRepository userRepository;

			@Override
			public UserDetails loadUserByUsername(String username) {
				User user = Optional.ofNullable(userRepository.findByUsername(username))
						.orElseThrow(() -> new UsernameNotFoundException("Cannot find user: " + username));
				return new ReturnsUserDetails(user);
			}
		};
	}

	@Bean
	public static BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

}
