package com.cryptoloanfront.views;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;

@PageTitle("Crypto Loan")
@CssImport("./styles/main-view.css")
public class BaseView extends Div { ;

    private final Div main = new Div();

    public BaseView() {
        setId("main-view");
        main.setId("main-panel");
    }

    public Div getMain() {
        return main;
    }

}
