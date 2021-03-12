package com.cryptoloanfront.views.loantypes;

import com.cryptoloanfront.domain.LoanType;
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
import java.util.List;

@Route("loantypes")
@PageTitle("Crypto Loan")
@CssImport("./styles/table-view.css")
public class LoanTypesView extends BaseView {

    private final CryptoLoanService api;
    private final Grid<LoanType> grid;

    public LoanTypesView(@Autowired CryptoLoanService cryptoLoanService) {
        api = cryptoLoanService;
        List<LoanType> loanTypes = api.getLoanTypes();

        Div horizontalDiv = new Div();
        horizontalDiv.setId("hDiv");

        Div main = getMain();

        main.add(new H1("Loan Types"));
        main.add(horizontalDiv);
        grid = new Grid<>();
        grid.setItems(loanTypes);
        grid.addColumn(LoanType::getId).setHeader("ID");
        grid.addColumn(LoanType::getName).setHeader("Name");
        grid.addColumn(LoanType::getTimePeriod).setHeader("Time Period");
        grid.addColumn(LoanType::getInterest).setHeader("Interest (%)");
        grid.addColumn(LoanType::getPunishment).setHeader("Punishment (%)");
        grid.addColumn(LoanType::getMinAmount).setHeader("Min. Amount");
        grid.addColumn(LoanType::getMaxAmount).setHeader("Max. Amount");
        main.add(grid);

        Div addLoanTypeDiv = createInstallmentDiv();

        Div deleteLoanTypeDiv = createRemoveInstallmentDiv();

        horizontalDiv.add(addLoanTypeDiv,deleteLoanTypeDiv);

        Div annotation = new Div();
        annotation.setText("Fields annotated with (*) are not required or even recommended when adding an entity.");
        Div annotation2 = new Div();
        annotation2.setText("Also select an entity to edit it faster.");
        main.add(annotation,annotation2);

        add(main);
    }

    private Div createInstallmentDiv() {
        Div addLoanTypeDiv = new Div();
        addLoanTypeDiv.setId("addDiv");
        NumberField id = new NumberField("ID *");
        id.setId("idInput");
        id.setPlaceholder("0");
        TextField name = new TextField("Name");
        name.setPlaceholder("Name");
        NumberField timePeriod = new NumberField("Months");
        timePeriod.setId("idInput");
        timePeriod.setPlaceholder("12");
        NumberField interest = new NumberField("Interest (%)");
        interest.setId("idInput");
        interest.setPlaceholder("12.3");
        NumberField punishment = new NumberField("Punishment (%)");
        punishment.setId("idInput");
        punishment.setPlaceholder("12.3");
        NumberField minAmount = new NumberField("Min. Amount");
        minAmount.setPlaceholder("1234.56");
        NumberField maxAmount = new NumberField("Max. Amount");
        maxAmount.setPlaceholder("1234.56");
        List<Component> loanTypeData = List.of(id,name,timePeriod,interest,punishment,minAmount,maxAmount);
        for (int i=1; i<loanTypeData.size(); i++) {
            loanTypeData.get(i).setId("inputField");
        }

        grid.addSelectionListener(e -> {
            if (e.getFirstSelectedItem().isPresent()) {
                LoanType loanType = e.getFirstSelectedItem().get();
                id.setValue(loanType.getId().doubleValue());
                name.setValue(loanType.getName());
                timePeriod.setValue(loanType.getTimePeriod().doubleValue());
                interest.setValue(loanType.getInterest());
                punishment.setValue(loanType.getPunishment());
                minAmount.setValue(Double.parseDouble(loanType.getMinAmount()));
                maxAmount.setValue(Double.parseDouble(loanType.getMaxAmount()));
            }
        });

        Button add = new Button("Add");
        add.addClickListener(e -> {
            api.saveLoanType(new LoanType(null,
                    name.getValue(),
                    timePeriod.getValue().intValue(),
                    interest.getValue(),
                    punishment.getValue(),
                    BigDecimal.valueOf(minAmount.getValue()).toPlainString(),
                    BigDecimal.valueOf(maxAmount.getValue()).toPlainString()));
            UI.getCurrent().getPage().reload();
            Notification.show("Loan type "+name.getValue()+" Added");
        });
        Button update = new Button("Update");
        update.addClickListener(e -> {
            api.updateLoanType(new LoanType(id.getValue().longValue(),
                    name.getValue(),
                    timePeriod.getValue().intValue(),
                    interest.getValue(),
                    punishment.getValue(),
                    BigDecimal.valueOf(minAmount.getValue()).toPlainString(),
                    BigDecimal.valueOf(maxAmount.getValue()).toPlainString()));
            UI.getCurrent().getPage().reload();
            Notification.show("Loan type "+id.getValue().intValue()+" Updated");
        });
        Button clear = new Button("Clear");
        clear.addClickListener(e -> {
            id.clear();
            name.clear();
            timePeriod.clear();
            interest.clear();
            punishment.clear();
            minAmount.clear();
            maxAmount.clear();
        });
        loanTypeData.forEach(addLoanTypeDiv::add);
        addLoanTypeDiv.add(add,update,clear);
        return addLoanTypeDiv;
    }

    private Div createRemoveInstallmentDiv() {
        Div deleteInstallmentDiv = new Div();
        deleteInstallmentDiv.setId("deleteDiv");
        NumberField deleteId = new NumberField("ID");
        deleteId.setId("idInput");
        deleteId.setPlaceholder("0");
        Button deleteButton = new Button("Delete");
        deleteButton.addClickListener(e -> {
            api.deleteLoanType(deleteId.getValue().intValue());
            UI.getCurrent().getPage().reload();
            Notification.show("Loan type " + deleteId.getValue().intValue() + " Deleted");
        });
        deleteButton.setId("deleteButton");
        deleteInstallmentDiv.add(deleteId,deleteButton);
        return deleteInstallmentDiv;
    }

}
