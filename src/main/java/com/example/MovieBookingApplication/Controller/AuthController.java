package com.example.MovieBookingApplication.Controller;

import com.example.MovieBookingApplication.DTO.LoginRequestDTO;
import com.example.MovieBookingApplication.DTO.LoginResponseDTO;
import com.example.MovieBookingApplication.DTO.RegisterRequestDTO;
import com.example.MovieBookingApplication.Entity.User;
import com.example.MovieBookingApplication.Repository.UserRepository;
import com.example.MovieBookingApplication.Service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private UserRepository userRepository; // ✅ added

	@PostMapping("/registernormaluser")
	public ResponseEntity<User> registerNormalUser(@RequestBody RegisterRequestDTO registerRequestDTO) {
		return ResponseEntity.ok(authenticationService.registerNormalUser(registerRequestDTO));
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
		return ResponseEntity.ok(authenticationService.login(loginRequestDTO));
	}

	// ✅ new endpoint — returns current user from JWT token
	@GetMapping("/me")
	public ResponseEntity<Map<String, Object>> getCurrentUser(
			@AuthenticationPrincipal UserDetails userDetails) {
		User user = userRepository.findByUsername(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));
		return ResponseEntity.ok(Map.of(
				"id", user.getId(),
				"username", user.getUsername(),
				"roles", user.getRoles()
		));
	}
}