package com.tutorial.demo.registration.token;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Override
    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    @Override
    public ConfirmationToken get(String token) {
        return confirmationTokenRepository.findByToken(token).orElseThrow(() -> new IllegalStateException("token not found"));
    }

    @Override
    public void setConfirmedAt(String token) {
        ConfirmationToken confirmationToken = this.get(token);
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenRepository.save(confirmationToken);
    }

}
