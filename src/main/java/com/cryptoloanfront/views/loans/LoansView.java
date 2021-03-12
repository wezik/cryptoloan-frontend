package com.cryptoloanfront.views.loans;

import com.cryptoloanfront.domain.Loan;
import com.cryptoloanfront.domain.LoanType;
import com.cryptoloanfront.domain.User;
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
import java.util.*;

@Route("loans")
@PageTitle("Crypto Loan")
@CssImport("./styles/table-view.css")
public class LoansView extends BaseView {

    private final CryptoLoanService api;
    private final Grid<Loan> grid;

    public LoansView(@Autowired CryptoLoanService cryptoLoanService) {
        api = cryptoLoanService;
        List<Loan> loans = api.getLoans();

        Div horizontalDiv = new Div();
        horizontalDiv.setId("hDiv");

        Div main = getMain();

        main.add(new H1("Loans"));
        main.add(horizontalDiv);
        grid = new Grid<>();
        grid.setItems(loans);
        grid.addColumn(Loan::getId).setHeader("ID");
        grid.addColumn(e -> e.getUser().getId()+"/"+e.getUser().getFirstName()+"."+e.getUser().getLastName().charAt(0)).setHeader("User");
        grid.addColumn(e -> e.getLoanType().getId()+"/"+e.getLoanType().getName()).setHeader("Loan Type");
        grid.addColumn(Loan::getInitialDate).setHeader("Initial Date");
        grid.addColumn(Loan::getFinalDate).setHeader("End Date");
        grid.addColumn(Loan::getAmountBorrowed).setHeader("Amount");
        grid.addColumn(Loan::getAmountToPay).setHeader("Amount To Pay");
        grid.addColumn(Loan::getCurrencyBorrowed).setHeader("Curr. Borrowed");
        grid.addColumn(Loan::getCurrencyPaidIn).setHeader("Curr. Paid in");
        grid.addColumn(Loan::getInstallmentsCreated).setHeader("Install. Created");
        grid.addColumn(Loan::getInstallmentsPaid).setHeader("Install. Paid");
        grid.addColumn(Loan::getInstallmentsTotal).setHeader("Install. Total");
        main.add(grid);

        Div addLoanDiv = createLoanDiv();

        Div deleteLoanDiv = createRemoveLoanDiv();

        horizontalDiv.add(addLoanDiv,deleteLoanDiv);

        Div annotation = new Div();
        annotation.setText("Fields annotated with (*) are not required or even recommended when adding an entity.");
        Div annotation2 = new Div();
        annotation2.setText("Also select an entity to edit it faster.");
        main.add(annotation,annotation2);

        add(main);
    }

    private Div createLoanDiv() {
        Div addLoanDiv = new Div();
        addLoanDiv.setId("addDiv");
        NumberField id = new NumberField("ID *");
        id.setId("idInput");
        id.setPlaceholder("0");
        List<User> users = api.getUsers();
        Map<String, User> userMap = new HashMap<>();
        List<String> userComboBoxChoices = new ArrayList<>();
        users.forEach(e -> {
            userMap.put(e.getId()+"/"+e.getFirstName()+"."+e.getLastName().charAt(0),e);
            userComboBoxChoices.add(e.getId()+"/"+e.getFirstName()+"."+e.getLastName().charAt(0));
        });
        ComboBox<String> user = new ComboBox<>("User");
        user.setItems(userComboBoxChoices);
        user.setPlaceholder("User");
        user.setClassName("shortenedComboBox");
        List<LoanType> loanTypes = api.getLoanTypes();
        Map<String, LoanType> loanTypeMap = new HashMap<>();
        List<String> loanTypeComboBoxChoices = new ArrayList<>();
        loanTypes.forEach(e -> {
            loanTypeMap.put(e.getId()+"/"+e.getName(),e);
            loanTypeComboBoxChoices.add(e.getId()+"/"+e.getName());
        });
        ComboBox<String> loanType = new ComboBox<>("Loan Type");
        loanType.setItems(loanTypeComboBoxChoices);
        loanType.setPlaceholder("Loan Type");
        loanType.setClassName("shortenedComboBox");
        DatePicker initialDate = new DatePicker("Initial Date");
        initialDate.setPlaceholder("1.11.1111");
        initialDate.setClassName("dateBox");
        DatePicker endDate = new DatePicker("End Date *");
        endDate.setPlaceholder("1.11.1111");
        endDate.setClassName("dateBox");
        NumberField amount = new NumberField("Amount");
        amount.setClassName("amountBox");
        amount.setPlaceholder("1234.56");
        NumberField amountToPay = new NumberField("Amount To Pay *");
        amount.setClassName("amountBox");
        amountToPay.setPlaceholder("1234.56");
        List<String> currencies = new ArrayList<>(api.getAllCurrencies());
        Collections.sort(currencies);
        ComboBox<String> currencyBorrowed = new ComboBox<>("Curr. Borrowed");
        currencyBorrowed.setItems(currencies);
        currencyBorrowed.setPlaceholder("EUR");
        currencyBorrowed.setClassName("currencyBox");
        ComboBox<String> currencyPaidIn = new ComboBox<>("Curr. Paid In");
        currencyPaidIn.setItems(currencies);
        currencyPaidIn.setPlaceholder("EUR");
        currencyPaidIn.setClassName("currencyBox");
        NumberField installCreated = new NumberField("Inst. Created *");
        installCreated.setClassName("idBox");
        installCreated.setPlaceholder("0");
        NumberField installPaid = new NumberField("Inst. Paid *");
        installPaid.setClassName("idBox");
        installPaid.setPlaceholder("0");
        NumberField installTotal = new NumberField("Inst. Total *");
        installTotal.setClassName("idBox");
        installTotal.setPlaceholder("0");

        grid.addSelectionListener(e -> {
            if (e.getFirstSelectedItem().isPresent()) {
                Loan loan = e.getFirstSelectedItem().get();
                id.setValue(loan.getId().doubleValue());
                user.setValue(loan.getUser().getId()+"/"+loan.getUser().getFirstName()+"."+loan.getUser().getLastName().charAt(0));
                loanType.setValue(loan.getLoanType().getId()+"/"+loan.getLoanType().getName());
                initialDate.setValue(loan.getInitialDate());
                endDate.setValue(loan.getFinalDate());
                amount.setValue(Double.parseDouble(loan.getAmountBorrowed()));
                amountToPay.setValue(Double.parseDouble(loan.getAmountToPay()));
                currencyBorrowed.setValue(loan.getCurrencyBorrowed());
                currencyPaidIn.setValue(loan.getCurrencyPaidIn());
                installCreated.setValue(loan.getInstallmentsCreated().doubleValue());
                installPaid.setValue(loan.getInstallmentsPaid().doubleValue());
                installTotal.setValue(loan.getInstallmentsTotal().doubleValue());
            }
        });

        List<Component> loanData = List.of(id,
                user,
                loanType,
                initialDate,
                endDate,
                amount,
                amountToPay,
                currencyBorrowed,
                currencyPaidIn,
                installCreated,
                installPaid,
                installTotal);
        for (int i=1; i<loanData.size(); i++) {
            loanData.get(i).setId("inputField");
        }
        Button add = new Button("Add");
        add.addClickListener(e -> {

            LoanType loanTypeEntry = loanTypeMap.get(loanType.getValue());

            api.saveLoan(new Loan(null,
                    userMap.get(user.getValue()),
                    loanTypeEntry,
                    initialDate.getValue(),
                    initialDate.getValue().plusDays(api.getTimeSettingInDays()*(loanTypeEntry.getTimePeriod()+1)),
                    BigDecimal.valueOf(amount.getValue()).stripTrailingZeros().toPlainString(),
                    BigDecimal.valueOf(amount.getValue()+(amount.getValue()*(loanTypeEntry.getInterest()*0.01))).stripTrailingZeros().toPlainString(),
                    currencyBorrowed.getValue(),
                    currencyPaidIn.getValue(),
                    installPaid.getValue()==null ? 0 : installPaid.getValue().intValue(),
                    installCreated.getValue()==null ? 0 : installCreated.getValue().intValue(),
                    installTotal.getValue()==null ? loanTypeEntry.getTimePeriod() : installTotal.getValue().intValue()));
            UI.getCurrent().getPage().reload();
            Notification.show("Loan "+id.getValue()+" Added");
        });
        Button update = new Button("Update");
        update.addClickListener(e -> {
                    api.updateLoan(new Loan(id.getValue().longValue(),
                            userMap.get(user.getValue()),
                            loanTypeMap.get(loanType.getValue()),
                            initialDate.getValue(),
                            endDate.getValue(),
                            BigDecimal.valueOf(amount.getValue()).stripTrailingZeros().toPlainString(),
                            BigDecimal.valueOf(amountToPay.getValue()).stripTrailingZeros().toPlainString(),
                            currencyBorrowed.getValue(),
                            currencyPaidIn.getValue(),
                            installPaid.getValue().intValue(),
                            installCreated.getValue().intValue(),
                            installTotal.getValue().intValue()));
                    UI.getCurrent().getPage().reload();
                    Notification.show("Loan " + id.getValue() + " Updated");
                });
        Button clear = new Button("Clear");
        clear.addClickListener(e -> {
            id.clear();
            user.clear();
            loanType.clear();
            initialDate.clear();
            endDate.clear();
            amount.clear();
            amountToPay.clear();
            currencyBorrowed.clear();
            currencyPaidIn.clear();
            installCreated.clear();
            installPaid.clear();
            installTotal.clear();
        });
        loanData.forEach(addLoanDiv::add);
        addLoanDiv.add(add,update,clear);
        return addLoanDiv;
    }

    private Div createRemoveLoanDiv() {
        Div deleteLoanDiv = new Div();
        deleteLoanDiv.setId("deleteDiv");
        NumberField deleteId = new NumberField("ID");
        deleteId.setId("idInput");
        deleteId.setPlaceholder("0");
        Button deleteButton = new Button("Delete");
        deleteButton.addClickListener(e -> {
            api.deleteLoan(deleteId.getValue().intValue());
            UI.getCurrent().getPage().reload();
            Notification.show("Loan "+deleteId.getValue().intValue()+" Deleted");
        });
        deleteButton.setId("deleteButton");
        deleteLoanDiv.add(deleteId,deleteButton);
        return deleteLoanDiv;
    }

}
