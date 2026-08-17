package com.arya.attendance.controller;

import com.arya.attendance.model.AuthProvider;
import com.arya.attendance.model.User;
import com.arya.attendance.repository.UserRepository;
import com.arya.attendance.security.AppUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest req, HttpServletRequest request) {
        if (userRepository.findByRollNumber(req.rollNumber).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "Roll number already registered"));
        }
        User user = new User(req.rollNumber, passwordEncoder.encode(req.password), null, AuthProvider.LOCAL);
        userRepository.save(user);
        loginProgrammatically(req.rollNumber, req.password, request);
        return ResponseEntity.ok(Map.of("rollNumber", user.getRollNumber()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req, HttpServletRequest request) {
        try {
            loginProgrammatically(req.rollNumber, req.password, request);
            return ResponseEntity.ok(Map.of("rollNumber", req.rollNumber));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid roll number or password"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@org.springframework.security.core.annotation.AuthenticationPrincipal AppUserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(Map.of("rollNumber", principal.getUser().getRollNumber()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    private void loginProgrammatically(String rollNumber, String password, HttpServletRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(rollNumber, password));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, null);
    }

    public static class AuthRequest {
        public String rollNumber;
        public String password;
    }
}