package com.github.filonovsv.smartix_test_tusk.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "abonents")
public class Abonent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String phone;
    private String password;
    private Long rubles;
    private Long copecks;
    private String name;
    private String surname;
    private String patronymic;
    private String gender;
    private String email;
    private Date birthday;
}
