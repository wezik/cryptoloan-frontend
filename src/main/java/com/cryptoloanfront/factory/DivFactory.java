package com.cryptoloanfront.factory;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import org.springframework.stereotype.Component;

@Component
public class DivFactory {

    public Div createCompleteDiv(String title, Div addDiv, Div deleteDiv, Div gridDiv) {
        Div resultDiv = createDivWithId("main-panel");
        resultDiv.add(new H1(title));
        Div menuDiv = createDivWithId("hDiv");
        menuDiv.add(addDiv,deleteDiv);
        Div footerDiv = createFooterDiv();
        resultDiv.add(menuDiv,gridDiv,footerDiv);
        return resultDiv;
    }

    public Div createDivWithId(String id) {
        Div resultDiv = new Div();
        resultDiv.setId(id);
        return resultDiv;
    }

    private Div createFooterDiv() {
        Div resultDiv = new Div();
        Div annotation = new Div();
        annotation.setText("Fields annotated with (*) are not required or even recommended when adding an entity.");
        Div annotation2 = new Div();
        annotation2.setText("Also select an entity to edit it faster.");
        resultDiv.add(annotation,annotation2);
        return resultDiv;
    }

}
