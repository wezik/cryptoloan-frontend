package com.cryptoloanfront.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Installment {
    private Long id;
    private LocalDate localDate;
    private String amountInBorrowed;
    private String amountInPaid;
    private boolean isPaid;
    private Loan loan;
}
