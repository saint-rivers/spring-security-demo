package com.tutorial.demo.registration;

import lombok.*;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class LoginRequest {
    private final String email;
    private final String password;
}
