package com.cryptoloanfront.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoanType {
    private Long id;
    private String name;
    private Integer timePeriod;
    private Double interest;
    private Double punishment;
    private String minAmount;
    private String maxAmount;
}
