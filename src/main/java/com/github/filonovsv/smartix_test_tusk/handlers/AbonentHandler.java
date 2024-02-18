package com.github.filonovsv.smartix_test_tusk.handlers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class AbonentHandler {
    private String phone;
    private String password;
    private String name;
    private String surname;
    private String patronymic;
    private String gender;
    private String email;
    private Date birthday;
    @JsonIgnore
    public AbonentHandler(String phone, String password){
        this.phone = phone;
        this.password = password;
        this.name = null;
        this.surname = null;
        this.patronymic = null;
        this.gender = null;
        this.email = null;
        this.birthday = null;
    }
}
