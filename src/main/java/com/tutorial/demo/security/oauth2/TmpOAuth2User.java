package com.tutorial.demo.security.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Collection;

public class TmpOAuth2User extends DefaultOidcUser {
    private String oAuth2ClientName;

    public TmpOAuth2User(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken) {
        super(authorities, idToken);
    }

    public String getOAuth2ClientName() {
        return this.oAuth2ClientName;
    }
}
