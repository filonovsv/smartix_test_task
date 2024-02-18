package com.github.filonovsv.smartix_test_tusk.config;

import com.github.filonovsv.smartix_test_tusk.models.Abonent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AbonentDetails implements UserDetails {
    private Abonent abonent;

    public AbonentDetails(Abonent abonent){
        this.abonent = abonent;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return abonent.getPassword();
    }

    @Override
    public String getUsername() {
        return abonent.getPhone();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
