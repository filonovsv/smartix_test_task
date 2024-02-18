package com.github.filonovsv.smartix_test_tusk.services;

import com.github.filonovsv.smartix_test_tusk.config.AbonentDetails;
import com.github.filonovsv.smartix_test_tusk.models.Abonent;
import com.github.filonovsv.smartix_test_tusk.repositories.AbonentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AbonentDetailsService implements UserDetailsService {
    @Autowired
    private AbonentRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Abonent> user = repository.findByPhone(username);
        if(user.isPresent()){
            return new AbonentDetails(user.get());
        }
        else {
            throw new UsernameNotFoundException(username);
        }
    }
}
