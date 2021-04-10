package com.cryptoloanfront.views;

import com.cryptoloanfront.domain.User;
import com.cryptoloanfront.factory.DivFactory;
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
import java.math.RoundingMode;
import java.util.List;

@Route("view")
@PageTitle("Crypto Loan")
@CssImport("./styles/table-view.css")
public class SomeView extends Div {

    private Button addButton, updateButton, clearButton, deleteButton;
    private NumberField idField, deleteIdField, securityNumberField;
    private TextField nameField, lastNameField, phoneNumberField;
    private Grid<User> grid;
    private final DivFactory divFactory = new DivFactory();
    private final List<User> users;
    private final CryptoLoanService api;

    public SomeView(@Autowired CryptoLoanService cryptoLoanService) {
        api = cryptoLoanService;
        users = api.getUsers();
        Div addDiv = createAddDiv();
        Div deleteDiv = createDeleteDiv();
        Div gridDiv = createGridDiv();
        createInteractions();
        Div main = divFactory.createCompleteDiv("Some View",addDiv,deleteDiv,gridDiv);
        add(main);
    }

    private Div createAddDiv() {
        Div addDiv = divFactory.createDivWithId("addDiv");
        idField = divFactory.createIdNumberField();
        nameField = divFactory.createTextField("First Name");
        lastNameField = divFactory.createTextField("Last Name");
        phoneNumberField = divFactory.createTextField("Phone Number","+12 345 678 910");
        securityNumberField = divFactory.createNumberField("Social Security Num","012345678");
        List<Component> addMenuFieldsList = List.of(idField,nameField,lastNameField,phoneNumberField,securityNumberField);

        addButton = divFactory.createAddButton();
        updateButton = divFactory.createUpdateButton();
        clearButton = divFactory.createClearButton();

        addMenuFieldsList.forEach(addDiv::add);
        addDiv.add(addButton,updateButton,clearButton);

        return addDiv;
    }

    private Div createDeleteDiv() {
        Div deleteDiv = divFactory.createDivWithId("deleteDiv");
        deleteIdField = divFactory.createDeleteIdNumberField();
        deleteButton = divFactory.createDeleteButton();
        deleteDiv.add(deleteIdField,deleteButton);
        return deleteDiv;
    }

    private Div createGridDiv() {
        Div div = divFactory.createDivWithId("grid-div");
        grid = createGrid();
        div.add(grid);
        return div;
    }

    private Grid<User> createGrid() {
        Grid<User> grid = new Grid<>();
        grid.setItems(users);
        grid.addColumn(User::getId).setHeader("ID");
        grid.addColumn(User::getFirstName).setHeader("First Name");
        grid.addColumn(User::getLastName).setHeader("Last Name");
        grid.addColumn(User::getPhoneNumber).setHeader("Phone Number");
        grid.addColumn(User::getSocialSecurityNumber).setHeader("Social Security");
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
                User user = e.getFirstSelectedItem().get();
                idField.setValue(user.getId().doubleValue());
                deleteIdField.setValue(user.getId().doubleValue());
                nameField.setValue(user.getFirstName());
                lastNameField.setValue(user.getLastName());
                phoneNumberField.setValue(user.getPhoneNumber());
                securityNumberField.setValue(Double.parseDouble(user.getSocialSecurityNumber()));
            }
        });
    }

    private void createClickListenerForAddButton() {
        addButton.addClickListener(e -> {
            User user = new User(null,
                    nameField.getValue(),
                    lastNameField.getValue(),
                    phoneNumberField.getValue(),
                    BigDecimal.valueOf(securityNumberField.getValue())
                            .setScale(0,RoundingMode.CEILING)
                            .toPlainString()
            );
            api.saveUser(user);
            UI.getCurrent().getPage().reload();
        });
    }

    private void createClickListenerForUpdateButton() {
        updateButton.addClickListener(e -> {
            User user = new User(idField.getValue().longValue(),
                    nameField.getValue(),
                    lastNameField.getValue(),
                    phoneNumberField.getValue(),
                    BigDecimal.valueOf(securityNumberField.getValue())
                            .setScale(0, RoundingMode.CEILING)
                            .toPlainString()
            );
            api.updateUser(user);
            UI.getCurrent().getPage().reload();
        });
    }

    private void createClickListenerForClearButton() {
        clearButton.addClickListener(e -> {
            idField.clear();
            deleteIdField.clear();
            nameField.clear();
            lastNameField.clear();
            phoneNumberField.clear();
            securityNumberField.clear();
        });
    }

    private void createClickListenerForDeleteButton() {
        deleteButton.addClickListener(e -> {
            api.deleteUser(deleteIdField.getValue().intValue());
            UI.getCurrent().getPage().reload();
        });
    }

}
