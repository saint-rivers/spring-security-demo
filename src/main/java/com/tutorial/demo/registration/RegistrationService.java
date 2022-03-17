package com.tutorial.demo.registration;

public interface RegistrationService {

    String register(RegistrationRequest request);

    String confirmToken(String token);
}
