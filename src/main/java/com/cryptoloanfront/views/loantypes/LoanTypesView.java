package com.cryptoloanfront.views.loantypes;

import com.cryptoloanfront.domain.LoanType;
import com.cryptoloanfront.factory.DivFactory;
import com.cryptoloanfront.factory.FieldFactory;
import com.cryptoloanfront.service.CryptoLoanService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

@Route("loantypes")
@PageTitle("Crypto Loan")
@CssImport("./styles/table-view.css")
public class LoanTypesView extends Div {

    private Button addButton, updateButton, clearButton, deleteButton;
    private NumberField idField, deleteIdField, monthsField, interestField, punishmentField, minAmountField, maxAmountField;
    private TextField nameField;
    private Grid<LoanType> grid;
    private final DivFactory divFactory;
    private final FieldFactory fieldFactory;
    private final List<LoanType> loanTypes;
    private final CryptoLoanService api;

    public LoanTypesView(@Autowired CryptoLoanService cryptoLoanService,
                     @Autowired DivFactory divFactory,
                     @Autowired FieldFactory fieldFactory) {
        this.divFactory = divFactory;
        this.fieldFactory = fieldFactory;
        this.api = cryptoLoanService;
        this.loanTypes = api.getLoanTypes();
        Div addDiv = createAddDiv();
        Div deleteDiv = createDeleteDiv();
        Div gridDiv = createGridDiv();
        createInteractions();
        Div main = divFactory.createCompleteDiv("Loan Types",addDiv,deleteDiv,gridDiv);
        add(main);
    }

    private Div createAddDiv() {
        Div addDiv = divFactory.createDivWithId("addDiv");
        idField = fieldFactory.createIdNumberField();
        nameField = fieldFactory.createTextField("Name");
        monthsField = fieldFactory.createIdNumberField("Months","12");
        interestField = fieldFactory.createIdNumberField("Interest (%)","12.3");
        punishmentField = fieldFactory.createIdNumberField("Punishment (%)","12.3");
        minAmountField = fieldFactory.createNumberField("Min. Amount","1234.56");
        maxAmountField = fieldFactory.createNumberField("Max. Amount", "1234.56");
        List<Component> addMenuFieldsList = List.of(
                idField,
                nameField,
                monthsField,
                interestField,
                punishmentField,
                minAmountField,
                maxAmountField
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

    private Grid<LoanType> createGrid() {
        Grid<LoanType> grid = new Grid<>();
        grid.setItems(loanTypes);
        grid.addColumn(LoanType::getId).setHeader("ID");
        grid.addColumn(LoanType::getName).setHeader("Name");
        grid.addColumn(LoanType::getTimePeriod).setHeader("Time Period");
        grid.addColumn(LoanType::getInterest).setHeader("Interest (%)");
        grid.addColumn(LoanType::getPunishment).setHeader("Punishment (%)");
        grid.addColumn(LoanType::getMinAmount).setHeader("Min. Amount");
        grid.addColumn(LoanType::getMaxAmount).setHeader("Max. Amount");
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
                LoanType loanType = e.getFirstSelectedItem().get();
                idField.setValue(loanType.getId().doubleValue());
                deleteIdField.setValue(loanType.getId().doubleValue());
                nameField.setValue(loanType.getName());
                monthsField.setValue(loanType.getTimePeriod().doubleValue());
                interestField.setValue(loanType.getInterest());
                punishmentField.setValue(loanType.getPunishment());
                minAmountField.setValue(Double.parseDouble(loanType.getMinAmount()));
                maxAmountField.setValue(Double.parseDouble(loanType.getMaxAmount()));
            }
        });
    }

    private void createClickListenerForAddButton() {
        addButton.addClickListener(e -> {
            LoanType loanType = new LoanType(null,
                    nameField.getValue(),
                    monthsField.getValue().intValue(),
                    interestField.getValue(),
                    punishmentField.getValue(),
                    BigDecimal.valueOf(minAmountField.getValue()).toPlainString(),
                    BigDecimal.valueOf(maxAmountField.getValue()).toPlainString()
            );
            api.saveLoanType(loanType);
            UI.getCurrent().getPage().reload();
        });
    }

    private void createClickListenerForUpdateButton() {
        updateButton.addClickListener(e -> {
            LoanType loanType = new LoanType(idField.getValue().longValue(),
                    nameField.getValue(),
                    monthsField.getValue().intValue(),
                    interestField.getValue(),
                    punishmentField.getValue(),
                    BigDecimal.valueOf(minAmountField.getValue()).toPlainString(),
                    BigDecimal.valueOf(maxAmountField.getValue()).toPlainString()
            );
            api.updateLoanType(loanType);
            UI.getCurrent().getPage().reload();
        });
    }

    private void createClickListenerForClearButton() {
        clearButton.addClickListener(e -> {
            idField.clear();
            deleteIdField.clear();
            nameField.clear();
            monthsField.clear();
            interestField.clear();
            punishmentField.clear();
            minAmountField.clear();
            maxAmountField.clear();
        });
    }

    private void createClickListenerForDeleteButton() {
        deleteButton.addClickListener(e -> {
            api.deleteLoanType(deleteIdField.getValue().intValue());
            UI.getCurrent().getPage().reload();
        });
    }

}
