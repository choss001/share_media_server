package com.media.share.controller;


import com.media.share.dto.JwtResponse;
import com.media.share.dto.SignInRequest;
import com.media.share.dto.SignUpRequest;
import com.media.share.filter.RequestLoggingFilter;
import com.media.share.model.ERole;
import com.media.share.model.Role;
import com.media.share.model.User;
import com.media.share.repository.RoleRepository;
import com.media.share.repository.UserRepository;
import com.media.share.service.UserDetailsImpl;
import com.media.share.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService;
    private JwtUtil jwtUtil;
    @Value("${app.environment}")
    private String environment;

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    public AuthController(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SignInRequest signInRequest){
        String password = passwordEncoder.encode(signInRequest.getPassword());
        System.out.println("password : "+password);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();


        boolean isProduction = "prod".equalsIgnoreCase(environment);
        ResponseCookie cookie = ResponseCookie.from("jwtToken", jwt)
                .httpOnly(true) // Prevents JavaScript access
                .secure(isProduction) // Requires HTTPS
                .sameSite(isProduction ? "None" : "Lax") // Allows cross-site requests (Important for Next.js)
                .path("/")
                .maxAge(Duration.ofDays(2))
                .build();



        JwtResponse res = new JwtResponse();
        res.setToken(jwt);
        res.setId(userDetails.getId());
        res.setUsername(userDetails.getUsername());
        res.setRoles(roles);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(res);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpRequest signUpRequest){
        if(userRepository.existsByUsername(signUpRequest.getUsername())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("username is already taken");
        }
        if(userRepository.existsByEmail(signUpRequest.getEmail())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email is already taken");
        }
        String hashedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        System.out.println("hasedPassword : "+hashedPassword);
        Set<Role> roles = new HashSet<>();
        Optional<Role> userRole = roleRepository.findByName(ERole.ROLE_USER);
        if (userRole.isEmpty()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("role not found");
        }
        roles.add(userRole.get());
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(hashedPassword);
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok("User registered success");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(){
        boolean isProduction = "prod".equalsIgnoreCase(environment);
        ResponseCookie cookie = ResponseCookie.from("jwtToken", null)
                .httpOnly(true) // Prevents JavaScript access
                .secure(isProduction) // Requires HTTPS
                .sameSite(isProduction ? "None" : "Lax") // Allows cross-site requests (Important for Next.js)
                .path("/")
                .maxAge(Duration.ofDays(0))
                .build();

        // Add the cookie to the response, so the browser deletes it

        // Respond with a success message
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(null);
    }
    @GetMapping("/validate-token")
    public ResponseEntity<String> validateToken(Authentication authentication, @RequestParam("mediaId") String mediaId){
        if (authentication == null || !authentication.isAuthenticated()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        logger.info("mediaId : {}", mediaId);
        return ResponseEntity.ok("Success");

    }


}
