package com.tutorial.demo.appuser;

import com.tutorial.demo.registration.token.ConfirmationToken;
import com.tutorial.demo.registration.token.ConfirmationTokenServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    final private AppUserRepository appUserRepository;
    final private BCryptPasswordEncoder bCryptPasswordEncoder;
    final private ConfirmationTokenServiceImpl confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(s).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public String saveUserIntoDatabase(AppUser appUser) {
        boolean userExists = appUserRepository.findByEmail(appUser.getEmail())
                .isPresent();
        if (userExists) {
            // todo: check if it's the same user
            // todo: if email is not confirmed, send confirmation email
            throw new IllegalStateException("Email already exists");
        }

        String appUserPassword = appUser.getPassword();
        String encodedPassword = bCryptPasswordEncoder.encode(appUserPassword);
        appUser.setPassword(encodedPassword);
        appUser.setIsEnabled(false);
        appUser.setIsLocked(false);
        appUserRepository.save(appUser);

        // save token in database
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15L), appUser
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }

    private AppUser findUserByEmail(String email){
        return appUserRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void enableUser(String email) {
        AppUser appUser = this.findUserByEmail(email);
        appUser.setIsEnabled(true);
        appUserRepository.save(appUser);
    }
}
