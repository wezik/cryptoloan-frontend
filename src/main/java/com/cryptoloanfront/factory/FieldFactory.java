package com.cryptoloanfront.factory;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.stereotype.Component;

@Component
public class FieldFactory {

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
