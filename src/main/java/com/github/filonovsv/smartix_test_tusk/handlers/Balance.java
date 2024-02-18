package com.github.filonovsv.smartix_test_tusk.handlers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Balance {
    private String phone;
    private Long rubles;
    private Long copecks;
}
