package com.tutorial.demo.security.jwt;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class JwtRequest implements Serializable {
    private static final long serialVersionUID = 5926468583005150707L;
    private final String email;
    private final String password;
}
