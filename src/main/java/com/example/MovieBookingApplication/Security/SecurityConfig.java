package com.example.MovieBookingApplication.Security;

import com.example.MovieBookingApplication.JWT.JWTAuthenticationFilter;
import com.example.MovieBookingApplication.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	private JWTAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth

						// ── Public: Auth (only login & register are truly public) ──
						.requestMatchers("/api/auth/login").permitAll()
						.requestMatchers("/api/auth/registernormaluser").permitAll()

						// ── Public CORS preflight ─────────────────────────────────
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

						// ── Authenticated: /api/auth/me requires a valid JWT ───────
						.requestMatchers("/api/auth/me").authenticated()

						// ── Admin only ────────────────────────────────────────────
						.requestMatchers("/api/admin/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST,   "/api/movies/addmovie").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT,    "/api/movies/updatemovie/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST,   "/api/theater/addtheater").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT,    "/api/theater/updatetheater/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/theater/deletetheater/**").hasRole("ADMIN")

						// ── Authenticated users (any role) ────────────────────────
						.requestMatchers(HttpMethod.GET,    "/api/movies/**").authenticated()
						.requestMatchers(HttpMethod.GET,    "/api/theater/**").authenticated()
						.requestMatchers(HttpMethod.GET,    "/api/show/**").authenticated()
						.requestMatchers(HttpMethod.POST,   "/api/show/createshow").authenticated()
						.requestMatchers(HttpMethod.PUT,    "/api/show/updateshow/**").authenticated()
						.requestMatchers(HttpMethod.DELETE, "/api/show/deleteshow/**").authenticated()
						.requestMatchers(HttpMethod.POST,   "/api/booking/createbooking").authenticated()
						.requestMatchers(HttpMethod.GET,    "/api/booking/**").authenticated()
						.requestMatchers(HttpMethod.PUT,    "/api/booking/**").authenticated()

						// ── Everything else requires authentication ────────────────
						.anyRequest().authenticated()
				)
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService());
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomUserDetailsService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList(
				"http://localhost:5173",
				"http://localhost:3000",
				"https://movie-booking-frontend.onrender.com"
		));
		configuration.setAllowedMethods(Arrays.asList(
				"GET", "POST", "PUT", "DELETE", "OPTIONS"
		));
		configuration.setAllowedHeaders(Arrays.asList(
				"Authorization",
				"Content-Type",
				"Accept"
		));
		configuration.setExposedHeaders(Arrays.asList("Authorization"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
