package com.tutorial.demo.registration;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class RegistrationController {

    final private RegistrationServiceImpl registrationService;

    @PostMapping
    public String register(@RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }

    @GetMapping("/confirm")
    public String confirmToken(@RequestParam String token){
        return registrationService.confirmToken(token);
    }

    @PostMapping("/login")
    @CrossOrigin(origins = {"http://localhost:4200/"})
    public String userLogin(@RequestBody LoginRequest loginRequest){
        // todo: implement jwt later
        System.out.println(loginRequest.toString());
        return "asdasdasd";
    }
}
