package com.tutorial.demo.registration;

import com.tutorial.demo.appuser.AppUserService;
import com.tutorial.demo.security.jwt.JwtResponse;
import com.tutorial.demo.security.jwt.JwtTokenUtil;
import com.tutorial.demo.security.jwt.JwtRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@CrossOrigin
@Slf4j
public class RegistrationController {

    final private RegistrationServiceImpl registrationService;

    final private AuthenticationManager authenticationManager;
    final private JwtTokenUtil jwtTokenUtil;
    final private AppUserService appUserService;

    @PostMapping
    public String register(@RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }

    @GetMapping("/confirm")
    public String confirmToken(@RequestParam String token){
        return registrationService.confirmToken(token);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> userLogin(@RequestBody JwtRequest jwtRequest) throws Exception {
        log.info("user login: {}", jwtRequest.getEmail());
        authenticate(jwtRequest.getEmail(), jwtRequest.getPassword());
        UserDetails userDetails = appUserService.loadUserByUsername(jwtRequest.getEmail());
        String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            throw new Exception("User disabled", e);
        } catch (BadCredentialsException bce){
            throw new Exception("Invalid Credentials", bce);
        }
    }
}
