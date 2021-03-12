package com.cryptoloanfront.views.home;

import com.cryptoloanfront.domain.Loan;
import com.cryptoloanfront.domain.LoanType;
import com.cryptoloanfront.domain.User;
import com.cryptoloanfront.service.CryptoLoanService;
import com.cryptoloanfront.views.BaseView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
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
import java.util.*;

@Route("home")
@PageTitle("Crypto Loan")
@CssImport("./styles/home-view.css")
public class HomeView extends BaseView {

    public HomeView(@Autowired CryptoLoanService cryptoLoanService) {
        Div div = getMain();

        Div headers = new Div();
        Div headerLeft = new Div();
        Div headerRight = new Div();
        headers.add(headerLeft,headerRight);
        List<String> currencies = new ArrayList<>(cryptoLoanService.getAllCurrencies());
        Collections.sort(currencies);
        NumberField amountField = new NumberField();
        amountField.setValue(1d);
        ComboBox<String> currency1Box = new ComboBox<>();
        currency1Box.setItems(currencies);
        currency1Box.setValue("EUR");
        ComboBox<String> currency2Box = new ComboBox<>();
        currency2Box.setItems(currencies);
        currency2Box.setValue("EUR");
        Text exchange = new Text("1"+" "+"EUR"+" exchanges for "+"1"+" "+"EUR");
        amountField.addValueChangeListener(e ->
                updateExchangeAmountInCalculator(exchange,e.getValue(),currency1Box.getValue(),currency2Box.getValue(),cryptoLoanService));
        currency1Box.addValueChangeListener(e ->
                updateExchangeAmountInCalculator(exchange,amountField.getValue(),e.getValue(),currency2Box.getValue(),cryptoLoanService));
        currency2Box.addValueChangeListener(e ->
                updateExchangeAmountInCalculator(exchange,amountField.getValue(),currency1Box.getValue(),e.getValue(),cryptoLoanService));
        headerLeft.setId("hDivLeft");
        headerRight.setId("hDivRight");
        headers.setId("hDiv");
        amountField.setId("amountField");
        currency1Box.setId("currency1Box");
        currency2Box.setId("currency2Box");
        Div headerLeftDiv1 = new Div();
        Div headerLeftDiv2 = new Div();
        headerLeftDiv1.add(amountField,currency1Box,currency2Box);
        headerLeftDiv2.add(exchange);
        headerLeft.add(headerLeftDiv1,headerLeftDiv2);
        Div rightUpper = new Div();
        rightUpper.setText("Current Bitcoin price "+cryptoLoanService.exchangeCurrency("BTC","EUR").setScale(0, RoundingMode.CEILING)+" EUR");
        Div rightLower = new Div();
        headerRight.add(rightUpper,rightLower);

        Div hero = new Div();
        hero.setId("heroDiv");
        Div heroHeader = new Div();
        Div heroHero = new Div();
        heroHero.setId("heroHeroDiv");
        Div heroFooter = new Div();
        heroHeader.add(new H1("Take a loan!"));


        ComboBox<String> userBox = new ComboBox<>("User ID");
        userBox.setPlaceholder("ID");
        List<User> users = cryptoLoanService.getUsers();
        Map<String, User> userMap = new HashMap<>();
        List<String> userComboBoxChoices = new ArrayList<>();
        users.forEach(e -> {
            userMap.put(e.getId()+"/"+e.getFirstName()+"."+e.getLastName().charAt(0),e);
            userComboBoxChoices.add(e.getId()+"/"+e.getFirstName()+"."+e.getLastName().charAt(0));
        });
        userBox.setItems(userComboBoxChoices);

        ComboBox<String> loanTypeBox = new ComboBox<>("Loan Type");
        loanTypeBox.setPlaceholder("Loan Type");
        List<LoanType> loanTypes = cryptoLoanService.getLoanTypes();
        Map<String, LoanType> loanTypeMap = new HashMap<>();
        List<String> loanTypeComboBoxChoices = new ArrayList<>();
        loanTypes.forEach(e -> {
            loanTypeMap.put(e.getId()+"/"+e.getName(),e);
            loanTypeComboBoxChoices.add(e.getId()+"/"+e.getName());
        });
        loanTypeBox.setItems(loanTypeComboBoxChoices);

        NumberField amountBorrowed = new NumberField();
        amountBorrowed.setLabel("Amount");
        amountBorrowed.setValue(0d);
        amountBorrowed.setMin(0);
        amountBorrowed.setMax(1);

        loanTypeBox.addValueChangeListener(e -> {
            LoanType lt = loanTypeMap.get(e.getValue());
            amountBorrowed.setMin(new BigDecimal(lt.getMinAmount()).doubleValue());
            amountBorrowed.setMax(new BigDecimal(lt.getMaxAmount()).doubleValue());
            if (amountBorrowed.getValue()>amountBorrowed.getMax()) {
                amountBorrowed.setValue(amountBorrowed.getMax());
            } else if (amountBorrowed.getValue()<amountBorrowed.getMin()) {
                amountBorrowed.setValue(amountBorrowed.getMin());
            }
        });

        Div currenciesHeroHeroDiv = new Div();
        currenciesHeroHeroDiv.setId("currenciesHeroHeroDiv");

        ComboBox<String> currencyBorrowed = new ComboBox<>("Borrow");
        currencyBorrowed.setItems(currencies);
        currencyBorrowed.setValue("EUR");

        ComboBox<String> currencyPaidIn = new ComboBox<>("Paid In");
        currencyPaidIn.setItems(currencies);
        currencyPaidIn.setValue("EUR");

        NumberField totalField = new NumberField();
        totalField.setLabel("To Pay");
        totalField.setReadOnly(true);

        amountBorrowed.addValueChangeListener(e -> {
            updateExchangeInLoanTakingProcess(totalField,loanTypeMap.get(loanTypeBox.getValue()),e.getValue(),currencyBorrowed.getValue());
            if (amountBorrowed.getValue()>amountBorrowed.getMax()) {
                amountBorrowed.setValue(amountBorrowed.getMax());
            } else if (amountBorrowed.getValue()<amountBorrowed.getMin()) {
                amountBorrowed.setValue(amountBorrowed.getMin());
            }
        });
        currencyBorrowed.addValueChangeListener(e -> updateExchangeInLoanTakingProcess(totalField,loanTypeMap.get(loanTypeBox.getValue()),amountBorrowed.getValue(),e.getValue()));
        currencyPaidIn.addValueChangeListener(e -> updateExchangeInLoanTakingProcess(totalField,loanTypeMap.get(loanTypeBox.getValue()),amountBorrowed.getValue(),currencyBorrowed.getValue()));
        loanTypeBox.addValueChangeListener(e -> updateExchangeInLoanTakingProcess(totalField,loanTypeMap.get(e.getValue()),amountBorrowed.getValue(),currencyBorrowed.getValue()));

        currenciesHeroHeroDiv.add(currencyBorrowed);
        currenciesHeroHeroDiv.add(currencyPaidIn);
        List<Component> choices = new ArrayList<>();
        choices.add(userBox);
        choices.add(loanTypeBox);
        choices.add(amountBorrowed);
        choices.add(currenciesHeroHeroDiv);
        choices.add(totalField);

        choices.forEach(heroHero::add);

        Button submit = new Button("Submit");
        submit.setId("submit");
        submit.addClickListener(e -> {
            if (createLoan(
                    userMap.get(userBox.getValue()),
                    loanTypeMap.get(loanTypeBox.getValue()),
                    amountBorrowed.getValue(),
                    totalField.getValue(),
                    currencyBorrowed.getValue(),
                    currencyPaidIn.getValue(),
                    cryptoLoanService)
            ) Notification.show("Loan was created");
            else {
                Notification.show("Failed to create a loan");
            }
        });
        heroFooter.add(submit);

        if (loanTypes.size()<=0 || users.size()<=0) {
            Div info = new Div();
            info.setText("For this section to work you have to create Users and Loan Types!");
            heroHeader.add(info);
        }

        hero.add(heroHeader,heroHero,heroFooter);

        div.add(new H1("Crypto Loan"));
        div.add(headers,hero);
        add(div);
    }

    private void updateExchangeAmountInCalculator(Text text, Double amount, String currency1, String currency2, CryptoLoanService cryptoLoanService) {
        if (amount==null||amount<0) amount=0.0;
        text.setText(
                BigDecimal.valueOf(amount).stripTrailingZeros()+
                        " "+
                        currency1+
                        " exchanges for "+
                        BigDecimal.valueOf(amount).multiply(cryptoLoanService.exchangeCurrency(currency1,currency2)).stripTrailingZeros().toPlainString() +
                        " "+
                        currency2);
    }

    private void updateExchangeInLoanTakingProcess(NumberField numberField,LoanType loanType, Double amount, String currency) {
        if (amount==null || amount<0) amount=0.0;
        numberField.setValue(
                !currency.equals("BTC") ? BigDecimal.valueOf(amount+(amount*(loanType.getInterest()*0.01))).setScale(2,RoundingMode.CEILING).doubleValue()
                        : BigDecimal.valueOf(amount+(amount*(loanType.getInterest()*0.01))).stripTrailingZeros().doubleValue()
        );
    }

    private boolean createLoan(User user, LoanType loanType, Double amount, Double amountToPay, String currencyBorrowed, String currencyPaidIn, CryptoLoanService cryptoLoanService) {
        int days = cryptoLoanService.getTimeSettingInDays();
        if (
                user == null ||
                        loanType == null ||
                        amount == null ||
                        amountToPay == null ||
                        currencyBorrowed == null ||
                        currencyPaidIn == null
        ) return false;

        Loan loan = new Loan(null,
                user,
                loanType,
                LocalDate.now(),
                LocalDate.now().plusDays(days * (loanType.getTimePeriod() + 1)),
                amount.toString(),
                amountToPay.toString(),
                currencyBorrowed,
                currencyPaidIn,
                0,
                0,
                loanType.getTimePeriod());
        cryptoLoanService.saveLoan(loan);
        return true;
    }

}
