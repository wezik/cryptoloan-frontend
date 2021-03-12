package com.cryptoloanfront.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User{
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String socialSecurityNumber;
}
