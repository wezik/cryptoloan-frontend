package com.cryptoloanfront.service;

import com.cryptoloanfront.api.client.*;
import com.cryptoloanfront.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CryptoLoanService {

    private final ApiUsersClient apiUsersClient;
    private final ApiLoanTypesClient apiLoanTypesClient;
    private final ApiLoansClient apiLoansClient;
    private final ApiInstallmentsClient apiInstallmentsClient;
    private final ApiInfoClient apiInfoClient;

    public User getUser(int id) {
        return apiUsersClient.fetchUser(id).get();
    }

    public List<User> getUsers() {
        return apiUsersClient.fetchUsers();
    }

    public void saveUser(User user) {
        apiUsersClient.createUser(user);
    }

    public void deleteUser(int id) {
        apiUsersClient.deleteUser(id);
    }

    public void updateUser(User user) {
        apiUsersClient.updateUser(user);
    }

    public LoanType getLoanType(int id) {
        return apiLoanTypesClient.fetchLoanType(id).get();
    }

    public List<LoanType> getLoanTypes() {
        return apiLoanTypesClient.fetchLoanTypes();
    }

    public void saveLoanType(LoanType loanType) {
        apiLoanTypesClient.createLoanType(loanType);
    }

    public void deleteLoanType(int id) {
        apiLoanTypesClient.deleteLoanType(id);
    }

    public void updateLoanType(LoanType loanType) {
        apiLoanTypesClient.updateLoanType(loanType);
    }

    public Loan getLoan(int id) {
        return apiLoansClient.fetchLoan(id).get();
    }

    public List<Loan> getLoans() {
        return apiLoansClient.fetchLoans();
    }

    public void saveLoan(Loan loan) {
        apiLoansClient.createLoan(loan);
    }

    public void deleteLoan(int id) {
        apiLoansClient.deleteLoan(id);
    }

    public void updateLoan(Loan loan) {
        apiLoansClient.updateLoan(loan);
    }

    public Installment getInstallment(int id) {
        return apiInstallmentsClient.fetchInstallment(id).get();
    }

    public List<Installment> getInstallments() {
        return apiInstallmentsClient.fetchInstallments();
    }

    public void saveInstallment(Installment installment) {
        apiInstallmentsClient.createInstallment(installment);
    }

    public void deleteInstallment(int id) {
        apiInstallmentsClient.deleteInstallment(id);
    }

    public void updateInstallment(Installment installment) {
        apiInstallmentsClient.updateInstallment(installment);
    }

    public Integer getTimeSettingInDays() {
        return apiInfoClient.fetchTimeInDays();
    }

    public Set<String> getAllCurrencies() {
        return apiInfoClient.fetchCurrencies();
    }

    public BigDecimal exchangeCurrency(String given, String returned) {
        return apiInfoClient.getExchangeForCurrencies(given,returned);
    }

}
