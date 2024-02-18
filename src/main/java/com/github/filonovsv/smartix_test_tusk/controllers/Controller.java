package com.github.filonovsv.smartix_test_tusk.controllers;

import com.github.filonovsv.smartix_test_tusk.handlers.AbonentHandler;
import com.github.filonovsv.smartix_test_tusk.handlers.Balance;
import com.github.filonovsv.smartix_test_tusk.handlers.PaymentHandler;
import com.github.filonovsv.smartix_test_tusk.models.Abonent;
import com.github.filonovsv.smartix_test_tusk.models.Payment;
import com.github.filonovsv.smartix_test_tusk.repositories.AbonentRepository;
import com.github.filonovsv.smartix_test_tusk.repositories.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class Controller {

    private AbonentRepository abonentRepository;
    private PaymentRepository paymentRepository;
    private PasswordEncoder passwordEncoder;

    @GetMapping("me")
    public ResponseEntity<Abonent> getMe(Principal principal) {
        Optional<Abonent> optionalAbonent = abonentRepository.findByPhone(principal.getName());
        return ResponseEntity.ok(optionalAbonent.get());
    }

    @PostMapping("create")
    public ResponseEntity<Void> addUser(@RequestBody AbonentHandler abonentHandler, UriComponentsBuilder ucb) {
        final long START_RUBLES = 1000L;
        final long START_COPECKS = 0L;
        Optional<Abonent> optionalAbonent = abonentRepository.findByPhone(abonentHandler.getPhone());
        if (optionalAbonent.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        if (abonentHandler.getGender() != null) {
            try {
                GENDER.valueOf(abonentHandler.getGender());
            } catch (IllegalArgumentException e) {
                abonentHandler.setGender(null);
            }
        }
        String phoneRegex = "\\d+";
        if (!abonentHandler.getPhone().matches(phoneRegex)) {
            return ResponseEntity.badRequest().build();
        }
        if (abonentHandler.getPassword() == null) {
            return ResponseEntity.badRequest().build();
        }
        Abonent abonent = new Abonent(
                null,
                abonentHandler.getPhone(),
                abonentHandler.getPassword(),
                START_RUBLES,
                START_COPECKS,
                abonentHandler.getName(),
                abonentHandler.getSurname(),
                abonentHandler.getPatronymic(),
                abonentHandler.getGender(),
                abonentHandler.getEmail(),
                abonentHandler.getBirthday());
        abonent.setPassword(passwordEncoder.encode(abonent.getPassword()));
        abonentRepository.save(abonent);
        return ResponseEntity.created(ucb.path("me").build().toUri()).build();
    }

    @PostMapping("pay")
    public ResponseEntity<Void> payment(@RequestBody PaymentHandler paymentHandler, Principal principal) {
        if (paymentHandler.getReceiver() == null || paymentHandler.getRubles() == null || paymentHandler.getCopecks() == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Abonent> optionalPayer = abonentRepository.findByPhone(principal.getName());
        Abonent payer = optionalPayer.get();
        if (payer.getRubles() * 100 + payer.getCopecks() >= paymentHandler.getRubles() * 100 + paymentHandler.getCopecks()) {
            Optional<Abonent> optionalReceiver = abonentRepository.findByPhone(paymentHandler.getReceiver());
            if (optionalReceiver.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            Abonent receiver = optionalReceiver.get();
            receiver.setCopecks((paymentHandler.getCopecks() + receiver.getCopecks()) % 100);
            receiver.setRubles(paymentHandler.getRubles() + receiver.getRubles() + ((paymentHandler.getCopecks() + receiver.getCopecks()) / 100));
            abonentRepository.save(receiver);
            long rubles = ((payer.getRubles() * 100 + payer.getCopecks()) -
                    (paymentHandler.getRubles() * 100 + paymentHandler.getCopecks())) / 100;
            long copecks = ((payer.getRubles() * 100 + payer.getCopecks()) -
                    (paymentHandler.getRubles() * 100 + paymentHandler.getCopecks())) % 100;
            payer.setRubles(rubles);
            payer.setCopecks(copecks);
            abonentRepository.save(payer);
            Payment payment = new Payment(
                    null,
                    principal.getName(),
                    receiver.getPhone(),
                    paymentHandler.getRubles(),
                    paymentHandler.getCopecks(),
                    Calendar.getInstance().getTime());
            paymentRepository.save(payment);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("update")
    public ResponseEntity<Void> updateUser(@RequestBody AbonentHandler abonentHandler, Principal principal) {
        Optional<Abonent> optionalAbonent = abonentRepository.findByPhone(principal.getName());
        if (!optionalAbonent.get().getPhone().equals(principal.getName()) && abonentHandler.getPhone() != null) {
            return ResponseEntity.badRequest().build();
        }
        Abonent abonent = optionalAbonent.get();
        abonent.setName(abonentHandler.getName());
        abonent.setSurname(abonentHandler.getSurname());
        abonent.setPatronymic(abonentHandler.getPatronymic());
        if (abonentHandler.getGender() != null) {
            try {
                GENDER.valueOf(abonentHandler.getGender());
            } catch (IllegalArgumentException e) {
                abonentHandler.setGender(null);
            }
        }
        abonent.setGender(abonentHandler.getGender());
        abonent.setEmail(abonentHandler.getEmail());
        abonent.setBirthday(abonentHandler.getBirthday());
        abonentRepository.save(abonent);
        return ResponseEntity.ok().build();
    }

    @GetMapping("balance")
    public ResponseEntity<Balance> getBalance(Principal principal) {
        Optional<Abonent> optionalAbonent = abonentRepository.findByPhone(principal.getName());
        Abonent abonent = optionalAbonent.get();
        return ResponseEntity.ok(new Balance(abonent.getPhone(), abonent.getRubles(), abonent.getCopecks()));
    }

    @GetMapping("history")
    public ResponseEntity<List<Payment>> getHistory(Pageable pageable, Principal principal) {
        Page<Payment> page = paymentRepository.findByPayer(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

    private enum GENDER {
        MALE("male"),
        FEMALE("female");
        private String gender;

        GENDER(String gender) {
            this.gender = gender;
        }
    }
}
