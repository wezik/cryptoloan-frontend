package com.cryptoloanfront.factory;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

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

    public TextField createTextField(String fieldName) {
        return createTextField(fieldName,fieldName);
    }

    public TextField createTextField(String fieldName, String placeHolder) {
        TextField field = new TextField(fieldName);
        field.setPlaceholder(placeHolder);
        field.setId("inputField");
        return field;
    }

    public NumberField createNumberField(String fieldName, String placeHolder) {
        NumberField field = new NumberField(fieldName);
        field.setPlaceholder(placeHolder);
        field.setId("inputField");
        return field;
    }

    public NumberField createIdNumberField() {
        NumberField field = new NumberField("ID *");
        field.setId("idInput");
        field.setPlaceholder("0");
        return field;
    }

    public NumberField createDeleteIdNumberField() {
        NumberField field = new NumberField("ID");
        field.setId("idInput");
        field.setPlaceholder("0");
        return field;
    }

    public Button createButton(String fieldName) {
        return new Button(fieldName);
    }

    public Button createAddButton() {
        return createButton("Add");
    }

    public Button createUpdateButton() {
        return createButton("Update");
    }

    public Button createClearButton() {
        return createButton("Clear");
    }

    public Button createDeleteButton() {
        Button button = createButton("Delete");
        button.setId("deleteButton");
        return button;
    }


}
