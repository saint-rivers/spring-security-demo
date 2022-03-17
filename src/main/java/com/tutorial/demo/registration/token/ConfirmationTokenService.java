package com.tutorial.demo.registration.token;

public interface ConfirmationTokenService {
     void saveConfirmationToken(ConfirmationToken token);

     ConfirmationToken get(String token);

     void setConfirmedAt(String token);
}
