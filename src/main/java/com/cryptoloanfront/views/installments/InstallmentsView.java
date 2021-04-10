package com.cryptoloanfront.views.installments;

import com.cryptoloanfront.domain.Installment;
import com.cryptoloanfront.domain.Loan;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Route("installments")
@PageTitle("Crypto Loan")
@CssImport("./styles/table-view.css")
public class InstallmentsView extends Div {

    private Button addButton, updateButton, clearButton, deleteButton;
    private NumberField idField, deleteIdField, amountField, amountToPayField;
    private ComboBox<String> loansCBox;
    private ComboBox<Boolean> isPaidCBox;
    private DatePicker datePicker;
    private Grid<Installment> grid;
    private final DivFactory divFactory;
    private final FieldFactory fieldFactory;
    private final List<Installment> installments;
    private List<Loan> loans;
    private Map<String,Loan> loanMap;
    private final CryptoLoanService api;

    public InstallmentsView(@Autowired CryptoLoanService cryptoLoanService,
                     @Autowired DivFactory divFactory,
                     @Autowired FieldFactory fieldFactory) {
        this.divFactory = divFactory;
        this.fieldFactory = fieldFactory;
        this.api = cryptoLoanService;
        this.installments = api.getInstallments();
        Div addDiv = createAddDiv();
        Div deleteDiv = createDeleteDiv();
        Div gridDiv = createGridDiv();
        createInteractions();
        Div main = divFactory.createCompleteDiv("Installments",addDiv,deleteDiv,gridDiv);
        add(main);
    }

    private Div createAddDiv() {
        loans = api.getLoans();
        loanMap = createLoanMap();
        List<String> loanComboBoxChoices = loans.stream().map(this::convertLoanIntoString).collect(Collectors.toList());
        Div addDiv = divFactory.createDivWithId("addDiv");
        idField = fieldFactory.createIdNumberField();
        datePicker = fieldFactory.createDatePicker("Last Updated *");
        amountField = fieldFactory.createIdNumberField("Amount *", "1234.56");
        amountToPayField = fieldFactory.createIdNumberField("Amount in paid *", "1234.56");
        isPaidCBox = createIsPaidCBox();
        loansCBox = fieldFactory.createComboBox("Loan",loanComboBoxChoices,"Loan","inputField");
        List<Component> addMenuFieldsList = List.of(
                idField,
                datePicker,
                amountField,
                amountToPayField,
                isPaidCBox,
                loansCBox
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

    private Grid<Installment> createGrid() {
        Grid<Installment> grid = new Grid<>();
        grid.setItems(installments);
        grid.addColumn(Installment::getId).setHeader("ID");
        grid.addColumn(Installment::getLocalDate).setHeader("Last Updated");
        grid.addColumn(Installment::getAmountInBorrowed).setHeader("Amount");
        grid.addColumn(Installment::getAmountInPaid).setHeader("Amount In Paid");
        grid.addColumn(e -> Boolean.toString(e.isPaid())).setHeader("Is Paid");
        grid.addColumn(e -> e.getLoan().getId() +
                "/" +
                e.getLoan().getUser().getFirstName() +
                "." +
                e.getLoan().getUser().getLastName().charAt(0) +
                "/" +
                e.getLoan().getLoanType().getName())
                .setHeader("Loan");
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
                Installment installment = e.getFirstSelectedItem().get();
                Loan loanEntry = installment.getLoan();

                idField.setValue(installment.getId().doubleValue());
                datePicker.setValue(installment.getLocalDate());
                amountField.setValue(Double.parseDouble(installment.getAmountInBorrowed()));
                amountToPayField.setValue(Double.parseDouble(installment.getAmountInPaid()));
                isPaidCBox.setValue(installment.isPaid());
                loansCBox.setValue(convertLoanIntoString(loanEntry));
            }
        });
    }

    private void createClickListenerForAddButton() {
        addButton.addClickListener(e -> {
            Loan loanEntry = loanMap.get(loansCBox.getValue());
            BigDecimal amountEntry = calculateInstallmentAmountFor(loanEntry);
            BigDecimal finalAmount = exchangeAmount(amountEntry, loanEntry);

            if (!loanEntry.getCurrencyPaidIn().equalsIgnoreCase("BTC")) {
                finalAmount = finalAmount.setScale(2,RoundingMode.CEILING).stripTrailingZeros();
            } else {
                finalAmount = finalAmount.stripTrailingZeros();
            }

            Installment installment = new Installment(
                    null,
                    LocalDate.now(),
                    amountEntry.toPlainString(),
                    finalAmount.toPlainString(),
                    isPaidCBox.getValue()==null ? false : isPaidCBox.getValue(),
                    loanEntry
            );

            api.saveInstallment(installment);
            UI.getCurrent().getPage().reload();
        });
    }

    private BigDecimal calculateInstallmentAmountFor(Loan loan) {
        return BigDecimal.valueOf(Double.parseDouble(loan.getAmountToPay()))
                .divide(BigDecimal.valueOf(loan.getInstallmentsTotal()), 9, RoundingMode.CEILING)
                .stripTrailingZeros();
    }

    private BigDecimal exchangeAmount(BigDecimal amount, Loan loan) {
        return amount.multiply(api.exchangeCurrency(loan.getCurrencyBorrowed(),loan.getCurrencyPaidIn()));
    }

    private void createClickListenerForUpdateButton() {
        updateButton.addClickListener(e -> {
            Loan loanEntry = loanMap.get(loansCBox.getValue());
            Installment installment = new Installment(
                    idField.getValue().longValue(),
                    datePicker.getValue(),
                    BigDecimal.valueOf(amountField.getValue()).stripTrailingZeros().toPlainString(),
                    BigDecimal.valueOf(amountToPayField.getValue()).stripTrailingZeros().toPlainString(),
                    isPaidCBox.getValue(),
                    loanEntry
            );

            api.updateInstallment(installment);
            UI.getCurrent().getPage().reload();
        });
    }

    private void createClickListenerForClearButton() {
        clearButton.addClickListener(e -> {
            idField.clear();
            deleteIdField.clear();
            datePicker.clear();
            amountField.clear();
            amountToPayField.clear();
            isPaidCBox.clear();
            loansCBox.clear();
        });
    }

    private void createClickListenerForDeleteButton() {
        deleteButton.addClickListener(e -> {
            api.deleteInstallment(deleteIdField.getValue().intValue());
            UI.getCurrent().getPage().reload();
        });
    }

    private String convertLoanIntoString(Loan loan) {
        return loan.getId() +
                "/" +
                loan.getUser().getFirstName() +
                "." +
                loan.getUser().getLastName().charAt(0) +
                "/" +
                loan.getLoanType().getName();
    }

    private Map<String,Loan> createLoanMap() {
        Map<String, Loan> map = new HashMap<>();
        loans.forEach(e -> map.put(convertLoanIntoString(e),e));
        return map;
    }

    private ComboBox<Boolean> createIsPaidCBox() {
        ComboBox<Boolean> box = new ComboBox<>("Is Paid *");
        List<Boolean> isPaidComboBoxChoices = List.of(false,true);
        box.setItems(isPaidComboBoxChoices);
        box.setPlaceholder("False");
        box.setClassName("booleanBox");
        return box;
    }

}
