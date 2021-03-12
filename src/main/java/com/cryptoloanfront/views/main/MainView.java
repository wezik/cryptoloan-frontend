package com.cryptoloanfront.views.main;

import com.cryptoloanfront.views.home.HomeView;
import com.cryptoloanfront.views.installments.InstallmentsView;
import com.cryptoloanfront.views.loans.LoansView;
import com.cryptoloanfront.views.loantypes.LoanTypesView;
import com.cryptoloanfront.views.users.UsersView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route()
@PageTitle("Crypto Loan")
@CssImport("./styles/main-view.css")
public class MainView extends Div {

    public MainView() {
        setId("main-view");
        Div div = new Div();
        div.setId("main-panel");

        div.add(
                new H1("Crypto Loan"),
                createButton("Home", HomeView.class),
                createButton("User Registration", UsersView.class),
                createButton("Loan Types", LoanTypesView.class),
                createButton("Loans", LoansView.class),
                createButton("Installments", InstallmentsView.class)
        );
        Text desc = new Text("Home is the main page for user interaction, the rest is purely administrative");
        Div div2 = new Div();
        div2.add(desc);
        div.add(div2);
        add(div);
    }

    private Button createButton(String name, Class<? extends Component> clazz) {
        Button button = new Button(name);
        button.addClickListener(e -> UI.getCurrent().navigate(clazz));
        return button;
    }

}
