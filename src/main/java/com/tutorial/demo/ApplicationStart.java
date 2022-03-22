package com.tutorial.demo;

import com.tutorial.demo.registration.RegistrationController;
import com.tutorial.demo.registration.RegistrationRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class ApplicationStart implements CommandLineRunner {

    private final RegistrationController registrationController;

    @Override
    public void run(String... args) {
        initializeDefaultUser();
    }

    private void initializeDefaultUser() {
        String token = registrationController.register(new RegistrationRequest("dayan", "eam", "eam.dayan@gmail.com", "asd"));
        log.info("Token: {}", token);
        log.info("Awaiting confirmation");
        String message = registrationController.confirmToken(token);
        log.info("Message: {}", message);
    }

}
