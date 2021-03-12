package com.cryptoloanfront.views.installments;

import com.cryptoloanfront.domain.Installment;
import com.cryptoloanfront.domain.Loan;
import com.cryptoloanfront.service.CryptoLoanService;
import com.cryptoloanfront.views.BaseView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route("installments")
@PageTitle("Crypto Loan")
@CssImport("./styles/table-view.css")
public class InstallmentsView extends BaseView {

    private final CryptoLoanService api;
    private final Grid<Installment> grid;

    public InstallmentsView(@Autowired CryptoLoanService cryptoLoanService) {
        api = cryptoLoanService;
        List<Installment> installments = api.getInstallments();

        Div horizontalDiv = new Div();
        horizontalDiv.setId("hDiv");

        Div main = getMain();

        main.add(new H1("Installments"));
        main.add(horizontalDiv);
        grid = new Grid<>();
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
        main.add(grid);

        Div addLoanDiv = createLoanDiv();

        Div deleteLoanDiv = createRemoveInstallmentDiv();

        horizontalDiv.add(addLoanDiv,deleteLoanDiv);

        Div annotation = new Div();
        annotation.setText("Fields annotated with (*) are not required or even recommended when adding an entity.");
        Div annotation2 = new Div();
        annotation2.setText("Also select an entity to edit it faster.");
        main.add(annotation,annotation2);

        add(main);
    }

    private Div createLoanDiv() {
        Div addInstallmentDiv = new Div();
        addInstallmentDiv.setId("addDiv");
        NumberField id = new NumberField("ID *");
        id.setId("idInput");
        id.setPlaceholder("0");

        DatePicker date = new DatePicker("Last Updated *");
        date.setPlaceholder("1.11.1111");

        NumberField amount = new NumberField("Amount *");
        amount.setId("idInput");
        amount.setPlaceholder("1234.56");
        NumberField amountToPay = new NumberField("Amount In Paid *");
        amountToPay.setPlaceholder("1234.56");

        ComboBox<Boolean> isPaid = new ComboBox<>("Is Paid *");
        isPaid.setItems(true,false);
        isPaid.setPlaceholder("false");
        isPaid.setClassName("booleanBox");

        List<Loan> loans = api.getLoans();
        Map<String, Loan> loansMap = new HashMap<>();
        List<String> loansComboBoxChoices = new ArrayList<>();
        loans.forEach(e -> {
            String entry = e.getId()+"/"+e.getUser().getFirstName()+"."+e.getUser().getLastName().charAt(0)+"/"+e.getLoanType().getName();
            loansMap.put(entry,e);
            loansComboBoxChoices.add(entry);
        });
        ComboBox<String> loan = new ComboBox<>("Loan");
        loan.setItems(loansComboBoxChoices);
        loan.setPlaceholder("Loan");

        grid.addSelectionListener(e -> {
            if (e.getFirstSelectedItem().isPresent()) {
                Installment installment = e.getFirstSelectedItem().get();
                Loan loanEntry = installment.getLoan();
                id.setValue(installment.getId().doubleValue());
                date.setValue(installment.getLocalDate());
                amount.setValue(Double.parseDouble(installment.getAmountInBorrowed()));
                amountToPay.setValue(Double.parseDouble(installment.getAmountInPaid()));
                isPaid.setValue(installment.isPaid());
                loan.setValue(loanEntry.getId()+"/"+loanEntry.getUser().getFirstName()+"."+loanEntry.getUser().getLastName().charAt(0)+"/"+loanEntry.getLoanType().getName());
            }
        });

        List<Component> installmentData = List.of(id,
                date,
                amount,
                amountToPay,
                isPaid,
                loan);
        for (int i=1; i<installmentData.size(); i++) {
            installmentData.get(i).setId("inputField");
        }
        Button add = new Button("Add");
        add.addClickListener(e -> {

            Loan loanEntry = loansMap.get(loan.getValue());
            BigDecimal amountEntry = BigDecimal.valueOf(Double.parseDouble(loanEntry.getAmountToPay()))
                    .subtract(BigDecimal.valueOf(loanEntry.getInstallmentsTotal()))
                    .stripTrailingZeros();

            BigDecimal finalAmount = amountEntry.multiply(api.exchangeCurrency(loanEntry.getCurrencyBorrowed(),loanEntry.getCurrencyPaidIn()));
            if (!loanEntry.getCurrencyPaidIn().equalsIgnoreCase("BTC")) {
                finalAmount = finalAmount.setScale(2,RoundingMode.CEILING).stripTrailingZeros();
            } else {
                finalAmount = finalAmount.stripTrailingZeros();
            }

            api.saveInstallment(new Installment(null,
                    LocalDate.now(),
                    amountEntry.toPlainString(),
                    finalAmount.toPlainString(),
                    isPaid.getValue()==null ? false : isPaid.getValue(),
                    loanEntry
            ));
            UI.getCurrent().getPage().reload();
            Notification.show("Installment "+id.getValue()+" Added");
        });
        Button update = new Button("Update");
        update.addClickListener(e -> {
            Loan loanEntry = loansMap.get(loan.getValue());

                    api.updateInstallment(new Installment(id.getValue().longValue(),
                            date.getValue(),
                            BigDecimal.valueOf(amount.getValue()).stripTrailingZeros().toPlainString(),
                            BigDecimal.valueOf(amountToPay.getValue()).stripTrailingZeros().toPlainString(),
                            isPaid.getValue(),
                            loanEntry
                    ));
                    UI.getCurrent().getPage().reload();
                    Notification.show("Installment " + id.getValue() + " Updated");
                });
        Button clear = new Button("Clear");
        clear.addClickListener(e -> {
            id.clear();
            date.clear();
            amount.clear();
            amountToPay.clear();
            isPaid.clear();
            loan.clear();
        });
        installmentData.forEach(addInstallmentDiv::add);
        addInstallmentDiv.add(add,update,clear);
        return addInstallmentDiv;
    }

    private Div createRemoveInstallmentDiv() {
        Div deleteInstallmentDiv = new Div();
        deleteInstallmentDiv.setId("deleteDiv");
        NumberField deleteId = new NumberField("ID");
        deleteId.setId("idInput");
        deleteId.setPlaceholder("0");
        Button deleteButton = new Button("Delete");
        deleteButton.addClickListener(e -> {
            api.deleteInstallment(deleteId.getValue().intValue());
            UI.getCurrent().getPage().reload();
            Notification.show("Installment "+deleteId.getValue().intValue()+" Deleted");
        });
        deleteButton.setId("deleteButton");
        deleteInstallmentDiv.add(deleteId,deleteButton);
        return deleteInstallmentDiv;
    }

}
