package com.cryptoloanfront.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Loan {
    private Long id;
    private User user;
    private LoanType loanType;
    private LocalDate initialDate;
    private LocalDate finalDate;
    private String amountBorrowed;
    private String amountToPay;
    private String currencyBorrowed;
    private String currencyPaidIn;
    private Integer installmentsPaid;
    private Integer installmentsCreated;
    private Integer installmentsTotal;
}
