package com.example.MovieBookingApplication.JWT;

import com.example.MovieBookingApplication.Repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JWTService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization");

		// No token → skip filter, let Spring Security handle it
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			final String jwtToken = authHeader.substring(7);
			final String username = jwtService.extractUsername(jwtToken);

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

				var userDetails = userRepository.findByUsername(username)
						.orElseThrow(() -> new RuntimeException("User not found: " + username));

				if (jwtService.isTokenValid(jwtToken, userDetails)) {

					// Normalize roles → always ensure ROLE_ prefix
					// because Spring's hasRole("ADMIN") checks for "ROLE_ADMIN"
					List<SimpleGrantedAuthority> authorities = userDetails.getRoles().stream()
							.map(role -> new SimpleGrantedAuthority(
									role.startsWith("ROLE_") ? role : "ROLE_" + role
							))
							.collect(Collectors.toList());

					UsernamePasswordAuthenticationToken authToken =
							new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					SecurityContextHolder.getContext().setAuthentication(authToken);

					System.out.println("✅ Authenticated: " + username + " | Roles: " + authorities);

				} else {
					System.out.println("❌ Token invalid for user: " + username);
				}
			}

		} catch (Exception e) {
			// Log the error but don't block the chain
			// Spring Security will return 401/403 appropriately
			System.out.println("❌ JWT Filter error: " + e.getMessage());
		}

		filterChain.doFilter(request, response);
	}
}