package com.tutorial.demo.appuser;

import com.tutorial.demo.registration.token.ConfirmationToken;
import com.tutorial.demo.registration.token.ConfirmationTokenServiceImpl;
import com.tutorial.demo.security.oauth2.OAuth2AppUser;
import com.tutorial.demo.security.oauth2.TmpOAuth2User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
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

    /**
     * This method saves a user into a database during his registration request
     */
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

    private AppUser findUserByEmail(String email) {
        return appUserRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void enableUser(String email) {
        AppUser appUser = this.findUserByEmail(email);
        appUser.setIsEnabled(true);
        appUserRepository.save(appUser);
    }

    /**
     * This method saves a user after their login with OAuth2
     *
     * @param oAuth2User Principal gotten from WebSecurityConfig
     * @see com.tutorial.demo.security.config.WebSecurityConfig
     */
    public void processOAuthPostLogin(DefaultOidcUser oAuth2User) {
        Optional<AppUser> appUser = appUserRepository.findByEmail(oAuth2User.getEmail());
        if (appUser.isEmpty()) {
            AppUser user = AppUser.builder()
                    .email(oAuth2User.getEmail())
                    .firstname(oAuth2User.getGivenName())
                    .lastname(oAuth2User.getFamilyName())
                    .isEnabled(true)
                    .isLocked(false)
                    .userRole(UserRole.USER)
                    // todo: properly get GOOGLE as the OAuth2ClientName
                    .provider(Provider.GOOGLE)
                    .build();
            appUserRepository.save(user);
        }
    }

    public void processOAuthPostLogin(OAuth2AppUser oAuth2User) {
        Optional<AppUser> appUser = appUserRepository.findByEmail(oAuth2User.getEmail());
        if (appUser.isEmpty()) {
            AppUser user = AppUser.builder()
                    .email(oAuth2User.getEmail())
                    .firstname(oAuth2User.getAttribute("first_name"))
                    .lastname(oAuth2User.getAttribute("last_name"))
                    .isEnabled(true)
                    .isLocked(false)
                    .userRole(UserRole.USER)
                    .provider(getProviderName(oAuth2User.getOAuth2ClientName()))
                    .build();
            System.out.println("priniting facebook oauth user " + user.toString());
            appUserRepository.save(user);
        }
    }

    private Provider getProviderName(String clientName) {
        return clientName.toLowerCase(Locale.ROOT).equals(Provider.GOOGLE.toString().toLowerCase(Locale.ROOT))
                ? Provider.GOOGLE
                : clientName.toLowerCase(Locale.ROOT).equals(Provider.FACEBOOK.toString().toLowerCase(Locale.ROOT))
                    ? Provider.FACEBOOK
                    : null;
    }
}
