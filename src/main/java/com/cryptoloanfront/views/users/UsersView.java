package com.cryptoloanfront.views.users;

import com.cryptoloanfront.domain.User;
import com.cryptoloanfront.service.CryptoLoanService;
import com.cryptoloanfront.views.BaseView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@PageTitle("Crypto Loan")
@Route("users")
@CssImport("./styles/table-view.css")
public class UsersView extends BaseView {

    private final List<User> users;
    private final CryptoLoanService api;
    private final Grid<User> userDataGrid;


    public UsersView(@Autowired CryptoLoanService cryptoLoanService) {
        api = cryptoLoanService;
        users = api.getUsers();
        userDataGrid = createUserDataGrid();

        Div main = getMain();

        Div horizontalDiv = new Div();
        horizontalDiv.setId("hDiv");

        Div addUserDiv = createAddUserDiv();

        Div deleteUserDiv = createRemoveUserDiv();

        horizontalDiv.add(addUserDiv,deleteUserDiv);



        main.add(new H1("Users"));
        main.add(horizontalDiv);
        main.add(userDataGrid);

        Div annotation = new Div();
        annotation.setText("Fields annotated with (*) are not required or even recommended when adding an entity.");
        Div annotation2 = new Div();
        annotation2.setText("Also select an entity to edit it faster.");
        main.add(annotation,annotation2);

        add(main);
    }

    private Grid<User> createUserDataGrid() {
        Grid<User> grid = new Grid<>();
        grid.setItems(users);
        grid.addColumn(User::getId).setHeader("ID");
        grid.addColumn(User::getFirstName).setHeader("First Name");
        grid.addColumn(User::getLastName).setHeader("Last Name");
        grid.addColumn(User::getPhoneNumber).setHeader("Phone Number");
        grid.addColumn(User::getSocialSecurityNumber).setHeader("Social Security");
        return grid;
    }

    private Div createAddUserDiv() {
        Div addUserDiv = new Div();
        addUserDiv.setId("addDiv");
        NumberField id = new NumberField("ID *");
        id.setPlaceholder("0");
        id.setId("idInput");
        TextField name = new TextField("First Name");
        name.setPlaceholder("First Name");
        TextField lastName = new TextField("Last Name");
        lastName.setPlaceholder("Last Name");
        TextField phoneNumber = new TextField("Phone Number");
        phoneNumber.setPlaceholder("+12 345 678 910");
        NumberField securityNumber = new NumberField("Social Security Num");
        securityNumber.setPlaceholder("012345678");
        List<Component> userData = List.of(id,name,lastName,phoneNumber,securityNumber);
        for (int i=1; i<userData.size(); i++) {
            userData.get(i).setId("inputField");
        }

        userDataGrid.addSelectionListener(e -> {
            if (e.getFirstSelectedItem().isPresent()) {
                User user = e.getFirstSelectedItem().get();
                id.setValue(user.getId().doubleValue());
                name.setValue(user.getFirstName());
                lastName.setValue(user.getLastName());
                phoneNumber.setValue(user.getPhoneNumber());
                securityNumber.setValue(Double.parseDouble(user.getSocialSecurityNumber()));
            }
        });

        Button add = new Button("Add");
        add.addClickListener(e -> {
            api.saveUser(new User(null,
                    name.getValue(),
                    lastName.getValue(),
                    phoneNumber.getValue(),
                    BigDecimal.valueOf(securityNumber.getValue()).setScale(0,RoundingMode.CEILING).toPlainString()));
            UI.getCurrent().getPage().reload();
            Notification.show("User "+name.getValue()+" Added");
        });
        Button update = new Button("Update");
        update.addClickListener(e -> {
            api.updateUser(new User(id.getValue().longValue(),
                    name.getValue(),
                    lastName.getValue(),
                    phoneNumber.getValue(),
                    BigDecimal.valueOf(securityNumber.getValue()).setScale(0,RoundingMode.CEILING).toPlainString()));
            UI.getCurrent().getPage().reload();
            Notification.show("User "+id.getValue().intValue()+" Updated");
        });

        Button clear = new Button("Clear");
        clear.addClickListener(e -> {
            id.clear();
            name.clear();
            lastName.clear();
            phoneNumber.clear();
            securityNumber.clear();
        });
        userData.forEach(addUserDiv::add);
        addUserDiv.add(add,update,clear);
        return addUserDiv;
    }

    private Div createRemoveUserDiv() {
        Div deleteUserDiv = new Div();
        deleteUserDiv.setId("deleteDiv");
        NumberField deleteId = new NumberField("ID");
        deleteId.setPlaceholder("0");
        deleteId.setId("idInput");
        Button deleteButton = new Button("Delete");
        deleteButton.addClickListener(e -> {
            api.deleteUser(deleteId.getValue().intValue());
            UI.getCurrent().getPage().reload();
            Notification.show("User "+deleteId.getValue().intValue()+" Deleted");
        });
        deleteButton.setId("deleteButton");
        deleteUserDiv.add(deleteId,deleteButton);
        return deleteUserDiv;
    }

}
