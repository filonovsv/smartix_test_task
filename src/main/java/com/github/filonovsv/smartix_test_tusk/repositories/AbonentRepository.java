package com.github.filonovsv.smartix_test_tusk.repositories;

import com.github.filonovsv.smartix_test_tusk.models.Abonent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AbonentRepository extends JpaRepository<Abonent, Long> {
    Optional<Abonent> findByPhone(String phone);
}
