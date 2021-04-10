package com.cryptoloanfront.views.loans;

import com.cryptoloanfront.domain.Loan;
import com.cryptoloanfront.domain.LoanType;
import com.cryptoloanfront.domain.User;
import com.cryptoloanfront.factory.DivFactory;
import com.cryptoloanfront.factory.FieldFactory;
import com.cryptoloanfront.service.CryptoLoanService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Route("loans")
@PageTitle("Crypto Loan")
@CssImport("./styles/table-view.css")
public class LoansView extends Div {

    private Button addButton, updateButton, clearButton, deleteButton;
    private NumberField idField, deleteIdField, amountField, amountToPayField, installCreatedField, installPaidField, installTotalField;
    private ComboBox<String> userCBox, loanTypeCBox, currencyBorrowedCBox, currencyPaidInCBox;
    private DatePicker initialDatePicker, endDatePicker;
    private Grid<Loan> grid;
    private final DivFactory divFactory;
    private final FieldFactory fieldFactory;
    private final List<Loan> loans;
    private List<User> users;
    private List<LoanType> loanTypes;
    private Map<String,User> userMap;
    private Map<String,LoanType> loanTypeMap;
    private final CryptoLoanService api;

    public LoansView(@Autowired CryptoLoanService cryptoLoanService,
                         @Autowired DivFactory divFactory,
                         @Autowired FieldFactory fieldFactory) {
        this.divFactory = divFactory;
        this.fieldFactory = fieldFactory;
        this.api = cryptoLoanService;
        this.loans = api.getLoans();
        Div addDiv = createAddDiv();
        Div deleteDiv = createDeleteDiv();
        Div gridDiv = createGridDiv();
        createInteractions();
        Div main = divFactory.createCompleteDiv("Loans",addDiv,deleteDiv,gridDiv);
        add(main);
    }

    private Div createAddDiv() {
        users = api.getUsers();
        loanTypes = api.getLoanTypes();
        userMap = createUserMap();
        loanTypeMap = createLoanTypeMap();
        List<String> userComboBoxChoices = users.stream().map(this::convertUserIntoString).collect(Collectors.toList());
        List<String> loanTypeComboBoxChoices = loanTypes.stream().map(this::convertLoanTypeIntoString).collect(Collectors.toList());
        List<String> currencies = new ArrayList<>(api.getAllCurrencies());
        Collections.sort(currencies);

        Div addDiv = divFactory.createDivWithId("addDiv");
        idField = fieldFactory.createIdNumberField();
        userCBox = fieldFactory.createComboBox(
                "User",
                userComboBoxChoices,
                "User",
                "shortenedComboBox"
        );
        loanTypeCBox = fieldFactory.createComboBox(
                "Loan Type"
                ,loanTypeComboBoxChoices,
                "Loan Type",
                "shortenedComboBox"
        );
        initialDatePicker = fieldFactory.createDatePicker("Initial Date");
        endDatePicker = fieldFactory.createDatePicker("End Date *");
        amountField = fieldFactory.createSmallNumberField("Amount","1234.56");
        amountToPayField = fieldFactory.createSmallNumberField("Amount To Pay *","1234.56");
        currencyBorrowedCBox = fieldFactory.createComboBox(
                "Curr. Borrowed",
                currencies,
                "EUR",
                "currencyBox"
        );
        currencyPaidInCBox = fieldFactory.createComboBox(
                "Curr. Paid In",
                currencies,
                "EUR",
                "currencyBox"
        );
        installCreatedField = fieldFactory.createIdNumberField("Inst. Created *","0");
        installPaidField = fieldFactory.createIdNumberField("Inst. Paid *","0");
        installTotalField = fieldFactory.createIdNumberField("Inst. Total *","0");
        List<Component> addMenuFieldsList = List.of(
                idField,
                userCBox,
                loanTypeCBox,
                initialDatePicker,
                endDatePicker,
                amountField,
                amountToPayField,
                currencyBorrowedCBox,
                currencyPaidInCBox,
                installCreatedField,
                installPaidField,
                installTotalField
        );
        addButton = fieldFactory.createAddButton();
        updateButton = fieldFactory.createUpdateButton();
        clearButton = fieldFactory.createClearButton();
        addMenuFieldsList.forEach(addDiv::add);
        addDiv.add(addButton,updateButton,clearButton);
        return addDiv;
    }

    private Div createDeleteDiv() {
        Div deleteDiv = divFactory.createDivWithId("deleteDiv");
        deleteIdField = fieldFactory.createDeleteIdNumberField();
        deleteButton = fieldFactory.createDeleteButton();
        deleteDiv.add(deleteIdField,deleteButton);
        return deleteDiv;
    }

    private Div createGridDiv() {
        Div div = divFactory.createDivWithId("grid-div");
        grid = createGrid();
        div.add(grid);
        return div;
    }

    private Grid<Loan> createGrid() {
        Grid<Loan> grid = new Grid<>();
        grid.setItems(loans);
        grid.addColumn(Loan::getId).setHeader("ID");
        grid.addColumn(e ->
                convertUserIntoString(e.getUser()))
                .setHeader("User");
        grid.addColumn(e ->
                convertLoanTypeIntoString(e.getLoanType()))
                .setHeader("Loan Type");
        grid.addColumn(Loan::getInitialDate).setHeader("Initial Date");
        grid.addColumn(Loan::getFinalDate).setHeader("End Date");
        grid.addColumn(Loan::getAmountBorrowed).setHeader("Amount");
        grid.addColumn(Loan::getAmountToPay).setHeader("Amount To Pay");
        grid.addColumn(Loan::getCurrencyBorrowed).setHeader("Curr. Borrowed");
        grid.addColumn(Loan::getCurrencyPaidIn).setHeader("Curr. Paid in");
        grid.addColumn(Loan::getInstallmentsCreated).setHeader("Install. Created");
        grid.addColumn(Loan::getInstallmentsPaid).setHeader("Install. Paid");
        grid.addColumn(Loan::getInstallmentsTotal).setHeader("Install. Total");
        return grid;
    }

    private void createInteractions() {
        createGridSelectionListener();
        createClickListenerForAddButton();
        createClickListenerForUpdateButton();
        createClickListenerForClearButton();
        createClickListenerForDeleteButton();
    }

    private void createGridSelectionListener() {
        grid.addSelectionListener(e -> {
            if (e.getFirstSelectedItem().isPresent()) {
                Loan loan = e.getFirstSelectedItem().get();
                idField.setValue(loan.getId().doubleValue());
                deleteIdField.setValue(loan.getId().doubleValue());
                userCBox.setValue(convertUserIntoString(loan.getUser()));
                loanTypeCBox.setValue(convertLoanTypeIntoString(loan.getLoanType()));
                initialDatePicker.setValue(loan.getInitialDate());
                endDatePicker.setValue(loan.getFinalDate());
                amountField.setValue(Double.parseDouble(loan.getAmountBorrowed()));
                amountToPayField.setValue(Double.parseDouble(loan.getAmountToPay()));
                currencyBorrowedCBox.setValue(loan.getCurrencyBorrowed());
                currencyPaidInCBox.setValue(loan.getCurrencyPaidIn());
                installCreatedField.setValue(loan.getInstallmentsCreated().doubleValue());
                installPaidField.setValue(loan.getInstallmentsPaid().doubleValue());
                installTotalField.setValue(loan.getInstallmentsTotal().doubleValue());
            }
        });
    }

    private void createClickListenerForAddButton() {
        addButton.addClickListener(e -> {
            LoanType loanTypeEntry = loanTypeMap.get(loanTypeCBox.getValue());
            Loan loan = new Loan(null,
                    userMap.get(userCBox.getValue()),
                    loanTypeEntry,
                    initialDatePicker.getValue(),
                    initialDatePicker.getValue()
                            .plusDays(api.getTimeSettingInDays()*(loanTypeEntry.getTimePeriod()+1)),
                    BigDecimal.valueOf(amountField.getValue())
                            .stripTrailingZeros()
                            .toPlainString(),
                    BigDecimal.valueOf(amountField.getValue()+(amountField.getValue()*(loanTypeEntry.getInterest()*0.01)))
                            .stripTrailingZeros()
                            .toPlainString(),
                    currencyBorrowedCBox.getValue(),
                    currencyPaidInCBox.getValue(),
                    installPaidField.getValue()==null ?
                            0 : installPaidField.getValue().intValue(),
                    installCreatedField.getValue()==null ?
                            0 : installCreatedField.getValue().intValue(),
                    installTotalField.getValue()==null ?
                            loanTypeEntry.getTimePeriod() : installTotalField.getValue().intValue()
            );
            api.saveLoan(loan);
            UI.getCurrent().getPage().reload();
        });
    }

    private void createClickListenerForUpdateButton() {
        updateButton.addClickListener(e -> {
            LoanType loanTypeEntry = loanTypeMap.get(loanTypeCBox.getValue());
            Loan loan = new Loan(idField.getValue().longValue(),
                    userMap.get(userCBox.getValue()),
                    loanTypeEntry,
                    initialDatePicker.getValue(),
                    endDatePicker.getValue(),
                    BigDecimal.valueOf(amountField.getValue())
                            .stripTrailingZeros()
                            .toPlainString(),
                    BigDecimal.valueOf(amountToPayField.getValue())
                            .stripTrailingZeros()
                            .toPlainString(),
                    currencyBorrowedCBox.getValue(),
                    currencyPaidInCBox.getValue(),
                    installPaidField.getValue().intValue(),
                    installCreatedField.getValue().intValue(),
                    installTotalField.getValue().intValue()
            );
            api.updateLoan(loan);
            UI.getCurrent().getPage().reload();
        });
    }

    private void createClickListenerForClearButton() {
        clearButton.addClickListener(e -> {
            idField.clear();
            deleteIdField.clear();
            userCBox.clear();
            loanTypeCBox.clear();
            initialDatePicker.clear();
            endDatePicker.clear();
            amountField.clear();
            amountToPayField.clear();
            currencyBorrowedCBox.clear();
            currencyPaidInCBox.clear();
            installCreatedField.clear();
            installPaidField.clear();
            installTotalField.clear();
        });
    }

    private void createClickListenerForDeleteButton() {
        deleteButton.addClickListener(e -> {
            api.deleteLoan(deleteIdField.getValue().intValue());
            UI.getCurrent().getPage().reload();
        });
    }

    private String convertUserIntoString(User user) {
        return user.getId()+"/"+user.getFirstName()+"."+user.getLastName().charAt(0);
    }

    private String convertLoanTypeIntoString(LoanType loanType) {
        return loanType.getId()+"/"+loanType.getName();
    }

    private Map<String,User> createUserMap() {
        Map<String, User> map = new HashMap<>();
        users.forEach(e -> map.put(convertUserIntoString(e),e));
        return map;
    }

    private Map<String,LoanType> createLoanTypeMap() {
        Map<String, LoanType> map = new HashMap<>();
        loanTypes.forEach(e -> map.put(convertLoanTypeIntoString(e),e));
        return map;
    }

}
